package utils;

import static utils.AzureProperties.*;

public class AzureKeys {

    public static String CONNECTION_URL = "";
    public static String DB_KEY = "";
    public static String DB_NAME = "";
    public static String STORAGE_CONNECTION_STRING = "";

    public AzureKeys() {}

    public static void setKeys() {
        CONNECTION_URL = AzureProperties.getProperties().getProperty(COSMOSDB_URL);
        DB_KEY = AzureProperties.getProperties().getProperty(COSMOSDB_KEY);
        DB_NAME = AzureProperties.getProperties().getProperty(COSMOSDB_DATABASE);
        STORAGE_CONNECTION_STRING = AzureProperties.getProperties().getProperty(BLOB_KEY);
    }
}
