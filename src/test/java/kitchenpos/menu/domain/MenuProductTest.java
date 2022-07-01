package kitchenpos.menu.domain;

import kitchenpos.common.domain.Name;
import kitchenpos.common.domain.Price;
import kitchenpos.common.domain.Quantity;
import kitchenpos.product.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class MenuProductTest {

    @Test
    @DisplayName("메뉴 상품 생성")
    void menuProduct() {
        // given
        Product 피자 = Product.of(Name.of("피자"), Price.of(BigDecimal.valueOf(17_000)));
        Quantity 수량 = Quantity.of(1);

        // when
        MenuProduct 메뉴_상품 = MenuProduct.of(피자, 수량);

        // then
        assertAll(
                () -> assertThat(메뉴_상품).isNotNull(),
                () -> assertThat(메뉴_상품.getProduct()).isEqualTo(피자),
                () -> assertThat(메뉴_상품.getQuantity()).isEqualTo(수량.getQuantity())
        );
    }

    @Test
    @DisplayName("메뉴 상품 합계 테스트")
    void getTotalPrice() {
        // given
        Product 피자 = Product.of(Name.of("피자"), Price.of(BigDecimal.valueOf(17_000)));
        Quantity 수량 = Quantity.of(10);

        MenuProduct 메뉴_상품 = MenuProduct.of(피자, 수량);

        // when & then
        assertThat(메뉴_상품.getProductPrice()).isEqualTo(BigDecimal.valueOf(170_000));
    }
}
