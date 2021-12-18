package kitchenpos.application;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderLineItemDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.dto.OrderLineItemRequest;
import kitchenpos.dto.OrderRequest;
import kitchenpos.dto.OrderResponse;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    public static final LocalDateTime TEST_CREATED_AT = LocalDateTime.of(2021, 12, 1, 12, 0);
    @Mock
    private MenuDao menuDao;
    @Mock
    private OrderDao orderDao;
    @Mock
    private OrderLineItemDao orderLineItemDao;
    @Mock
    private OrderTableDao orderTableDao;
    @InjectMocks
    private OrderService orderService;

    @DisplayName("주문 생성")
    @Nested
    class CreateTest {
        @DisplayName("주문을 생성한다")
        @Test
        void testCreate() {
            // given
            OrderTable orderTable = new OrderTable(1L, null, 4, false);

            OrderLineItemRequest orderLineItemRequest1 = new OrderLineItemRequest(1L, 1);
            OrderLineItemRequest orderLineItemRequest2 = new OrderLineItemRequest(2L, 1);
            List<OrderLineItemRequest> orderLineItemRequests = Arrays.asList(orderLineItemRequest1, orderLineItemRequest2);
            OrderRequest orderRequest = new OrderRequest(orderTable.getId(), orderLineItemRequests);

            OrderLineItem orderLineItem1 = new OrderLineItem(orderLineItemRequest1.getMenuId(), orderLineItemRequest1.getQuantity());
            OrderLineItem orderLineItem2 = new OrderLineItem(orderLineItemRequest2.getMenuId(), orderLineItemRequest2.getQuantity());
            List<OrderLineItem> orderLineItems = Arrays.asList(orderLineItem1, orderLineItem2);
            Order expectedOrder = new Order(orderTable.getId(), orderLineItems);

            given(menuDao.countByIdIn(any(List.class))).willReturn((long) orderLineItems.size());
            given(orderTableDao.findById(anyLong())).willReturn(Optional.of(orderTable));
            given(orderDao.save(any(Order.class))).willReturn(expectedOrder);
            given(orderLineItemDao.save(any(OrderLineItem.class))).willReturn(orderLineItem1, orderLineItem2);

            // when
            OrderResponse order = orderService.create(orderRequest);

            // then
            assertThat(order).isEqualTo(OrderResponse.of(expectedOrder));
        }

        @DisplayName("주문 항목이 있어야 한다")
        @Test
        void requiredOrderItem() {
            // given
            OrderRequest orderRequest = new OrderRequest(1L, Collections.emptyList());

            // when
            ThrowableAssert.ThrowingCallable callable = () -> orderService.create(orderRequest);

            // then
            assertThatThrownBy(callable).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문 항목에 메뉴에 있는 메뉴만 있어야 한다")
        @Test
        void validateMenu() {
            // given
            OrderTable orderTable = new OrderTable(1L, null, 4, false);

            OrderLineItemRequest orderLineItemRequest1 = new OrderLineItemRequest(1L, 1);
            OrderLineItemRequest orderLineItemRequest2 = new OrderLineItemRequest(2L, 1);
            List<OrderLineItemRequest> orderLineItemRequests = Arrays.asList(orderLineItemRequest1, orderLineItemRequest2);
            OrderRequest orderRequest = new OrderRequest(orderTable.getId(), orderLineItemRequests);

            given(menuDao.countByIdIn(any(List.class))).willReturn(0L);

            // when
            ThrowableAssert.ThrowingCallable callable = () -> orderService.create(orderRequest);

            // then
            assertThatThrownBy(callable).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문 테이블이 있어야 한다")
        @Test
        void validateTable() {
            OrderTable orderTable = new OrderTable(1L, null, 4, false);

            OrderLineItemRequest orderLineItemRequest1 = new OrderLineItemRequest(1L, 1);
            OrderLineItemRequest orderLineItemRequest2 = new OrderLineItemRequest(2L, 1);
            List<OrderLineItemRequest> orderLineItemRequests = Arrays.asList(orderLineItemRequest1, orderLineItemRequest2);
            OrderRequest orderRequest = new OrderRequest(orderTable.getId(), orderLineItemRequests);

            given(menuDao.countByIdIn(any(List.class))).willReturn((long) orderLineItemRequests.size());
            given(orderTableDao.findById(anyLong())).willReturn(Optional.empty());

            // when
            ThrowableAssert.ThrowingCallable callable = () -> orderService.create(orderRequest);

            // then
            assertThatThrownBy(callable).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문 테이블이 비어있지 않아야 한다")
        @Test
        void notEmptyTable() {
            // given
            OrderTable orderTable = new OrderTable(1L, null, 4, true);

            OrderLineItemRequest orderLineItemRequest1 = new OrderLineItemRequest(1L, 1);
            OrderLineItemRequest orderLineItemRequest2 = new OrderLineItemRequest(2L, 1);
            List<OrderLineItemRequest> orderLineItemRequests = Arrays.asList(orderLineItemRequest1, orderLineItemRequest2);
            OrderRequest orderRequest = new OrderRequest(orderTable.getId(), orderLineItemRequests);

            given(menuDao.countByIdIn(any(List.class))).willReturn((long) orderLineItemRequests.size());
            given(orderTableDao.findById(anyLong())).willReturn(Optional.of(orderTable));

            // when
            ThrowableAssert.ThrowingCallable callable = () -> orderService.create(orderRequest);

            // then
            assertThatThrownBy(callable).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문을 생성하면 조리상태가 된다")
        @Test
        void testOrderStatus() {
            // given
            OrderTable orderTable = new OrderTable(1L, null, 4, false);

            OrderLineItemRequest orderLineItemRequest1 = new OrderLineItemRequest(1L, 1);
            OrderLineItemRequest orderLineItemRequest2 = new OrderLineItemRequest(2L, 1);
            List<OrderLineItemRequest> orderLineItemRequests = Arrays.asList(orderLineItemRequest1, orderLineItemRequest2);
            OrderRequest orderRequest = new OrderRequest(orderTable.getId(), orderLineItemRequests);

            OrderLineItem orderLineItem1 = new OrderLineItem(orderLineItemRequest1.getMenuId(), orderLineItemRequest1.getQuantity());
            OrderLineItem orderLineItem2 = new OrderLineItem(orderLineItemRequest2.getMenuId(), orderLineItemRequest2.getQuantity());
            List<OrderLineItem> orderLineItems = Arrays.asList(orderLineItem1, orderLineItem2);
            Order expectedOrder = new Order(1L, orderTable.getId(), OrderStatus.COOKING.name(), orderLineItems);

            given(menuDao.countByIdIn(any(List.class))).willReturn((long) orderLineItems.size());
            given(orderTableDao.findById(anyLong())).willReturn(Optional.of(orderTable));
            given(orderDao.save(any(Order.class))).willReturn(expectedOrder);
            given(orderLineItemDao.save(any(OrderLineItem.class))).willReturn(orderLineItem1, orderLineItem2);

            // when
            OrderResponse order = orderService.create(orderRequest);

            // then
            assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.COOKING.name());
        }
    }

    @DisplayName("주문상태 변경")
    @Nested
    class ChangeStatusTest {
        @DisplayName("주문 상태를 변경한다")
        @Test
        void testChangeOrderStatus() {
            // given
            OrderRequest requestOrder = new OrderRequest(OrderStatus.COMPLETION);
            Order savedOrder = new Order(1L, 1L, OrderStatus.COOKING.name(), TEST_CREATED_AT, Collections.emptyList());

            given(orderDao.findById(anyLong())).willReturn(Optional.of(savedOrder));

            // when
            OrderResponse order = orderService.changeOrderStatus(savedOrder.getId(), requestOrder);

            // then
            assertThat(order.getOrderStatus()).isEqualTo(requestOrder.getOrderStatus());
        }

        @DisplayName("생성된 주문이 있어야 한다")
        @Test
        void hasSavedOrder() {
            // given
            OrderRequest requestOrder = new OrderRequest(OrderStatus.COMPLETION);
            Order savedOrder = new Order(1L, 1L, OrderStatus.COOKING.name(), TEST_CREATED_AT, Collections.emptyList());

            given(orderDao.findById(anyLong())).willReturn(Optional.empty());

            // when
            ThrowableAssert.ThrowingCallable callable = () -> orderService.changeOrderStatus(savedOrder.getId(), requestOrder);

            // then
            assertThatThrownBy(callable).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("계산완료 상태에선 변경할 수 없다")
        @Test
        void canNotChangeWhenCompleteStatus() {
            // given
            OrderRequest requestOrder = new OrderRequest(OrderStatus.COMPLETION);
            given(orderDao.findById(anyLong())).willReturn(Optional.empty());

            // when
            ThrowableAssert.ThrowingCallable callable = () -> orderService.changeOrderStatus(anyLong(), requestOrder);

            // then
            assertThatThrownBy(callable).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("모든 주문을 조회한다")
    @Test
    void testList() {
        // given
        List<OrderLineItem> orderLineItems = Collections.emptyList();
        Order order = new Order(1L, 1L, OrderStatus.COOKING.name(), TEST_CREATED_AT, orderLineItems);
        List<Order> expectedOrders = Arrays.asList(order);

        given(orderDao.findAll()).willReturn(expectedOrders);
        given(orderLineItemDao.findAllByOrderId(anyLong())).willReturn(orderLineItems);

        // when
        List<OrderResponse> orders = orderService.list();

        // then
        assertThat(orders).isEqualTo(OrderResponse.ofList(expectedOrders));
    }
}
