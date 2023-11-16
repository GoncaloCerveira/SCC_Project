package data.rental;


import data.ObjectDAO;

/**
 * Represents a Rental, as stored in the database
 */
public class RentalDAO extends Rental implements ObjectDAO {
    private String _rid; // added by CosmosDB, which is the id of item
    private String _ts; // added by CosmosDB, which is the timestamp of the last update to the item

    public RentalDAO() {
    }

    public RentalDAO(String id, String houseId, String user, String fromDate, String toDate, String location, int cost, int discount, boolean free) {
        super(id, houseId, user, fromDate, toDate, location, cost, discount, free);
    }

    public RentalDAO(Rental r) {
        super(r.getId(), r.getHouseId(), r.getUser(), r.getFromDate(), r.getToDate(), r.getLocation(), r.getCost(), r.getDiscount(), r.isFree());
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