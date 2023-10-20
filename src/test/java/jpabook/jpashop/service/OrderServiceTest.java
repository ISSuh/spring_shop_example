package jpabook.jpashop.service;

import static org.junit.Assert.fail;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {
  
  @PersistenceContext
  EntityManager em;

  @Autowired OrderRepository repository;
  @Autowired OrderService service;

  @Test
  public void orderItem() {
    // given
    Member member = createMember();
    Item item = creataeBook("JPA", 10000, 10);
    int orderCount = 2;

    // where
    Long orderId = service.order(member.getId(), item.getId(), orderCount);

    // then
    Order order = repository.findOne(orderId);

    Assertions.assertThat(order.getStatus()).isEqualTo(OrderStatus.ORDER);
    Assertions.assertThat(order.getOrderItems().size()).isEqualTo(1);
    Assertions.assertThat(order.getTotalPrice()).isEqualTo(10000 * orderCount);
    Assertions.assertThat(item.getStockQuantity()).isEqualTo(10 - orderCount);
  }

  @Test
  public void overStock() {
    // given
    Member member = createMember();
    Item item = creataeBook("JPA", 10000, 10);
    int orderCount = 100;

    // where
    // then
    Assertions.assertThatThrownBy(
      () -> service.order(member.getId(), item.getId(), orderCount))
        .isInstanceOf(NotEnoughStockException.class);
  }

  @Test
  public void cancelOrder() {
    // given
    Member member = createMember();
    Item item = creataeBook("JPA", 10000, 10);
    int orderCount = 2;
    Long orderId = service.order(member.getId(), item.getId(), orderCount);

    // where
    service.cancelOrder(orderId);

    // then
    Order order = repository.findOne(orderId);
    Assertions.assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCEL);
    Assertions.assertThat(item.getStockQuantity()).isEqualTo(10);
  }

  private Member createMember() {
    Member member = new Member();
    member.setName("user1");
    member.setAddress(new Address("korea", "mount", "111-111"));
    em.persist(member);
    return member;
  }

  private Book creataeBook(String name, int price, int stockQuantity) {
    Book book = new Book();
    book.setName(name);
    book.setPrice(price);
    book.setStockQuantity(stockQuantity);
    em.persist(book);
    return book;
  }

}
