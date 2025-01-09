package com.be.dohands.leaderQuest.repository;

import static com.be.dohands.leaderQuest.QLeaderQuestExp.leaderQuestExp;

import com.be.dohands.leaderQuest.LeaderQuestExp;
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
public class LeaderQuestExpQueryRepository {

    private final JPAQueryFactory factory;

    public CursorResult<LeaderQuestExp> findLeaderQuestExpsByEmployeeNumber(String employeeNumber, String cursor, int size) {
        if (cursor != null && cursor.equals("-1")) {
            return new CursorResult<>(Collections.emptyList(), "-1");
        }

        JPAQuery<LeaderQuestExp> ipq = factory.selectFrom(leaderQuestExp)
                .where(leaderQuestExp.employeeNumber.eq(employeeNumber));

        if (cursor == null) {
            List<LeaderQuestExp> rq = ipq.orderBy(leaderQuestExp.createdAt.desc())
                    .limit(size)
                    .fetch();

            return new CursorResult<>(
                    rq,
                    rq.isEmpty() ? "-1" : rq.get(0).getCreatedAt().toString());
        }

        List<LeaderQuestExp> rq = ipq.where(cursorCondition(cursor))
                .orderBy(leaderQuestExp.createdAt.desc())
                .limit(size)
                .fetch();

        int rSize = rq.size();
        CursorResult<LeaderQuestExp> result = new CursorResult<>(
                rq,
                rq.isEmpty() ? "-1" : rq.get(0).getCreatedAt().toString());
        if (rSize < size) result.updateLast();
        return result;
    }

    private BooleanExpression cursorCondition(String cursor) {
        return leaderQuestExp.createdAt.lt(LocalDateTime.parse(cursor));
    }
}
