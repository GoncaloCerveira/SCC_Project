package data.house;

import data.ObjectDAO;

/**
 * Represents a House, as stored in the database
 */
public class HouseDAO extends House implements ObjectDAO {
    private String _rid; // added by CosmosDB, which is the id of item
    private String _ts; // added by CosmosDB, which is the timestamp of the last update to the item

    public HouseDAO() {
    }

    public HouseDAO(String id, String name, String location, String ownerId, String description) {
        super(id, name, location, ownerId, description);
    }

    public HouseDAO(House h) {
        super(h.getId(), h.getName(), h.getLocation(),h.getOwner(), h.getDescription());
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