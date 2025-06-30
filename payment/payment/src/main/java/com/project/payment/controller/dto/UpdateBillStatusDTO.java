package com.project.payment.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateBillStatusDTO(

        @NotBlank(message = "status é obrigatório")
        @Size(max = 50, message = "Status deve ter no máximo 50 caracteres")
        String status
) {
}