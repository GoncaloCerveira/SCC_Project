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

        if(question != null) {
            return question;
        }

        CosmosPagedIterable<QuestionDAO> questionDB = qdb.getQuestionById(id);
        if(questionDB.iterator().hasNext()) {
            writeToCache("getQuestionById", id, questionDB);
        }

        return questionDB.stream().toList();
    }

    public static List<QuestionDAO> getQuestionByHouseAndUser(String houseId, String userId) {
        String id = houseId + userId;
        List<QuestionDAO> questions = readFromCache("getQuestionByHouseAndUser", id, new TypeReference<>() {});

        if(questions != null) {
            return questions;
        }

        CosmosPagedIterable<QuestionDAO> questionsDB = qdb.getQuestionByHouseAndUser(houseId, userId);
        if(questionsDB.iterator().hasNext()) {
            writeToCache("getQuestionByHouseAndUser", id, questionsDB);
        }

        return questionsDB.stream().toList();
    }

    public static List<QuestionDAO> getQuestions(String st, String len) {
        List<QuestionDAO> questions = readFromCache("getQuestions", "", new TypeReference<>() {});

        if(questions != null) {
            return questions;
        }

        CosmosPagedIterable<QuestionDAO> questionsDB = qdb.getQuestions(st, len);
        if(questionsDB.iterator().hasNext()) {
            writeToCache("getQuestionById", "", questionsDB);
        }

        return questionsDB.stream().toList();
    }

    public static List<QuestionDAO> getHouseQuestions(String st, String len, String houseId) {
        List<QuestionDAO> questions = readFromCache("getHouseQuestions", houseId, new TypeReference<>() {});

        if(questions != null) {
            return questions;
        }

        CosmosPagedIterable<QuestionDAO> questionsDB = qdb.getHouseQuestions(st, len, houseId);
        writeToCache("getHouseQuestions", houseId, questionsDB);
        if(questionsDB.iterator().hasNext()) {
            writeToCache("getHouseQuestions", houseId, questionsDB);
        }

        return questionsDB.stream().toList();
    }

    public static List<QuestionDAO> getHouseQuestionsStatus(String st, String len, String houseId) {
        List<QuestionDAO> questions = readFromCache("getHouseQuestionsStatus", houseId, new TypeReference<>() {});

        if(questions != null) {
            return questions;
        }

        CosmosPagedIterable<QuestionDAO> questionsDB = qdb.getHouseQuestionsStatus(st, len, houseId);
        writeToCache("getHouseQuestionsStatus", houseId, questionsDB);
        if(questionsDB.iterator().hasNext()) {
            writeToCache("getHouseQuestionsStatus", houseId, questionsDB);
        }

        return questionsDB.stream().toList();
    }


}
