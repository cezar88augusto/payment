package com.project.payment.validator;

import com.project.payment.exception.AlreadyRegisteredBillException;
import com.project.payment.exception.BillNotFoundException;
import com.project.payment.exception.InvalidPeriodException;
import com.project.payment.model.Bill;
import com.project.payment.repository.BillRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillValidatorTest {

    private static final LocalDate DUE_DATE = LocalDate.of(2025, 7, 1);
    private static final BigDecimal AMOUNT = new BigDecimal("100.00");
    private static final String STATUS_PAID = "PAGO";
    private static final UUID BILL_ID = UUID.randomUUID();

    @Mock
    private BillRepository repository;

    @InjectMocks
    private BillValidator validator;

    @Test
    void checkBillAlreadyRegistered_shouldThrowExceptionWhenBillExists_throwsAlreadyRegisteredBillException() {
        var bill = mockBill();

        when(repository.findByDueDateAndAmountAndStatus(DUE_DATE, AMOUNT, STATUS_PAID))
                .thenReturn(Optional.of(bill));

        var exception = assertThrows(AlreadyRegisteredBillException.class, () -> validator.checkBillAlreadyRegistered(bill));
        assertEquals(
                "A conta com a data de vencimento 2025-07-01, valor 100.00 e status 'PAGO' já foi cadastrada!",
                exception.getMessage()
        );
    }

    @Test
    void checkBillAlreadyRegistered_shouldPassWhenBillDoesNotExist_returnsVoid() {
        var bill = mockBill();

        when(repository.findByDueDateAndAmountAndStatus(DUE_DATE, AMOUNT, STATUS_PAID))
                .thenReturn(Optional.empty());

        assertDoesNotThrow(() -> validator.checkBillAlreadyRegistered(bill));
    }

    @Test
    void checkExistingBill_shouldReturnBillWhenExists() {
        var bill = mockBill();

        when(repository.findById(BILL_ID)).thenReturn(Optional.of(bill));

        var result = validator.checkExistingBill(BILL_ID);
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(bill, result)
        );
    }

    @Test
    void checkExistingBill_shouldThrowExceptionWhenBillNotFound_throwsBillNotFoundException() {
        when(repository.findById(BILL_ID)).thenReturn(Optional.empty());

        var exception = assertThrows(BillNotFoundException.class, () -> validator.checkExistingBill(BILL_ID));
        assertEquals("Conta não encontrada.", exception.getMessage());
    }

    @Test
    void validateSearchPeriodForSumOfBills_shouldThrowExceptionWhenStartDateAfterEndDate_throwsInvalidPeriodException() {
        var startDate = LocalDate.of(2025, 7, 10);
        var endDate = LocalDate.of(2025, 7, 1);

        var exception = assertThrows(InvalidPeriodException.class, () -> validator.validateSearchPeriodForSumOfBills(startDate, endDate));
        assertEquals("A data inicial deve ser menor do que a data final.", exception.getMessage());
    }

    @Test
    void validateSearchPeriodForSumOfBills_shouldPassWhenPeriodIsValid_returnsVoid() {
        var startDate = LocalDate.of(2025, 7, 1);
        var endDate = LocalDate.of(2025, 7, 10);

        assertDoesNotThrow(() -> validator.validateSearchPeriodForSumOfBills(startDate, endDate));
    }

    private Bill mockBill() {
        return Bill.builder()
                .dueDate(DUE_DATE)
                .amount(AMOUNT)
                .status(STATUS_PAID)
                .build();
    }
}