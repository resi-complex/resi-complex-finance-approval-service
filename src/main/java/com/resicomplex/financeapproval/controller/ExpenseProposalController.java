package com.resicomplex.financeapproval.controller;

import com.resicomplex.financeapproval.dto.CastVoteRequest;
import com.resicomplex.financeapproval.dto.CreateExpenseProposalRequest;
import com.resicomplex.financeapproval.dto.ExpenseProposalResponse;
import com.resicomplex.financeapproval.dto.ExpenseVoteResponse;
import com.resicomplex.financeapproval.service.IFinanceApprovalService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/expense-proposals")
public class ExpenseProposalController {

    private final IFinanceApprovalService service;

    public ExpenseProposalController(IFinanceApprovalService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseProposalResponse create(@Valid @RequestBody CreateExpenseProposalRequest request) {
        return service.createProposal(request);
    }

    @GetMapping
    public List<ExpenseProposalResponse> getAll(
            @RequestParam(name = "status", required = false) String status) {
        return service.getProposals(status);
    }

    @GetMapping("/{id}")
    public ExpenseProposalResponse getById(@PathVariable("id") String id) {
        return service.getProposalById(id);
    }

    @PostMapping("/{id}/votes")
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseProposalResponse castVote(
            @PathVariable("id") String id,
            @Valid @RequestBody CastVoteRequest request) {
        return service.castVote(id, request);
    }

    @GetMapping("/{id}/votes")
    public List<ExpenseVoteResponse> getVotes(@PathVariable("id") String id) {
        return service.getVotesByProposal(id);
    }
}
