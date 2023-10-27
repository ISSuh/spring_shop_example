package jpabook.jpashop.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * xToOne(ManyToOne, OneToOne) 관계 최적화
 * Order
 * Order -> Member
 * Order -> Delivery
 *
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

  private final OrderRepository repository;
 
  /**
   * V1. 엔티티 직접 노출
   * - Hibernate5Module 모듈 등록, LAZY=null 처리
   * - 양방향 관계 문제 발생 -> @JsonIgnore
   * 
   * 엔티티를 직접 노출할 때는 양방향 연관관계가 걸린 곳은 꼭! 한곳을  @JsonIgnore 처리 해야 한다. 
   * 안그러면 양쪽을 서로 호출하면서 무한 루프가 걸린다
   * 
   */
  @GetMapping("/api/v1/simple-orders")
  public List<Order> ordersV1() {
    List<Order> allOrders = repository.findAllByString(new OrderSearch());
    for (Order order : allOrders) {
      // // lazy 강제 초기화
      order.getMember().getName();

      // // lazy 강제 초기화
      order.getDelivery().getAddress();
    }
    return allOrders;
  }

  /**
   * V2. 엔티티를 조회해서 DTO로 변환(fetch join 사용X)
   * - 단점: 지연로딩으로 쿼리 N번 호출
   */
  @GetMapping("/api/v2/simple-orders")
  public Result<List<SimpleOrderDto>> ordersV2() {
    log.info("OrderSimpleApiController.ordersV2");
    List<Order> orders = repository.findAllByString(new OrderSearch());
    List<SimpleOrderDto> simpleOrderDtoes =
      orders.stream()
        .map(order -> new SimpleOrderDto(order))
        .collect(Collectors.toList());

    Result<List<SimpleOrderDto>> result = new Result<>(simpleOrderDtoes);
    return result;
  }
  
  @Data
  static class SimpleOrderDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;

    public SimpleOrderDto(Order order) {
      this.orderId = order.getId();

      // Lazy loading 초기화
      this.name = order.getMember().getName();
      this.orderDate = order.getOrderDate();
      this.orderStatus = order.getStatus();

      // Lazy loading 초기화
      this.address = order.getDelivery().getAddress();
    }
  }

  @Data
  static class Result<T> {
    private T data;

    public Result(T data) {
      this.data = data;
    }
  }
}
