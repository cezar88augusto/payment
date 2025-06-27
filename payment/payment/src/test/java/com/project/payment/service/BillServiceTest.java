package com.project.payment.service;

import com.project.payment.controller.dto.SaveBillDTO;
import com.project.payment.controller.dto.UpdateBillDTO;
import com.project.payment.controller.mapper.BillMapperImpl;
import com.project.payment.model.Bill;
import com.project.payment.repository.BillRepository;
import com.project.payment.validator.BillValidator;
import com.project.payment.validator.CsvFileValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static java.math.BigDecimal.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillServiceTest {

    private static final LocalDate DUE_DATE = LocalDate.parse("2025-07-01");
    private static final LocalDate PAYMENT_DATE = LocalDate.parse("2025-08-01");
    private static final String STATUS_PAID = "PAGO";
    private static final String STATUS_PENDING = "PEDING";
    private static final String DESCRIPTION = "Conta de Luz";
    private static final UUID BILL_ID = UUID.randomUUID();
    private static final String CSV_BASE64 = "base64-mock";
    private static final LocalDate START_DATE = LocalDate.parse("2025-01-01");
    private static final LocalDate END_DATE = LocalDate.parse("2025-12-31");
    private static final BigDecimal AMOUNT = TEN;

    @Mock
    private BillRepository repository;

    @Mock
    private BillValidator billValidator;

    @Mock
    private CsvFileValidator csvFileValidator;

    @InjectMocks
    private BillService service;

    @BeforeEach
    void setUp() {
        var mapper = new BillMapperImpl();
        service = new BillService(repository, billValidator, csvFileValidator, mapper);
    }

    @Test
    void saveBill_successWhenSavingBill_returnsBill() {
        var saveBillDTO = mockSaveBillDTO();
        var billEntity = mockBillEntity();

        when(repository.save(billEntity)).thenReturn(billEntity);

        var result = service.saveBill(saveBillDTO);
        assertAll(
                () -> assertEquals(billEntity, result),
                () -> verify(billValidator, times(1)).checkBillAlreadyRegistered(billEntity),
                () -> verify(repository, times(1)).save(billEntity)
        );
    }

    @Test
    void updateBill_successWhenUpdatingBill_returnsVoid() {
        var updateBillDTO = mockUpdateBillDTO();
        var billEntity = mockBillEntity();

        when(billValidator.checkExistingBill(BILL_ID)).thenReturn(billEntity);

        service.updateBill(BILL_ID, updateBillDTO);
        assertAll(
                () -> verify(billValidator, times(1)).checkExistingBill(BILL_ID),
                () -> verify(billValidator, times(1)).checkBillAlreadyRegistered(billEntity),
                () -> verify(repository, times(1)).save(billEntity)
        );
    }

    @Test
    void updateBillStatus_successWhenUpdatingStatus_returnsVoid() {
        var bill = mockBillEntity();

        when(billValidator.checkExistingBill(BILL_ID)).thenReturn(bill);

        service.updateBillStatus(BILL_ID, STATUS_PENDING);
        assertAll(
                () -> assertEquals(STATUS_PENDING, bill.getStatus()),
                () -> verify(repository, times(1)).save(bill)
        );
    }

    @Test
    void findBills() {
    }

    @Test
    void findBillById() {
    }

    @Test
    void sumBillAmountByPaymentDateBetween() {
    }

    @Test
    void saveCsvBills() {
    }

    private Bill mockBillEntity() {
        return Bill.builder()
                .dueDate(DUE_DATE)
                .paymentDate(PAYMENT_DATE)
                .amount(TEN)
                .status(STATUS_PAID)
                .description(DESCRIPTION)
                .build();
    }

    private SaveBillDTO mockSaveBillDTO() {
        return SaveBillDTO.builder()
                .dueDate(DUE_DATE)
                .paymentDate(PAYMENT_DATE)
                .amount(TEN)
                .status(STATUS_PAID)
                .description(DESCRIPTION)
                .build();
    }

    private UpdateBillDTO mockUpdateBillDTO() {
        return UpdateBillDTO.builder()
                .dueDate(DUE_DATE)
                .amount(TEN)
                .description(DESCRIPTION)
                .build();
    }
}