package data.discount;

/**
 * Represents a Discount, as returned to the clients
 */
public class Discount {
    private String id;
    private String houseId;
    private int startDate;
    private int endDate;
    private int price;

    public Discount() {}

    public Discount(String id, String houseId, int startDate, int endDate, int price) {
        this.id = id;
        this.houseId = houseId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
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

    public int getStartDate() {
        return startDate;
    }

    public void setStartDate(int startDate) {
        this.startDate = startDate;
    }

    public int getEndDate() {
        return endDate;
    }

    public void setEndDate(int endDate) {
        this.endDate = endDate;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }


}
