package srv.resources;

import data.User;
import data.UserDAO;
import db.CosmosDBUsersLayer;
import jakarta.ws.rs.*;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.logging.Logger;

@Path("/user")
public class UsersResource {

    private CosmosDBUsersLayer db;
    private MediaResource media;
    private static final Logger Log = Logger.getLogger(UsersResource.class.getName());

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(User user) {
        Log.info("createUser : " + user.getId());
        if(user.getId() == null || user.getName() == null || user.getPwd() == null || user.getPhotoId() == null) {
            Log.info("Null information was given");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        String id = user.getId();
        boolean exists = db.getUserById(id).iterator().hasNext();
        if(exists) {
            Log.info("User already exists.");
            throw new WebApplicationException(Response.Status.CONFLICT);
        }

        //String hashedPwd = Hash.of(user.getPwd());
        //user.setPwd(hashedPwd);
        db.putUser(new UserDAO(user));
        Log.info("User created.");
        return Response.ok().build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") String id, User user) {
        Log.info("updateUser : " + id);

        boolean exists = db.getUserById(id).iterator().hasNext();
        if(!exists) {
            Log.info("User does not exist.");
            throw new WebApplicationException(Response.Status.CONFLICT);
        }

        UserDAO toUpdate = db.getUserById(id).iterator().next();
        if(user.getPhotoId() != null) {
            if(!media.fileExists("images", user.getPhotoId())) {
                Log.info("ID of photo does not exist");
                throw new WebApplicationException(Response.Status.CONFLICT);
            }
            else toUpdate.setPhotoId(user.getPhotoId());
        }
        if(user.getName() != null)
            toUpdate.setName(user.getName());
        if(user.getPwd() != null)
           //toUpdate.setPwd(Hash.of(pwd));
            toUpdate.setPwd(user.getPwd());
        db.putUser(toUpdate);
        Log.info("User updated.");
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") String id) {
        Log.info("deleteUser : " + id);

        boolean exists = db.getUserById(id).iterator().hasNext();
        if(!exists) {
            Log.info("User does not exist.");
            throw new WebApplicationException(Response.Status.CONFLICT);
        }

        UserDAO toDelete = db.getUserById(id).iterator().next();
        db.delUser(toDelete);
        Log.info("User deleted.");
        return Response.ok().build();
    }



}
