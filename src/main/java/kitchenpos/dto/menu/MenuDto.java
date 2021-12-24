package kitchenpos.dto.menu;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import kitchenpos.domain.menu.Menu;
import kitchenpos.domain.menu.MenuProduct;

public class MenuDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private Long menuGroupId;
    private List<MenuProductDto> menuProducts;

    protected MenuDto() {
    }

    private MenuDto(Long id, String name, BigDecimal price, Long menuGroupId, List<MenuProductDto> menuProducts) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.menuGroupId = menuGroupId;

        if (menuProducts == null) {
            this.menuProducts = new ArrayList<>();
        }

        this.menuProducts = menuProducts;
    }

    public static MenuDto of(String name, BigDecimal price, Long menuGroupId, List<MenuProductDto> menuProducts) {
        return new MenuDto(null, name, price, menuGroupId, menuProducts);
    }

    public static MenuDto of(Menu menu) {
        if (menu.getMenuGroupId() == null) {
            return new MenuDto(menu.getId(), menu.getName(), BigDecimal.valueOf(menu.getPrice().value()), menu.getMenuGroupId().value(), null);
        }

        List<MenuProductDto> menuProductDtos = new ArrayList<>();

        for (int i = 0; i < menu.getMenuProducts().size(); i++ ){
            MenuProduct menuProduct = menu.getMenuProducts().get(i);
            menuProductDtos.add(MenuProductDto.of(menuProduct.getSeq(), menuProduct.getMenu().getId(),  menuProduct.getProductId().value(), menuProduct.getQuantity()));
        }
        
        return new MenuDto(menu.getId(), menu.getName(), BigDecimal.valueOf(menu.getPrice().value()), menu.getMenuGroupId().value(), menuProductDtos);
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public Long getMenuGroupId() {
        return this.menuGroupId;
    }
    public List<MenuProductDto> getMenuProducts() {
        return this.menuProducts;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof MenuDto)) {
            return false;
        }
        MenuDto menuDto = (MenuDto) o;
        return Objects.equals(id, menuDto.id) && Objects.equals(name, menuDto.name) && Objects.equals(price, menuDto.price) && Objects.equals(menuGroupId, menuDto.menuGroupId) && Objects.equals(menuProducts, menuDto.menuProducts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, menuGroupId, menuProducts);
    }
}
