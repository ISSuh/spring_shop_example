package jpabook.jpashop.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
  
  private final MemberRepository repository;

  // 회원 가입
  @Transactional
  public Long join(Member member) {
    validateDuplicateMember(member);
    repository.save(member);
    return member.getId();
  }

  // 전체 회원 조횐
  public List<Member> findMembers() {
    return repository.findAll();
  }

  public Member findOne(Long memberId) {
    return repository.findOne(memberId);
  }

  private void validateDuplicateMember(Member member) {
    List<Member> findMembers = repository.findByMember(member.getName());
    if (!findMembers.isEmpty()) {
      throw new IllegalStateException("이미 존재하는 회원");
    }
  }
}
