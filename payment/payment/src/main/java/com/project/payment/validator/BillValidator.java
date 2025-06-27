package com.project.payment.validator;

import com.project.payment.exception.AlreadyRegisteredBillException;
import com.project.payment.exception.BillNotFoundException;
import com.project.payment.exception.InvalidPeriodException;
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
        var isBillRegistered = repository.findByDueDateAndAmountAndStatus(
                bill.getDueDate(), bill.getAmount(), bill.getStatus()
        ).isPresent();

        if (isBillRegistered) {
            var messageError = String.format("A conta com a data de vencimento %s, valor %.2f e status '%s' já foi cadastrada!", bill.getDueDate(), bill.getAmount(), bill.getStatus());
            throw new AlreadyRegisteredBillException(messageError);
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
