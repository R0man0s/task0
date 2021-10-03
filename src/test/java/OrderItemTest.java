import enums.Products_status;
import models.Order;
import models.OrderItem;
import models.Product;
import org.junit.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OrderItemTest {
    private OrderItem orderItem;
    private Product product;
    private Order order;

    @Before
    public void setUp() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        product = new Product(1,"TEST",10, Products_status.IN_STOCK,sdf.format(d));
        order = new Order(1, 11, "confirmed", sdf.format(d));
        orderItem = new OrderItem(order, product, 3);

    }

    @Test
    public void GetProduct_CORRECT_TYPE(){
        Assert.assertSame(product.getClass(), orderItem.getProduct().getClass());
    }

    @Test
    public void GetOrder_CORRECT_TYPE(){
        Assert.assertSame(order.getClass(), orderItem.getOrder().getClass());
    }

}
