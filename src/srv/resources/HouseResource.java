package srv.resources;

import data.house.House;
import data.house.HouseDAO;

import db.CosmosDBHousesLayer;
import db.CosmosDBUsersLayer;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.*;
import java.util.logging.Logger;

@Path("/house")
public class HouseResource {

    private CosmosDBHousesLayer hdb;
    private CosmosDBUsersLayer udb;
    private MediaResource media;
    //private AuthResource auth;
    private static final Logger Log = Logger.getLogger(HouseResource.class.getName());

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@QueryParam("pwd") String pwd, House house) {
        Log.info("createHouse of : " + house.getOwnerID());
        if(house.getId() == null || house.getOwnerID()== null || house.getLocation() == null || pwd == null) {
            Log.info("Null information was given");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        boolean exists = udb.getUserById(house.getOwnerID()).iterator().hasNext();
        if(!exists) {
            Log.info("A user with the given id does not exist.");
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        if(!pwd.equals(udb.getUserById(house.getOwnerID()).iterator().next().getPwd())) {
            Log.info("Password is incorrect.");
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }

        String id = UUID.randomUUID().toString();
        house.setId(id);
        hdb.putHouse(new HouseDAO(house));

        String mediaId = id+"#"+System.currentTimeMillis();
        //media.uploadImage(mediaId, contents);

        Log.info("House added with id: "+id);
        return Response.ok().build();
    }


    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@CookieParam("scc:session") Cookie session, House house) {
        Log.info("createHouse of : " + house.getOwnerID());
        try {
            //check correction of house
            //auth.checkCookie(session, house.getOwnerID());
            //create house
            return Response.ok().build();
        } catch (WebApplicationException e) {
            throw e;
        } catch(Exception e) {
            throw new InternalServerErrorException(e);
        }
    }



    @PATCH
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") String id, String pwd, House house) {
        Log.info("updateHouse : " + id);
        if(pwd == null) {
            Log.info("Null information was given.");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        if(house.getId() != null) {
            if(!house.getId().equals(id))
                Log.info("House ID cannot be modified.");
        }

        boolean exists = udb.getUserById(house.getOwnerID()).iterator().hasNext();
        if(!exists) {
            Log.info("A user with the given id does not exist.");
            throw new WebApplicationException(Response.Status.CONFLICT);
        }

        if(!pwd.equals(udb.getUserById(house.getOwnerID()).iterator().next().getPwd())) {
            Log.info("Password is incorrect.");
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }

        HouseDAO toUpdate = hdb.getHouseById(id).iterator().next();
        //if(house.getPhoto() != null) {
            //atualizar photo
        //}
        if(house.getOwnerID() != null)
            house.setOwnerID(house.getOwnerID());
        if(house.getLocation() != null)
            house.setLocation(house.getLocation());
        //toUpdate.setPwd(Hash.of(pwd));

        hdb.putHouse(toUpdate);
        Log.info("House updated.");
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") String id, String pwd) {
        Log.info("deleteHouse: " + id);
        if(pwd == null) {
            Log.info("Null information was given.");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        boolean exists = hdb.getHouseById(id).iterator().hasNext();
        if(!exists) {
            Log.info("A house with the given id does not exist.");
            throw new WebApplicationException(Response.Status.CONFLICT);
        }

        HouseDAO house = hdb.getHouseById(id).iterator().next();
        if(!pwd.equals(udb.getUserById(house.getOwnerID()).iterator().next().getPwd())) {
            Log.info("Password is incorrect.");
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }

        hdb.delHouseById(id);
        //apagar media desta casa
        //apagar do utilizador e dos rentals

        Log.info("House deleted.");
        return Response.ok().build();
    }

}