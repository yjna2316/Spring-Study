package hello.hellospringmock.service;

import hello.hellospringmock.domain.Member;
import hello.hellospringmock.repository.MemberRepository;
import hello.hellospringmock.repository.MemoryMemberRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MemberServiceTest {

    private final MemberService memberService = new MemberService();

    @Test
    void join() {
        // Given
        Member member = new Member();
        member.setName("user01");
        // When
        memberService.join(member);
        // Then
        // command option V : auto create variable
//        Optional<Member> result = memberService.findById(member.getId());

    }

    @Test
    void findMembers() {
    }

    @Test
    void findOneMember() {
    }
}