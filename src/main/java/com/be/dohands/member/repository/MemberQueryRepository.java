package com.be.dohands.member.repository;

import static com.be.dohands.member.QMember.member;

import com.be.dohands.member.Member;
import com.be.dohands.member.dto.MemberSlice;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {

    private final JPAQueryFactory factory;

    public MemberSlice<Member> findAllMembers(Long next, int size) {
        List<Member> members = factory.selectFrom(member)
                .where(gtNext(next))
                .orderBy(member.userId.asc())
                .limit(size)
                .fetch();

        return sliceResult(size, members);
    }

    public MemberSlice<Member> findMembersByName(String name, Long next, int size) {
        List<Member> members = factory.selectFrom(member)
                .where(member.name.eq(name).and(gtNext(next)))
                .orderBy(member.userId.asc())
                .limit(size)
                .fetch();

        return sliceResult(size, members);
    }

    private MemberSlice<Member> sliceResult(int size, List<Member> members) {
        boolean hasNext = false;
        Long next = null;
        if (members != null && members.size() >= size) {
            next = members.get(members.size() - 1).getUserId();
            hasNext = true;
        }

        return new MemberSlice<>(members, next, hasNext);
    }

    private BooleanExpression gtNext(Long next) {
        if (next == null) {
            return null;
        }
        return member.userId.gt(next);
    }
}
