package com.project.payment.controller.mapper;

import com.project.payment.controller.dto.BillDTO;
import com.project.payment.model.Bill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BillMapper {

    @Mapping(source = "dueDate", target = "dueDate")
    @Mapping(source = "paymentDate", target = "paymentDate")
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "status", target = "status")
    Bill toBillEntity(BillDTO billDTO);
}