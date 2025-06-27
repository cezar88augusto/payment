package com.project.payment.service;

import com.project.payment.controller.dto.SaveBillDTO;
import com.project.payment.controller.dto.UpdateBillDTO;
import com.project.payment.controller.mapper.BillMapper;
import com.project.payment.exceptions.BillNotFoundException;
import com.project.payment.model.Bill;
import com.project.payment.repository.BillRepository;
import com.project.payment.validator.BillValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static java.math.BigDecimal.*;

@Service
@RequiredArgsConstructor
public class BillService {

    private final BillRepository repository;
    private final BillValidator billValidator;
    private final BillMapper mapper;

    public Bill save(SaveBillDTO saveBillDTO) {
        var bill = mapper.toBillEntity(saveBillDTO);
        billValidator.checkBillAlreadyRegistered(bill);

        return repository.save(bill);
    }

    public void updateBillFields(UUID billId, UpdateBillDTO updateBillDTO) {
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

    public Bill findBillById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new BillNotFoundException("Conta não encontrada."));
    }

    public BigDecimal sumBillAmountByPaymentDateBetween(LocalDate startDate, LocalDate endDate) {
        billValidator.validateSearchPeriodForSumOfBills(startDate, endDate);

        return repository.sumBillAmountByPaymentDateBetween(startDate, endDate)
                .orElse(ZERO);
    }
}