package data.house;

import java.util.Map;

public class House {
    private String id;
    //definir formato location
    private String location;

    private String ownerID;

    private byte[] photo;

    public House(String id, String location, String ownerId) {
        this.id = id;
        this.location = location;
        this.ownerID = ownerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    //private String ownerId;

}
