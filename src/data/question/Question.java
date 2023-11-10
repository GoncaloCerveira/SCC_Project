package data.question;

public class Question {
    private String id;
    private String houseId;
    private String userId;
    private String ownerId; // Facilita a pesquisa de questões que um user recebeu sem ser necessário
                            // pesquisar pelos Ids das casas dele
    private String question;
    private String reply;

    public Question() {}

    public Question(String id, String houseId, String userId, String ownerId, String question, String reply) {
        this.id = id;
        this.houseId = houseId;
        this.userId = userId;
        this.ownerId = ownerId;
        this.question = question;
        this.reply = reply;
    }

    public boolean validateCreate() {
        return this.userId != null && this.ownerId != null && this.question != null;
    }

    public boolean validateReply() {
        return validateCreate() & this.reply != null;
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
