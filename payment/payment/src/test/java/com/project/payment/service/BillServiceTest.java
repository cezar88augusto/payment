package com.project.payment.service;

import com.project.payment.controller.dto.SaveBillDTO;
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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillServiceTest {

    private static final LocalDate DUE_DATE = LocalDate.parse("2025-07-01");
    private static final LocalDate PAYMENT_DATE = LocalDate.parse("2025-08-01");
    private static final String STATUS = "PAGO";
    private static final String DESCRIPTION = "Conta de Luz";

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
                () -> verify(billValidator, times(1)).checkBillAlreadyRegistered(billEntity),
                () -> verify(repository, times(1)).save(billEntity),
                () -> assertEquals(billEntity, result)
        );
    }

    @Test
    void updateBill() {
    }

    @Test
    void updateBillStatus() {
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
                .amount(BigDecimal.TEN)
                .status(STATUS)
                .description(DESCRIPTION)
                .build();
    }

    private SaveBillDTO mockSaveBillDTO() {
        return SaveBillDTO.builder()
                .dueDate(DUE_DATE)
                .paymentDate(PAYMENT_DATE)
                .amount(BigDecimal.TEN)
                .status(STATUS)
                .description(DESCRIPTION)
                .build();
    }
}