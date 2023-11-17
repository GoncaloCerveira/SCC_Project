package data.house;

/**
 * Represents a House, as returned to the clients
 */
public class House {
    private String id;
    private String name;
    private String location;
    private String owner;
    private String description;

    public House() {}

    public House(String id, String name, String location, String owner, String description) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.owner = owner;
        this.description = description;
    }

    public boolean createValidate() {
        return this.name!=null && this.location!=null && this.owner !=null;
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
