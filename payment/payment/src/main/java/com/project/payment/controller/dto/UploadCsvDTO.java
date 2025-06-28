package com.project.payment.controller.dto;

import jakarta.validation.constraints.NotNull;

public record UploadCsvDTO(

        @NotNull(message = "arquivo CSV em formato base64 é obrigatório")
        String fileBase64
) {
}