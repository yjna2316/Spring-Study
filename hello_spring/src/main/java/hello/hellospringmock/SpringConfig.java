package hello.hellospringmock;

import hello.hellospringmock.repository.JDBCMemberRepository;
import hello.hellospringmock.repository.JdbcTemplateMemberRepository;
import hello.hellospringmock.repository.MemberRepository;
import hello.hellospringmock.repository.MemoryMemberRepository;
import hello.hellospringmock.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import javax.xml.crypto.Data;

@Configuration
public class SpringConfig {

    // Configuration 파일도 스프링 빈으로 관리되기 때문에
    // 스프링 부트가 application.properties를 보고 DataSource를 빈으로 등록해주고 DI해줌
    private DataSource dataSource;

    @Autowired
    public SpringConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public MemberService memberService() {
        // 무얼 넣어줘야 하지? -> command + p
        return new MemberService(memberRepository());
    }

    @Bean
    public MemberRepository memberRepository() {
        /**
         * OCP 원칙 - 코드의 수정 없이 기능 수정이 가능하다.
         **/
        return new JdbcTemplateMemberRepository(dataSource);
//      return new JDBCMemberRepository(dataSource)
//      return new MemoryMemberRepository();
    }
}
