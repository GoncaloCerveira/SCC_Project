package data.rental;

/**
 * Represents a Rental, as returned to the clients
 */
public class Rental {
    private String id;
    private String houseId;
    private String userId;
    private int startDate;
    private int endDate;
    private int price;

    public Rental() {}

    public Rental(String id, String houseId, String userId, int startDate, int endDate, int price) {
        this.id = id;
        this.houseId = houseId;
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
    }

    public boolean validateCreate() {
        return this.id != null && this.startDate != 0 && this.endDate != 0 && this.price != 0 && this.startDate <= this.endDate;
    }

    public boolean validateStartDate(int startDate) {
        return startDate <= this.endDate;
    }

    public boolean validateEndDate(int endDate) {
        return endDate >= this.startDate;
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
