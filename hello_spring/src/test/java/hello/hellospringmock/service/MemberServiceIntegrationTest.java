package hello.hellospringmock.service;

import hello.hellospringmock.domain.Member;
import hello.hellospringmock.repository.MemberRepository;
import hello.hellospringmock.repository.MemoryMemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
// 실제 스프링 부트가 뜨고 SpringConfig 애들도 올라간다.
class MemberServiceIntegrationTest {

/*
    MemberService memberService;
    MemoryMemberRepository memberRepository;

    @BeforeEach
    public void beforeEach() {
        memberRepository = new MemoryMemberRepository();
        memberService = new MemberService(memberRepository); //  DI -> MemberService입장에서 memberRepo를 외부에서 주입당하고 있다.
    }
*/

    /**
     * 기존에는 직접 객체를 생성해서 넣었는데 통합T에서는 스프링 컨테이너한테 직접 달라고 해야한다.
     **/
    @Autowired  MemberService memberService;
    @Autowired  MemberRepository memberRepository;

    /**
     * @Transactional가 자동으로 테스트 하나(트랜잭션 하나)가 끝날 때마다 DB에 넣었던 데이터를 rollback 해주기 때문에 DB에 반영이 안됨 -> 아래 코드 필요 없다.
     *                  서비스 같은 코드에 붙었을 때는 롤백되지 않는다.
     */
/*
    @AfterEach
    public void afterEach() {
        memberRepository.clearStore();
    }
*/


    @Test
    void 회원가입() {
        // Given
        Member member = new Member();
        member.setName("user04");

        // When
        Long saveId = memberService.join(member);

        // Then
        // 검증해야 할 것: 우리가 저장한게 레포지토리에 있는게 맞아?
        // command option V : auto create variable

        Member findMember = memberRepository.findById(saveId).get();
        assertEquals(member.getName(), findMember.getName());
    }

    @Test
    public void 중복_회원_예외() {
        // Given
        Member member1 = new Member();
        member1.setName("spring");

        Member member2 = new Member();
        member2.setName("spring");

        // When
        /* s2. assertThrows(A, B) 사용한 방법 */
        memberService.join(member1);
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.join(member2)); // B 발생시 A(예외)가 발생해야 한다.

        assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");

        /*
        s1. try-catch 이용한 방법
        memberService.join(member1);
        try {
            memberService.join(member2);
            fail();
        } catch (IllegalStateException e) {
            assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
        }
       */
    }

    @Test
    void findMembers() {
    }

    @Test
    void findOne() {
    }
}