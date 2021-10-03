package starterconsole;

import controller.Controller;
import enums.Products_status;
import models.Order;
import models.OrderItem;
import models.Product;

import java.io.Console;
import java.text.SimpleDateFormat;
import java.util.*;

public class StarterConsole {
    public static void main(String[] args) {
        Controller.CreateDBTables();

        ShowMainMenu();

        Scanner sc = new Scanner(System.in);
        String inp = "";
        while (! inp.equals("0")) {
            inp = sc.next();

            switch (inp) {
                case "1":
                    System.out.println("command <1. Create a Product> is running...");
                    Product productsInfo = GetProductsInfo();

                    if (productsInfo != null) {
                        int rows = Controller.AddProductToDB(productsInfo);
                        if (rows > 0) {
                            System.out.printf("    Product <%s> was successfully added to the database!",productsInfo.getName());
                        } else {
                            System.out.printf("    !Error! Product <%s> wasn't added to the database!",productsInfo.getName());
                        }
                    }

                    ShowMainMenu();
                    break;

                case "*": {
                    ShowMainMenu();
                    break;
                }
                case "2": {
                    System.out.println("command <2. Create an Order> is running...");
                    ArrayList<OrderItem> orderItems = GetNewOrderEntriesSet();
                    if (orderItems == null) {
                        ShowMainMenu();
                        break;
                    }
                    Order order = Controller.AddOrderToDB(orderItems);

                    if (order != null) {
                        System.out.printf("    Order %s was successfully added to the database!", order.toStringView());
                    } else {
                        System.out.println("    !Error! Order wasn't added to the database!");
                    }
                    ShowMainMenu();

                    break;
                }
                case "3": {
                    System.out.println("command <3. Show all Products> is running...");
                    Controller.ShowAllProducts();
                    System.out.println("\nPls enter the command number to continue...");

                    break;
                }
                case "4": {
                    System.out.println("command <4. Show all ordered Products> is running...");
                    Controller.ShowAllOrderedProducts();
                    System.out.println("\nPls enter the command number to continue...");

                    break;
                }
                case "5": {
                    System.out.println("command <5. Show all Orders> is running...");
                    Controller.ShowOrders(0);
                    System.out.println("\nPls enter the command number to continue...");

                    break;
                }
                case "6": {
                    System.out.println("command <6. Show Order by ID> is running...");
                    ShowOrderByID();
                    System.out.println("\nPls enter the command number to continue...");

                    break;
                }
                case "7": {
                    System.out.println("command <7. Delete Products> is running...");
                    DeleteProducts();
                    System.out.println("\nPls enter the command number to continue...");

                    break;
                }
                default:
                    if (!inp.equals("0")) {
                        System.out.printf(" Warning! There is no definition for the command #%s",inp);
                    }
                    break;
            }

        }
    }

    private static void DeleteProducts() {
        Scanner sc1 = new Scanner(System.in);
        System.out.println(" - type the Product ID to delete or type 'all' to delete all Products:");

        String inp = sc1.next().trim();
        if (inp.toLowerCase(Locale.ROOT).equals("all")) {
            Console console = System.console();
            if (console == null) {
                System.out.println("No console available");
                return;
            }
            char[] pswd = console.readPassword("Enter password");
            String passwd = String.valueOf(pswd);

            if (passwd.equals("killall")) {
                Controller.DeleteProductsFromDB(0);
            }

            return;
        } else if (inp.equals("")) {
            System.out.println("  !The Product ID didn't enter, it was impossible to find the Product!");
            return;
        }
        try {
            Integer prod_id =  Integer.parseInt(inp);
            Controller.DeleteProductsFromDB(prod_id);
        } catch (NumberFormatException nfe) {
            System.out.printf("! #%s is not a Product ID, it is impossible to find the Product!", inp);
        }

    }

    private static void ShowOrderByID() {

        Scanner sc1 = new Scanner(System.in);

        System.out.println(" - type the Order ID:");
        String strOrder_id = sc1.next().trim();
        if (strOrder_id.equals("")) {
            System.out.println("  !The Order ID didn't enter, it was impossible to find the Order!");
            return;
        }
        try {
            Integer order_id =  Integer.parseInt(strOrder_id);
            Controller.ShowOrders(order_id);
        } catch (NumberFormatException nfe) {
            System.out.printf("! #%s is not a Order ID, it is impossible to find the Order!", strOrder_id);
        }

    }

