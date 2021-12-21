package kitchenpos.dto.tablegroup;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import kitchenpos.domain.tablegroup.TableGroup;
import kitchenpos.dto.table.OrderTableDto;

public class TableGroupDto {
    private Long id;
    private LocalDateTime createdDate;
    private List<OrderTableDto> orderTables;

    protected TableGroupDto() {
    }

    private TableGroupDto(Long id, LocalDateTime createdDate, List<OrderTableDto> orderTables) {
        this.id = id;
        this.createdDate = createdDate;

        if (orderTables == null) {
            this.orderTables = new ArrayList<>();
            return;
        }

        this.orderTables = orderTables;
    }

    public static TableGroupDto of(List<OrderTableDto> orderTables) {
        return new TableGroupDto(null, null, orderTables);
    }

    public static TableGroupDto of(TableGroup tableGroup) {
        if (tableGroup.getOrderTables() == null) {
            return new TableGroupDto(tableGroup.getId(), tableGroup.getCreatedDate(), null);
        }

        List<OrderTableDto> tempOrderTables = new ArrayList<>();

        for (int index = 0; index < tableGroup.getOrderTables().size(); index++ ) {
            tempOrderTables.add(OrderTableDto.of(tableGroup.getOrderTables().get(index)));
        }

        return new TableGroupDto(tableGroup.getId(), tableGroup.getCreatedDate(), tempOrderTables);
    }

    public Long getId() {
        return this.id;
    }

    public List<OrderTableDto> getOrderTables() {        
        return this.orderTables;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof TableGroupDto)) {
            return false;
        }
        TableGroupDto tableGroupDto = (TableGroupDto) o;
        return Objects.equals(id, tableGroupDto.id) && Objects.equals(createdDate, tableGroupDto.createdDate) && Objects.equals(orderTables, tableGroupDto.orderTables);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdDate, orderTables);
    }
}
