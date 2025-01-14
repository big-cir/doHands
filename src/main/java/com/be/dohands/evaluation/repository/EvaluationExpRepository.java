package com.be.dohands.evaluation.repository;

import com.be.dohands.evaluation.EvaluationExp;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EvaluationExpRepository extends JpaRepository<EvaluationExp, Long> {

    List<EvaluationExp> findEvaluationExpsByEmployeeNumber(String employeeNumber);

    @Query(value = "select cast(coalesce(count(e.evaluationExpId),0) as int) "
        + "from EvaluationExp e left join Member m on e.employeeNumber = m.employeeNumber "
        + "where m.userId = :userId and e.year = :year")
    Integer countIdsByUserIdAndYear(@Param("userId") Long userId, @Param("year") Integer year);

    Optional<EvaluationExp> findTopByEmployeeNumberOrderByCreatedAtDesc(String employeeNumber);
}
