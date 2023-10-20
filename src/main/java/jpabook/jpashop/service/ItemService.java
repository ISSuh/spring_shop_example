package jpabook.jpashop.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
  
  private final ItemRepository repository;

  @Transactional
  public void saveItem(Item item) {
    repository.save(item);
  }

  public Item findOne(Long itemId) {
    return repository.findOne(itemId);
  }

  public List<Item> findItems() {
    return repository.findAll();
  }
}
