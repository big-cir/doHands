package com.be.dohands.quest.repository;

import static com.be.dohands.quest.entity.QJobQuestExp.jobQuestExp;

import com.be.dohands.member.dto.CursorResult;
import com.be.dohands.quest.entity.JobQuestExpEntity;
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
public class JobQuestExpQueryRepository {

    private final JPAQueryFactory factory;

    public CursorResult<JobQuestExpEntity> findJobQuestDetailByIds(List<Long> jobQuestIds, String cursor, int size) {
        if (cursor != null && cursor.equals("-1")) {
            return new CursorResult<>(Collections.emptyList(), "-1");
        }

        JPAQuery<JobQuestExpEntity> ipq = factory.selectFrom(jobQuestExp)
                .where(jobQuestExp.jobQuestId.in(jobQuestIds));

        if (cursor == null) {
            List<JobQuestExpEntity> rq = ipq.orderBy(jobQuestExp.createdAt.desc())
                    .limit(size)
                    .fetch();

            return new CursorResult<>(
                    rq,
                    rq.isEmpty() ? "-1" : rq.get(0).getCreatedAt().toString());
        }

        List<JobQuestExpEntity> rq = ipq.where(cursorCondition(cursor))
                .orderBy(jobQuestExp.createdAt.desc())
                .limit(size)
                .fetch();

        int rSize = rq.size();
        CursorResult<JobQuestExpEntity> result = new CursorResult<>(
                rq,
                rq.isEmpty() ? "-1" : rq.get(0).getCreatedAt().toString());
        if (rSize < size) result.updateLast();
        return result;
    }

    private BooleanExpression cursorCondition(String cursor) {
        return jobQuestExp.createdAt.lt(LocalDateTime.parse(cursor));
    }
}
