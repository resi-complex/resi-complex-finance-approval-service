package com.resicomplex.financeapproval.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.resicomplex.financeapproval.dto.CastVoteRequest;
import com.resicomplex.financeapproval.dto.CreateExpenseProposalRequest;
import com.resicomplex.financeapproval.dto.ExpenseProposalResponse;
import com.resicomplex.financeapproval.model.ExpenseProposal;
import com.resicomplex.financeapproval.model.ExpenseVote;
import com.resicomplex.financeapproval.repository.ExpenseProposalRepository;
import com.resicomplex.financeapproval.repository.ExpenseVoteRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class FinanceApprovalServiceTest {

    @Mock
    private ExpenseProposalRepository proposalRepository;

    @Mock
    private ExpenseVoteRepository voteRepository;

    private FinanceApprovalService service;

    @BeforeEach
    void setUp() {
        service = new FinanceApprovalService(proposalRepository, voteRepository);
    }

    @Test
    void createProposalInitializesInPendingReviewStatus() {
        when(proposalRepository.save(any(ExpenseProposal.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateExpenseProposalRequest req = new CreateExpenseProposalRequest(
                "Mantenimiento preventivo de ascensores",
                "Cambio de cables de tracción y revisión de frenos",
                new BigDecimal("4800000.00"),
                "admin-01");

        ExpenseProposalResponse res = service.createProposal(req);

        assertNotNull(res.id());
        assertEquals("PENDING_REVIEW", res.status());
        assertEquals(new BigDecimal("4800000.00"), res.estimatedAmount());
    }

    @Test
    void castVoteApprovesProposalWhenQuorumIsReachedAndPreventsDuplicateVote() {
        ExpenseProposal proposal = new ExpenseProposal();
        proposal.setId("prop-1");
        proposal.setTitle("Impermeabilización de techos");
        proposal.setDescription("Impermeabilización torre A y B");
        proposal.setEstimatedAmount(new BigDecimal("3500000.00"));
        proposal.setProposedBy("admin-01");
        proposal.setStatus("PENDING_REVIEW");
        proposal.setCreatedAt(LocalDateTime.now());

        ExpenseVote v1 = new ExpenseVote();
        v1.setId("v1");
        v1.setProposalId("prop-1");
        v1.setBoardMemberId("member-1");
        v1.setVote("APPROVED");
        v1.setVotedAt(LocalDateTime.now());

        ExpenseVote v2 = new ExpenseVote();
        v2.setId("v2");
        v2.setProposalId("prop-1");
        v2.setBoardMemberId("member-2");
        v2.setVote("APPROVED");
        v2.setVotedAt(LocalDateTime.now());

        when(proposalRepository.findById("prop-1")).thenReturn(Optional.of(proposal));
        when(voteRepository.existsByProposalIdAndBoardMemberId("prop-1", "member-2")).thenReturn(false);
        when(voteRepository.save(any(ExpenseVote.class))).thenAnswer(inv -> inv.getArgument(0));
        when(voteRepository.findByProposalId("prop-1")).thenReturn(List.of(v1, v2));
        when(proposalRepository.save(any(ExpenseProposal.class))).thenAnswer(inv -> inv.getArgument(0));

        ExpenseProposalResponse res = service.castVote("prop-1",
                new CastVoteRequest("member-2", "APPROVED", "De acuerdo con la cotización"));

        assertEquals("APPROVED", res.status());
        assertEquals(2L, res.approvedVotes());
        assertNotNull(res.approvalDate());

        ResponseStatusException closedEx = assertThrows(ResponseStatusException.class,
                () -> service.castVote("prop-1", new CastVoteRequest("member-3", "REJECTED", "Tarde")));
        assertEquals(HttpStatus.CONFLICT, closedEx.getStatusCode());
    }
}
