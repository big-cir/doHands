package com.be.dohands.sheet;

import com.be.dohands.member.Member;
import com.be.dohands.member.repository.MemberRepository;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class SpreadSheetServiceTest {

    @Autowired
    private SpreadSheetService spreadSheetService;

    @Autowired
    private MemberRepository memberRepository;

    private String spreadsheetId = "1nEA36Rft_qqzLjRaojQC1hCwxGuhBQz_ABoi5jRXdOk";
    private String sheetName = "참고. 구성원 정보";

    @Test
    void createMemberInfoToSheet() throws GeneralSecurityException, IOException {

        Member member = Member.builder()
            .loginId("김땡땡_두핸즈")                  // 로그인 ID
            .password("1111")          // 비밀번호
            .employeeNumber("2022011511")           // 사번
            .department("음성 1센터")          // 부서
            .name("김땡땡")                     // 이름
            .levelId(5L)                         // 레벨 ID
            .hireDate(LocalDate.of(2022, 1, 15))  // 입사일
            .jobGroup("2")                        // 직무 그룹
            .jobCategory("현장직")             // 직무 카테고리
            .build();

        memberRepository.save(member);

        spreadSheetService.createMemberInfoToSheet(spreadsheetId, member);
    }
}