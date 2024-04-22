package org.zerock.boardex.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.zerock.boardex.domain.Member;
import org.zerock.boardex.dto.MemberSecurityDTO;
import org.zerock.boardex.repository.MemberRepository;

import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    //UsernameNotFoundException : username을 가진 사용자가 없다면 예외처리함
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //사용자 아이디를 콘솔에 표시
        log.info("loadUserByUsername : "+username);
        //데이터베이스에서 사용자 아이디의 권한과 정보 찾아서 result에 저장
        Optional<Member> result = memberRepository.getWithRoles(username);
        //해당 아이디를 가진 사용자가 없다면
        if(result.isEmpty()){
            throw new UsernameNotFoundException("사용자가 없습니다.");
        }
        //사용자 아이디가 있다면 그 해당정보를 얻어서 member에 저장
        Member member = result.get();
        //MemberSecurityDTO 객체의 아이디, 비밀번호, 이메일, del값, social(false), 권한정보 등을 매핑
        //SimplegrantedAuthority : GrantedAuthority를 상속받은 클래스임 (ROLE_USER, ROLE_ADMIN 둘 중 하나)
        MemberSecurityDTO memberSecurityDTO = new MemberSecurityDTO(
                member.getMid(),
                member.getMpw(),
                member.getEmail(),
                member.isDel(),
                false,
                member.getRoleSet()
                        .stream().map(memberRole -> new SimpleGrantedAuthority("ROLE_"+memberRole.name())).collect(Collectors.toList())
        );
        log.info("memberSecurityDTO");
        log.info(memberSecurityDTO);
        return memberSecurityDTO;
    };


//    private PasswordEncoder passwordEncoder;
//
//    public CustomUserDetailsService() {
//        this.passwordEncoder = new BCryptPasswordEncoder();
//    }

    //UserDetails 반환타입 : 사용자 권한 정보, 인증을 위한 비밀번호 정보, 아이디 정보, 계정 만료 여부, 계정 잠김 여부를 알 수 있다.
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
//
//        log.info("사용자 이름: " + username);
//        //비밀번호 1111을 암호화해서 userDetails에 저장
//        //authorities() : 권한 설정
//        UserDetails userDetails = User.builder()
//                .username("user1")
//                .password(passwordEncoder.encode("1111"))
//                .authorities("ROLE_USER")
//                .build();
//        return userDetails;
//    }

}
