# 키친포스

## 🚀 1단계 - 테스트를 통한 코드 보호

### 요구 사항

- [X] kitchenpos 패키지의 코드를 보고 키친포스의 요구 사항을 README.md에 작성한다.
  - [X] 메뉴 목록 (menu-groups)
    - 메뉴 그룹 조회
    - 메뉴 그룹 등록
      - 이름 정보가 필수로 등록되어야함

  - [X] 메뉴 정보 (menus)
    - 메뉴 정보 등록
      - 이름은 필수로 지정해야한다
      - 가격은 없거나, 0원 이하일 수 없다.
      - 메뉴 그룹 ID가 필수로 존재해야한다.
      - 메뉴는 상품들의 가격의 합(수량*가격) 보다 크면 안됨
    - 메뉴 정보 조회

  - [X] 주문 정보 (orders)
    - 주문 정보 등록
      - 주문 등록 정보 (주문 테이블 ID, 주문 상품, 주문 개수)
    - 주문 정보 조회 (상태 포함)
    - 주문 상태 변경
      - 주문건이 없거나, 상태변겅이 불가능한 경우 제외

  - [X] 상품 정보 (products)
    - 상품 정보 등록 (이름, 가격)
      - 가격 0원 이상 필수
      - 상품의 이름 등록
    - 상품 정보 조회

  - [X] 주문 테이블
    - 주문 테이블 등록
      - 주문 테이블 등록시 초기에 테이블 그룹은 미등록
    - 주문 테이블 조회
    - 주문 테이블을 빈 테이블로 변경
      - 단체 테이블 그룹이 존재 한다면 변경 불가
      - COOKING, MEAL 상태 인경우 변경이 불가
    - 주문 테이블 인원 수 변경
      - 인원이 1명 이상
      - 주문 테이블이 존재해야함
      - 주문 테이블이 비어있지 않아야함

  - [X] 주문 테이블 그룹
    - 주문 테이블 등록
      - 주문 테이블이 없거나, 2개 미만일 수 없다.
      - 주문 테이블은 모두 존재하는 상태여야 한다.
      - 주문 테이블은 비어있지 않아야 하며, 다른 테이블 그룹에 속하면 안된다.
    - 주문 테이블 해지
      - COOKING, MEAL 상태가 포함되어 있는경우 해지할 수 없다.

- [] 정리한 키친포스의 요구 사항을 토대로 테스트 코드를 작성한다.
  - 모든 Business Object에 대한 테스트 코드를 작성한다.
  - @SpringBootTest를 이용한 통합 테스트 코드 또는 @ExtendWith(MockitoExtension.class)를 이용한 단위 테스트 코드를 작성한다.

### 프로그래밍 요구 사항
- Lombok은 그 강력한 기능만큼 사용상 주의를 요한다.

- 무분별한 setter 메서드 사용
  - 객체 간에 상호 참조하는 경우 무한 루프에 빠질 가능성 
  - Lombok 사용상 주의점(Pitfall)
  - *이번 과정에서는 Lombok 없이 미션을 진행해 본다.*

## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
| 상품 | product | 메뉴를 관리하는 기준이 되는 데이터 |
| 메뉴 그룹 | menu group | 메뉴 묶음, 분류 |
| 메뉴 | menu | 메뉴 그룹에 속하는 실제 주문 가능 단위 |
| 메뉴 상품 | menu product | 메뉴에 속하는 수량이 있는 상품 |
| 금액 | amount | 가격 * 수량 |
| 주문 테이블 | order table | 매장에서 주문이 발생하는 영역 |
| 빈 테이블 | empty table | 주문을 등록할 수 없는 주문 테이블 |
| 주문 | order | 매장에서 발생하는 주문 |
| 주문 상태 | order status | 주문은 조리 ➜ 식사 ➜ 계산 완료 순서로 진행된다. |
| 방문한 손님 수 | number of guests | 필수 사항은 아니며 주문은 0명으로 등록할 수 있다. |
| 단체 지정 | table group | 통합 계산을 위해 개별 주문 테이블을 그룹화하는 기능 |
| 주문 항목 | order line item | 주문에 속하는 수량이 있는 메뉴 |
| 매장 식사 | eat in | 포장하지 않고 매장에서 식사하는 것 |
