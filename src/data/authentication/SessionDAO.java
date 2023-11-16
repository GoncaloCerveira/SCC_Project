package data.authentication;

import data.ObjectDAO;

public class SessionDAO extends Session implements ObjectDAO {
    private String _rid; // added by CosmosDB, which is the id of item
    private String _ts; // added by CosmosDB, which is the timestamp of the last update to the item

    public SessionDAO() {
    }

    public SessionDAO(String id, String name) {
        super(id, name);
    }

    public SessionDAO(Session s) {
        super(s.getId(), s.getName());
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
