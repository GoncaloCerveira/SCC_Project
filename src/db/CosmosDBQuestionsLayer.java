package db;

import com.azure.cosmos.*;
import com.azure.cosmos.implementation.NotFoundException;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedIterable;
import data.question.QuestionDAO;
import utils.AzureProperties;

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
        db = client.getDatabase(AzureProperties.DB_NAME);
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
    }

    public CosmosItemResponse<QuestionDAO> putQuestion(QuestionDAO question) {
        init();
        CosmosItemResponse<QuestionDAO> res = questions.replaceItem(question, question.getId(), new PartitionKey(question.getOwner()), new CosmosItemRequestOptions());
        if(res.getStatusCode()<300)
            return res;
        else throw new NotFoundException();
    }

    public CosmosPagedIterable<QuestionDAO> getQuestionById(String id) {
        init();
        String query = "SELECT * FROM questions WHERE questions.id=\"" + id + "\"";
        return questions.queryItems(query, new CosmosQueryRequestOptions(), QuestionDAO.class);
    }

    public CosmosPagedIterable<QuestionDAO> getQuestionByHouseAndUser(String houseId, String userId) {
        init();
        String query = "SELECT * FROM questions WHERE questions.house=\"" + houseId + "\" AND questions.user=\"" + userId + "\"";
        return questions.queryItems(query, new CosmosQueryRequestOptions(), QuestionDAO.class);
    }

    public CosmosPagedIterable<QuestionDAO> getQuestions(String st, String len) {
        init();
        String query = "SELECT * FROM questions";
        if(st != null && len != null) {
            query = query + " OFFSET " + st + " LIMIT " + len;
        }
        return questions.queryItems(query, new CosmosQueryRequestOptions(), QuestionDAO.class);
    }

    public CosmosPagedIterable<QuestionDAO> getHouseQuestions(String st, String len, String houseId) {
        init();
        String query = "SELECT * FROM questions WHERE questions.house=\"" + houseId;
        if(st != null && len != null) {
            query = query + " OFFSET " + st + " LIMIT " + len;
        }
        return questions.queryItems(query, new CosmosQueryRequestOptions(), QuestionDAO.class);
    }

    public CosmosPagedIterable<QuestionDAO> getHouseQuestionsByStatus(String st, String len, String houseId, boolean noAnswer) {
        init();
        String query = "SELECT * FROM questions WHERE questions.houseId=\"" + houseId + "\" questions.noAnswer=\"" + noAnswer + "\"";
        if(st != null && len != null) {
            query = query + " OFFSET " + st + " LIMIT " + len;
        }
        return questions.queryItems(query, new CosmosQueryRequestOptions(), QuestionDAO.class);
    }

    public void close() {
        client.close();
    }


}
