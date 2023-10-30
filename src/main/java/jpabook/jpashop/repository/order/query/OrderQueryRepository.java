package jpabook.jpashop.repository.order.query;

import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import jpabook.jpashop.domain.OrderItem;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
  
  private final EntityManager entityManager;

  /**
   * 컬렉션은 별도로 조회
   * Query: 루트 1번, 컬렉션 N 번
   * 단건 조회에서 많이 사용하는 방식
   */
  public List<OrderQueryDto> findOrderQueryDtos() {
    // 루트 조회(toOne 코드를 모두 한번에 조회)
    List<OrderQueryDto> orders = findOrders();

    // 루프를 돌면서 컬렉션 추가(추가 쿼리 실행)
    orders.forEach(order -> {
      List<OrderItemQueryDto> orderItems = findOrderItems(order.getOrderId());
      order.setOrderItems(orderItems);
    });
    return orders;
  }

  /**
   * 1:N 관계(컬렉션)를 제외한 나머지를 한번에 조회
   */
  private List<OrderQueryDto> findOrders() {
    String jpql =
      "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address) " +
      "from Order o " +
        "join o.member m " +
        "join o.delivery d";
    return entityManager.createQuery(jpql, OrderQueryDto.class).getResultList();
  }

  /**
   * 1:N 관계인 orderItems 조회
   */
  private List<OrderItemQueryDto> findOrderItems(Long orderId) {
    String jpql =
      "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count) " +
      "from OrderItem oi " +
        "join oi.item i " +
      "where oi.order.id = :orderId";
    return entityManager.createQuery(jpql, OrderItemQueryDto.class)
                          .setParameter("orderId", orderId)
                          .getResultList();
  }

  /**
   * 최적화
   * Query: 루트 1번, 컬렉션 1번
   * 데이터를 한꺼번에 처리할 때 많이 사용하는 방식
   *
   */
  public List<OrderQueryDto> findAllByDto_optimization() {
    // 루트 조회(toOne 코드를 모두 한번에 조회)
    List<OrderQueryDto> result = findOrders();

    // orderItem 컬렉션을 map 한번에 조회
    Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrdeItemMap(toOrderIds(result));

    // 루프를 돌면서 컬렉션 추가(추가 쿼리 실행 x)
    result.forEach(
      o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

    return result;
  }

  private List<Long> toOrderIds(List<OrderQueryDto> result) {
    return result.stream()
                  .map(o -> o.getOrderId())
                  .collect(Collectors.toList());
  }

  private Map<Long, List<OrderItemQueryDto>> findOrdeItemMap(List<Long> orderIds) {
    String jpql =
      "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count) " +
      "from OrderItem oi " +
        "join oi.item i " +
      "where oi.order.id in :orderIds";
    
    List<OrderItemQueryDto> orderItems =
      entityManager.createQuery(jpql, OrderItemQueryDto.class)
                    .setParameter("orderIds", orderIds)
                    .getResultList();

    return orderItems.stream()
            .collect(Collectors.groupingBy(OrderItemQueryDto::getOrderId));
  }

  public List<OrderFlatDto> findAllByDto_flat() {
    String jpql =
      "select new jpabook.jpashop.repository.order.query.OrderFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count) " +
      "from Order o " +
        "join o.member m " +
        "join o.delivery d " +
        "join o.orderItems oi " +
        "join oi.item i";

    return entityManager.createQuery(jpql, OrderFlatDto.class).getResultList();
  }
}
