package hello.hellospringmock.repository;

import hello.hellospringmock.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MemoryMemberRepositoryTest {

    MemoryMemberRepository repository = new MemoryMemberRepository();

    @AfterEach
    public void afterEach() {
        repository.clearStore();
    }

    @Test
    public void save() {
        System.out.println(0);
        // Given
        Member member = new Member();
        member.setName("user01");

        // When
        repository.save(member);
        Member result = repository.findById(member.getId()).get();

        // Then
        assertThat(result).isEqualTo(member);
    }

    @Test
    public void findByName() {
        System.out.println(1);
        // Given
        Member member1 = new Member();
        member1.setName("user01");
        repository.save(member1);

        Member member2 = new Member();
        member2.setName("user02");
        repository.save(member2);

        // Then
        Member result = repository.findByName("user02").get();
        assertThat(result).isEqualTo(member2);
    }

    @Test
    public void findAll() {
        System.out.println(2);
        // Given
        Member member1 = new Member();
        member1.setName("user01");
        repository.save(member1);

        Member member2 = new Member();
        member2.setName("user02");
        repository.save(member2);


        // option + enter : auto complete
        List<Member> result = repository.findAll();


        // Then
        assertThat(result.size()).isEqualTo(2);
    }
}
