package com.be.dohands.evaluation.repository;

import com.be.dohands.evaluation.EvaluationExp;
import java.util.List;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationExpRepository extends JpaRepository<EvaluationExp, Long> {

    List<EvaluationExp> findEvaluationExpsByEmployeeNumber(String employeeNumber);
}
