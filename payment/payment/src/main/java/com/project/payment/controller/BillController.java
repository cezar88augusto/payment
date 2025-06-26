package com.project.payment.controller;

import com.project.payment.controller.dto.BillDTO;
import com.project.payment.controller.dto.ErrorResponseDTO;
import com.project.payment.controller.mapper.BillMapper;
import com.project.payment.exceptions.AlreadyRegisteredBillException;
import com.project.payment.service.BillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

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
            @ApiResponse(responseCode = "409", description = "Conta já cadastrada!")
    })
    public ResponseEntity<Object> save(@RequestBody @Valid BillDTO billDTO) {
        try {
            var bill = mapper.toBillEntity(billDTO);
            var savedBill = service.save(bill);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .buildAndExpand(savedBill.getId())
                    .toUri();

            return ResponseEntity.created(location).build();
        } catch (AlreadyRegisteredBillException exception) {
            var errorResponse = ErrorResponseDTO.conflit(exception.getMessage());
            return ResponseEntity.status(errorResponse.status()).body(errorResponse);
        }
    }
}