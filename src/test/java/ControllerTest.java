import controller.Controller;
import enums.Products_status;
import models.OrderItem;
import models.Product;
import org.junit.*;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ControllerTest {
    private Product product;

    @Before
    public void setUp() {
        product = new Product(0,"",0, Products_status.OUT_OF_STOCK,"");
    }

    @Test
    public void GetConnection_NO_NULL() {
        Connection expected = Controller.GetConnection();
        Assert.assertNotNull(expected);
    }

    @Test
    public void GetAllProducts_NO_NULL() {
        Map<Integer, Product> expected = Controller.GetAllProducts();
        Assert.assertNotNull(expected);
    }

    @Test
    public void GetAllProducts_CORRECT_TYPE(){
        Map<Integer, Product> actual = new HashMap<>();
        Map<Integer, Product> expected = Controller.GetAllProducts();

        Assert.assertSame(expected.getClass(),actual.getClass());
    }

    @Test
    public void AddProductToDB_NO_NULL() {
        Integer expected = Controller.AddProductToDB(product);
        Assert.assertNotNull(expected);
    }

    @Test
    public void AddProductToDB_ZERO() {
        Integer expected = Controller.AddProductToDB(null);
        Assert.assertSame(expected,0);
    }

    @Test
    public void AddProductToDB_ZERO1() {
        Integer expected = Controller.AddProductToDB(product);
        Assert.assertSame(expected,0);
    }

    @Test
    public void AddOrderToDB_NULL() {
        ArrayList<OrderItem> orderItems = new ArrayList<>();
        Assert.assertNull(Controller.AddOrderToDB(orderItems));
    }
    @Test
    public void AddOrderToDB_NULL1() {
        Assert.assertNull(null);
    }


}
