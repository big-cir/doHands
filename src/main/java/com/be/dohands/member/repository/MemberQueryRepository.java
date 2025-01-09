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

    public MemberSlice findAllMembers(Long lastUserId, int size) {
        List<Member> members = factory.selectFrom(member)
                .where(gtUserId(lastUserId))
                .orderBy(member.userId.asc())
                .limit(size)
                .fetch();

        return sliceResult(size, members);
    }

    public MemberSlice findMembersByName(String name, Long lastUserId, int size) {
        List<Member> members = factory.selectFrom(member)
                .where(member.name.eq(name).and(gtUserId(lastUserId)))
                .orderBy(member.userId.asc())
                .limit(size)
                .fetch();

        return sliceResult(size, members);
    }


    private MemberSlice sliceResult(int size, List<Member> members) {
        boolean hasNext = false;
        Long lastUserId = null;
        if (members != null && members.size() > size) {
            lastUserId = members.get(members.size() - 1).getUserId();
            hasNext = true;
        }

        return new MemberSlice(members, lastUserId, hasNext);
    }

    private BooleanExpression gtUserId(Long userId) {
        if (userId == null) {
            return null;
        }
        return member.userId.gt(userId);
    }
}
