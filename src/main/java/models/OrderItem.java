package models;

public class OrderItem {
    private Order order;
    private Product product;
    private int quantity;

    public Order getOrder() {
        return order;
    }
    public Product getProduct() {
        return product;
    }
    public int getQuantity() {
        return quantity;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public OrderItem(Order order, Product product, int quantity) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
    }
}
