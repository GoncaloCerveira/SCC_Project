package data.question;

import data.ObjectDAO;

public class QuestionDAO extends Question implements ObjectDAO {
    private String _rid; // added by CosmosDB, which is the id of item
    private String _ts; // added by CosmosDB, which is the timestamp of the last update to the item

    public QuestionDAO(String id, String houseId, String userId, String ownerId, String question, String reply) {
        super(id, houseId, userId, ownerId, question, reply);
    }

    public QuestionDAO(Question q) {
        super(q.getId(), q.getHouseId(), q.getUserId(), q.getOwnerId(), q.getQuestion(), q.getReply());
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
