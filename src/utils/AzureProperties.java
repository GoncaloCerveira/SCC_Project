package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AzureProperties {
    private static final String PROPS_FILE = "azurekeys-westeurope.props";
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
    public static String CACHE_HOSTNAME = "";
    public static String CACHE_KEY = "";

    public AzureProperties() {}

    public static void setKeys() {
        CONNECTION_URL = System.getenv(COSMOSDB_URL);
        DB_KEY = System.getenv(COSMOSDB_KEY);
        DB_NAME = System.getenv(COSMOSDB_DATABASE);
        STORAGE_CONNECTION_STRING = System.getenv(BLOB_KEY);
        CACHE_HOSTNAME = System.getenv(REDIS_URL);
        CACHE_KEY = System.getenv(REDIS_KEY);
    }

    public static void setLocalKeys() {
        Properties props = new Properties();
        try {
            props.load( new FileInputStream(PROPS_FILE));
        } catch (IOException e) {
            // do nothing
        }

        CONNECTION_URL = props.getProperty(COSMOSDB_URL);
        DB_KEY = props.getProperty(COSMOSDB_KEY);
        DB_NAME = props.getProperty(COSMOSDB_DATABASE);
        STORAGE_CONNECTION_STRING = props.getProperty(BLOB_KEY);
        CACHE_HOSTNAME = props.getProperty(REDIS_URL);
        CACHE_KEY = props.getProperty(REDIS_KEY);
    }


}