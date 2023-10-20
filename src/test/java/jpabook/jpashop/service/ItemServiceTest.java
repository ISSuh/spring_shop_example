package jpabook.jpashop.service;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ItemServiceTest {
  
  @Autowired private ItemService service;

  @Test
  public void registItem() {
    // given
    Item item = new Item();

    // where
    service.saveItem(item);

    // then
    Item findItem = service.findOne(item.getId());
    Assertions.assertThat(findItem).isEqualTo(item);
  }

  @Test
  public void quantityException() {
    // given
    Item item = new Item();

    // wherer
    item.addStock(5);

    // then
    Assertions.assertThatThrownBy(
      () -> item.removeStock(10)).isInstanceOf(NotEnoughStockException.class);
  }
}
