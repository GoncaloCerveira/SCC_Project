package data.availability;

/**
 * Represents a Discount, as returned to the clients
 */
public class Availability {
    private String id;
    private String houseId;
    private String fromDate;
    private String toDate;
    private String location;
    private int cost;
    private int discount;

    public Availability() {
    }

    public Availability(String id, String houseId, String fromDate, String toDate, String location, int cost, int discount) {
        this.id = id;
        this.houseId = houseId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.cost = cost;
        this.discount = discount;
    }

    public boolean validate() {
        return this.fromDate != null && this.toDate != null && this.fromDate.compareTo(this.toDate) < 0 && this.cost != 0 && this.discount != 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId;
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


}
