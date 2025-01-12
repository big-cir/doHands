package com.be.dohands.member.controller;

import com.be.dohands.common.JwtUtil;
import com.be.dohands.common.security.CustomUserDetails;
import com.be.dohands.member.Member;
import com.be.dohands.member.Role;
import com.be.dohands.member.dto.LoginDto;
import com.be.dohands.member.dto.LoginResponse;
import com.be.dohands.member.repository.MemberRepository;
import com.be.dohands.notification.service.FcmService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final MemberRepository memberRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final FcmService fcmService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.loginId(), loginDto.password()));

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            String token = jwtUtil.generateToken(userDetails.getUsername(), roles);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + token);

            Member member = memberRepository.findByLoginId(loginDto.loginId()).get();
            Long userId = member.getUserId();
            fcmService.saveToken(loginDto.fcmToken(), userId);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new LoginResponse(userId, member.getRole().equals(Role.ROLE_ADMIN)));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
