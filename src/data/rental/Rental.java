package data.rental;

/**
 * Represents a Rental, as returned to the clients
 */
public class Rental {
    private String id;
    private String house;
    private String user;
    private String fromDate;
    private String toDate;
    private String location;
    private int cost;
    private int discount;
    private boolean free;

    public Rental() {
    }

    public Rental(String id, String house, String user, String fromDate, String toDate, String location, int cost, int discount, boolean free) {
        this.id = id;
        this.house = house;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.location = location;
        this.cost = cost;
        this.discount = discount;
        this.free = free;
    }

    public boolean validate() {
        return this.fromDate != null && this.toDate != null && this.fromDate.compareTo(this.toDate) < 0 && this.cost != 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }


}
