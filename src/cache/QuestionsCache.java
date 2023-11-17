package cache;

import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.core.type.TypeReference;
import data.question.QuestionDAO;
import db.CosmosDBQuestionsLayer;

import java.util.List;

public class QuestionsCache extends RedisCache {
    private static final CosmosDBQuestionsLayer qdb = CosmosDBQuestionsLayer.getInstance();

    public static List<QuestionDAO> getQuestionById(String id) {
        String key = id;
        List<QuestionDAO> question = readFromCache("getQuestionById", key, new TypeReference<>() {});

        if(question != null) {
            return question;
        }

        CosmosPagedIterable<QuestionDAO> questionDB = qdb.getQuestionById(id);
        if(questionDB.iterator().hasNext()) {
            writeToCache("getQuestionById", key, questionDB);
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
        String key = st + len;
        List<QuestionDAO> questions = readFromCache("getQuestions", key, new TypeReference<>() {});

        if(questions != null) {
            return questions;
        }

        CosmosPagedIterable<QuestionDAO> questionsDB = qdb.getQuestions(st, len);
        if(questionsDB.iterator().hasNext()) {
            writeToCache("getQuestions", key, questionsDB);
        }

        return questionsDB.stream().toList();
    }

    public static List<QuestionDAO> getHouseQuestions(String st, String len, String houseId) {
        String key = st + len + houseId;
        List<QuestionDAO> questions = readFromCache("getHouseQuestions", key, new TypeReference<>() {});

        if(questions != null) {
            return questions;
        }

        CosmosPagedIterable<QuestionDAO> questionsDB = qdb.getHouseQuestions(st, len, houseId);
        if(questionsDB.iterator().hasNext()) {
            writeToCache("getHouseQuestions", key, questionsDB);
        }

        return questionsDB.stream().toList();
    }

    public static List<QuestionDAO> getHouseQuestionsByStatus(String st, String len, String noAnswer) {
        String key = st + len + noAnswer;
        List<QuestionDAO> questions = readFromCache("getHouseQuestionsByStatus", key, new TypeReference<>() {});

        if(questions != null) {
            return questions;
        }

        CosmosPagedIterable<QuestionDAO> questionsDB = qdb.getHouseQuestionsByStatus(st, len, noAnswer);
        if(questionsDB.iterator().hasNext()) {
            writeToCache("getHouseQuestionsByStatus", key, questionsDB);
        }

        return questionsDB.stream().toList();
    }


}
