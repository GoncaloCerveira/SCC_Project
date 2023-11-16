package srv.resources;

import cache.*;
import data.availability.AvailabilityDAO;
import data.house.House;
import data.house.HouseDAO;

import data.media.MediaDAO;
import db.CosmosDBAvailabilitiesLayer;
import db.CosmosDBHousesLayer;
import db.CosmosDBMediaLayer;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import utils.AuthValidation;
import utils.MultiPartFormData;

import java.util.*;
import java.util.logging.Logger;

@Path("/house")
public class HouseResource {
    private final CosmosDBHousesLayer hdb = CosmosDBHousesLayer.getInstance();
    private final CosmosDBMediaLayer mdb = CosmosDBMediaLayer.getInstance();
    private final CosmosDBAvailabilitiesLayer adb = CosmosDBAvailabilitiesLayer.getInstance();
    private final AuthValidation auth = new AuthValidation();
    private final MediaResource media = new MediaResource();

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

            if (!house.createValidate() || contents.length == 0) {
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }

            boolean empty = UsersCache.getUserById(ownerId).isEmpty();
            if (empty) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            String houseId = UUID.randomUUID().toString();
            house.setId(houseId);

            String mediaId = media.uploadImage(contents);
            mdb.postMedia(new MediaDAO(mediaId, house.getId()));

            hdb.postHouse(new HouseDAO(house));
            return Response.ok(house).build();
        } catch (WebApplicationException e) {
            throw e;
        } catch(Exception e) {
            throw new InternalServerErrorException(e);
        }

    }

    @GET
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response listHouses(@QueryParam("location") String location,
                               @QueryParam("initDate") String initDate, @QueryParam("endDate") String endDate,
                               @QueryParam("st") String st , @QueryParam("len") String len) {
        List<String> houseIds;
        List<HouseDAO> houses;
        if(initDate != null && endDate != null) {
            houseIds = AvailabilityCache.getHouseIdByPeriodLocation(st, len, initDate, endDate);
            houses = HousesCache.getHousesById(st, len, houseIds);
        }
        else if(location != null) {
            houses = HousesCache.getHousesByLocation(st, len, location);
        } else {
            houses = HousesCache.getHouses(st, len);
        }

        return Response.ok(houses).build();
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

            List<HouseDAO> results = HousesCache.getHouseById(id);
            if(results.isEmpty()) {
                throw new WebApplicationException(Response.Status.CONFLICT);
            }

            HouseDAO houseDB = results.get(0);
            auth.checkCookieUser(session, houseDB.getOwnerId());

            String ownerId = house.getOwnerId();
            boolean empty = UsersCache.getUserById(ownerId).isEmpty();
            if (empty) {
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

            boolean empty = HousesCache.getHouseById(id).isEmpty();
            if (empty) {
                throw new WebApplicationException(Response.Status.CONFLICT);
            }

            HouseDAO house = HousesCache.getHouseById(id).get(0);

            auth.checkCookieUser(session, house.getOwnerId());

            for (MediaDAO mediaDAO : MediaCache.getMediaByItemId(id)) {
                media.deleteFile("images", mediaDAO.getId());
            }

            hdb.delHouse(house);
            return Response.ok().build();
        } catch (WebApplicationException e) {
            throw e;
        } catch(Exception e) {
            throw new InternalServerErrorException(e);
        }

    }

    @POST
    @Path("/{houseId}/available")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response availability(@CookieParam("scc:session") Cookie session, @PathParam("houseId") String houseId, AvailabilityDAO availability){
        try {
            List<HouseDAO> houses = HousesCache.getHouseById(houseId);
            if (houses.isEmpty()) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            HouseDAO houseDB = houses.get(0);
            auth.checkCookieUser(session, houseDB.getOwnerId());

            if(!availability.validate()) {
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }

            String[] fromSplit = availability.getFromDate().split("/");
            String[] toSplit = availability.getToDate().split("/");
            if (fromSplit.length != 2 || toSplit.length != 2) {
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }
            int fromMonth = Integer.parseInt(fromSplit[0]);
            int fromYear = Integer.parseInt(fromSplit[1]);
            int toMonth = Integer.parseInt(toSplit[0]);
            int toYear = Integer.parseInt(toSplit[1]);
            int numSlots = toMonth - fromMonth + (toYear - fromYear) * 12;

            availability.setLocation(houseDB.getLocation());
            availability.setHouseId(houseId);
            for(int i = 0 ; i < numSlots ; i++) {
                String id = UUID.randomUUID().toString();
                availability.setId(id);
                availability.setFromDate(fromMonth + "/" + fromYear);

                fromMonth = fromMonth % 12 + 1;
                fromYear = fromYear + (1 / fromMonth);

                availability.setToDate(fromMonth + "/" + fromYear);
                adb.postAvailability(availability);
            }
            return Response.ok().build();
        } catch (WebApplicationException e) {
            throw e;
        } catch(Exception e) {
            throw new InternalServerErrorException(e);
        }
    }

    @GET
    @Path("/discount")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response listDiscounts(@QueryParam("st") String st , @QueryParam("len") String len) {
        return Response.ok(HousesCache.getHouseDiscounts(st, len)).build();
    }


}