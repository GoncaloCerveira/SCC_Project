package srv.resources;

import cache.QuestionsCache;
import data.authentication.AuthResource;
import data.question.Question;
import data.question.QuestionDAO;
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

            auth.checkCookieUser(session, question.getUserId());

            if (!question.validateCreate()) {
                Log.info("Null information was given");
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }

            String userId = question.getUserId();
            boolean empty = QuestionsCache.getQuestionByHouseAndUser(houseId, userId).isEmpty();
            if (!empty) {
                Log.info("Question already exists.");
                throw new WebApplicationException(Response.Status.CONFLICT);
            }

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
    public Response reply(@CookieParam("scc:session") Cookie session, @PathParam("id") String houseId, Question question) {

        try {
            Log.info("replyQuestion from: " + question.getUserId() + " for: " + houseId);

            if (!question.validateReply()) {
                Log.info("Null information was given");
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }

            String userId = question.getUserId();
            boolean empty = QuestionsCache.getQuestionByHouseAndUser(houseId, userId).isEmpty();
            if (empty) {
                Log.info("Question does not exist.");
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            auth.checkCookieUser(session, question.getOwnerId());

            QuestionDAO toUpdate = QuestionsCache.getQuestionByHouseAndUser(houseId, userId).get(0);
            toUpdate.setReply(question.getReply());

            qdb.putQuestion(new QuestionDAO(toUpdate));
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

            // TODO Get house ownerID

            if (houseId == null) {
                Log.info("Null information was given");
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }

            List<QuestionDAO> questions = QuestionsCache.getHouseQuestions(houseId);
            if (questions.isEmpty()) {
                Log.info("House does not exist or has no questions.");
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            Log.info("Questions retrieved.");
            return Response.ok(questions).build();
        } catch (WebApplicationException e) {
            throw e;
        } catch(Exception e) {
            throw new InternalServerErrorException(e);
        }
    }


}
