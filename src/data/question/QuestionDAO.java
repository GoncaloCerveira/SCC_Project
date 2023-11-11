package data.question;

import data.ObjectDAO;

/**
 * Represents a Question, as stored in the database
 */
public class QuestionDAO extends Question implements ObjectDAO {
    private String _rid; // added by CosmosDB, which is the id of item
    private String _ts; // added by CosmosDB, which is the timestamp of the last update to the item

    public QuestionDAO(Question q) {
        super(q.getId(), q.getHouseId(), q.getUserId(), q.getOwnerId(), q.getQuestion(), q.getReply());
    }

    public QuestionDAO() {

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
