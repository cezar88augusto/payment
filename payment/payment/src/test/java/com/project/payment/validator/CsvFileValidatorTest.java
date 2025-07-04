package com.project.payment.validator;

import com.project.payment.exception.AlreadyRegisteredBillException;
import com.project.payment.model.Bill;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CsvFileValidatorTest {

    private static final LocalDate DUE_DATE_1 = LocalDate.of(2025, 7, 1);
    private static final LocalDate PAYMENT_DATE_1 = LocalDate.of(2025, 7, 2);
    private static final BigDecimal AMOUNT_1 = new BigDecimal("150.00");
    private static final String DESCRIPTION_1 = "Conta de energia";
    private static final String STATUS_1 = "PENDENTE";
    private static final LocalDate DUE_DATE_2 = LocalDate.of(2025, 7, 5);
    private static final LocalDate PAYMENT_DATE_2 = LocalDate.of(2025, 7, 6);
    private static final BigDecimal AMOUNT_2 = new BigDecimal("220.75");
    private static final String DESCRIPTION_2 = "Conta de água";
    private static final String STATUS_2 = "PAGO";
    private static final String MESSAGE_ERROR = "A conta com a data de vencimento %s, valor %.2f e status '%s' já foi cadastrada!";

    private static final String CSV_VALID_FILE_CONTENT = """
            2025-07-01,2025-07-02,150.00,Conta de energia,PENDENTE
            2025-07-05,2025-07-06,220.75,Conta de água,PAGO
            """;

    private static final String CSV_INVALID_FILE_CONTENT = """
            2025-07-01,2025-07-02,150.00,Conta de energia,PENDENTE
            INVALID,LINE,ONLY,FOUR
            2025-07-05,2025-07-06,220.75,Conta de água,PAGO
            """;

    private static final String CSV_INVALID_DUE_DATE = """
            INVALID,2025-07-02,150.00,Conta de energia,PENDENTE
            """;

    private static final String CSV_INVALID_PAYMENT_DATE = """
            2025-07-01,INVALID,150.00,Conta de energia,PENDENTE
            """;

    private static final String CSV_INVALID_AMOUNT = """
            2025-07-01,2025-07-02,INVALID,Conta de energia,PENDENTE
            """;

    private static final String CSV_INVALID_STATUS = """
            2025-07-01,2025-07-02,150.00,Conta de energia,INVALID
            """;

    @Mock
    private BillValidator billValidator;

    @InjectMocks
    private CsvFileValidator validator;

    @Test
    void processCsvBase64_shouldReturnValidBillList_whenValidBase64IsPassed() {
        var base64CsvFile = convertBase64(CSV_VALID_FILE_CONTENT);
        var result = validator.processCsvBase64(base64CsvFile);
        var expected = expectedBillsFromCsv();

        assertAll(
                () -> assertEquals(expected.size(), result.size()),
                () -> assertEquals(expected, result),
                () -> verify(billValidator, times(2)).checkBillAlreadyRegistered(any(Bill.class))
        );
    }

    @Test
    void processCsvBase64_shouldSkipInvalidLines_whenLineHasLessColumns() {
        var base64CsvFile = convertBase64(CSV_INVALID_FILE_CONTENT);

        var result = validator.processCsvBase64(base64CsvFile);
        assertAll(
                () -> assertEquals(2, result.size()),
                () -> verify(billValidator, times(2)).checkBillAlreadyRegistered(any(Bill.class))
        );
    }

    @Test
    void processCsvBase64_shouldThrowExceptionWhenLineAlreadyExists_throwsAlreadyRegisteredBillException() {
        var base64CsvFile = convertBase64(CSV_VALID_FILE_CONTENT);

        doThrow(new AlreadyRegisteredBillException(MESSAGE_ERROR))
                .when(billValidator).checkBillAlreadyRegistered(any(Bill.class));

        assertThrows(AlreadyRegisteredBillException.class, () -> validator.processCsvBase64(base64CsvFile));
    }

    @Test
    void processCsvBase64_shouldThrowException_whenDueDateIsInvalid() {
        var base64 = convertBase64(CSV_INVALID_DUE_DATE);

        var exception = assertThrows(IllegalArgumentException.class, () -> validator.processCsvBase64(base64));

        assertEquals("A linha 1 coluna Data Vencimento possui formato inválido.", exception.getMessage());
    }

    @Test
    void processCsvBase64_shouldThrowException_whenPaymentDateIsInvalid() {
        var base64 = convertBase64(CSV_INVALID_PAYMENT_DATE);

        var exception = assertThrows(IllegalArgumentException.class, () -> validator.processCsvBase64(base64));

        assertEquals("A linha 1 coluna Data Pagamento possui formato inválido.", exception.getMessage());
    }

    @Test
    void processCsvBase64_shouldThrowException_whenAmountIsInvalid() {
        var base64 = convertBase64(CSV_INVALID_AMOUNT);

        var exception = assertThrows(IllegalArgumentException.class, () -> validator.processCsvBase64(base64));

        assertEquals("A linha 1 coluna Total possui formato inválido.", exception.getMessage());
    }

    @Test
    void processCsvBase64_shouldThrowException_whenStatusIsInvalid() {
        var base64 = convertBase64(CSV_INVALID_STATUS);

        var exception = assertThrows(IllegalArgumentException.class, () -> validator.processCsvBase64(base64));

        assertEquals("A linha 1 coluna Status possui valor inválido: INVALID", exception.getMessage());
    }

    private String convertBase64(String file) {
        return Base64.getEncoder().encodeToString(file.getBytes());
    }

    private List<Bill> expectedBillsFromCsv() {
        return List.of(
                Bill.builder()
                        .dueDate(DUE_DATE_1)
                        .paymentDate(PAYMENT_DATE_1)
                        .amount(AMOUNT_1)
                        .description(DESCRIPTION_1)
                        .status(STATUS_1)
                        .build(),
                Bill.builder()
                        .dueDate(DUE_DATE_2)
                        .paymentDate(PAYMENT_DATE_2)
                        .amount(AMOUNT_2)
                        .description(DESCRIPTION_2)
                        .status(STATUS_2)
                        .build()
        );
    }
}