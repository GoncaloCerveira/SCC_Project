package data.house;

public class HouseDAO {

    private String _rid; // added by CosmosDB, which is the id of item
    private String _ts; // added by CosmosDB, which is the timestamp of the last update to the item
    private String id;
    //definir formato location
    private String location;

    private String ownerID;

    public HouseDAO() {
    }

    public HouseDAO( House h) {
        this(h.getId(), h.getLocation(),h.getOwnerID());
    }

    public HouseDAO(String id, String location, String ownerId) {
        super();
        this.id = id;
        this.location = location;
        this.ownerID = ownerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
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

    //private String ownerId;

}