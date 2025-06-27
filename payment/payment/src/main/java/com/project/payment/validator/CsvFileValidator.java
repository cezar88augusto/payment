package com.project.payment.validator;

import com.project.payment.model.Bill;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static com.project.payment.constants.AppConstants.CsvFileConstants.*;

@Component
@RequiredArgsConstructor
public class CsvFileValidator {

    private  final BillValidator billValidator;

    public List<Bill> processCsvBase64(String base64) {
        var decodedFile = Base64.getDecoder().decode(base64);
        var decodedCsv = new String(decodedFile);
        var lines = splitCsvFileInLines(decodedCsv);
        var bills = new ArrayList<Bill>();

        generateBills(lines, bills);

        return bills;
    }

    private void generateBills(String[] lines, ArrayList<Bill> bills) {
        for (String line : lines) {
            var billLine = line.split(",");
            if (billLine.length < COLUMNS_NUMBER) continue;

            var bill = createBillEntity(billLine);
            billValidator.checkBillAlreadyRegistered(bill);

            bills.add(bill);
        }
    }

    private Bill createBillEntity(String[] column) {
        return Bill.builder()
                .dueDate(LocalDate.parse(column[DUE_DATE_COLUMN].trim()))
                .paymentDate(LocalDate.parse(column[PAYMENT_DATE_COLUMN].trim()))
                .amount(new BigDecimal(column[AMOUNT_COLUMN].trim()))
                .description(column[DESCRIPTION_COLUMN].trim())
                .status(column[STATUS_COLUMN].trim())
                .build();
    }

    private String[] splitCsvFileInLines(String decodedCsv) {
        return decodedCsv.split("\\r?\\n");
    }
}