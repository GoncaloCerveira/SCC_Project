package utils;

public class AzureProperties {
    private static final String BLOB_KEY = "BlobStoreConnection";
    private static final String COSMOSDB_KEY = "COSMOSDB_KEY";
    private static final String COSMOSDB_URL = "COSMOSDB_URL";
    private static final String COSMOSDB_DATABASE = "COSMOSDB_DATABASE";
    public static String CONNECTION_URL = "";
    public static String DB_KEY = "";
    public static String DB_NAME = "";
    public static String STORAGE_CONNECTION_STRING = "";

    public AzureProperties() {}

    public static void setKeys() {
        CONNECTION_URL = System.getenv(COSMOSDB_URL);
        DB_KEY = System.getenv(COSMOSDB_KEY);
        DB_NAME = System.getenv(COSMOSDB_DATABASE);
        STORAGE_CONNECTION_STRING = System.getenv(BLOB_KEY);
    }
}