package srv.resources;

import data.User;
import data.UserDAO;
import db.CosmosDBUsersLayer;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import utils.Hash;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/user")
public class UsersResource {

    private CosmosDBUsersLayer db;
    private MediaResource media;

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String create(String nick, String name, String pwd, byte[] photo) {
        if(nick == null || name == null || pwd == null || photo == null)
            return "FAILED - mandatory information needs to be filled (nickname, name, pwd, profile photo)";
        boolean exists = db.getUserById(nick).iterator().hasNext();
        if(exists)
            return "FAILED - "+nick+" already exists.";
        media.uploadImage(nick, photo);
        User user = new User(nick, name, Hash.of(pwd));
        db.putUser(new UserDAO(user));
        return "SUCCESS - "+nick+" was successfully added.";
    }

    @PATCH
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String update(@PathParam("id") String nick, String name, String pwd, byte[] photo) {
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
