package data;

public class Rental {

    private String id;
    private String houseId;
    private String userId;
    private int period;
    // total price
    private int price;

    public Rental(String id, String houseId, String userId, int period, int price) {
        this.id = id;
        this.houseId = houseId;
        this.userId = userId;
        this.period = period;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
