package data.media;

public class Media {
    private String id;
    private String itemId;

    public Media() {
    }

    public Media(String id, String itemId) {
        this.id = id;
        this.itemId = itemId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }


}
