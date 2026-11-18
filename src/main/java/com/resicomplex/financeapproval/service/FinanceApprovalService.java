package com.resicomplex.financeapproval.service;

import com.resicomplex.financeapproval.dto.CastVoteRequest;
import com.resicomplex.financeapproval.dto.CreateExpenseProposalRequest;
import com.resicomplex.financeapproval.dto.ExpenseProposalResponse;
import com.resicomplex.financeapproval.dto.ExpenseVoteResponse;
import com.resicomplex.financeapproval.model.ExpenseProposal;
import com.resicomplex.financeapproval.model.ExpenseVote;
import com.resicomplex.financeapproval.repository.ExpenseProposalRepository;
import com.resicomplex.financeapproval.repository.ExpenseVoteRepository;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FinanceApprovalService implements IFinanceApprovalService {

    private static final String STATUS_PENDING_REVIEW = "PENDING_REVIEW";
    private static final String STATUS_APPROVED = "APPROVED";
    private static final String STATUS_REJECTED = "REJECTED";
    private static final int QUORUM_MAJORITY_VOTES = 2;

    private final ExpenseProposalRepository proposalRepository;
    private final ExpenseVoteRepository voteRepository;

    public FinanceApprovalService(
            ExpenseProposalRepository proposalRepository,
            ExpenseVoteRepository voteRepository) {
        this.proposalRepository = proposalRepository;
        this.voteRepository = voteRepository;
    }

    @Override
    public ExpenseProposalResponse createProposal(CreateExpenseProposalRequest request) {
        ExpenseProposal proposal = new ExpenseProposal();
        proposal.setId(UUID.randomUUID().toString());
        proposal.setTitle(request.title());
        proposal.setDescription(request.description());
        proposal.setEstimatedAmount(request.estimatedAmount().setScale(2, RoundingMode.HALF_UP));
        proposal.setProposedBy(request.proposedBy());
        proposal.setStatus(STATUS_PENDING_REVIEW);
        proposal.setCreatedAt(LocalDateTime.now());

        return toProposalResponse(proposalRepository.save(proposal), List.of());
    }

    @Override
    public List<ExpenseProposalResponse> getProposals(String status) {
        List<ExpenseProposal> proposals = (status == null || status.isBlank())
                ? proposalRepository.findAll()
                : proposalRepository.findByStatus(status);

        return proposals.stream()
                .map(p -> toProposalResponse(p, voteRepository.findByProposalId(p.getId())))
                .toList();
    }

    @Override
    public ExpenseProposalResponse getProposalById(String id) {
        ExpenseProposal proposal = proposalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense proposal not found"));
        return toProposalResponse(proposal, voteRepository.findByProposalId(id));
    }

    @Override
    public ExpenseProposalResponse castVote(String proposalId, CastVoteRequest request) {
        ExpenseProposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense proposal not found"));

        if (!STATUS_PENDING_REVIEW.equals(proposal.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Expense proposal is already closed with status: " + proposal.getStatus());
        }

        if (voteRepository.existsByProposalIdAndBoardMemberId(proposalId, request.boardMemberId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Board member has already voted on this proposal");
        }

        ExpenseVote vote = new ExpenseVote();
        vote.setId(UUID.randomUUID().toString());
        vote.setProposalId(proposalId);
        vote.setBoardMemberId(request.boardMemberId());
        vote.setVote(request.vote());
        vote.setComments(request.comments());
        vote.setVotedAt(LocalDateTime.now());
        voteRepository.save(vote);

        List<ExpenseVote> allVotes = voteRepository.findByProposalId(proposalId);
        long approvedCount = allVotes.stream().filter(v -> STATUS_APPROVED.equals(v.getVote())).count();
        long rejectedCount = allVotes.stream().filter(v -> STATUS_REJECTED.equals(v.getVote())).count();

        if (approvedCount >= QUORUM_MAJORITY_VOTES) {
            proposal.setStatus(STATUS_APPROVED);
            proposal.setApprovalDate(LocalDateTime.now());
            proposal = proposalRepository.save(proposal);
        } else if (rejectedCount >= QUORUM_MAJORITY_VOTES) {
            proposal.setStatus(STATUS_REJECTED);
            proposal.setApprovalDate(LocalDateTime.now());
            proposal = proposalRepository.save(proposal);
        }

        return toProposalResponse(proposal, allVotes);
    }

    @Override
    public List<ExpenseVoteResponse> getVotesByProposal(String proposalId) {
        if (!proposalRepository.existsById(proposalId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense proposal not found");
        }
        return voteRepository.findByProposalId(proposalId).stream()
                .map(this::toVoteResponse)
                .toList();
    }

    private ExpenseProposalResponse toProposalResponse(ExpenseProposal p, List<ExpenseVote> votes) {
        List<ExpenseVoteResponse> voteResponses = votes.stream().map(this::toVoteResponse).toList();
        long approved = votes.stream().filter(v -> STATUS_APPROVED.equals(v.getVote())).count();
        long rejected = votes.stream().filter(v -> STATUS_REJECTED.equals(v.getVote())).count();

        return new ExpenseProposalResponse(
                p.getId(),
                p.getTitle(),
                p.getDescription(),
                p.getEstimatedAmount(),
                p.getProposedBy(),
                p.getStatus(),
                p.getApprovalDate(),
                p.getCreatedAt(),
                approved,
                rejected,
                voteResponses);
    }

    private ExpenseVoteResponse toVoteResponse(ExpenseVote v) {
        return new ExpenseVoteResponse(
                v.getId(),
                v.getProposalId(),
                v.getBoardMemberId(),
                v.getVote(),
                v.getComments(),
                v.getVotedAt());
    }
}
