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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.math.BigDecimal.TEN;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillServiceTest {

    private static final UUID BILL_ID = UUID.randomUUID();

    private static final String STATUS_PAID = "PAGO";
    private static final String STATUS_PENDING = "PEDING";
    private static final String DESCRIPTION = "Conta de Luz";
    private static final String CSV_BASE64 = "MjAyNS0wNy0wMSwyMDI1LTA3LTAyLDE1MC4wMCxDb250YSBkZSBlbmVyZ2lhLFBFTkRFTlRFk";
    private static final LocalDate START_DATE = LocalDate.parse("2025-01-01");
    private static final LocalDate END_DATE = LocalDate.parse("2025-12-31");
    private static final LocalDate DUE_DATE = LocalDate.parse("2025-07-01");
    private static final LocalDate PAYMENT_DATE = LocalDate.parse("2025-08-01");
    private static final Integer PAGE_NUMBER = 0;
    private static final Integer PAGE_SIZE = 10;

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
    void findBills_successWhenCalled_returnsPagedResult() {
        var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        var matcher = mockMatcher();
        var bill = mockBill();
        var bills = mockBills(bill);
        var example = Example.of(bill, matcher);
        var pageResult = new PageImpl<>(bills, pageable, bills.size());

        when(repository.findAll(example, pageable)).thenReturn(pageResult);
        
        var result = service.findBills(DUE_DATE, DESCRIPTION, PAGE_NUMBER, PAGE_SIZE);
        assertAll(
                () -> assertEquals(pageResult, result),
                () -> verify(repository, times(1)).findAll(example, pageable)
        );
    }

    @Test
    void findBillById_successWhenBillExists_returnsBill() {
        var bill = mockBillEntity();

        when(billValidator.checkExistingBill(BILL_ID)).thenReturn(bill);

        var result = service.findBillById(BILL_ID);
        assertAll(
                () -> assertEquals(bill, result),
                () -> verify(billValidator, times(1)).checkExistingBill(BILL_ID)
        );
    }

    @Test
    void sumBillAmountByPaymentDateBetween_successWhenValidDates_returnsSum() {
        var expected = TEN;

        when(repository.sumBillAmountByPaymentDateBetween(START_DATE, END_DATE)).thenReturn(Optional.of(expected));

        var result = service.sumBillAmountByPaymentDateBetween(START_DATE, END_DATE);
        assertAll(
                () -> assertEquals(expected, result),
                () -> verify(repository, times(1)).sumBillAmountByPaymentDateBetween(START_DATE, END_DATE),
                () -> verify(billValidator, times(1)).validateSearchPeriodForSumOfBills(START_DATE, END_DATE)
        );
    }

    @Test
    void saveCsvBills_successWhenSavingCsvFile_returnsVoid() {
        var bills = mockBills(mockBillEntity(), mockBillEntity());

        when(csvFileValidator.processCsvBase64(CSV_BASE64)).thenReturn(bills);

        service.saveCsvBills(CSV_BASE64);
        assertAll(
                () -> verify(csvFileValidator).processCsvBase64(CSV_BASE64),
                () -> verify(repository, times(1)).saveAll(bills)
        );
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

    private ExampleMatcher mockMatcher() {
        return ExampleMatcher
                .matching()
                .withIgnoreNullValues()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
    }

    private List<Bill> mockBills(Bill... bill) {
        return List.of(bill);
    }

    private Bill mockBill() {
        return Bill.builder()
                .dueDate(DUE_DATE)
                .description(DESCRIPTION)
                .build();
    }
}