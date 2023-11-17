package srv.resources;

import cache.*;
import data.authentication.Session;
import data.rental.Rental;
import data.rental.RentalDAO;
import data.house.House;
import data.house.HouseDAO;

import data.media.MediaDAO;
import db.CosmosDBHousesLayer;
import db.CosmosDBMediaLayer;

import db.CosmosDBRentalsLayer;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import utils.AuthValidation;
import utils.MultiPartFormData;

import java.util.*;

@Path("/house")
public class HouseResource {
    private final CosmosDBHousesLayer hdb = CosmosDBHousesLayer.getInstance();
    private final CosmosDBRentalsLayer rdb = CosmosDBRentalsLayer.getInstance();
    private final CosmosDBMediaLayer mdb = CosmosDBMediaLayer.getInstance();
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

            Session s = auth.checkCookieUser(session, null);
            String ownerId = s.getName();

            if (!house.createValidate() || contents.length <= 2) {
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

    @PUT
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
            auth.checkCookieUser(session, houseDB.getOwner());

            String ownerId = house.getOwner();
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
                houseDB.setOwner(ownerId);
            }
            if (description != null) {
                houseDB.setDescription(description);
            }
            if (contents.length > 2) {
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
    @Path("/{houseId}/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@CookieParam("scc:session") Cookie session, @PathParam("houseId") String houseId) {
        try {

            boolean empty = HousesCache.getHouseById(houseId).isEmpty();
            if (empty) {
                throw new WebApplicationException(Response.Status.CONFLICT);
            }

            HouseDAO house = HousesCache.getHouseById(houseId).get(0);

            auth.checkCookieUser(session, house.getOwner());

            for (MediaDAO mediaDAO : MediaCache.getItemMedia(null, null, houseId)) {
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
    public Response makeAvailable(@CookieParam("scc:session") Cookie session, @PathParam("houseId") String houseId, Rental rental){
        try {
            List<HouseDAO> houses = HousesCache.getHouseById(houseId);
            if (houses.isEmpty()) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            HouseDAO houseDB = houses.get(0);
            auth.checkCookieUser(session, houseDB.getOwner());

            if(!rental.validate()) {
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }

            String[] fromSplit = rental.getFromDate().split("-");
            String[] toSplit = rental.getToDate().split("-");
            if (fromSplit.length != 2 || toSplit.length != 2) {
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }
            int fromMonth = Integer.parseInt(fromSplit[0]);
            int fromYear = Integer.parseInt(fromSplit[1]);
            int toMonth = Integer.parseInt(toSplit[0]);
            int toYear = Integer.parseInt(toSplit[1]);
            int numSlots = toMonth - fromMonth + (toYear - fromYear) * 12;


            rental.setHouse(houseId);
            rental.setLocation(houseDB.getLocation());
            rental.setFree(true);
            for(int i = 0 ; i < numSlots ; i++) {
                String id = UUID.randomUUID().toString();
                rental.setId(id);
                rental.setFromDate(fromMonth + "-" + fromYear);

                fromMonth = fromMonth % 12 + 1;
                fromYear = fromYear + (1 / fromMonth);

                rental.setToDate(fromMonth + "-" + fromYear);
                rdb.postRental(new RentalDAO(rental));
            }
            return Response.ok().build();
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
    public Response list(@QueryParam("location") String location,
                         @QueryParam("initDate") String initDate, @QueryParam("endDate") String endDate,
                         @QueryParam("st") String st , @QueryParam("len") String len) {
        List<String> houseIds;
        List<HouseDAO> houses;
        if(initDate != null && endDate != null) {
            houseIds = RentalsCache.getHouseIdsByPeriodLocation(st, len, initDate, endDate, location);
            houses = HousesCache.getHousesById(st, len, houseIds);
        }
        else if(location != null) {
            houses = HousesCache.getHousesByLocation(st, len, location);
        } else {
            houses = HousesCache.getHouses(st, len);
        }

        return Response.ok(houses).build();
    }

    @GET
    @Path("/discount")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response listDiscounts(@QueryParam("st") String st , @QueryParam("len") String len) {
        return Response.ok(HousesCache.getHousesOnDiscount(st, len)).build();
    }


}