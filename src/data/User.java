package data;

import java.util.Arrays;

/**
 * Represents a User, as returned to the clients
 * 
 * NOTE: array of house ids is shown as an example of how to store a list of elements and 
 * handle the empty list.
 */
public class User {
	private String id;
	private String name;
	// already hashed
	private String pwd;
	private String photoId;

	//private String email;

	//private String phoneNumber;
	// owned houses
	private String[] ownedIds;
	// rented houses
	private String[] rentalIds;
	public User(String id, String name, String pwd) {
		super();
		this.id = id;
		this.name = name;
		this.pwd = pwd;
		//this.email = email;
		//this.phoneNumber = phoneNumber;
		//this.ownedIds = ownedIds;
		//this.rentalIds = rentalIds;

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

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", pwd=" + pwd + ", photoId=" + photoId + ", ownedIds="
				+ Arrays.toString(ownedIds) + ", rentalIds="
				+ Arrays.toString(rentalIds) +"]";
	}

	/*public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}*/

	/*public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String[] getOwnedIds() {
		return ownedIds;
	}

	public void setOwnedIds(String[] ownedIds) {
		this.ownedIds = ownedIds;
	}

	public String[] getRentalIds() {
		return rentalIds;
	}

	public void setRentalIds(String[] rentalIds) {
		this.rentalIds = rentalIds;
	}*/
}
