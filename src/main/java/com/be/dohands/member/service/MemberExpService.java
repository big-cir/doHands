package com.be.dohands.member.service;

import com.be.dohands.member.MemberExp;
import com.be.dohands.member.repository.MemberExpRepository;
import jakarta.transaction.Transactional;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberExpService {

    private final MemberExpRepository memberExpRepository;

    @Transactional
    public void addGivenExp(Long userId, Integer givenExp) {
        MemberExp memberExp = memberExpRepository.findByUserId(userId)
            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 memberId"));

        memberExp.addToCurrentExp(givenExp);
        memberExpRepository.save(memberExp);
    }

}
