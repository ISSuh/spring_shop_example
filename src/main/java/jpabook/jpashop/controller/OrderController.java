package jpabook.jpashop.controller;

import java.util.List;

import javax.websocket.server.PathParam;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;
  private final MemberService memberService;
  private final ItemService itemService;

  @GetMapping("/order")
  public String createForm(Model model) {
    List<Member> findMembers = memberService.findMembers();
    List<Item> findItems = itemService.findItems();

    model.addAttribute("members", findMembers);
    model.addAttribute("items", findItems);
    return "order/orderForm";
  }


  @PostMapping("/order")
  public String order(
    @RequestParam("memberId") Long memberId,
    @RequestParam("itemId") Long itemId,
    @RequestParam("count") int count) {
    orderService.order(memberId, itemId, count);
    return "redirect:/orders";
  }

  @GetMapping("/orders")
  public String orderList(
    @ModelAttribute("orderSearch") OrderSearch orderSearch,
    Model model) {
    List<Order> findOrders = orderService.findOrders(orderSearch);
    model.addAttribute("orders", findOrders);
    return "order/orderList";
  }

  @PostMapping("/orders/{orderId}/cancel")
  public String cancelOrde(@PathVariable("orderId") Long orderId) {
    orderService.cancelOrder(orderId);
    return "redirect:/orders";
  }
}
