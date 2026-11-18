package com.resicomplex.financeapproval.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CreateExpenseProposalRequest(
        @NotBlank @Size(max = 150) String title,
        @NotBlank String description,
        @NotNull @DecimalMin(value = "0.01") BigDecimal estimatedAmount,
        @NotBlank @Size(max = 36) String proposedBy) {
}
