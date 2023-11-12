package srv.resources;

import cache.AuthCache;
import cache.UsersCache;
import data.authentication.Session;
import data.media.MediaDAO;
import data.user.Login;
import data.user.User;
import data.user.UserDAO;

import db.CosmosDBHousesLayer;
import db.CosmosDBMediaLayer;
import db.CosmosDBUsersLayer;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import utils.MultiPartFormData;

import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

@Path("/user")
public class UserResource {
    private final CosmosDBUsersLayer udb = CosmosDBUsersLayer.getInstance();
    private final CosmosDBHousesLayer hdb = CosmosDBHousesLayer.getInstance();
    private final CosmosDBMediaLayer mdb = CosmosDBMediaLayer.getInstance();
    private final MediaResource media = new MediaResource();
    private data.authentication.AuthResource auth;
    private static final Logger Log = Logger.getLogger(UserResource.class.getName());

    @POST
    @Path("/")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(byte[] formData) {
        MultiPartFormData<User> mpfd = new MultiPartFormData<>();
        mpfd.extractItemMedia(formData, User.class);

        User user = mpfd.getItem();
        byte[] contents = mpfd.getMedia();

        Log.info("createUser : " + user.getId());

        if(!user.validate() || contents.length == 0) {
            Log.info("Null information was given");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        boolean exists = UsersCache.getUserById(user.getId()).iterator().hasNext();
        if(exists) {
            Log.info("User already exists.");
            throw new WebApplicationException(Response.Status.CONFLICT);
        }

        String mediaId = media.uploadImage(contents);
        mdb.postMedia(new MediaDAO(mediaId, user.getId()));

        udb.postUser(new UserDAO(user));
        Log.info("User created.");
        return Response.ok(mediaId).build();
    }

    @POST
    @Path("/auth")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response auth(Login user) {
        boolean pwdOk = false;

        var results = UsersCache.getUserById(user.getId()).iterator().next();
        if (Objects.equals(results.getPwd(), user.getPwd())){
            pwdOk = true;
        }

        if( pwdOk) {
            String uid = UUID.randomUUID().toString();
            NewCookie cookie = new NewCookie.Builder("scc:session")
                    .value(uid)
                    .path("/")
                    .comment("sessionid")
                    .maxAge(3600)
                    .secure(false)
                    .httpOnly(true)
                    .build();
            AuthCache.putSession( new Session( uid, user.getId() ));
            return Response.ok().cookie(cookie).build();
        } else
            throw new NotAuthorizedException("Incorrect login");
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

            Log.info("updateUser : " + id);

            var results = UsersCache.getUserById(id).iterator();
            if(!results.hasNext()) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            UserDAO toUpdate = results.next();

            if(user.getName() != null) {
                toUpdate.setName(user.getName());
            }
            if(user.getPwd() != null) {
                toUpdate.setPwd(user.getPwd());
            }
            if(contents.length > 0) {
                String mediaId = media.uploadImage(contents);
                mdb.postMedia(new MediaDAO(mediaId, id));
            }

            udb.putUser(toUpdate);
            Log.info("User updated.");
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

            var results = UsersCache.getUserById(id).iterator();

            if(!results.hasNext()) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            UserDAO toDelete = results.next();

            for (MediaDAO mediaDAO : mdb.getMediaByItemId(id)) {
                media.deleteFile("images", mediaDAO.getId());
            }

            udb.delUser(toDelete);
            return Response.ok().build();
        } catch (WebApplicationException e) {
            throw e;
        } catch(Exception e) {
            throw new InternalServerErrorException(e);
        }
    }

    @GET
    @Path("/{id}/list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response listHouses(@CookieParam("scc:session") Cookie session, @PathParam("id") String id) {

        try{

            auth.checkCookieUser(session, id);

            var results = UsersCache.getUserById(id).iterator();

            if(!results.hasNext()) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            return Response.ok(hdb.getUserHouses(id)).build();
        } catch (WebApplicationException e) {
            throw e;
        } catch(Exception e) {
            throw new InternalServerErrorException(e);
        }
    }


}
