package hello.hellospringmock;

import hello.hellospringmock.repository.MemberRepository;
import hello.hellospringmock.repository.MemoryMemberRepository;
import hello.hellospringmock.service.MemberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {
    @Bean
    public MemberService memberService() {
        // 무얼 넣어줘야 하지? -> command + p
        return new MemberService(memberRepository());
    }

    @Bean
    public MemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }
}
