package hello.hellospringmock.repository;

import hello.hellospringmock.domain.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    Member save(Member member); // 회원 정보 저장
    Optional<Member> findById(Long id); // ID로 멤버 조회
    Optional<Member> findByName(String name); // 이름 조회
    List<Member> findAll(); // 모든 회원 조회
}
