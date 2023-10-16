package srv.resources;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
/**
 * Resource for managing media files, such as images.
 */
@Path("/media")
public class MediaResource
{
	private final String STORAGE_CONNECTION_STRING = "DefaultEndpointsProtocol=https;AccountName=sccbgmproject1;AccountKey=9nW50uDsrT55AhZn5ZN5bk5P9NM3owOWu6Wr0aA2tnwRvvgEOBYKUyVstvogJNWd2NL3lBDrn1Or+AStsrPiBg==;EndpointSuffix=core.windows.net ";

	/**
	 * Post a new image.The id of the image is its hash.
	 */
	@POST
	@Path("/image/")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces(MediaType.APPLICATION_JSON)
	public String uploadImage(String key, byte[] contents) {
		//String key = Hash.of(contents);
		BlobContainerClient containerClient = new BlobContainerClientBuilder()
				.connectionString(STORAGE_CONNECTION_STRING)
				.containerName("images")
				.buildClient();
		BlobClient blob = containerClient.getBlobClient(key);
		blob.upload(BinaryData.fromBytes(contents));
		System.out.println( "Photo uploaded with key " + key);
		return key;
	}

	@POST
	@Path("/video/")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces(MediaType.APPLICATION_JSON)
	public String uploadVideo(String key, byte[] contents) {
		//String key = Hash.of(contents);
		BlobContainerClient containerClient = new BlobContainerClientBuilder()
				.connectionString(STORAGE_CONNECTION_STRING)
				.containerName("videos")
				.buildClient();
		BlobClient blob = containerClient.getBlobClient(key);
		blob.upload(BinaryData.fromBytes(contents));
		System.out.println( "File uploaded with key " + key);
		return key;
	}

	/**
	 * Post a new image.The id of the image is its hash.
	 */
	@POST
	@Path("/text/")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces(MediaType.APPLICATION_JSON)
	public String uploadText(String key, byte[] contents) {
		//String key = Hash.of(contents);
		//String id = key+"_text";
		BlobContainerClient containerClient = new BlobContainerClientBuilder()
				.connectionString(STORAGE_CONNECTION_STRING)
				.containerName("texts")
				.buildClient();
		BlobClient blob = containerClient.getBlobClient(key);
		blob.upload(BinaryData.fromBytes(contents));
		System.out.println( "File uploaded with key " + key);
		return key;
	}

	/**
	 * Return the contents of an image. Throw an appropriate error message if
	 * id does not exist.
	 */
	@GET
	@Path("/text/{id}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public byte[] downloadText(@PathParam("id") String id) {
		BlobContainerClient containerClient = new BlobContainerClientBuilder()
				.connectionString(STORAGE_CONNECTION_STRING)
				.containerName("texts")
				.buildClient();
		// Get client to blob
		BlobClient blob = containerClient.getBlobClient(id);
		// Download contents to BinaryData (check documentation for other alternatives)
		BinaryData data = blob.downloadContent();
		return data.toBytes();
	}

	/**
	 * Return the contents of an image. Throw an appropriate error message if
	 * id does not exist.
	 */
	@GET
	@Path("/image/{id}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public byte[] downloadImage(@PathParam("id") String id) {
		BlobContainerClient containerClient = new BlobContainerClientBuilder()
				.connectionString(STORAGE_CONNECTION_STRING)
				.containerName("images")
				.buildClient();
		// Get client to blob
		BlobClient blob = containerClient.getBlobClient(id);
		// Download contents to BinaryData (check documentation for other alternatives)
		BinaryData data = blob.downloadContent();
		return data.toBytes();
	}

	/**
	 * Return the contents of an image. Throw an appropriate error message if
	 * id does not exist.
	 */
	@GET
	@Path("/video/{id}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public byte[] downloadVideo(@PathParam("id") String id) {
		BlobContainerClient containerClient = new BlobContainerClientBuilder()
				.connectionString(STORAGE_CONNECTION_STRING)
				.containerName("videos")
				.buildClient();
		// Get client to blob
		BlobClient blob = containerClient.getBlobClient(id);
		// Download contents to BinaryData (check documentation for other alternatives)
		BinaryData data = blob.downloadContent();
		return data.toBytes();
	}

	public void deleteFile(String containerName, String id) {
		BlobContainerClient containerClient = new BlobContainerClientBuilder()
				.connectionString(STORAGE_CONNECTION_STRING)
				.containerName(containerName)
				.buildClient();
		containerClient.getBlobClient(id).delete();
	}

	public boolean fileExists(String containerName, String id) {
		BlobContainerClient containerClient = new BlobContainerClientBuilder()
				.connectionString(STORAGE_CONNECTION_STRING)
				.containerName(containerName)
				.buildClient();
		return containerClient.getBlobClient(id).exists();
	}

}
