package srv.resources;

import cache.*;
import data.user.User;
import data.user.UserDAO;

import db.CosmosDBUsersLayer;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import utils.AuthValidation;
import utils.MultiPartFormData;

import java.util.List;

@Path("/user")
public class UserResource {
    private final CosmosDBUsersLayer udb = CosmosDBUsersLayer.getInstance();
    private final MediaResource media = new MediaResource();
    private final AuthValidation auth = new AuthValidation();

    @POST
    @Path("/")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(byte[] formData) {
        MultiPartFormData<User> mpfd = new MultiPartFormData<>();
        mpfd.extractItemMedia(formData, User.class);

        User user = mpfd.getItem();
        byte[] contents = mpfd.getMedia();

        if(!user.validateCreate() || contents.length <= 2) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        boolean empty = UsersCache.getUserById(user.getId()).isEmpty();
        if(!empty) {
            throw new WebApplicationException(Response.Status.CONFLICT);
        }

        String mediaId = media.uploadImage(contents);
        user.setPhotoId(mediaId);

        udb.postUser(new UserDAO(user));
        return Response.ok(user).build();
    }

    @PATCH
    @Path("/{id}/update")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@CookieParam("scc:session") Cookie session, @PathParam("id") String id, byte[] formData) {
        try{
            auth.checkCookieUser(session, id);

            MultiPartFormData<User> mpfd = new MultiPartFormData<>();
            mpfd.extractItemMedia(formData, User.class);

            User user = mpfd.getItem();
            byte[] contents = mpfd.getMedia();

            List<UserDAO> results = UsersCache.getUserById(id);
            if(results.isEmpty()) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            UserDAO userDB = results.get(0);
            String name = user.getName();
            String pwd = user.getPwd();

            if(name != null) {
                userDB.setName(name);
            }
            if(pwd != null) {
                userDB.setPwd(pwd);
            }
            if(contents.length > 2) {
                media.updateImage(contents, userDB.getPhotoId());
            }

            udb.putUser(userDB);
            return Response.ok().build();
        } catch (WebApplicationException e) {
            throw e;
        } catch(Exception e) {
            throw new InternalServerErrorException(e);
        }

    }

    @DELETE
    @Path("/{id}/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@CookieParam("scc:session") Cookie session, @PathParam("id") String id) {
        try{
            auth.checkCookieUser(session, id);

            List<UserDAO> results = UsersCache.getUserById(id);
            if(results.isEmpty()) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            UserDAO userDB = results.get(0);

            media.deleteFile("images", userDB.getPhotoId());

            udb.delUser(userDB);
            return Response.ok().build();
        } catch (WebApplicationException e) {
            throw e;
        } catch(Exception e) {
            throw new InternalServerErrorException(e);
        }

    }

    @GET
    @Path("/{id}/houses")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response listHouses(@CookieParam("scc:session") Cookie session, @PathParam("id") String id,
                               @QueryParam("st") String st , @QueryParam("len") String len) {
        try{
            auth.checkCookieUser(session, id);

            List<UserDAO> results = UsersCache.getUserById(id);
            if(results.isEmpty()) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            return Response.ok(HousesCache.getUserHouses(st, len, id)).build();
        } catch (WebApplicationException e) {
            throw e;
        } catch(Exception e) {
            throw new InternalServerErrorException(e);
        }

    }

    @GET
    @Path("/{id}/rentals")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response listRentals(@CookieParam("scc:session") Cookie session, @PathParam("id") String id,
                                @QueryParam("st") String st , @QueryParam("len") String len) {
        try{
            auth.checkCookieUser(session, id);

            List<UserDAO> results = UsersCache.getUserById(id);
            if(results.isEmpty()) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            return Response.ok(RentalsCache.getUserRentals(st, len, id)).build();
        } catch (WebApplicationException e) {
            throw e;
        } catch(Exception e) {
            throw new InternalServerErrorException(e);
        }

    }


}
