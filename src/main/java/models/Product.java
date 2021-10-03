package models;

import enums.Products_status;

public class Product {
    private int id;
    private String name;
    private int price;
    private Products_status status;
    private String created;

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public int getPrice() {
        return price;
    }
    public Products_status getStatus() {
        return status;
    }
    public String getCreated() {
        return created;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public void setStatus(Products_status status) {
        this.status = status;
    }
    public void setCreated(String created) {
        this.created = created;
    }

    public Product(int id, String name, int price, Products_status status, String created) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.status = status;
        this.created = created;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", status='" + status + '\'' +
                ", created='" + created + '\'' +
                '}';
    }

    public String toStringView() {
        return String.format("#%s  %s	%s$	%s  %s", id, name, price, status, created);
    }

}
