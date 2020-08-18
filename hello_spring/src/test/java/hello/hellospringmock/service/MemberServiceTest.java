package hello.hellospringmock.service;

import hello.hellospringmock.domain.Member;
import hello.hellospringmock.repository.MemberRepository;
import hello.hellospringmock.repository.MemoryMemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;


// ctrl + R 직전에 실행한 테스트코드 실행해줌
class MemberServiceTest {

    /*
    private final MemberService memberService = new MemberService();
    private final MemberRepository memberRepository = new MemoryMemberRepository();
    */

    /**
     * 문제점: MemberService에서 사용하는 MemberRepo랑, Test에서 사용하는 MemberRepo가 서로 다른 인스턴스이다.
     *       여기서는 memory가 static이라 문제되지 않지만, memory가 static이 아니라면 서로 다른 memory를 바라보게 되므로 문제가 될 수 있다.
     *       때문에, 테스트시 MemberRepo 객체를 새로 만들지 않고 외부에서 만든걸 주입하도록 만든다 -> DI
     **/


    MemberService memberService;
    MemoryMemberRepository memberRepository;

    // 각 테스트는 독립적으로 수행되어야 한다.
    // 서로 영향이 없도록 새로운 객체를 생성하고, 의존관계도 새로 맺어준다.
    @BeforeEach
    public void beforeEach() {
        memberRepository = new MemoryMemberRepository();
        memberService = new MemberService(memberRepository); //  DI -> MemberService입장에서 memberRepo를 외부에서 주입당하고 있다.
    }

    // 각 테스트는 독립적으로 수행되어야 한다.
    // 메모리가 공유되고 있으므로 테스트가 끝나면 지워준다.
    @AfterEach
    public void afterEach() {
        memberRepository.clearStore();
    }

    @Test
    void 회원가입() {
        // Given
        Member member = new Member();
        member.setName("user");

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
        member1.setName("user");

        Member member2 = new Member();
        member2.setName("user");

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