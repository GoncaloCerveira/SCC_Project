package cache;

import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.core.type.TypeReference;
import data.question.QuestionDAO;
import db.CosmosDBQuestionsLayer;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class QuestionsCache extends RedisCache {
    private static final CosmosDBQuestionsLayer qdb = CosmosDBQuestionsLayer.getInstance();

    public static List<QuestionDAO> getQuestionById(String id) {
        List<QuestionDAO> question = readFromCache("getQuestionById", id, new TypeReference<>() {});

        CompletableFuture<List<QuestionDAO>> asyncQuestionDB = CompletableFuture.supplyAsync(() -> {
            CosmosPagedIterable<QuestionDAO> questionDB = qdb.getQuestionById(id);
            writeToCache("getQuestionById", id, questionDB);
            return questionDB.stream().toList();
        });

        if(question != null) {
            return question;
        }

        return asyncQuestionDB.join();
    }

    public static List<QuestionDAO> getQuestionByHouseAndUser(String houseId, String userId) {
        String id = houseId + "#" + userId;
        List<QuestionDAO> questions = readFromCache("getQuestionByHouseAndUser", id, new TypeReference<>() {});

        CompletableFuture<List<QuestionDAO>> asyncQuestionsDB = CompletableFuture.supplyAsync(() -> {
            CosmosPagedIterable<QuestionDAO> questionsDB = qdb.getQuestionByHouseAndUser(houseId, userId);
            writeToCache("getQuestionByHouseAndUser", id, questionsDB);
            return questionsDB.stream().toList();
        });

        if(questions != null) {
            return questions;
        }

        return asyncQuestionsDB.join();
    }

    public static List<QuestionDAO> getQuestions() {
        List<QuestionDAO> questions = readFromCache("getQuestions", "", new TypeReference<>() {});

        CompletableFuture<List<QuestionDAO>> asyncQuestionsDB = CompletableFuture.supplyAsync(() -> {
            CosmosPagedIterable<QuestionDAO> questionsDB = qdb.getQuestions();
            writeToCache("getQuestions", "", questionsDB);
            return questionsDB.stream().toList();
        });

        if(questions != null) {
            return questions;
        }

        return asyncQuestionsDB.join();
    }

    public static List<QuestionDAO> getHouseQuestions(String houseId) {
        List<QuestionDAO> questions = readFromCache("getHouseQuestions", houseId, new TypeReference<>() {});

        CompletableFuture<List<QuestionDAO>> asyncQuestionsDB = CompletableFuture.supplyAsync(() -> {
            CosmosPagedIterable<QuestionDAO> questionsDB = qdb.getHouseQuestions(houseId);
            writeToCache("getHouseQuestions", houseId, questionsDB);
            return questionsDB.stream().toList();
        });

        if(questions != null) {
            return questions;
        }

        return asyncQuestionsDB.join();
    }


}
