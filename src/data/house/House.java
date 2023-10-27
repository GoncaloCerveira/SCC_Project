package data.house;

import java.util.Map;

public class House {

    private String id;
    private String location;
    private String ownerID;
    private String[] photoIDs;

    public House(String location, String ownerId, String[] photoIDs) {
        this.id = id;
        this.location = location;
        this.ownerID = ownerId;
        this.photoIDs = photoIDs;
    }

    public House(String id, String location, String ownerId, String[] photoIDs) {
        this.id = id;
        this.location = location;
        this.ownerID = ownerId;
        this.photoIDs = photoIDs;
    }

    public boolean validate() {
        return this.location!=null && this.ownerID!=null && this.photoIDs!=null;
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

}
