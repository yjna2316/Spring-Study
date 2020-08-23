package hello.hellospringmock;

import hello.hellospringmock.repository.*;
import hello.hellospringmock.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import javax.xml.crypto.Data;

@Configuration
public class SpringConfig {

    // Configuration 파일도 스프링 빈으로 관리되기 때문에
    // 스프링 부트가 application.properties를 보고 DataSource를 빈으로 등록해주고 DI해줌
/*
    private final DataSource dataSource;
    private final EntityManager em;

    @Autowired
    public SpringConfig(DataSource dataSource, EntityManager em) {
        this.dataSource = dataSource;
        this.em = em;
    }
*/
    private final MemberRepository memberRepository;

    public SpringConfig(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }


    @Bean
    public MemberService memberService() {
        // 무얼 넣어줘야 하지? -> command + p
        // return new MemberService(memberRepository());
        return new MemberService(memberRepository);
    }

//    @Bean
//    public MemberRepository memberRepository() {
        /**
         * OCP 원칙 - 코드의 수정 없이 기능 수정이 가능하다.
         **/
//        return new JpaMemberRepository(em);
//      return new JdbcTemplateMemberRepository(dataSource);
//      return new JDBCMemberRepository(dataSource)
//      return new MemoryMemberRepository();
//    }
}
