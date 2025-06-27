package com.project.payment.controller;

import com.project.payment.controller.dto.ErrorResponseDTO;
import com.project.payment.controller.dto.SaveBillDTO;
import com.project.payment.controller.dto.UpdateBillDTO;
import com.project.payment.controller.dto.UpdateBillStatusDTO;
import com.project.payment.exceptions.AlreadyRegisteredBillException;
import com.project.payment.exceptions.BillNotFoundException;
import com.project.payment.model.Bill;
import com.project.payment.service.BillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("payments")
@Tag(name = "bill")
@RequiredArgsConstructor
public class BillController {

    private final BillService service;

    @PostMapping
    @Operation(summary = "Save Bill", description = "Cadastrar nova conta.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Conta registrada com sucesso."),
            @ApiResponse(responseCode = "409", description = "Conta com estas informações já foi cadastrada!")
    })
    public ResponseEntity<Object> saveBill(@RequestBody @Valid SaveBillDTO saveBillDTO) {
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
    @Operation(summary = "Update Bill", description = "Atualizar conta existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conta atualizada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada."),
            @ApiResponse(responseCode = "409", description = "Conta com estas informações já foi cadastrada!")
    })
    public ResponseEntity<Object> updateBill(
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

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update Bill Status", description = "Atualizar apenas o status da conta.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada.")
    })
    public ResponseEntity<Object> updateBillStatus(
            @PathVariable("id") UUID billId,
            @RequestBody @Valid UpdateBillStatusDTO updateBillStatusDTO
    ) {
        try {
            service.updateBillStatus(billId, updateBillStatusDTO.status());
            return ResponseEntity.ok().build();
        } catch (BillNotFoundException exception) {
            var errorResponse = ErrorResponseDTO.notFound("Conta não encontrada.");
            return ResponseEntity.status(errorResponse.status()).body(errorResponse);
        }
    }

    @GetMapping
    @Operation(summary = "List Bills", description = "Retorna uma lista de contas, com filtros opcionais por dueDate e description.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de contas retornada com sucesso.")
    })
    public ResponseEntity<List<Bill>> getBills(
            @Parameter(description = "Filtra por data de vencimento (YYYY-MM-DD)")
            @RequestParam(value = "dueDate", required = false)
            LocalDate dueDate,

            @Parameter(description = "Filtra por texto contido na descrição")
            @RequestParam(value = "description", required = false)
            String description
    ) {
        var bills = service.findBills(dueDate, description);
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Bill by ID", description = "Busca uma conta pelo ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conta encontrada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada.")
    })
    public ResponseEntity<Object> getBillById(@PathVariable UUID id) {
        try {
            var bill = service.findBillById(id);
            return ResponseEntity.ok(bill);
        } catch (BillNotFoundException exception) {
            var errorResponse = ErrorResponseDTO.notFound("Conta não encontrada.");
            return ResponseEntity.status(errorResponse.status()).body(errorResponse);
        }
    }
}