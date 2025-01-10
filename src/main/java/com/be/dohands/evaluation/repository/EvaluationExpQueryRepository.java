package com.be.dohands.evaluation.repository;

import static com.be.dohands.evaluation.QEvaluationExp.evaluationExp;

import com.be.dohands.evaluation.EvaluationExp;
import com.be.dohands.member.dto.CursorResult;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EvaluationExpQueryRepository {

    private final JPAQueryFactory factory;

    public CursorResult<EvaluationExp> findEvaluationExpsByEmployeeNumber(String employeeNumber, String cursor, int size) {
        if (cursor != null && cursor.equals("-1")) {
            return new CursorResult<>(Collections.emptyList(), "-1");
        }

        JPAQuery<EvaluationExp> ipq = factory.selectFrom(evaluationExp)
                .where(evaluationExp.employeeNumber.eq(employeeNumber));

        if (cursor == null) {
            List<EvaluationExp> rq = ipq.orderBy(evaluationExp.createdAt.desc())
                    .limit(size)
                    .fetch();

            return new CursorResult<>(
                    rq,
                    rq.isEmpty() ? "-1" : rq.get(0).getCreatedAt().toString());
        }

        List<EvaluationExp> rq = ipq.where(cursorCondition(cursor))
                .orderBy(evaluationExp.createdAt.desc(), evaluationExp.evaluationExpId.desc())
                .limit(size)
                .fetch();

        int rSize = rq.size();
        CursorResult<EvaluationExp> result = new CursorResult<>(
                rq,
                rq.isEmpty() ? "-1" : rq.get(0).getCreatedAt().toString());
        if (rSize < size) result.updateLast();
        return result;
    }

    private BooleanExpression cursorCondition(String cursor) {
        return evaluationExp.createdAt.lt(LocalDateTime.parse(cursor));
    }
}
