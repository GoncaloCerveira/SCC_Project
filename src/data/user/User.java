package data.user;

/**
 * Represents a User, as returned to the clients
 */
public class User {
	private String id;
	private String name;
	private String pwd;
	private String photoId;

	public User() {
	}

	public User(String id, String name, String pwd) {
		super();
		this.id = id;
		this.name = name;
		this.pwd = pwd;
	}

	public boolean validate() {
		return this.id != null && this.pwd!=null && this.name!=null && this.photoId!=null;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getPhotoId() {
		return photoId;
	}

	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}


}
