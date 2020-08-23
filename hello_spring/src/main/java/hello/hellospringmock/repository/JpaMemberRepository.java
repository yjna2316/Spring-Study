package hello.hellospringmock.repository;

import hello.hellospringmock.domain.Member;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class JpaMemberRepository implements MemberRepository {

    // JPA는 EntityManager로 모든게 돌아간다.
    private final EntityManager em;

    public JpaMemberRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Member save(Member member) {
        // persist: insert쿼리 만들어서 set Id까지 다해준다.
        em.persist(member);
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    @Override
    public Optional<Member> findByName(String name) {
        List<Member> result = em.createQuery("select m from Member m where m.name = :name", Member.class)
                                .setParameter("name", name)
                                .getResultList();
        return result.stream().findAny();
    }

    @Override
    public List<Member> findAll() {
        // m: 객체 자체를 select 한다. 이전처럼 맵핑을 따로 해줄 필요가 없다.
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }
}
