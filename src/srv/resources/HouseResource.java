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

            String ownerId = house.getOwnerId();

            auth.checkCookieUser(session, ownerId);

            Log.info("createHouse of : " + ownerId);

            if (!house.createValidate() || contents.length == 0) {
                Log.info("Null information was given");
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }

            boolean empty = UsersCache.getUserById(ownerId).isEmpty();
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

            Log.info("updateHouse : " + id);

            List<HouseDAO> results = HousesCache.getHouseById(id);
            if(results.isEmpty()) {
                Log.info("A house with the given id does not exist.");
                throw new WebApplicationException(Response.Status.CONFLICT);
            }

            HouseDAO houseDB = results.get(0);
            auth.checkCookieUser(session, houseDB.getOwnerId());

            String ownerId = house.getOwnerId();
            boolean empty = UsersCache.getUserById(ownerId).isEmpty();
            if (empty) {
                Log.info("A user with the given id does not exist.");
                throw new WebApplicationException(Response.Status.CONFLICT);
            }

            String name = house.getName();
            String location = house.getLocation();
            String description = house.getDescription();

            if (name!= null) {
                houseDB.setName(name);
            }
            if (location != null) {
                houseDB.setLocation(location);
            }
            if (ownerId != null) {
                houseDB.setOwnerId(ownerId);
            }
            if (description != null) {
                houseDB.setDescription(description);
            }
            if (contents.length > 0) {
                String mediaId = media.uploadImage(contents);
                mdb.postMedia(new MediaDAO(mediaId, id));
            }

            hdb.putHouse(houseDB);
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

    @POST
    @Path("/{id}/available")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response availability(@CookieParam("scc:session") Cookie session, @PathParam("id") String id, HouseDAO house){
        try {
            Log.info("houseAvailable: " + id);

            boolean empty = HousesCache.getHouseById(id).isEmpty();
            if (empty) {
                Log.info("A house with the given id does not exist.");
                throw new WebApplicationException(Response.Status.CONFLICT);
            }

            HouseDAO houseDB = HousesCache.getHouseById(id).get(0);
            auth.checkCookieUser(session, house.getOwnerId());

            String startDate = house.getStartDate();
            String endDate = house.getEndDate();
            int cost = house.getCost();
            int discount = house.getDiscount();

            if (startDate != null) {
                houseDB.setStartDate(startDate);
            }
            if(endDate != null){
                houseDB.setEndDate(endDate);
            }
            if(cost != 0){
                houseDB.setCost(cost);
            }
            if (discount <= 30){
                houseDB.setDiscount(discount);
            }

            hdb.putHouse(houseDB);
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