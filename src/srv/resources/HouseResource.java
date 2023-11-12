package srv.resources;

import data.house.House;
import data.house.HouseDAO;

import data.media.MediaDAO;
import db.CosmosDBHousesLayer;
import db.CosmosDBMediaLayer;
import db.CosmosDBUsersLayer;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import utils.MultiPartFormData;

import java.util.*;
import java.util.logging.Logger;

@Path("/house")
public class HouseResource {
    private final CosmosDBHousesLayer hdb = CosmosDBHousesLayer.getInstance();
    private final CosmosDBUsersLayer udb = CosmosDBUsersLayer.getInstance();
    private final CosmosDBMediaLayer mdb = CosmosDBMediaLayer.getInstance();
    private final MediaResource media = new MediaResource();
    private static final Logger Log = Logger.getLogger(HouseResource.class.getName());

    @POST
    @Path("/")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(byte[] formData) {
        MultiPartFormData<House> mpfd = new MultiPartFormData<>();
        mpfd.extractItemMedia(formData, House.class);

        House house = mpfd.getItem();
        byte[] contents = mpfd.getMedia();

        Log.info("createHouse of : " + house.getOwnerId());

        if(!house.validate() || contents.length == 0) {
            Log.info("Null information was given");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        boolean exists = udb.getUserById(house.getOwnerId()).iterator().hasNext();
        if(!exists) {
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
    }

    @PATCH
    @Path("/{id}/update")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") String id, byte[] formData) {
        MultiPartFormData<House> mpfd = new MultiPartFormData<>();
        mpfd.extractItemMedia(formData, House.class);

        House house = mpfd.getItem();
        byte[] contents = mpfd.getMedia();

        Log.info("updateHouse : " + id);

        if(house.getId() != null) {
            if(!house.getId().equals(id))
                Log.info("House ID cannot be modified.");
        }

        boolean exists = udb.getUserById(house.getOwnerId()).iterator().hasNext();
        if(!exists) {
            Log.info("A user with the given id does not exist.");
            throw new WebApplicationException(Response.Status.CONFLICT);
        }

        HouseDAO toUpdate = hdb.getHouseById(id).iterator().next();

        if(house.getOwnerId() != null) {
            toUpdate.setOwnerId(house.getOwnerId());
        }
        if(house.getLocation() != null) {
            toUpdate.setLocation(house.getLocation());
        }
        if(contents.length > 0) {
            String mediaId = media.uploadImage(contents);
            mdb.postMedia(new MediaDAO(mediaId, id));
        }

        hdb.putHouse(toUpdate);
        Log.info("House updated.");
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") String id) {
        Log.info("deleteHouse: " + id);

        boolean exists = hdb.getHouseById(id).iterator().hasNext();
        if(!exists) {
            Log.info("A house with the given id does not exist.");
            throw new WebApplicationException(Response.Status.CONFLICT);
        }

        HouseDAO house = hdb.getHouseById(id).iterator().next();

        for (MediaDAO mediaDAO : mdb.getMediaByItemId(id)) {
            media.deleteFile("images", mediaDAO.getId());
        }

        hdb.delHouse(house);
        Log.info("House deleted.");
        return Response.ok().build();
    }

    @GET
    @Path("/{location}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response listLocation(@PathParam("location") String location) {
        Log.info("listLocation: " + location);

        return Response.ok(hdb.getHousesByLocation(location)).build();
    }


}