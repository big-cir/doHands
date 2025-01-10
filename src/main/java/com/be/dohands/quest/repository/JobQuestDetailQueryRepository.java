package com.be.dohands.jobQuest.repository;

import static com.be.dohands.jobQuest.QJobQuestDetail.jobQuestDetail;

import com.be.dohands.jobQuest.JobQuestDetail;
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
public class JobQuestDetailQueryRepository {

    private final JPAQueryFactory factory;

    public CursorResult<JobQuestDetail> findJobQuestDetailByIds(List<Long> jobQuestIds, String cursor, int size) {
        if (cursor != null && cursor.equals("-1")) {
            return new CursorResult<>(Collections.emptyList(), "-1");
        }

        JPAQuery<JobQuestDetail> ipq = factory.selectFrom(jobQuestDetail)
                .where(jobQuestDetail.jobQuestId.in(jobQuestIds));

        if (cursor == null) {
            List<JobQuestDetail> rq = ipq.orderBy(jobQuestDetail.createdAt.desc())
                    .limit(size)
                    .fetch();

            return new CursorResult<>(
                    rq,
                    rq.isEmpty() ? "-1" : rq.get(0).getCreatedAt().toString());
        }

        List<JobQuestDetail> rq = ipq.where(cursorCondition(cursor))
                .orderBy(jobQuestDetail.createdAt.desc())
                .limit(size)
                .fetch();

        int rSize = rq.size();
        CursorResult<JobQuestDetail> result = new CursorResult<>(
                rq,
                rq.isEmpty() ? "-1" : rq.get(0).getCreatedAt().toString());
        if (rSize < size) result.updateLast();
        return result;
    }

    private BooleanExpression cursorCondition(String cursor) {
        return jobQuestDetail.createdAt.lt(LocalDateTime.parse(cursor));
    }
}
