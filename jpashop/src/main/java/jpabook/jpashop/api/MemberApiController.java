package jpabook.jpashop.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    // 회원 조회 api

    /**
     * 화면을 뿌려주기 위한 로직이 추가될 것이다. @JsonIgnore
     * api 로직이 바뀌면 api 스펙도 의도치 않게 바뀌기 쉬어진다. -> 장애 발생 가능
     * 유연하지도 유지보수하기도 어려운 구조이다.
     **/

    @GetMapping("/api/v1/members")
    public List<Member> membersV1() { return memberService.findMembers(); }

    @GetMapping("/api/v2/members")
    public Result memberV2() {
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());

        //        return new Result(collect);
        return new Result(collect.size(), collect);
     }

    @Data
    @AllArgsConstructor
    // []이 아닌 {}로 감싸서 (유지보수, 유연성)
    // {count: 2, data: [{},{}]}
    // 엔티티 그대로가 아닌, 원하는 일부만 보여줄 수 있고, 추가도 가능하다 (유연성)
    static class Result<T> {
        private int count;
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

    // 회원 등록 api

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) { // api 만들 때는 엔티티를 파라메터로 이렇게 받으면 안된다. mapping 문제 때문. -> v2 권장
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) { // 별도의 DTO 받는 방법
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }
    // 회원 수정 api

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request) {
        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);

        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }
    // 안에서만 쓸꺼니까 Inner class
    @Data
    static class UpdateMemberRequest {
        private String name;

    }
    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;

    }
    // ? 왜 static일까?
    @Data
    static class CreateMemberRequest {
        private String name;

    }
    @Data
    static class CreateMemberResponse {

        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

}