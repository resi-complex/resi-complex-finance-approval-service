package com.resicomplex.financeapproval.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "expense_proposals")
@Getter
@Setter
@NoArgsConstructor
public class ExpenseProposal {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "estimated_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal estimatedAmount;

    @Column(name = "proposed_by", nullable = false, length = 36)
    private String proposedBy;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
