package kitchenpos.product.domain;

import kitchenpos.common.domain.Name;
import kitchenpos.common.domain.Price;
import org.springframework.util.Assert;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Name name;

    @Embedded
    private Price price;

    protected Product() {
    }

    private Product(Long id, Name name, Price price) {
        Assert.notNull(name, "상품 이름은 반드시 존재해야 합니다.");
        Assert.notNull(price, "상품 가격은 반드시 존재해야 합니다.");

        this.id = id;
        this.name = name;
        this.price = price;
    }

    public static Product of(Long id, Name name, Price price) {
        return new Product(id, name, price);
    }

    public static Product of(Name name, Price price) {
        return new Product(null, name, price);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getName();
    }

    public BigDecimal getPrice() {
        return price.getPrice();
    }
}
