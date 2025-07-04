package com.project.payment.controller;

import com.project.payment.controller.dto.*;
import com.project.payment.exception.AlreadyRegisteredBillException;
import com.project.payment.exception.BillNotFoundException;
import com.project.payment.exception.InvalidPeriodException;
import com.project.payment.model.Bill;
import com.project.payment.service.BillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("payments")
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
            var savedBill = service.saveBill(saveBillDTO);
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
            @ApiResponse(responseCode = "204", description = "Conta atualizada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada."),
            @ApiResponse(responseCode = "409", description = "Conta com estas informações já foi cadastrada!")
    })
    public ResponseEntity<Object> updateBill(
            @PathVariable("id") UUID billId,
            @RequestBody @Valid UpdateBillDTO updateBillDTO
    ) {
        try {
            service.updateBill(billId, updateBillDTO);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (BillNotFoundException exception) {
            var errorResponse = ErrorResponseDTO.notFound(exception.getMessage());
            return ResponseEntity.status(errorResponse.status()).body(errorResponse);
        } catch (AlreadyRegisteredBillException exception) {
            var errorResponse = ErrorResponseDTO.conflit(exception.getMessage());
            return ResponseEntity.status(errorResponse.status()).body(errorResponse);
        }
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update Bill Status", description = "Atualizar apenas o status da conta.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Status atualizado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada.")
    })
    public ResponseEntity<Object> updateBillStatus(
            @PathVariable("id") UUID billId,
            @RequestBody @Valid UpdateBillStatusDTO updateBillStatusDTO
    ) {
        try {
            service.updateBillStatus(billId, updateBillStatusDTO.status());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (BillNotFoundException exception) {
            var errorResponse = ErrorResponseDTO.notFound(exception.getMessage());
            return ResponseEntity.status(errorResponse.status()).body(errorResponse);
        }
    }

    @GetMapping
    @Operation(summary = "List Bills", description = "Retorna uma lista de contas, com filtros opcionais por dueDate e description.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de contas retornada com sucesso.")
    })
    public ResponseEntity<Page<Bill>> getBills(
            @Parameter(description = "Filtra por data de vencimento (YYYY-MM-DD)")
            @RequestParam(value = "dueDate", required = false)
            LocalDate dueDate,

            @Parameter(description = "Filtra por texto contido na descrição")
            @RequestParam(value = "description", required = false)
            String description,

            @Parameter(description = "Número da página")
            @RequestParam(value = "pageNumber", defaultValue = "0")
            int pageNumber,

            @Parameter(description = "Tamanho da página")
            @RequestParam(value = "pageSize", defaultValue = "10")
            int pageSize
    ) {
        var bills = service.findBills(dueDate, description, pageNumber, pageSize);
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
            var errorResponse = ErrorResponseDTO.notFound(exception.getMessage());
            return ResponseEntity.status(errorResponse.status()).body(errorResponse);
        }
    }

    @GetMapping("/total")
    @Operation(
            summary = "Total amount of bills by period",
            description = "Retorna a soma dos valores dentro de um intervalo de datas de pagamento."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Soma do período calculada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Parâmetros obrigatórios não informados.")
    })
    public ResponseEntity<Object> getTotalByPaymentPeriod(
            @RequestParam(value = "startDate") @Parameter(description = "Data inicial do pagamento (YYYY-MM-DD)", required = true) LocalDate startDate,
            @RequestParam(value = "endDate") @Parameter(description = "Data final do pagamento (YYYY-MM-DD)", required = true) LocalDate endDate
    ) {
        try {
            var total = service.sumBillAmountByPaymentDateBetween(startDate, endDate);
            return ResponseEntity.ok(total);
        } catch (InvalidPeriodException exception) {
            var errorResponse = ErrorResponseDTO.invalidPeriod(exception.getMessage());
            return ResponseEntity.status(errorResponse.status()).body(errorResponse);
        }
    }

    @PostMapping("/uploads")
    @Operation(summary = "Upload CSV file", description = "Recebe um arquivo CSV codificado em base64 e salva cada linha no banco de dados.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Dados salvos com sucesso."),
            @ApiResponse(responseCode = "409", description = "Arquivo com conta(s) já cadastrada(s)"),
            @ApiResponse(responseCode = "422", description = "Arquivo com dado(s) inválido(s)")
    })
    public ResponseEntity<Object> uploadCsvBase64(@RequestBody @Valid UploadCsvDTO uploadCsvDTO) {
        try {
            service.saveCsvBills(uploadCsvDTO.fileBase64());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (AlreadyRegisteredBillException exception) {
            var errorResponse = ErrorResponseDTO.conflit(exception.getMessage());
            return ResponseEntity.status(errorResponse.status()).body(errorResponse);
        } catch (IllegalArgumentException exception) {
            var errorResponse = ErrorResponseDTO.invalidCsvFile(exception.getMessage());
            return ResponseEntity.status(errorResponse.status()).body(errorResponse);
        }
    }
}