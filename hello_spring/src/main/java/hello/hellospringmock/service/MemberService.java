package hello.hellospringmock.service;


import hello.hellospringmock.domain.Member;
import hello.hellospringmock.repository.MemberRepository;
import hello.hellospringmock.repository.MemoryMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// command + shift + T : test auto create
// @Service
public class MemberService {
//    private final MemberRepository memberRepository = new MemoryMemberRepository();

    private final MemberRepository memberRepository;
    // @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
    /**
     * 회원 가입
     */
     public long join(Member member) {
         validateDuplicateMember(member); // 이름 중복 체크
         memberRepository.save(member); // 회원 정보 생성
         return member.getId();
     }

    private void validateDuplicateMember(Member member) {
         memberRepository.findByName(member.getName())
                 .ifPresent(mem -> {
                     throw new IllegalStateException("이미 존재하는 회원입니다.");
                 });
    }

    /**
     * 전체 회원 조회
     */
    public List<Member> findMembers() { return memberRepository.findAll(); }

    /**
     * 회원 한명 조회
     */
    public Optional<Member> findOne(Long memberId) { return memberRepository.findById(memberId);}
}


