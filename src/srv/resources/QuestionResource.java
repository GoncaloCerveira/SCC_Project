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

/**
 * Resource for managing creating, replying and listing questions.
 */
@Path("/house/{houseId}/question")
public class QuestionResource {
    private final CosmosDBQuestionsLayer qdb = CosmosDBQuestionsLayer.getInstance();
    private final AuthValidation auth = new AuthValidation();

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@CookieParam("scc:session") Cookie session, @PathParam("houseId") String houseId, Question question) {
        try {
            auth.checkCookieUser(session, null);
            String userId = session.getName();

            if (!question.validateCreate()) {
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }

            boolean empty = QuestionsCache.getQuestionByHouseAndUser(houseId, userId).isEmpty();
            if (!empty) {
                throw new WebApplicationException(Response.Status.CONFLICT);
            }

            question.setUser(userId);
            question.setReply(null);

            qdb.postQuestion(new QuestionDAO(question));
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
    public Response reply(@PathParam("houseId") String houseId, @PathParam("questionId") String questionId,
                                  @BodyParam("reply") String reply) {
        List<HouseDAO> hResults = HousesCache.getHouseById(houseId);
        if (hResults.isEmpty()) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        List<QuestionDAO> qResults = QuestionsCache.getQuestionById(questionId);
        if (qResults.isEmpty()) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        QuestionDAO questionDB = qResults.get(0);
        if(!questionDB.isNoAnswer()) {
            throw new WebApplicationException(Response.Status.CONFLICT);
        }
        questionDB.setReply(reply);
        qdb.putQuestion(questionDB);

        return Response.ok(questionDB).build();
    }

    @GET
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response list(@PathParam("houseId") String houseId, @QueryParam("noanswer") String noAnswer,
                         @QueryParam("st") String st , @QueryParam("len") String len) {
        List<HouseDAO> results = HousesCache.getHouseById(houseId);
        if (results.isEmpty()) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        List<QuestionDAO> questions;
        if(noAnswer != null) {
            questions = QuestionsCache.getHouseQuestionsByStatus(st, len, houseId);
        } else {
            questions = QuestionsCache.getHouseQuestions(st, len, houseId);
        }
        return Response.ok(questions).build();
    }


}
