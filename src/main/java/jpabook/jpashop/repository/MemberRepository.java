package jpabook.jpashop.repository;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

  private final EntityManager entityManager;

  public void save(Member Member) {
    entityManager.persist(Member);
  }

  public Member findOne(Long id) {
    return entityManager.find(Member.class, id);
  }

  public List<Member> findAll() {
    return entityManager.createQuery(
      "select m from Member m", Member.class)
        .getResultList();
  }

  public List<Member> findByMember(String name) {
    return entityManager.createQuery(
      "select m from Member m where m.name = :name", Member.class)
        .setParameter("name", name)  
        .getResultList();
  }
}
