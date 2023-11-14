package data.house;

/**
 * Represents a House, as returned to the clients
 */
public class House {
    private String id;
    private String name;
    private String location;
    private String description;
    private String ownerId;
    private String startDate;
    private String endDate;
    private int cost;
    private int discount;

    public House() {}

    public House(String id, String name, String location, String ownerId, String description) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.description = description;
        this.ownerId = ownerId;
    }

    public boolean createValidate() {
        return this.name!=null && this.location!=null && this.ownerId !=null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getCost(){
        return this.cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getDiscount(){
        return this.discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

}
