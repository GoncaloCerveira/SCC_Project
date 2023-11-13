package cache;

import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.core.type.TypeReference;
import data.media.MediaDAO;
import db.CosmosDBMediaLayer;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MediaCache extends RedisCache {
    private static final CosmosDBMediaLayer mdb = CosmosDBMediaLayer.getInstance();

    public static List<MediaDAO> getMediaByItemId(String itemId) {
        List<MediaDAO> media = readFromCache("getMediaByItemId", itemId, new TypeReference<>() {});

        if(media != null) {
            return media;
        }
        CosmosPagedIterable<MediaDAO> mediaDB = mdb.getMediaByItemId(itemId);
        if(mediaDB.iterator().hasNext()) {
            writeToCache("getMediaByItemId", itemId, mediaDB);
        }

        return mediaDB.stream().toList();
    }
}
