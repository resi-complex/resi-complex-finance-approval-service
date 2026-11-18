package com.resicomplex.financeapproval.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CastVoteRequest(
        @NotBlank @Size(max = 36) String boardMemberId,
        @NotBlank @Pattern(regexp = "APPROVED|REJECTED") String vote,
        @Size(max = 255) String comments) {
}
