package utils;

import com.nimbusds.jose.shaded.gson.Gson;
import data.user.User;

public class MultiPartFormData<T> {
    private T item;
    private byte[] media;

    public MultiPartFormData() {
    }

    public T getItem() {
        return item;
    }

    public byte[] getMedia() {
        return media;
    }

    public void extractItemMedia(byte[] data, Class<T> itemType) {
        byte[] delimiter = new byte[Math.min(52, data.length)];
        System.arraycopy(data, 0, delimiter, 0, delimiter.length);

        byte[][] splitData = ByteArray.splitByteArray(data, delimiter, 4);
        String itemString = new String (splitData[1]);
        byte[] contents = splitData[2];

        itemString = itemString.split("\\r\\n\\r\\n",2)[1].trim();
        itemString = itemString.replaceAll("\n", "").trim();

        Gson gson = new Gson();
        item = gson.fromJson(itemString, itemType);

        media = ByteArray.splitByteArray(contents,"\r\n\r\n".getBytes(), 2)[1];

    }


}
