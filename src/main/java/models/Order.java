package models;

public class Order {
    private int id;
    private int user_id;
    private String status;//new, confirmed
    private String created;

    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getStatus() {
        return status;
    }

    public String getCreated() {
        return created;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public Order(int id, int user_id, String status, String created) {
        this.id = id;
        this.user_id = user_id;
        this.status = status;
        this.created = created;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", status='" + status + '\'' +
                ", created='" + created + '\'' +
                '}';
    }

    public String toStringView() {
        return String.format("#%s  %s	%s  %s", id, user_id, status, created);
    }

}
