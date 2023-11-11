package data.house;

/**
 * Represents a House, as returned to the clients
 */
public class House {
    private String id;
    private String name;
    private String location;
    private String ownerId;

    public House() {}

    public House(String id, String name, String location, String ownerId) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.ownerId = ownerId;
    }

    public boolean validate() {
        return this.location!=null && this.ownerId !=null;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }


}