    private static ArrayList<OrderItem> GetNewOrderEntriesSet() {
        Map<Integer, Product> all_pr = Controller.GetAllProducts();
        ArrayList<OrderItem> orderItems = new ArrayList<>();

        if (all_pr == null || all_pr.isEmpty()) {
            System.out.println("There are no Products in database, pls create Products then try again!");
            return null;
        }

        System.out.println("Products list for new Order:");

        for (Map.Entry<Integer, Product> entry: all_pr.entrySet()) {
            //System.out.printf("    %s", entry.getValue().toStringView());
            System.out.println(String.format("    %s", entry.getValue().toStringView()));
        }

        System.out.println(" type Product #id to add into the new Order (finally type 'Y' to confirm the Order or 'C' to cancel the Order):");

        String status = "new";
        Random rand = new Random();
        int user_id  =  1 +  rand.nextInt(999);

        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        Order order = new Order(0, user_id, status, sdf.format(d));

        Scanner sc2 = new Scanner(System.in);
        String inp2 = "";
        while (! inp2.equals("Y")) {
            inp2 = sc2.next();

            if (inp2.toLowerCase(Locale.ROOT).equals("c")) {
                System.out.println("The Order was cancelled");
                return null;
            }
            if (inp2.toLowerCase(Locale.ROOT).equals("y")) {
                break;
            }
            try
            {
                int pr_id = Integer.parseInt(inp2);

                Product cur_pr = all_pr.get(pr_id);

                if (cur_pr == null) {
                    System.out.printf("! %s is not in the Product List, type a correct number id and try again!", inp2);
                    continue;
                }

                System.out.printf(" set the quantity for selected (#%s) %s:", cur_pr.getId(), cur_pr.getName());
                String inp3 = sc2.next();
                int pr_qnt = 1;
                try {
                    pr_qnt = Integer.parseInt(inp3);

                } catch (NumberFormatException nfe) {
                    System.out.printf(" '%s' can't be recognise as quantity. '1' will be set as default quantity", cur_pr.getName());
                }
                OrderItem ordItem = new OrderItem(order, cur_pr, pr_qnt);
                orderItems.add(ordItem);

                System.out.printf("    - (#%s) %s with quantity= %s was added to the Order", cur_pr.getName(), cur_pr.getName(), pr_qnt);
                System.out.println(" type other Product #id to add into the current Order (finally type 'Y' to confirm the Order or 'C' to cancel the current Order):");
            } catch (NumberFormatException nfe) {
                System.out.printf("! #%s is not a Product id, type a number of Product id again!", inp2);
            }
        }

        if (orderItems.isEmpty()) {
            System.out.println("!The Order doesn't consist any Products entry, it is impossible to save empty Order!");
            return null;
        }

        System.out.println("The Order was confirmed!");

        return orderItems;
    }

    private static Product GetProductsInfo(){
        String pr_name;
        int pr_price;
        String pr_created;

        Scanner sc1 = new Scanner(System.in);

        System.out.println(" - type the Name (step 1 of 3):");
        String name = sc1.next();
        if (name.equals("")) {
            System.out.println("  !The Name of product didn't enter, it was impossible to create the Product!");
            return null;
        }
        pr_name = name.toLowerCase(Locale.ROOT);


        System.out.println("  - type the Price$ (step 2 of 3):");

        String strprice = sc1.next().trim();
        if (strprice.equals("")) {
            System.out.println("  !The Price of product didn't enter, it was impossible to create the Product!!");
            return null;
        }
        pr_price =  Integer.parseInt(strprice);

        Products_status pr_status = Products_status.IN_STOCK;

        System.out.println("  - type the Status '1' -IN STOCK(by default); '2' -OUT OF STOCK; '3' -RUNNING LOW (step 3 of 3):");
        String strstatus = sc1.next().trim();

        if (strstatus.equals("2")) {
            pr_status = Products_status.OUT_OF_STOCK;
        } else if (strstatus.equals("3")) {
            pr_status = Products_status.RUNNING_LOW;
        }

        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        pr_created = sdf.format(d);

        return new Product(0,pr_name, pr_price, pr_status, pr_created);
    }

    public static void ShowMainMenu() {
        System.out.println("\n The list of SimplePurchase commands:");
        System.out.println("\n 1. Create a Product");
        System.out.println(" 2. Create an Order");
        System.out.println(" 3. Show all Products");
        System.out.println(" 4. Show all ordered Products");
        System.out.println(" 5. Show all Orders");
        System.out.println(" 6. Show Order by ID");
        System.out.println(" 7. Delete Products");

        System.out.println("\n 0. Exit");

        System.out.println("Pls enter the command number to continue...");
    }



}
