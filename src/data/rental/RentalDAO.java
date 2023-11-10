package data.rental;

public class RentalDAO {
    private String _rid; // added by CosmosDB, which is the id of item
    private String _ts; // added by CosmosDB, which is the timestamp of the last update to the item
    private String id;
    private String houseId;
    private String userId;
    private int startDate;
    private int endDate;
    private int price;

    public RentalDAO( Rental r){
        this(r.getId(), r.getHouseId(), r.getUserId(), r.getStartDate(), r.getEndDate(), r.getPrice());
    }


    public RentalDAO(String id, String houseId, String userId, int startDate, int endDate, int price) {
        super();
        this.id = id;
        this.houseId = houseId;
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
    }

    public boolean validateCreate() {
        return this.userId != null && this.startDate != 0 && this.endDate != 0 && this.price != 0;
    }

    public boolean validateUpdate() {
        return validateCreate() && this.id != null;
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

    public String get_rid() {
        return _rid;
    }

    public void set_rid(String _rid) {
        this._rid = _rid;
    }

    public String get_ts() {
        return _ts;
    }

    public void set_ts(String _ts) {
        this._ts = _ts;
    }
}
