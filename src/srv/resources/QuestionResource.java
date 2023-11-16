package srv.resources;

import cache.HousesCache;
import cache.QuestionsCache;
import com.azure.core.annotation.BodyParam;
import data.house.HouseDAO;
import data.question.Question;
import data.question.QuestionDAO;
import db.CosmosDBQuestionsLayer;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import utils.AuthValidation;

import java.util.*;
import java.util.logging.Logger;

/**
 * Resource for managing creating, replying and listing questions.
 */
@Path("/house/{houseId}/question")
public class QuestionResource {
    private final CosmosDBQuestionsLayer qdb = CosmosDBQuestionsLayer.getInstance();
    private final AuthValidation auth = new AuthValidation();
    private static final Logger Log = Logger.getLogger(QuestionResource.class.getName());

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@CookieParam("scc:session") Cookie session, @PathParam("houseId") String houseId, Question question) {
        try {
            Log.info("createQuestion from: " + question.getUserId() + " for: " + houseId);

            String userId = auth.getUserId(session);

            if (!question.validateCreate()) {
                Log.info("Null information was given");
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }

            boolean empty = QuestionsCache.getQuestionByHouseAndUser(houseId, userId).isEmpty();
            if (!empty) {
                Log.info("Question already exists.");
                throw new WebApplicationException(Response.Status.CONFLICT);
            }

            question.setUserId(userId);
            question.setReply(null);

            qdb.postQuestion(new QuestionDAO(question));
            Log.info("Question created.");
            return Response.ok().build();
        } catch (WebApplicationException e) {
            throw e;
        } catch(Exception e) {
            throw new InternalServerErrorException(e);
        }

    }

    @PUT
    @Path("/{questionId}/reply")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response replyQuestion(@PathParam("houseId") String houseId, @PathParam("questionId") String questionId,
                                  @QueryParam("st") String st , @QueryParam("len") String len,
                                  @BodyParam("reply") String reply) {
        Log.info("replyQuestion for: " + houseId);

        List<HouseDAO> hResults = HousesCache.getHouseById(houseId);
        if (hResults.isEmpty()) {
            Log.info("House does not exist.");
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        List<QuestionDAO> qResults = QuestionsCache.getQuestionById(questionId);
        if (qResults.isEmpty()) {
            Log.info("Question does not exist.");
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        QuestionDAO questionDB = qResults.get(0);
        if(!questionDB.isNoAnswer()) {
            throw new WebApplicationException(Response.Status.CONFLICT);
        }
        questionDB.setReply(reply);
        qdb.putQuestion(questionDB);

        Log.info("Question replied.");
        return Response.ok(questionDB).build();
    }

    @GET
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response listQuestions(@PathParam("houseId") String houseId, @QueryParam("noanswer") String noAnswer,
                                  @QueryParam("st") String st , @QueryParam("len") String len) {
        Log.info("listUnanswered for: " + houseId);

        List<HouseDAO> results = HousesCache.getHouseById(houseId);
        if (results.isEmpty()) {
            Log.info("House does not exist.");
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        List<QuestionDAO> questions;
        if(noAnswer != null) {
            questions = QuestionsCache.getHouseQuestionsStatus(len, st, houseId);
        } else {
            questions = QuestionsCache.getHouseQuestions(len, st, houseId);
        }
        Log.info("Rentals retrieved.");
        return Response.ok(questions).build();
    }


}
