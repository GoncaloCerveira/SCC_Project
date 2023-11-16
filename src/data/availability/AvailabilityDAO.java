package data.availability;


import data.ObjectDAO;

/**
 * Represents a Discount, as stored in the database
 */
public class AvailabilityDAO extends Availability implements ObjectDAO {
    private String _rid; // added by CosmosDB, which is the id of item
    private String _ts; // added by CosmosDB, which is the timestamp of the last update to the item

    public AvailabilityDAO() {
    }

    public AvailabilityDAO(String id, String houseId, String fromDate, String toDate, String location, int cost, int discount) {
        super(id, houseId, fromDate, toDate, location, cost, discount);
    }

    public AvailabilityDAO(Availability d) {
        super(d.getId(), d.getHouseId(), d.getFromDate(), d.getToDate(), d.getLocation(), d.getCost(), d.getDiscount());
    }

    @Override
    public String get_rid() {
        return _rid;
    }

    @Override
    public void set_rid(String _rid) {
        this._rid = _rid;
    }

    @Override
    public String get_ts() {
        return _ts;
    }

    @Override
    public void set_ts(String _ts) {
        this._ts = _ts;
    }


}