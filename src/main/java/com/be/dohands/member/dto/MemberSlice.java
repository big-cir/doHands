package com.be.dohands.member.dto;

import com.be.dohands.member.Member;
import java.util.List;
import lombok.Getter;

@Getter
public class MemberSlice {
    private List<Member> members;
    private Long next;
    private boolean hasNext;

    public MemberSlice(List<Member> members, Long next, boolean hasNext) {
        this.members = members;
        this.next = next;
        this.hasNext = hasNext;
    }
}
