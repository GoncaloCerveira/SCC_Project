package utils;

public class AzureProperties {
    private static final String BLOB_KEY = "BlobStoreConnection";
    private static final String COSMOSDB_KEY = "COSMOSDB_KEY";
    private static final String COSMOSDB_URL = "COSMOSDB_URL";
    private static final String COSMOSDB_DATABASE = "COSMOSDB_DATABASE";
    private static  final String REDIS_URL = "REDIS_URL";
    private static final String REDIS_KEY = "REDIS_KEY";
    public static String CONNECTION_URL = "";
    public static String DB_KEY = "";
    public static String DB_NAME = "";
    public static String STORAGE_CONNECTION_STRING = "";
    public static String CACHE_HOSTNAME = "rediswesteurope68881.redis.cache.windows.net";
    public static String CACHE_KEY = "VtDyZorArQ0rbLFB43uconCp5blgLF1L1AzCaDJUCP8=";

    public AzureProperties() {}

    public static void setKeys() {
        CONNECTION_URL = System.getenv(COSMOSDB_URL);
        DB_KEY = System.getenv(COSMOSDB_KEY);
        DB_NAME = System.getenv(COSMOSDB_DATABASE);
        STORAGE_CONNECTION_STRING = System.getenv(BLOB_KEY);
        CACHE_HOSTNAME = System.getenv(REDIS_URL);
        CACHE_KEY = System.getenv(REDIS_KEY);
    }
}