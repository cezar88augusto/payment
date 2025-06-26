package com.project.payment.validator;

import com.project.payment.exceptions.AlreadyRegisteredBillException;
import com.project.payment.model.Bill;
import com.project.payment.repository.BillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BillValidator {

    private final BillRepository repository;

    public void checkBillAlreadyRegistered(Bill bill) {
        var isBillRegisterd = repository.findByDueDateAndAmountAndStatus(bill.getDueDate(), bill.getAmount(), bill.getStatus())
                .isPresent();

        if (isBillRegisterd) {
            throw new AlreadyRegisteredBillException("Conta já cadastrada!");
        }
    }
}
