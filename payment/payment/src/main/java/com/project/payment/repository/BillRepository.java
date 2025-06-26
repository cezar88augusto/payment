package com.project.payment.repository;

import com.project.payment.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface BillRepository extends JpaRepository<Bill, UUID>, JpaSpecificationExecutor<Bill> {

    Optional<Bill> findByDueDateAndAmountAndStatus(LocalDate dueDate, BigDecimal amount, String status);
}