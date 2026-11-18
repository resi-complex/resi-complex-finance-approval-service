package com.resicomplex.financeapproval.repository;

import com.resicomplex.financeapproval.model.ExpenseProposal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseProposalRepository extends JpaRepository<ExpenseProposal, String> {

    List<ExpenseProposal> findByStatus(String status);
}
