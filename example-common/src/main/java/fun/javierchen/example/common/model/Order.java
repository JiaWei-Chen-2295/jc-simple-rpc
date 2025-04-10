package fun.javierchen.example.common.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class Order implements Serializable {
    private long id;
    private BigDecimal price;

    public Order() {
    }

    public Order(long id, BigDecimal price) {
        this.id = id;
        this.price = price;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
