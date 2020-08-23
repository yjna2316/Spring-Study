package hello.hellospringmock.repository;

import hello.hellospringmock.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Parameter Info: command + p

/**
 * Spring JPA가 JpaRepository 인터페이스를 받고 있는 SpringDataJpaMemberRepository를 보면
 * 구현체를 만들어줘서 자동으로 스프링 빈에 등록시켜준다.
 */

public interface SpringDataJpaMemberRepository extends JpaRepository<Member, Long>, MemberRepository {

    // JPQL select m from Member m where m.name = ?
    @Override
    Optional<Member> findByName(String name);
//    Optional<Member> findByNameAndId(String name, Long Id);
}
