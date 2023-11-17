package cache;

import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.core.type.TypeReference;
import data.media.MediaDAO;
import db.CosmosDBMediaLayer;

import java.util.List;

public class MediaCache extends RedisCache {
    private static final CosmosDBMediaLayer mdb = CosmosDBMediaLayer.getInstance();

    public static List<MediaDAO> getItemMedia(String st, String len, String itemId) {
        String key = st + len + itemId;
        List<MediaDAO> media = readFromCache("getItemMedia", key, new TypeReference<>() {});

        if(media != null) {
            return media;
        }

        CosmosPagedIterable<MediaDAO> mediaDB = mdb.getItemMedia(st, len, itemId);
        if(mediaDB.iterator().hasNext()) {
            writeToCache("getItemMedia", key, mediaDB);
        }

        return mediaDB.stream().toList();
    }
}
