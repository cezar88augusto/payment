package com.project.payment.controller.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(name = "bill")
public record BillDTO(

        @NotNull(message = "dueDate é obrigatório")
        LocalDate dueDate,

        @NotNull(message = "paymentDate é obrigatório")
        LocalDate paymentDate,

        @NotNull(message = "amount é obrigatório")
        @DecimalMin(value = "0.01", inclusive = true, message = "amount deve ser maior que zero")
        BigDecimal amount,

        @NotBlank(message = "description é obrigatória")
        @Size(max = 255, message = "description deve ter no máximo 255 caracteres")
        String description,

        @NotBlank(message = "status é obrigatório")
        @Size(max = 50, message = "status deve ter no máximo 50 caracteres")
        String status
) {
}
