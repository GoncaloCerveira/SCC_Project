package cache;

import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.core.type.TypeReference;
import data.availability.AvailabilityDAO;
import data.rental.RentalDAO;
import db.CosmosDBAvailabilitiesLayer;
import db.CosmosDBRentalsLayer;

import java.util.List;

public class AvailabilityCache extends RedisCache {
    private static final CosmosDBAvailabilitiesLayer adb = CosmosDBAvailabilitiesLayer.getInstance();

    public static List<String> getHouseIdByPeriodLocation(String st, String len, String initDate, String endDate) {
        String key = initDate + endDate;
        List<String> houseIds = readFromCache("getHouseIdByPeriodLocation", key, new TypeReference<>() {});

        if(houseIds != null) {
            return houseIds;
        }

        CosmosPagedIterable<String> houseIdsDB = adb.getHouseIdByPeriodLocation(st, len, initDate, endDate);
        if(houseIdsDB.iterator().hasNext()) {
            writeToCache("getHouseIdByPeriodLocation", key, houseIdsDB);
        }
        return houseIdsDB.stream().toList();
    }


}
