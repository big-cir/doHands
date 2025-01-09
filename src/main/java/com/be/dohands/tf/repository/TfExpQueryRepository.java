package com.be.dohands.tf.repository;

import static com.be.dohands.tf.QTfExp.tfExp;

import com.be.dohands.member.dto.CursorResult;
import com.be.dohands.tf.TfExp;
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
public class TfExpQueryRepository {

    private final JPAQueryFactory factory;

    public CursorResult<TfExp> findTfExpsByEmployeeNumber(String employeeNumber, String cursor, int size) {
        if (cursor != null && cursor.equals("-1")) {
            return new CursorResult<>(Collections.emptyList(), "-1");
        }

        JPAQuery<TfExp> ipq = factory.selectFrom(tfExp)
                .where(tfExp.employeeNumber.eq(employeeNumber));


        if (cursor == null) {
            List<TfExp> rq = ipq.orderBy(tfExp.createdAt.desc())
                    .limit(size)
                    .fetch();

            return new CursorResult<>(
                    rq,
                    rq.isEmpty() ? "-1" : rq.get(0).getCreatedAt().toString());
        }

        List<TfExp> rq = ipq.where(cursorCondition(cursor))
                .orderBy(tfExp.createdAt.desc(), tfExp.tfExpId.desc())
                .limit(size)
                .fetch();

        int rSize = rq.size();
        CursorResult<TfExp> result = new CursorResult<>(
                rq,
                rq.isEmpty() ? "-1" : rq.get(0).getCreatedAt().toString());
        if (rSize < size) result.updateLast();
        return result;
    }

    private BooleanExpression cursorCondition(String cursor) {
        return tfExp.createdAt.lt(LocalDateTime.parse(cursor));
    }
}
