package data.question;

/**
 * Represents a Question, as returned to the clients
 */
public class Question {
    private String id;
    private String house;
    private String user;
    private String owner;
    private String text;
    private String reply;
    private boolean noAnswer;

    public Question() {
    }

    public Question(String id, String house, String user, String owner, String text, String reply, boolean noAnswer) {
        this.id = id;
        this.house = house;
        this.user = user;
        this.owner = owner;
        this.text = text;
        this.reply = reply;
        this.noAnswer = noAnswer;
    }

    public boolean validateCreate() {
        return this.id != null && this.house != null && this.owner != null && this.text != null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
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
