package data.discount;


import data.ObjectDAO;

/**
 * Represents a Discount, as stored in the database
 */
public class DiscountDAO extends Discount implements ObjectDAO {
    private String _rid; // added by CosmosDB, which is the id of item
    private String _ts; // added by CosmosDB, which is the timestamp of the last update to the item

    public DiscountDAO(Discount d) {
        super(d.getId(), d.getHouseId(), d.getStartDate(), d.getEndDate(), d.getPrice());
    }

    public DiscountDAO() {

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