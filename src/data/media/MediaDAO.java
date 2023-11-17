package data.media;

import data.ObjectDAO;

public class MediaDAO extends Media implements ObjectDAO {
    private String _rid; // added by CosmosDB, which is the id of item
    private String _ts; // added by CosmosDB, which is the timestamp of the last update to the item

    public MediaDAO() {
    }

    public MediaDAO(Media media) {
        super(media.getId(), media.getItem());
    }

    public MediaDAO(String id, String itemId) {
        super(id, itemId);
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
