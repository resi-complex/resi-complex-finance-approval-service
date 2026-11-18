package com.resicomplex.financeapproval.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "expense_votes")
@Getter
@Setter
@NoArgsConstructor
public class ExpenseVote {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "proposal_id", nullable = false, length = 36)
    private String proposalId;

    @Column(name = "board_member_id", nullable = false, length = 36)
    private String boardMemberId;

    @Column(name = "vote", nullable = false, length = 20)
    private String vote;

    @Column(name = "comments", length = 255)
    private String comments;

    @Column(name = "voted_at", nullable = false)
    private LocalDateTime votedAt;
}
