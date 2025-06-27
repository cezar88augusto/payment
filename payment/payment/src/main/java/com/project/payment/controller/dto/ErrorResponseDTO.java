package com.project.payment.controller.dto;

import org.springframework.http.HttpStatus;

public record ErrorResponseDTO(int status, String mensagem) {

    public static ErrorResponseDTO conflit(String messageError) {
        return new ErrorResponseDTO(HttpStatus.CONFLICT.value(), messageError);
    }

    public static ErrorResponseDTO notFound(String messageError) {
        return new ErrorResponseDTO(HttpStatus.NOT_FOUND.value(), messageError);
    }

    public static ErrorResponseDTO invalidPeriod(String messageError) {
        return new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), messageError);
    }
}