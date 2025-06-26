package com.project.payment.service;

import com.project.payment.model.Bill;
import com.project.payment.repository.BillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BillService {

    private final BillRepository repository;

    public void save(Bill bill) {
        repository.save(bill);
    }
}