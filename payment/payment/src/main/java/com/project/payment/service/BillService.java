package com.project.payment.service;

import com.project.payment.model.Bill;
import com.project.payment.repository.BillRepository;
import com.project.payment.validator.BillValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BillService {

    private final BillRepository repository;
    private final BillValidator billValidator;

    public Bill save(Bill bill) {
        billValidator.checkBillAlreadyRegistered(bill);
        return repository.save(bill);
    }
}