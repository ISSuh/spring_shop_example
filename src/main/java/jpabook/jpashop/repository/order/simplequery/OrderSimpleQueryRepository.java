package jpabook.jpashop.repository.order.simplequery;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {
  
  private final EntityManager entityManager;

  public List<OrderSimpleQueryDto> findOrdersDtos() {
    String jpql =
      "select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address) " +
      "from Order o " +
        "join o.member m " +
        "join o.delivery d";

    return entityManager.createQuery(jpql, OrderSimpleQueryDto.class)
                          .getResultList();
  }
}
