package com.project.payment.controller.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record UpdateBillDTO(

        LocalDate dueDate,

        @DecimalMin(value = "0.01", message = "Amount deve ser maior que zero")
        BigDecimal amount,

        @Size(max = 255, message = "Description deve ter no máximo 255 caracteres")
        String description
) {
}