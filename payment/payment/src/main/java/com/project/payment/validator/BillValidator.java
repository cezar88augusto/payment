package com.project.payment.validator;

import com.project.payment.exceptions.AlreadyRegisteredBillException;
import com.project.payment.exceptions.BillNotFoundException;
import com.project.payment.exceptions.InvalidPeriodException;
import com.project.payment.model.Bill;
import com.project.payment.repository.BillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BillValidator {

    private final BillRepository repository;

    public void checkBillAlreadyRegistered(Bill bill) {
        var isBillRegisterd = repository.findByDueDateAndAmountAndStatus(bill.getDueDate(), bill.getAmount(), bill.getStatus())
                .isPresent();

        if (isBillRegisterd) {
            throw new AlreadyRegisteredBillException("Conta com estas informações já foi cadastrada!");
        }
    }

    public Bill checkExistingBill(UUID billId) {
        return repository.findById(billId)
                .orElseThrow(() -> new BillNotFoundException("Conta não encontrada"));
    }

    public void validateSearchPeriodForSumOfBills(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new InvalidPeriodException("A data inicial deve ser menor do que a data final.");
        }
    }
}
