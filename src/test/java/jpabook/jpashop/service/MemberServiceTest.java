package jpabook.jpashop.service;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {
  
  @Autowired MemberService service;
  @Autowired MemberRepository repository;

  @Test
  public void registMember() throws Exception {
    // given
    Member member = new Member();
    member.setName("suh");

    // when
    Long saveId = service.join(member);

    // then
    Member findMember = service.findOne(saveId);
    Assertions.assertThat(findMember).isEqualTo(member);
  }

  @Test
  public void exceptionFromDuplicateMember() throws Exception {
    // given
    Member member1 = new Member();
    member1.setName("suh");

    Member member2 = new Member();
    member2.setName("suh");

    // when
    service.join(member1);

    // then
    Assertions.assertThatThrownBy(
      () -> service.join(member2)).isInstanceOf(IllegalStateException.class);
  }
}
