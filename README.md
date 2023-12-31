# spring_shop_example
**인프런의 [실전! 스프링 부트와 JPA 활용1(이영한)](https://www.inflearn.com/course/스프링부트-JPA-활용-1) 강의 예제 구현**

## 도메인 분석 설계

### 요구사항 분석

#### 기능 목록
  - 회원 기능
    - 회원 등록
    - 회원 조회
  - 상품 기능
    - 상품 등록
    - 상품 수정
    - 상품 조회
  - 주문 기능
    - 상품 주문
    - 주문 내역 조회
    - 주문 취소
  - 기타 요구사항
    - 상품은 재고 관리가 필요하다.
    - 상품의 종류는 도서, 음반, 영화가 있다.
    - 상품을 카테고리로 구분할 수 있다.
    - 상품 주문시 배송 정보를 입력할 수 있다

### 도메인 모델과 테이블 설계

#### 도메인 모델
![alt](https://www.plantuml.com/plantuml/png/NO-nRe8n38JtF8MLiH93zmwqPUc0El04WTyA4OaT1KEBr8Tlb43aiFo_VNTMlbkVnHMvf6WaI4ImkoDdm5-NSjqsd5mWsFA2CP2tnfnFWR8hBi61KsP2aftSQhJWGHpvYCOC1xQOzbYKy5sMQTzW35Sij-V8bs1IsjFwwGGzJrVlSN_HaG_camRXQwXadGViguNLmZ-q0ljYxr3jgwms3Zvjb-2FeauRtPYQFZ8rtsppO6wsUhoxxV3jh-kzpAprGhDqBxL6Mcgy_-8_)

**회원, 주문, 상품의 관계**
  - 회원은 여러 상품을 주문 할 수 있음
    - 한번 주문할 때 여러 상품을 선택할 수 있으므로 주문과 상품은 다대다 관계
  - 주문상품이라는 엔티티를 추가형 다개다 관계를 일대다, 다대일 관계로 수정

**상품 분류**
  - 상품은 도서, 음반, 영화로 구분되며 상품이라는 공통 속성을 사용하여 상속구조로 표현

#### 엔티티 분석
![alt](https://www.plantuml.com/plantuml/png/RL9DZzGm3BtdLqJbLcaEN8VHQe7bWBGhG8EuPnep4ssQLAwxC7pyTvmFbwYQI-Eyz_pisBsVoI0jerVao8FkZfSuQVXfnebl7mAZL_qKBNWNpfrIWi1u0jI_bTREwhr-ZE7CSJ0ZyEr8wFBTM8impmozBH5Z4IrWWXxTJEhlQlc9SN7aJz-FfSQ-5klxZERqZmIZM5GyTVW2U6NqeORCr78FXamY3053_6HQEBsKFEPRquYaIo_LG_GtdzMOIlhMLxJwSFXc_0BwwtM2kxjSPM0-Uo00SV3BJKEqq4v7djrEoANxNYTOfjDs-l_zdz4DaEh49RGpRmtBzlYOafWzNA3SKnoUlomc9oGBGt2EwE362jyNxhgnb5Ot6viCGY369AtYmyLvkqhR0VhJKjerI4no03GqWdSnFXT-eKl4rCHy2etWARuuo0hh40RA6fDF5gdwLtUlExspyKVOwQxlwjRvkw4ub5qh9P7UUVXEktNPhmXDcCjJttWNODy0gdL5TxwGLApBLcl4UADXVQQLZ8xffcTXS-BXJy7B-3POcjW6oZDIwXw2PNav_3y0)

  - **회원(Member)**
    - 이름과 입베디드 타입인 주소와 주문 리스트를 가짐
  - **주문(Order)**
    - 한번 주문시 여러 상품을 주문할 수 있으르모 주문과 주문상품은 일대다 관계
    - 주문은 상품을 주만한 회원과 배송정보, 주문 날짜, 주문 상태를 가짐
    - 주문 상태는 열거형이며 주문(ORDER), 취소(CANCEL)를 표현할 수 있음
  - **주문상품(OrderItem)**
    - 주문한 상품정보와 주문금액, 주문 수량 정보를 가짐
  - **상품(Item)**
    - 이름, 가격, 재고수향을 가짐
  - **배송(Delivery)**
    - 주문시 하나의 배송 정보를 생성
    - 주문과 배송은 일대일 관계
  - **카테고리(Category)**
    - 상품과 다대다 관계
    - parent, child로 부모, 자식 카테고리를 연결
  - **주소(Address)**
    - 임베디드 타입이며 회원과 배송에서 사용됨

> 회원이 주문을 하기 떄문에 회원이 주문리스트를 가지는 것은 얼핏 보면 잘 설계한 것 같지만, 객체세상은 실제 세계와는 다름.
> 실무에서는 회원이 주문을 참조하지 않고, 주문이 회원을 참조하는 것으로 충분함.

#### 테이블 설계
![alt](https://www.plantuml.com/plantuml/png/ZLBDRfj04BxlKqpTYx5gFq4KPGFh5gM0kumhkHUBb8c3oay4f2KAUVVkGXSsgLpL1y_2pFVp3NlLj4dTFXOvBDhaDgTZKQMKP-LfmTgipGaMyNtr0DGbnODwGZq37ZNPgLmmzYwbkwma6A3Bpe7lUT8qqC751Ipnm_ljbgkV0vUNq-XTLPC27t_GamQiLXqijpVEQiNOo19GSKkr0yyC8131jP17trFdzNgj_aCU25LS7m-gn2Y5G7Nuvk_So1FinR14TKfrCx94qdjDOdkO99mt3KzyzhyAUT0jG-9n58CintqyYtbgOp-fVXhbPg1HD8Z_DkwtLBpoZY8u5y2aU-jVOsplE-cxm-gYVOYptYnbYTYhDhWO8_Vcovw7E4RmyB3Jxxb4FzPH1BhwSO-V8gdfukjGZ_bIk3YyuRhEocxIqgaoMtCvYe-HF9oBkUDIXAW36Mj_4SrHRA0X_iUADDLuxQ3lDsdrgznKpp1U8JQMgT7tO3utitfrThflD_qVb7R2fXEyLARVmz1YnhAjPyUO1Wn-r3RmAohJn-8t)

  - **Member**
    - 회원 엔티티의 Address 임베디드 타입 정보가 회원 테이블에 그대로 들어감
  - **Item**
    - 앨범, 도서, 영화 타입을 통하여 하나의 테이블로 구성
    - DTYPE 컬럼으로 타입을 구분

#### 연관관계 매핑 분석
  - **회원과 주문**
    - 일대다, 다대일의 양방향 관계
    - 외래 키가 있는 주문을 연관관계의 주인으로 정하는 것이 좋음
    - 그러므로 Order.member를 Orders.MEMBER_ID 외래 키와 매핑
  - **주문상품과 주문**
    - 다대일 양방향 관계
    - 외래 키가 주문상품에 있으므로 주문상품이 연관관계의 주인
    - 그러므로 OrderItem.order를 OrderItem.ORDER_ID 외래 키와 매핑
  - **주문살품과 상품**
    - 다대일 단방향 관계
    - OrderItem.item을 OrderItem.ITEM_ID 외래 키와 매핑
  - **주문과 배송**
    - 일대일 양방향 관계
    - Order.delivery를 Orders.DELIVERY_ID 외래 키와 매핑
  - **카테고리와 상품**
    - @ManyToMany를 사용하여 매핑
    - 실무에서는 @ManyToMany를 사용하지 말것

## 어플리케이션 아키텍처
![alt](http://www.plantuml.com/plantuml/png/TOr1JiGm303lVeNLErz0ks9VG5zWKekrIEp8SO7-dXogDZrmYZDZZMyjo6Cj6IVoz9JW5Alp110IcN6QdrQHVwdK_hlNcYLHY2dUh-ljIxIIKy5afPgonXnRPlI-GlgP6U0m-6OQRZcp3t1c_vR40tddQat2V1lWmmg9ma917zGO7_i0S5RnvVN8xMz7O-ySk_YdiTYFThaVF3fNF8Qk1cwiorh-0000)

  - 계층형 구조 사용
    - controller : 웹 계층
    - service : 비지니스 로직 및 트랜잭션 처리
    - repository : JPA를 직접 사용하는 계층. 엔티티 매니저 사용
    - domin : 엔티티가 모여있는 계층. 모든 계층에서 사용
  - 패키지 구조
    - jpabook.jpashop
      - domin
      - exception
      - repository
      - service
