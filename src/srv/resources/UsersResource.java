package srv.resources;

import data.User;
import data.UserDAO;
import db.CosmosDBUsersLayer;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import utils.Hash;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.net.http.HttpResponse;
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
    public void create(User user, byte[] photo) {
        Log.info("createUser : " + user.getId());
        if(user.getId() == null || user.getName() == null || user.getPwd() == null || photo == null) {
            Log.info("Null information was given");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        String nick = user.getId();
        boolean exists = db.getUserById(nick).iterator().hasNext();
        if(exists) {
            Log.info("User already exists.");
            throw new WebApplicationException(Response.Status.CONFLICT);
        }

        media.uploadImage(nick, photo);
        String hashedPwd = Hash.of(user.getPwd());
        user.setPwd(hashedPwd);
        db.putUser(new UserDAO(user));
    }

    @PATCH
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String update(@PathParam("id") String nick, User user, byte[] photo) {
        Log.info("updateUser : " + nick);

        boolean exists = db.getUserById(nick).iterator().hasNext();
        if(!exists)
            return "FAILED - "+nick+" does not belong to any user. You can't change nickname.";
        if(photo != null)
            media.uploadImage(nick, photo);
        UserDAO toUpdate = db.getUserById(nick).iterator().next();
        if(name != null)
          toUpdate.setName(name);
        if(pwd != null)
           toUpdate.setPwd(Hash.of(pwd));
        db.putUser(toUpdate);
        return "SUCCESS - "+nick+" was successfully added.";
    }

}
