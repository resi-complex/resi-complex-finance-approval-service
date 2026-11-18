package com.resicomplex.financeapproval.service;

import com.resicomplex.financeapproval.dto.CastVoteRequest;
import com.resicomplex.financeapproval.dto.CreateExpenseProposalRequest;
import com.resicomplex.financeapproval.dto.ExpenseProposalResponse;
import com.resicomplex.financeapproval.dto.ExpenseVoteResponse;
import java.util.List;

public interface IFinanceApprovalService {

    ExpenseProposalResponse createProposal(CreateExpenseProposalRequest request);

    List<ExpenseProposalResponse> getProposals(String status);

    ExpenseProposalResponse getProposalById(String id);

    ExpenseProposalResponse castVote(String proposalId, CastVoteRequest request);

    List<ExpenseVoteResponse> getVotesByProposal(String proposalId);
}
