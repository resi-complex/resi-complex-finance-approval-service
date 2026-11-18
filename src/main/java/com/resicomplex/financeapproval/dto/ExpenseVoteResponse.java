package com.resicomplex.financeapproval.dto;

import java.time.LocalDateTime;

public record ExpenseVoteResponse(
        String id,
        String proposalId,
        String boardMemberId,
        String vote,
        String comments,
        LocalDateTime votedAt) {
}
