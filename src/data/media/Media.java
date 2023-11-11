package data.media;

public class Media {
    private String mediaId;
    private String itemId;

    public Media() {}

    public Media(String mediaId, String itemId) {
        this.mediaId = mediaId;
        this.itemId = itemId;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }


}
