package jpabook.jpashop;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.springframework.stereotype.Component;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Book;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InitDb {

  private final InitSerive initSerive;

  @PostConstruct
  public void init() {
    initSerive.dbInit1();
    initSerive.dbInit2();
  }

  @Component
  @Transactional
  @RequiredArgsConstructor
  static class InitSerive {
    private final EntityManager em;

    public void dbInit1() {
      Member member = createMember("userA", "seoul", "1", "1111");
      em.persist(member);

      Book book1 = createBook("testBook1", 10000, 100);
      em.persist(book1);
      
      Book book2 = createBook("testBook2", 20000, 100);
      em.persist(book2);

      Delivery delivery = createDelivery(member);
      OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
      OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);
      Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
      em.persist(order);
    }

    public void dbInit2() {
      Member member = createMember("userB", "busan", "2", "2222");
      em.persist(member);

      Book book1 = createBook("testtestBook1", 20000, 200);
      em.persist(book1);
      
      Book book2 = createBook("testtestBook2", 40000, 300);
      em.persist(book2);

      Delivery delivery = createDelivery(member);
      OrderItem orderItem1 = OrderItem.createOrderItem(book1, 20000, 3);
      OrderItem orderItem2 = OrderItem.createOrderItem(book2, 40000, 4);
      Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
      em.persist(order);
    }

    private Member createMember(String name, String city, String street, String zipcode) {
      Member member = new Member();
      member.setName(name);
      member.setAddress(new Address(city, street, zipcode));
      return member;
    }

    private Book createBook(String name, int price, int stockQuantity) {
      Book book = new Book();
      book.setName(name);
      book.setPrice(price);
      book.setStockQuantity(stockQuantity);
      return book;
    }

    private Delivery createDelivery(Member member) {
      Delivery delivery = new Delivery();
      delivery.setAddress(member.getAddress());
      return delivery;
    }
  }
}
