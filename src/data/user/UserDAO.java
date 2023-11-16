package data.user;

import data.ObjectDAO;

/**
 * Represents a User, as stored in the database
 */
public class UserDAO extends User implements ObjectDAO {
	private String _rid; // added by CosmosDB, which is the id of item
	private String _ts; // added by CosmosDB, which is the timestamp of the last update to the item

	public UserDAO() {
	}

	public UserDAO(User u) {
		super(u.getId(), u.getName(), u.getPwd(), u.getPhotoId());
	}

	public UserDAO(String id, String name, String pwd, String photoId) {
		super(id, name, pwd, photoId);
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
