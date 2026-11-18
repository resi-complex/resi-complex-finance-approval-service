package com.resicomplex.financeapproval.repository;

import com.resicomplex.financeapproval.model.ExpenseVote;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseVoteRepository extends JpaRepository<ExpenseVote, String> {

    List<ExpenseVote> findByProposalId(String proposalId);

    boolean existsByProposalIdAndBoardMemberId(String proposalId, String boardMemberId);
}
