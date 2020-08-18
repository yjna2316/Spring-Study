package hello.hellospringmock.controller;

import hello.hellospringmock.domain.Member;
import hello.hellospringmock.domain.MemberForm;
import hello.hellospringmock.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class MemberController {

    private final MemberService memberService;

    @Autowired // Spring 컨테이너에서 서비스를 가져와 해당 컨트롤러와 연결 => 의존관계 생성(DI)
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // 회원 등록하기 위한 페이지
    @GetMapping("/members/new")
    public String createForm() {
        return "members/createMemberForm";
    }

    // 회원 등록 페이지 -> 회원 정보 생성
    @PostMapping("/members/new")
    public String create(MemberForm form) {  // 1. MemberForm 에서 name을 세팅한 후 가지고 온다.
        Member member = new Member();
        member.setName(form.getName());

        memberService.join(member);

        return "redirect:/";
    }

    // command option V : auto create variable
    @GetMapping("/members")
    public String members(Model model) {
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";
    }
}
