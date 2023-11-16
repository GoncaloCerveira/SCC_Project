package data.rental;

/**
 * Represents a Rental, as returned to the clients
 */
public class Rental {
    private String id;
    private String houseId;
    private String userId;
    private int initDate;
    private int endDate;
    private int price;

    public Rental() {
    }

    public Rental(String id, String houseId, String userId, int initDate, int endDate, int price) {
        this.id = id;
        this.houseId = houseId;
        this.userId = userId;
        this.initDate = initDate;
        this.endDate = endDate;
        this.price = price;
    }

    public boolean validateCreate() {
        return this.id != null && this.initDate != 0 && this.endDate != 0 && this.price != 0 && this.initDate <= this.endDate;
    }

    public boolean validateStartDate(int startDate) {
        return startDate <= this.endDate;
    }

    public boolean validateEndDate(int endDate) {
        return endDate >= this.initDate;
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

    public int getInitDate() {
        return initDate;
    }

    public void setInitDate(int initDate) {
        this.initDate = initDate;
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
