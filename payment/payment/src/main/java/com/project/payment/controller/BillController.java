package com.project.payment.controller;

import com.project.payment.controller.dto.ErrorResponseDTO;
import com.project.payment.controller.dto.SaveBillDTO;
import com.project.payment.controller.dto.UpdateBillDTO;
import com.project.payment.controller.mapper.BillMapper;
import com.project.payment.exceptions.AlreadyRegisteredBillException;
import com.project.payment.exceptions.BillNotFoundException;
import com.project.payment.service.BillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("payments")
@Tag(name = "bill")
@RequiredArgsConstructor
public class BillController {

    private final BillService service;
    private final BillMapper mapper;

    @PostMapping
    @Operation(summary = "Save", description = "Cadastrar nova conta.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Conta registrada com sucesso."),
            @ApiResponse(responseCode = "409", description = "Conta com estas informações já foi cadastrada!")
    })
    public ResponseEntity<Object> save(@RequestBody @Valid SaveBillDTO saveBillDTO) {
        try {
            var savedBill = service.save(saveBillDTO);
            var location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .buildAndExpand(savedBill.getId())
                    .toUri();

            return ResponseEntity.created(location).build();
        } catch (AlreadyRegisteredBillException exception) {
            var errorResponse = ErrorResponseDTO.conflit(exception.getMessage());
            return ResponseEntity.status(errorResponse.status()).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update", description = "Atualizar conta existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conta atualizada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada."),
            @ApiResponse(responseCode = "409", description = "Conta com estas informações já foi cadastrada!")
    })
    public ResponseEntity<Object> update(
            @PathVariable("id") UUID billId,
            @RequestBody @Valid UpdateBillDTO updateBillDTO
    ) {
        try {
            service.updateBillFields(billId, updateBillDTO);
            return ResponseEntity.ok().build();
        } catch (BillNotFoundException exception) {
            var errorResponse = ErrorResponseDTO.notFound("Conta não encontrada.");
            return ResponseEntity.status(errorResponse.status()).body(errorResponse);
        } catch (AlreadyRegisteredBillException exception) {
            var errorResponse = ErrorResponseDTO.conflit("Conta com estas informações já foi cadastrada!");
            return ResponseEntity.status(errorResponse.status()).body(errorResponse);
        }
    }
}