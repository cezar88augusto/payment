package com.project.payment.controller;

import com.project.payment.controller.dto.BillDTO;
import com.project.payment.controller.mapper.BillMapper;
import com.project.payment.service.BillService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("payments")
@Tag(name = "bill")
@RequiredArgsConstructor
public class BillController {

    private final BillService service;
    private final BillMapper mapper;

    @PostMapping
    public ResponseEntity<Object> salvar(@RequestBody @Valid BillDTO billDTO) {
        var bill = mapper.toBillEntity(billDTO);
        service.save(bill);

        return null;
    }
}