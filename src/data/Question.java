package data;

public class Question {
    private String houseId;
    private String userId;
    private String question;
    private String reply;

    public Question(String houseId, String userId, String question, String reply) {
        this.houseId = houseId;
        this.userId = userId;
        this.question = question;
        this.reply = reply;
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

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}
