package kitchenpos.application.table;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kitchenpos.application.order.OrderService;
import kitchenpos.domain.order.OrderStatus;
import kitchenpos.domain.order.Orders;
import kitchenpos.domain.table.OrderTable;
import kitchenpos.domain.table.OrderTableRepository;
import kitchenpos.domain.table.OrderTables;
import kitchenpos.domain.table.TableGroup;
import kitchenpos.domain.table.TableGroupRepository;
import kitchenpos.dto.table.OrderTableDto;
import kitchenpos.dto.table.TableGroupDto;
import kitchenpos.exception.table.HasOtherTableGroupException;
import kitchenpos.exception.table.NotGroupingOrderTableCountException;
import kitchenpos.exception.table.NotRegistedMenuOrderTableException;

@ExtendWith(MockitoExtension.class)
public class TableGroupServiceTest {
    @Mock
    private OrderService orderService;

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private TableGroupRepository tableGroupRepository;

    @InjectMocks
    private TableGroupService tableGroupService;

    @DisplayName("단체지정이 저장된다.")
    @Test
    void create_tableGroup() {
        // given
        OrderTable 치킨_주문_단체테이블 = OrderTable.of(0, true);
        OrderTable 치킨2_주문_단체테이블 = OrderTable.of(0, true);

        List<OrderTable> 조회된_주문테이블_리스트 = List.of(치킨_주문_단체테이블, 치킨2_주문_단체테이블);

        when(orderTableRepository.findAllByIdIn(anyList())).thenReturn(조회된_주문테이블_리스트);
        when(tableGroupRepository.save(any(TableGroup.class))).thenReturn(TableGroup.of(OrderTables.of(List.of(OrderTable.of(0, true), OrderTable.of(0, true)))));

        TableGroupDto 단체지정_요청전문 = TableGroupDto.of(List.of(OrderTableDto.of(치킨_주문_단체테이블), OrderTableDto.of(치킨2_주문_단체테이블)));

        // when
        TableGroupDto createdTableGroup = tableGroupService.create(단체지정_요청전문);

        // then
        Assertions.assertThat(createdTableGroup).isNotNull();
    }

    @DisplayName("주문테이블의 개수가 2개 미만으로 단체지정시 예외가 발생된다.")
    @Test
    void exception_createTableGoup_underTwoCountOrderTable() {
        // given
        OrderTable 치킨_주문_단체테이블 = OrderTable.of(0, true);
        

        List<OrderTable> 조회된_주문테이블_리스트 = List.of(치킨_주문_단체테이블);

        when(orderTableRepository.findAllByIdIn(anyList())).thenReturn(조회된_주문테이블_리스트);

        // when
        // then
        Assertions.assertThatExceptionOfType(NotGroupingOrderTableCountException.class)
                    .isThrownBy(() -> tableGroupService.create(TableGroupDto.of(List.of(OrderTableDto.of(2)))));
    }

    @DisplayName("미존재 주문테이블가 포함된 단체지정으로 저장시 예외가 발생된다.")
    @Test
    void exception_createTableGoup_containNotExistOrderTable() {
        // given
        OrderTable 치킨_주문_단체테이블 = OrderTable.of(0, true);
        OrderTable 치킨2_주문_단체테이블 = OrderTable.of(0, true);
        TableGroup 단체주문테이블 = TableGroup.of(OrderTables.of(List.of(치킨_주문_단체테이블, 치킨2_주문_단체테이블)));
        
        List<OrderTable> 조회된_주문테이블_리스트 = List.of(치킨_주문_단체테이블);

        when(orderTableRepository.findAllByIdIn(anyList())).thenReturn(조회된_주문테이블_리스트);

        // when
        // then
        Assertions.assertThatExceptionOfType(NotRegistedMenuOrderTableException.class)
                    .isThrownBy(() -> tableGroupService.create(TableGroupDto.of(단체주문테이블)));
    }

    @DisplayName("단체지정 될 주문테이블이 이미 단체지정에 등록된 경우 예외가 발생된다.")
    @Test
    void exception_createTableGoup_existOrderTableInOtherTableGroup() {
        // given
        OrderTable 치킨_주문_단체테이블 = OrderTable.of(0, true);
        OrderTable 치킨2_주문_단체테이블 = OrderTable.of(0, true);
        OrderTable 치킨3_주문_단체테이블 =  OrderTable.of(0, true);

        TableGroup 단체주문테이블 = TableGroup.of(OrderTables.of(List.of(치킨_주문_단체테이블, 치킨2_주문_단체테이블)));

        List<OrderTable> 조회된_주문테이블_리스트 = List.of(치킨2_주문_단체테이블, 치킨3_주문_단체테이블);

        when(orderTableRepository.findAllByIdIn(anyList())).thenReturn(조회된_주문테이블_리스트);

        // when
        // then
        Assertions.assertThatExceptionOfType(HasOtherTableGroupException.class)
                    .isThrownBy(() -> tableGroupService.create(TableGroupDto.of(단체주문테이블)));
    }

    @DisplayName("단체지정이 해제된다.")
    @Test
    void update_tableUnGroup() {
        // given
        OrderTable 치킨_주문_단체테이블 = OrderTable.of(10, false);
        OrderTable 치킨2_주문_단체테이블 = OrderTable.of(10, false);

        Orders 주문  = Orders.of(치킨_주문_단체테이블, OrderStatus.COMPLETION);
        Orders 주문2  = Orders.of(치킨2_주문_단체테이블, OrderStatus.COMPLETION);

        치킨_주문_단체테이블.changeEmpty(true, 주문);
        치킨2_주문_단체테이블.changeEmpty(true, 주문2);

        TableGroup 단체주문테이블 = TableGroup.of(OrderTables.of(Lists.newArrayList(치킨_주문_단체테이블, 치킨2_주문_단체테이블)));
        List<OrderTable> 조회된_주문테이블_리스트 = List.of(치킨_주문_단체테이블, 치킨2_주문_단체테이블);

        when(orderTableRepository.findAllByTableGroupId(nullable(Long.class))).thenReturn(조회된_주문테이블_리스트);
        when(orderService.findAllByOrderTableIdIn(anyList())).thenReturn(List.of(주문, 주문2));
        
        // when
        tableGroupService.ungroup(단체주문테이블.getId());

        // then
        Assertions.assertThat(단체주문테이블.getOrderTables().size()).isEqualTo(0);
    }
}
