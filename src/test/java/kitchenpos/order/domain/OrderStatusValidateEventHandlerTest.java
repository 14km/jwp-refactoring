package kitchenpos.order.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import kitchenpos.common.exception.KitchenposException;
import kitchenpos.table.domain.OrderStatusValidateEvent;

@ExtendWith(MockitoExtension.class)
class OrderStatusValidateEventHandlerTest {
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderStatusValidateEventHandler orderStatusValidateEventHandler;

    @DisplayName("완료 상태일 때 정상 실행")
    @Test
    void handle() {
        // given
        Mockito.when(orderRepository.existsByOrderTableIdAndOrderStatusIn(Mockito.anyLong(), Mockito.anyList()))
            .thenReturn(false);

        OrderStatusValidateEvent event = new OrderStatusValidateEvent(1L);

        // when and then
        assertThatCode(() -> orderStatusValidateEventHandler.handle(event))
            .doesNotThrowAnyException();
    }

    @DisplayName("MEAL 또는 COOKING 존재시 에러")
    @Test
    void handleFail() {
        // given
        Mockito.when(orderRepository.existsByOrderTableIdAndOrderStatusIn(Mockito.anyLong(), Mockito.anyList()))
            .thenReturn(true);

        OrderStatusValidateEvent event = new OrderStatusValidateEvent(1L);

        // when and then
        assertThatExceptionOfType(KitchenposException.class)
            .isThrownBy(() -> orderStatusValidateEventHandler.handle(event))
            .withMessage("사용중인 테이블이 있습니다.");
    }
}