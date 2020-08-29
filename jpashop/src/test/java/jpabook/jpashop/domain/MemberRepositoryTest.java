package jpabook.jpashop.domain;

import com.fasterxml.jackson.databind.deser.std.StdKeyDeserializer;
import com.sun.xml.bind.v2.schemagen.xmlschema.AttrDecls;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Test
    @Transactional
    public void save() {
        Member member = new Member();
        member.setUsername("A");

        Long saveId = memberRepository.save(member);
        Member findMember = memberRepository.find(saveId);

        //then
        Assertions.assertThat(findMember.getId()).isEqualTo((member.getId()));
        Assertions.assertThat(findMember.getUsername()).isEqualTo((member.getUsername()));
    }

    @Test
    public void find() {
    }

}