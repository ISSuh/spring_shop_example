package jpabook.jpashop.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

  private final EntityManager entityManager;

  public void save(Order order) {
    entityManager.persist(order);
  }

  public Order findOne(Long id) {
    return entityManager.find(Order.class, id);
  }

  public List<Order> findAllByString(OrderSearch orderSearch) {
    // language = JPAQL
    String jpql = "select o from Order o join o.member m";
    boolean isFirstCondition = true;

    // 주문상태 검색
    if (orderSearch.getOrderStatus() != null) {
      if (isFirstCondition) {
        jpql += " where";
        isFirstCondition = false;
      } else {
        jpql += " and";
      }
      jpql += " o.status == :status";
    }

    // 회원 이름 검색
    if (StringUtils.hasText(orderSearch.getMemberName())) {
      if (isFirstCondition) {
        jpql += " where";
        isFirstCondition = false;
      } else {
        jpql += " and";
      }
      jpql += " m.name like :name";
    }

    TypedQuery<Order> query = entityManager.createQuery(jpql, Order.class)
        .setMaxResults(1000);

    if (orderSearch.getOrderStatus() != null) {
      query = query.setParameter("status", orderSearch.getOrderStatus());
    }

    if (StringUtils.hasText(orderSearch.getMemberName())) {
      query = query.setParameter("name", orderSearch.getMemberName());
    }

    return query.getResultList();
  }

  // public List<Order> findAllByCriteria(OrderSearch orderSearch) {
  //   CriteriaBuilder cb = entityManager.getCriteriaBuilder();
  //   CriteriaQuery<Order> cq = cb.createQuery(Order.class);
  //   Root<Order> o = cq.from(Order.class);
  //   JoIn<Order, Member> m = o.join("member", JoinType.INNER);

  //   List<Predicate> criteria = new ArrayList<>();

  //   if (orderSearch.getOrderStatus() != null) {
  //     Predicate status =
  //       cb.equal(o.get("status"), orderSearch.getOrderStatus());
  //     criteria.add(status);
  //   }

  //   if (StringUtils.hasText(orderSearch.getMemberName())) {
  //     Predicate name =
  //       cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName() + "%");
  //     criteria.add(name);
  //   }

  //   cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
  //   TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);
  //   return query.getResultList();
  // }

  // public List<Order> findAllByQueryDSL(OrderSearch orderSearch) {
  //   Order order = QOrder.order;
  //   QMember member = QMember.member;

  //   return query
  //           .select(order)
  //           .from(order)
  //           .join(order.member, member)
  //           .where(
  //             statusEqal(orderSearch.getOrderStatus()),
  //             nameLike(orderSearch.getMemberName())
  //           )
  //           .limit(1000)
  //           .fetch();
  // }

  // private BooleanExpression statusEqal(OrderStatus orderStatus) {
  //   if (orderStatus == null) {
  //     return null;
  //   }
  //   return order.status.eq(orderStatus);
  // }

  // private BooleanExpression nameLike(String name) {
  //   if (!StringUtils.hasText(name)) {
  //     return null;
  //   }
  //   return order.name.like(name);
  // }

  public List<Order> findAllWithMemberDelivery() {
    String jpql = 
      "select o " +
      "from Order o " +
        "join fetch o.member m " +
        "join fetch o.delivery d";

    return entityManager.createQuery(jpql, Order.class)
            .getResultList();
  }

  public List<Order> findAllItems() {
    String jpql = 
      "select distinct o " +
      "from Order o " +
        "join fetch o.member m " +
        "join fetch o.delivery d " +
        "join fetch o.orderItems oi " +
        "join fetch oi.item i";

    return entityManager.createQuery(jpql, Order.class)
                          .getResultList();
  }

  public List<Order> findAllItemsWithPaging(int offset, int limit) {
    String jpql = 
      "select o " +
      "from Order o " +
        "join fetch o.member m " +
        "join fetch o.delivery d";

    return entityManager.createQuery(jpql, Order.class)
                          .setFirstResult(offset)
                          .setMaxResults(limit)
                          .getResultList();
  }
}
