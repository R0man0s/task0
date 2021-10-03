package controller;

import enums.Products_status;
import models.Order;
import models.OrderItem;
import models.Product;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class Controller {
    public static Connection GetConnection() {
        String driver;
        String url;
        String username;
        String password;

        Connection conn;

        Properties property = new Properties();

        try {

            String propFileName = "config.properties";

            InputStream inputStream = Controller.class.getClassLoader().getResourceAsStream(propFileName);

            property.load(inputStream);

            driver = property.getProperty("db.driver");
            url = property.getProperty("db.url");
            username = property.getProperty("db.username");
            password = property.getProperty("db.password");

            inputStream.close();

        } catch (IOException e) {
            System.err.println("Error: properties file does not exist!");
            return null;
        }

        try{

            Class.forName(driver);

            conn = DriverManager.getConnection(url,username,password);
            //System.out.println("    Connected database successfully...");
        } catch (Exception e) {
            System.out.printf("getConnection(): connection error- %s",e.getMessage());
            System.out.printf("getConnection(): connection error- %s",e.getMessage());
            return null;
        }

        return conn;
    }

    public static void CreateDBTables(){

        Connection conn = GetConnection();
        if (conn == null) {
            return;
        }
        try{
            System.out.println("Creating table in given database if not exists...");
            Statement stmt = conn.createStatement();

            String sql = "CREATE TABLE IF NOT EXISTS products"+
                    "(id INT auto_increment primary key, " +
                    " name VARCHAR(255) NOT NULL, " +
                    " price INT NOT NULL, " +
                    " status ENUM('OUT_OF_STOCK','IN_STOCK','RUNNING_LOW') NOT NULL, " +
                    " created_at DATETIME NOT NULL)";

            stmt.executeUpdate(sql);

            sql = "CREATE TABLE IF NOT EXISTS orders"+
                    "(id INT auto_increment primary key, " +
                    " user_id INT NOT NULL, " +
                    " status VARCHAR(255) NOT NULL, " +
                    " created_at DATETIME NOT NULL)";

            stmt.executeUpdate(sql);

            sql = "CREATE TABLE IF NOT EXISTS order_items"+
                    "(order_id INT NOT NULL, " +
                    " product_id INT NOT NULL, " +
                    " quantity INT NOT NULL, " +
                    " FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE," +
                    " FOREIGN key (product_id) REFERENCES products (id) ON DELETE CASCADE)";

            stmt.executeUpdate(sql);
            stmt.close();
            conn.close();


        } catch (Exception e) {
            System.out.printf("createDBTables(): error- %s",e.getMessage());
        }

    }

    public static void ShowAllProducts() {
        Connection conn = GetConnection();
        if (conn == null) {
            return;
        }
        try{
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery("select id, name, price, status from products");

            System.out.println("\n#    Product name       Price       Status");

            while(rs.next()){
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int price = rs.getInt("price");
                Products_status status = Products_status.valueOf(rs.getString("status"));

                System.out.println(""+ id + "    "+ name + "               " + price + "          " + status);

            }

            stmt.close();
            conn.close();

        } catch (Exception e) {
            System.out.printf("showAllProducts(): error- %s",e.getMessage());
        }

    }

    public static void ShowAllOrderedProducts() {
        Connection conn = GetConnection();
        if (conn == null) {
            return;
        }
        try{
            Statement stmt = conn.createStatement();

            String sql = "SELECT name, created_at, quantity, SUM(quantity) AS total_qnt "+
                    " FROM order_items" +
                    " LEFT JOIN products ON product_id = products.id" +
                    " GROUP BY name" +
                    " ORDER BY total_qnt DESC, name ASC";

            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("\nProduct_Name     Total_Quantity");

            while(rs.next()){
                String name = rs.getString("name");
                int total_qnt = rs.getInt("total_qnt");

                System.out.println(""+name + "      " + total_qnt);

            }

            stmt.close();
            conn.close();

        } catch (Exception e) {
            System.out.printf("ShowAllOrderedProducts(): error- %s", e.getMessage());
        }

    }


    public static void DeleteProductsFromDB(Integer prod_id) {
        if (prod_id == null){
            return;
        }
        Connection conn = GetConnection();
        if (conn == null) {
            return;
        }
        try{
            Statement stmt = conn.createStatement();
            String cond = "";
            String msg = "\nAll Products were successfully deleted from DB";

            if (prod_id > 0) {
                cond = " WHERE id = " +prod_id;
                msg = String.format("\nProduct #%s was successfully deleted from DB!", prod_id);
            }

            String sql = String.format("DELETE FROM products%s", cond);

            int rows = stmt.executeUpdate(sql);

            if (rows > 0) {
                System.out.println(msg);
            }
            stmt.close();
            conn.close();

        } catch (Exception e) {
            System.out.printf("DeleteProductsFromDB(): error- %s", e.getMessage());
        }
    }

    public static void ShowOrders(Integer order_id) {
        Connection conn = GetConnection();
        if (conn == null) {
            return;
        }
        try{
            Statement stmt = conn.createStatement();
            String cond = "";
            String err = "Orders were not found!\n";

            if (order_id > 0) {
                cond = String.format("AND orders.id = %s", order_id);
                err = String.format("Order was not found by ID #%s!", order_id);
            }

            String sql = "SELECT orders.id AS order_id, orders.created_at AS order_crt," +
                    " products.name AS product_name, order_items.quantity, products.price" +
                    " FROM orders" +
                    " LEFT JOIN order_items ON orders.id = order_id" +
                    " LEFT JOIN products ON product_id = products.id" +
                    " WHERE order_items.quantity > 0 " + cond +
                    " ORDER BY orders.id DESC, products.name ASC";

            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("\nOrder_id     Product_Name    Product_Price    Products_Quantity   Total_Price   Order_Created");
            boolean isResult = false;

            while(rs.next()){
                int c_ord_id = rs.getInt("order_id");
                int pr_quantity = rs.getInt("quantity");
                int pr_price = rs.getInt("price");
                int pr_ord_price = pr_quantity * pr_price;
                String pr_name = rs.getString("product_name");
                String ord_created = rs.getString("order_crt");

                System.out.println(""+c_ord_id + "          " + pr_name + "                " + pr_price + "                   " + pr_quantity + "               "+ pr_ord_price +"         "+ord_created);
                isResult =  true;

            }
            if (! isResult) {
                System.out.println(err);
            }
            stmt.close();
            conn.close();

        } catch (Exception e) {
            System.out.println("ShowAllOrders(): error- " + e.getMessage());
        }
    }

    public static int AddProductToDB(Product productsInfo) {
        if (productsInfo == null) {
            return 0;
        }
        if (productsInfo.getName().isBlank()) {
            return 0;
        }
        Connection conn = GetConnection();
        if (conn == null) {
            return 0;
        }
        try {
            Statement stmt = conn.createStatement();
            String sql = "INSERT products(name, price, status, created_at)" +
                    " VALUES ('"+productsInfo.getName()+"', "+productsInfo.getPrice()+", '"+productsInfo.getStatus()+"', '"+productsInfo.getCreated()+"')";

            int rows = stmt.executeUpdate(sql);

            stmt.close();
            conn.close();

            return rows;
        }
        catch(Exception ex){
            System.out.println("Connection failed...");

            System.out.printf("AddProductToDB(): error- %s", ex.getMessage());
            return 0;
        }
    }

    public static Order AddOrderToDB(ArrayList<OrderItem> orderItems) {
        if (orderItems == null) {
            return null;
        }
        if (orderItems.size() == 0){
            return null;
        }
        Connection conn = GetConnection();
        if (conn == null) {
            return null;
        }

        Order order = orderItems.get(0).getOrder();
        String nstatus = "confirmed";

        try {

            Statement stmt = conn.createStatement();

            String sql = "INSERT orders(user_id, status, created_at)" +
                    " VALUES ("+order.getUser_id()+", '"+ nstatus +"', '"+order.getCreated()+"')";

            int rows = stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);

            ResultSet rs = stmt.getGeneratedKeys();

            int lastId = 0;
            if (rs.next()){
                lastId = rs.getInt(1);
            }

            if (rows == 0) {
                return null;
            }

            order.setId(lastId);
            order.setStatus(nstatus);

            StringBuilder strSql = new StringBuilder();
            strSql.append("INSERT order_items(order_id, product_id, quantity) VALUES ");
            String tmpStr;
            for (OrderItem orderItem: orderItems) {
                tmpStr = "("+order.getId()+", "+ orderItem.getProduct().getId() +", "+ orderItem.getQuantity() +"), ";
                strSql.append(tmpStr);
            }

            sql = strSql.toString();
            sql = sql.trim();
            sql = sql.substring(0, sql.length() - 1);

            rows = stmt.executeUpdate(sql);

            if (rows == 0) {
                return null;
            }

            stmt.close();
            conn.close();

        }
        catch(Exception ex){
            System.out.printf("Connection failed: %s!", ex.getMessage());

            return null;
        }

        return order;
    }

    public static Map<Integer, Product> GetAllProducts() {
        Statement stmt;
        Map<Integer, Product> prlst = new HashMap<>();

        Connection conn = GetConnection();
        if (conn == null) {
            return prlst;
        }

        try {

            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from products");

            while(rs.next()){
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int price = rs.getInt("price");
                Products_status status = Products_status.valueOf(rs.getString("status"));
                String created = rs.getString("created_at");
                Product product = new Product(id, name, price, status, created);

                prlst.put(product.getId(), product);

            }
            rs.close();
            stmt.close();
            conn.close();
        }
        catch(Throwable th) {
            System.out.printf("Error! GetAllProducts() - %s", th.getMessage());
            th.printStackTrace();
            return null;
        }

        return prlst;
    }

}
