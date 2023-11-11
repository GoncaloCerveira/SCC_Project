package srv.resources;

import data.question.Question;
import data.question.QuestionDAO;
import db.CosmosDBQuestionsLayer;

import jakarta.ws.rs.*;
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
    private static final Logger Log = Logger.getLogger(QuestionResource.class.getName());

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@PathParam("id") String houseId, Question question) {
        Log.info("createQuestion from: " + question.getUserId() + " for: " + houseId);

        if(!question.validateCreate()) {
            Log.info("Null information was given");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        String userId = question.getUserId();
        boolean exists = qdb.getQuestionByHouseAndUser(houseId, userId).iterator().hasNext();
        if(exists) {
            Log.info("Question already exists.");
            throw new WebApplicationException(Response.Status.CONFLICT);
        }

        qdb.postQuestion(new QuestionDAO(question));
        Log.info("Question created.");
        return Response.ok().build();
    }

    @PATCH
    @Path("/reply")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response reply(@PathParam("id") String houseId, Question question) {
        Log.info("replyQuestion from: " + question.getUserId() + " for: " + houseId);

        if(!question.validateReply()) {
            Log.info("Null information was given");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        String userId = question.getUserId();
        boolean exists = qdb.getQuestionByHouseAndUser(houseId, userId).iterator().hasNext();
        if(!exists) {
            Log.info("Question does not exist.");
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        QuestionDAO toUpdate = qdb.getQuestionByHouseAndUser(houseId, userId).iterator().next();
        toUpdate.setReply(question.getReply());

        qdb.putQuestion(new QuestionDAO(toUpdate));
        Log.info("Question replied.");
        return Response.ok().build();
    }

    @GET
    @Path("/list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response list(@PathParam("id") String houseId) {
        Log.info("listQuestions for: " + houseId);

        if(houseId == null) {
            Log.info("Null information was given");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        Iterator<QuestionDAO> questions = qdb.getHouseQuestions(houseId).iterator();
        if(!questions.hasNext()) {
            Log.info("House does not exist or has no questions.");
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        Log.info("Questions retrieved.");
        return Response.ok(questions).build();
    }
}
