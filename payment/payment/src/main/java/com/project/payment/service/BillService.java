package com.project.payment.service;

import com.project.payment.controller.dto.SaveBillDTO;
import com.project.payment.controller.dto.UpdateBillDTO;
import com.project.payment.controller.mapper.BillMapper;
import com.project.payment.model.Bill;
import com.project.payment.repository.BillRepository;
import com.project.payment.validator.BillValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Base64;
import java.util.UUID;

import static java.math.BigDecimal.ZERO;

@Service
@RequiredArgsConstructor
public class BillService {

    private final BillRepository repository;
    private final BillValidator billValidator;
    private final BillMapper mapper;

    public Bill saveBill(SaveBillDTO saveBillDTO) {
        var bill = mapper.toBillEntity(saveBillDTO);
        billValidator.checkBillAlreadyRegistered(bill);

        return repository.save(bill);
    }

    public void updateBill(UUID billId, UpdateBillDTO updateBillDTO) {
        var bill = billValidator.checkExistingBill(billId);
        mapper.updateBillFromDTO(updateBillDTO, bill);
        billValidator.checkBillAlreadyRegistered(bill);

        repository.save(bill);
    }

    public void updateBillStatus(UUID billId, String status) {
        var bill = billValidator.checkExistingBill(billId);
        bill.setStatus(status);

        repository.save(bill);
    }

    public Page<Bill> findBills(LocalDate dueDate, String description, int pageNumber, int pageSize) {
        var bill = Bill.builder()
                .dueDate(dueDate)
                .description(description)
                .build();

        var matcher = ExampleMatcher
                .matching()
                .withIgnoreNullValues()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        var pageable = PageRequest.of(pageNumber, pageSize);

        return repository.findAll(Example.of(bill, matcher), pageable);
    }

    public Bill findBillById(UUID billId) {
        return billValidator.checkExistingBill(billId);
    }

    public BigDecimal sumBillAmountByPaymentDateBetween(LocalDate startDate, LocalDate endDate) {
        billValidator.validateSearchPeriodForSumOfBills(startDate, endDate);

        return repository.sumBillAmountByPaymentDateBetween(startDate, endDate)
                .orElse(ZERO);
    }

    public void processCsvBase64(String base64) {
        byte[] decoded = Base64.getDecoder().decode(base64);
        String decodedCsv = new String(decoded);
        String[] lines = decodedCsv.split("\\r?\\n");

        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length < 5) continue;

            var bill = Bill.builder()
                    .dueDate(LocalDate.parse(parts[0].trim()))
                    .paymentDate(LocalDate.parse(parts[1].trim()))
                    .amount(new BigDecimal(parts[2].trim()))
                    .description(parts[3].trim())
                    .status(parts[4].trim())
                    .build();

            repository.save(bill);
        }
    }

}