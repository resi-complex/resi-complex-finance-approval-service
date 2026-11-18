package com.resicomplex.financeapproval.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ExpenseProposalResponse(
        String id,
        String title,
        String description,
        BigDecimal estimatedAmount,
        String proposedBy,
        String status,
        LocalDateTime approvalDate,
        LocalDateTime createdAt,
        long approvedVotes,
        long rejectedVotes,
        List<ExpenseVoteResponse> votes) {
}
