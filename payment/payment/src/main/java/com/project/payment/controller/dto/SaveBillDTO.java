package com.project.payment.controller.dto;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record SaveBillDTO(

        @NotNull(message = "dueDate é obrigatório")
        LocalDate dueDate,

        @NotNull(message = "paymentDate é obrigatório")
        LocalDate paymentDate,

        @NotNull(message = "amount é obrigatório")
        @DecimalMin(value = "0.01", message = "Amount deve ser maior que zero")
        BigDecimal amount,

        @NotBlank(message = "description é obrigatória")
        @Size(max = 255, message = "Description deve ter no máximo 255 caracteres")
        String description,

        @NotBlank(message = "status é obrigatório")
        @Size(max = 50, message = "Status deve ter no máximo 50 caracteres")
        String status
) {
}