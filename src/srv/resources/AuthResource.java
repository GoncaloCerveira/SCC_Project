package srv.resources;

import cache.AuthCache;
import cache.UsersCache;
import data.authentication.Login;
import data.authentication.Session;
import data.user.UserDAO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Path("/user/auth")
public class AuthResource {

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response auth(Login login) {
        boolean pwdOk = false;
        String id = login.getUser();

        List<UserDAO> results = UsersCache.getUserById(id);
        if(results.isEmpty()) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        UserDAO user = results.get(0);
        if (Objects.equals(login.getPwd(), user.getPwd())){
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
            AuthCache.putSession(new Session(uid,id));
            return Response.ok().cookie(cookie).build();
        } else {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

    }


}
