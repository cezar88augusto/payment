package com.project.payment.service;

import com.project.payment.controller.dto.SaveBillDTO;
import com.project.payment.controller.dto.UpdateBillDTO;
import com.project.payment.controller.mapper.BillMapper;
import com.project.payment.model.Bill;
import com.project.payment.repository.BillRepository;
import com.project.payment.validator.BillValidator;
import com.project.payment.validator.CsvFileValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static java.math.BigDecimal.ZERO;

@Service
@RequiredArgsConstructor
public class BillService {

    private final BillRepository repository;
    private final BillValidator billValidator;
    private final CsvFileValidator csvFileValidator;
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

    public void saveCsvBills(String csvBase64) {
       var bills = csvFileValidator.processCsvBase64(csvBase64);
        repository.saveAll(bills);
    }
}