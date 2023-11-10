package db;

import com.azure.cosmos.*;
import com.azure.cosmos.implementation.NotFoundException;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedIterable;
// TODO
import data.question.QuestionDAO;

// TODO
// Modificar o id para usar o id da casa e o id do user como chave da questão

public class CosmosDBQuestionsLayer {
    private static CosmosDBQuestionsLayer instance;

    public static synchronized CosmosDBQuestionsLayer getInstance() {
        if( instance != null)
            return instance;

        CosmosClient client = CosmosDB.createClient();
        instance = new CosmosDBQuestionsLayer(client);
        return instance;

    }

    private CosmosClient client;
    private CosmosDatabase db;
    private CosmosContainer questions;

    public CosmosDBQuestionsLayer(CosmosClient client) {
        this.client = client;
    }

    private synchronized void init() {
        if( db != null)
            return;
        db = client.getDatabase(CosmosDB.DB_NAME);
        questions = db.getContainer("questions");

    }

    public CosmosItemResponse<Object> delQuestionById(String id) {
        init();
        PartitionKey key = new PartitionKey( id);
        return questions.deleteItem(id, key, new CosmosItemRequestOptions());
    }

    public CosmosItemResponse<Object> delQuestion(QuestionDAO question) {
        init();
        return questions.deleteItem(question, new CosmosItemRequestOptions());
    }

    public CosmosItemResponse<QuestionDAO> postQuestion(QuestionDAO question) {
        init();
        CosmosItemResponse<QuestionDAO> res = questions.createItem(question);
        if(res.getStatusCode()<300)
            return res;
        else throw new NotFoundException();
        //return rentals.createItem(rentals);
    }

    public CosmosItemResponse<QuestionDAO> putQuestion(QuestionDAO question) {
        init();
        CosmosItemResponse<QuestionDAO> res = questions.replaceItem(question, question.getId(), new PartitionKey(question.getId()), new CosmosItemRequestOptions());
        if(res.getStatusCode()<300)
            return res;
        else throw new NotFoundException();
        //return questions.createItem(question);
    }

    public CosmosPagedIterable<QuestionDAO> getQuestionById(String id) {
        init();
        return questions.queryItems("SELECT * FROM questions WHERE questions.id=\"" + id + "\"", new CosmosQueryRequestOptions(), QuestionDAO.class);
    }

    public CosmosPagedIterable<QuestionDAO> getQuestionByHouseAndUser(String houseId, String userId) {
        init();
        return questions.queryItems("SELECT * FROM questions WHERE questions.houseId=\"" + houseId + "\" AND questions.userID=\"" + userId + "\"", new CosmosQueryRequestOptions(), QuestionDAO.class);
    }

    public CosmosPagedIterable<QuestionDAO> getQuestions() {
        init();
        return questions.queryItems("SELECT * FROM questions ", new CosmosQueryRequestOptions(), QuestionDAO.class);
    }

    public CosmosPagedIterable<QuestionDAO> getHouseQuestions(String houseId) {
        init();
        return questions.queryItems("SELECT * FROM rentals where questions.houseid=\"" + houseId + "\"", new CosmosQueryRequestOptions(), QuestionDAO.class);
    }

    public void close() {
        client.close();
    }

}
