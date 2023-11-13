package srv.resources;

import cache.HousesCache;
import cache.MediaCache;
import cache.UsersCache;
import data.authentication.AuthResource;
import data.house.House;
import data.house.HouseDAO;

import data.media.MediaDAO;
import db.CosmosDBHousesLayer;
import db.CosmosDBMediaLayer;
import db.CosmosDBUsersLayer;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import utils.MultiPartFormData;

import java.util.*;
import java.util.logging.Logger;

@Path("/house")
public class HouseResource {
    private final CosmosDBHousesLayer hdb = CosmosDBHousesLayer.getInstance();
    private final CosmosDBMediaLayer mdb = CosmosDBMediaLayer.getInstance();
    private final MediaResource media = new MediaResource();
    private final data.authentication.AuthResource auth = new AuthResource();
    private static final Logger Log = Logger.getLogger(HouseResource.class.getName());

    @POST
    @Path("/")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@CookieParam("scc:session") Cookie session, byte[] formData) {

        try {

            MultiPartFormData<House> mpfd = new MultiPartFormData<>();
            mpfd.extractItemMedia(formData, House.class);

            House house = mpfd.getItem();
            byte[] contents = mpfd.getMedia();

            auth.checkCookieUser(session, house.getOwnerId());

            Log.info("createHouse of : " + house.getOwnerId());

            if (!house.validate() || contents.length == 0) {
                Log.info("Null information was given");
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }

            boolean empty = UsersCache.getUserById(house.getOwnerId()).isEmpty();
            if (empty) {
                Log.info("A user with the given id does not exist.");
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            String id = UUID.randomUUID().toString();
            house.setId(id);

            String mediaId = media.uploadImage(contents);
            mdb.postMedia(new MediaDAO(mediaId, house.getId()));

            hdb.postHouse(new HouseDAO(house));
            Log.info("House added with id: " + id);
            return Response.ok().build();
        } catch (WebApplicationException e) {
            throw e;
        } catch(Exception e) {
            throw new InternalServerErrorException(e);
        }
    }

    @PATCH
    @Path("/{id}/update")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@CookieParam("scc:session") Cookie session, @PathParam("id") String id, byte[] formData) {

        try {

            MultiPartFormData<House> mpfd = new MultiPartFormData<>();
            mpfd.extractItemMedia(formData, House.class);

            House house = mpfd.getItem();
            byte[] contents = mpfd.getMedia();

            auth.checkCookieUser(session, house.getOwnerId());

            Log.info("updateHouse : " + id);

            if (house.getId() != null) {
                if (!house.getId().equals(id))
                    Log.info("House ID cannot be modified.");
            }

            boolean empty = UsersCache.getUserById(house.getOwnerId()).isEmpty();
            if (empty) {
                Log.info("A user with the given id does not exist.");
                throw new WebApplicationException(Response.Status.CONFLICT);
            }

            HouseDAO toUpdate = HousesCache.getHouseById(id).get(0);

            if (house.getOwnerId() != null) {
                toUpdate.setOwnerId(house.getOwnerId());
            }
            if (house.getLocation() != null) {
                toUpdate.setLocation(house.getLocation());
            }
            if (contents.length > 0) {
                String mediaId = media.uploadImage(contents);
                mdb.postMedia(new MediaDAO(mediaId, id));
            }

            hdb.putHouse(toUpdate);
            Log.info("House updated.");
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

        try {
            Log.info("deleteHouse: " + id);

            boolean empty = HousesCache.getHouseById(id).isEmpty();
            if (empty) {
                Log.info("A house with the given id does not exist.");
                throw new WebApplicationException(Response.Status.CONFLICT);
            }

            HouseDAO house = HousesCache.getHouseById(id).get(0);

            auth.checkCookieUser(session, house.getOwnerId());

            for (MediaDAO mediaDAO : MediaCache.getMediaByItemId(id)) {
                media.deleteFile("images", mediaDAO.getId());
            }

            hdb.delHouse(house);
            Log.info("House deleted.");
            return Response.ok().build();
        } catch (WebApplicationException e) {
            throw e;
        } catch(Exception e) {
            throw new InternalServerErrorException(e);
        }
    }

    @GET
    @Path("/{location}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response listLocation(@PathParam("location") String location) {
        Log.info("listLocation: " + location);

        return Response.ok(HousesCache.getHousesByLocation(location)).build();
    }


}