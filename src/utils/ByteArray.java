package utils;

public class ByteArray {
    public static byte[][] splitByteArray(byte[] data, byte[] delimiter, int limit) {
        int delimiterLength = delimiter.length;
        int count = 1;
        int[] start = new int[limit + 1];
        int[] end = new int[limit + 1];
        start[0] = 0;

        for (int i = 0; i <= data.length - delimiterLength; i++) {
            boolean isDelimiter = true;

            if(count == limit) {
                break;
            }

            for (int j = 0; j < delimiterLength; j++) {
                if (data[i + j] != delimiter[j]) {
                    isDelimiter = false;
                    break;
                }
            }

            if (isDelimiter) {
                end[count - 1] = i;
                start[count] = i + delimiterLength;
                count++;

            }

        }
        end[count - 1] = data.length;

        byte[][] result = new byte[count][];

        for(int i = 0 ; i < count ; i++) {
            int len = end[i] - start[i];
            byte[] split = new byte[len];
            System.arraycopy(data, start[i], split, 0, len);
            result[i] = split;
        }

        return result;
    }


}
