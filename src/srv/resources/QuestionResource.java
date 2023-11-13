package srv.resources;

import cache.AuthCache;
import cache.HousesCache;
import cache.QuestionsCache;
import data.authentication.AuthResource;
import data.house.HouseDAO;
import data.question.Question;
import data.question.QuestionDAO;
import db.CosmosDBHousesLayer;
import db.CosmosDBQuestionsLayer;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.*;
import java.util.logging.Logger;

/**
 * Resource for managing creating, replying and listing questions.
 */
@Path("/house/{id}/question")
public class QuestionResource {
    private final CosmosDBQuestionsLayer qdb = CosmosDBQuestionsLayer.getInstance();
    private final data.authentication.AuthResource auth = new AuthResource();
    private static final Logger Log = Logger.getLogger(QuestionResource.class.getName());

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@CookieParam("scc:session") Cookie session, @PathParam("id") String houseId, Question question) {
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

    @PATCH
    @Path("/reply")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response reply(@CookieParam("scc:session") Cookie session, @PathParam("id") String houseId,
                          @QueryParam("qid") String questionId, Question question) {
        try {
            List<QuestionDAO> results = QuestionsCache.getQuestionById(questionId);
            if (results.isEmpty()) {
                Log.info("Question does not exist.");
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            QuestionDAO questionDB = results.get(0);
            auth.checkCookieUser(session, questionDB.getOwnerId());

            Log.info("replyQuestion from: " + questionDB.getOwnerId() + " for: " + houseId);

            if (!question.validateReply()) {
                Log.info("Null information was given");
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }
            if(questionDB.getReply() != null) {
                Log.info("Question already replied");
                throw new WebApplicationException(Response.Status.CONFLICT);
            }

            questionDB.setReply(question.getReply());

            qdb.putQuestion(new QuestionDAO(questionDB));
            Log.info("Question replied.");
            return Response.ok().build();
        } catch (WebApplicationException e) {
            throw e;
        } catch(Exception e) {
            throw new InternalServerErrorException(e);
        }

    }

    @GET
    @Path("/list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response list(@CookieParam("scc:session") Cookie session, @PathParam("id") String houseId) {
        try {
            Log.info("listQuestions for: " + houseId);

            List<HouseDAO> results = HousesCache.getHouseById(houseId);
            if (results.isEmpty()) {
                Log.info("House does not exist.");
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            String ownerId = results.get(0).getOwnerId();
            auth.checkCookieUser(session, ownerId);

            List<QuestionDAO> questions = QuestionsCache.getHouseQuestions(houseId);
            Log.info("Questions retrieved.");
            return Response.ok(questions).build();
        } catch (WebApplicationException e) {
            throw e;
        } catch(Exception e) {
            throw new InternalServerErrorException(e);
        }

    }


}
