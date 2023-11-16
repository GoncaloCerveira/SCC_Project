package data.question;

/**
 * Represents a Question, as returned to the clients
 */
public class Question {
    private String id;
    private String houseId;
    private String userId;
    private String ownerId;
    private String text;
    private String reply;
    private boolean noAnswer;

    public Question() {
    }

    public Question(String id, String houseId, String userId, String ownerId, String text, String reply, boolean noAnswer) {
        this.id = id;
        this.houseId = houseId;
        this.userId = userId;
        this.ownerId = ownerId;
        this.text = text;
        this.reply = reply;
        this.noAnswer = noAnswer;
    }

    public boolean validateCreate() {
        return this.id != null && this.houseId != null && this.ownerId != null && this.text != null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public boolean isNoAnswer() {
        return noAnswer;
    }

    public void setNoAnswer(boolean noAnswer) {
        this.noAnswer = noAnswer;
    }


}
