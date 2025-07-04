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

    private final BillValidator billValidator;

    public List<Bill> processCsvBase64(String base64) {
        var decodedFile = Base64.getDecoder().decode(base64);
        var decodedCsv = new String(decodedFile);
        var lines = splitCsvFileInLines(decodedCsv);
        var bills = new ArrayList<Bill>();

        generateBills(lines, bills);

        return bills;
    }

    private void generateBills(String[] lines, ArrayList<Bill> bills) {
        for (int i = 0; i < lines.length; i++) {
            var line = lines[i];
            var billLine = line.split(",");
            if (billLine.length < COLUMNS_NUMBER) continue;

            var bill = createBillEntity(billLine, i + 1);
            billValidator.checkBillAlreadyRegistered(bill);
            bills.add(bill);
        }
    }

    private Bill createBillEntity(String[] column, int lineNumber) {
        return Bill.builder()
                .dueDate(validateDate(column[DUE_DATE_COLUMN], DUE_DATE, lineNumber))
                .paymentDate(validateDate(column[PAYMENT_DATE_COLUMN], PAYMENT, lineNumber))
                .amount(validateAmount(column[AMOUNT_COLUMN], lineNumber))
                .description(column[DESCRIPTION_COLUMN].trim())
                .status(validateStatus(column[STATUS_COLUMN], lineNumber))
                .build();
    }

    private LocalDate validateDate(String value, String columnName, int lineNumber) {
        try {
            return LocalDate.parse(value.trim());
        } catch (Exception exception) {
            throw new IllegalArgumentException("A linha " + lineNumber + " coluna " + columnName + " possui formato inválido.");
        }
    }

    private BigDecimal validateAmount(String value, int lineNumber) {
        try {
            return new BigDecimal(value.trim());
        } catch (Exception exception) {
            throw new IllegalArgumentException("A linha " + lineNumber + " coluna " + AMOUNT + " possui formato inválido.");
        }
    }

    private String validateStatus(String value, int lineNumber) {
        var status = value.trim().toUpperCase();
        var allowedStatus = List.of("PAGO", "PENDENTE", "ATRASADO");

        if (!allowedStatus.contains(status)) {
            throw new IllegalArgumentException("A linha " + lineNumber + " coluna " + STATUS + " possui valor inválido: " + value);
        }

        return status;
    }

    private String[] splitCsvFileInLines(String decodedCsv) {
        return decodedCsv.split("\\r?\\n");
    }
}