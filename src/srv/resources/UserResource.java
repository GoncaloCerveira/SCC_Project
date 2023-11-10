package srv.resources;

import data.user.User;
import data.user.UserDAO;

import db.CosmosDBUsersLayer;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.logging.Logger;

@Path("/user")
public class UserResource {

    private CosmosDBUsersLayer db = CosmosDBUsersLayer.getInstance();
    private MediaResource media;
    //private AuthResource auth;
    private static final Logger Log = Logger.getLogger(UserResource.class.getName());

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(User user) {
        Log.info("createUser : " + user.getId());
        if(!user.validate()) {
            Log.info("Null information was given");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        boolean exists = db.getUserById(user.getId()).iterator().hasNext();
        if(exists) {
            Log.info("User already exists.");
            throw new WebApplicationException(Response.Status.CONFLICT);
        }

        /*if(!media.fileExists("images", user.getPhotoId())) {
            Log.info("ID of photo does not exist.");
            throw new WebApplicationException(Response.Status.CONFLICT);
        }*/

        db.putUser(new UserDAO(user));
        Log.info("User created.");
        return Response.ok().build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@CookieParam("scc:session") Cookie session, @PathParam("id") String id, User user) {
        Log.info("updateUser : " + id);
        try {
            //auth.checkCookie(session, id);

            var results = db.getUserById(id).iterator();
            if(!results.hasNext()) {
                Log.info("User does not exist.");
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            UserDAO toUpdate = results.next();
            if(user.getPhotoId() != null) {
                if(!media.fileExists("images", user.getPhotoId())) {
                    Log.info("ID of photo does not exist");
                    throw new WebApplicationException(Response.Status.CONFLICT);
                }
                else {
                    toUpdate.setPhotoId(user.getPhotoId());
                    media.deleteFile("images", id);
                }
            }
            if(user.getName() != null)
                toUpdate.setName(user.getName());
            if(user.getPwd() != null)
                //toUpdate.setPwd(Hash.of(pwd));
                toUpdate.setPwd(user.getPwd());
            db.putUser(toUpdate);
            Log.info("User updated.");
            return Response.ok().build();
        } catch (WebApplicationException e) {
            throw e;
        } catch(Exception e) {
            throw new InternalServerErrorException(e);
        }
    }

    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@CookieParam("scc:session") Cookie session, @PathParam("id") String id) {
        Log.info("deleteUser : " + id);
        try {
            //auth.checkCookie(session, id);
            var results = db.getUserById(id).iterator();
            if(!results.hasNext()) {
                Log.info("User does not exist.");
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            UserDAO toDelete = results.next();
            db.delUser(toDelete);
            media.deleteFile("images", id);
            Log.info("User deleted.");
            //apagar a sessão deste utilizador
            return Response.ok().build();
        } catch (WebApplicationException e) {
            throw e;
        } catch(Exception e) {
            throw new InternalServerErrorException(e);
        }
    }

}
