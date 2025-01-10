package com.be.dohands.quest.repository;

import static com.be.dohands.quest.entity.QLeaderQuestExpEntity.leaderQuestExpEntity;

import com.be.dohands.member.dto.CursorResult;
import com.be.dohands.quest.entity.LeaderQuestExpEntity;
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

    public CursorResult<LeaderQuestExpEntity> findLeaderQuestExpsByEmployeeNumber(String employeeNumber, String cursor, int size) {
        if (cursor != null && cursor.equals("-1")) {
            return new CursorResult<>(Collections.emptyList(), "-1");
        }

        JPAQuery<LeaderQuestExpEntity> ipq = factory.selectFrom(leaderQuestExpEntity)
                .where(leaderQuestExpEntity.employeeNumber.eq(employeeNumber));

        if (cursor == null) {
            List<LeaderQuestExpEntity> rq = ipq.orderBy(leaderQuestExpEntity.createdAt.desc())
                    .limit(size)
                    .fetch();

            return new CursorResult<>(
                    rq,
                    rq.isEmpty() ? "-1" : rq.get(0).getCreatedAt().toString());
        }

        List<LeaderQuestExpEntity> rq = ipq.where(cursorCondition(cursor))
                .orderBy(leaderQuestExpEntity.createdAt.desc())
                .limit(size)
                .fetch();

        int rSize = rq.size();
        CursorResult<LeaderQuestExpEntity> result = new CursorResult<>(
                rq,
                rq.isEmpty() ? "-1" : rq.get(0).getCreatedAt().toString());
        if (rSize < size) result.updateLast();
        return result;
    }

    private BooleanExpression cursorCondition(String cursor) {
        return leaderQuestExpEntity.createdAt.lt(LocalDateTime.parse(cursor));
    }
}
