package com.be.dohands.member.dto;

import com.be.dohands.member.Member;
import java.util.List;
import lombok.Getter;

@Getter
public class MemberSlice<T> {
    private List<T> members;
    private Long next;
    private boolean hasNext;

    public MemberSlice(List<T> members, Long next, boolean hasNext) {
        this.members = members;
        this.next = next;
        this.hasNext = hasNext;
    }
}
