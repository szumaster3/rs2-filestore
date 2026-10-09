package com.alex.util.gzip;

import com.alex.io.Stream;

import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class GZipDecompressor {
    private static final Inflater inflaterInstance = new Inflater(true);

    public static boolean decompress(Stream stream, byte[] data) {
        synchronized (inflaterInstance) {
            byte[] buffer = stream.getBuffer();
            int offset = stream.getOffset();

            if (buffer[offset] == 31 && buffer[offset + 1] == -117) {
                try {
                    int length = buffer.length - offset - 18;
                    inflaterInstance.setInput(buffer, offset + 10, length);
                    inflaterInstance.inflate(data);

                } catch (DataFormatException e) {
                    inflaterInstance.reset();
                    return false;
                } catch (Exception e) {
                    inflaterInstance.reset();
                    return false;
                }

                inflaterInstance.reset();
                return true;
            } else {
                return false;
            }
        }
    }

    public static final void decompress(ByteBuffer buffer, byte data[]) {
        synchronized(inflaterInstance) {
            if (~buffer.get(buffer.position()) != -32 || buffer.get(buffer.position() + 1) != -117) {
                data = null;
            }
            try {
                inflaterInstance.setInput(buffer.array(), buffer.position() + 10, -buffer.position() - 18 + buffer.limit());
                inflaterInstance.inflate(data);
            } catch (Exception e) {
                data = null;
            }
            inflaterInstance.reset();
        }
    }

    public static final boolean decompress(byte[] compressed, byte data[], int offset, int length) {
        synchronized(inflaterInstance) {
            if (data[offset] != 31 || data[offset + 1] != -117)
                return false;
            try {
                inflaterInstance.setInput(data, offset + 10, -offset - 18 + length);
                inflaterInstance.inflate(compressed);
            } catch (Exception e) {
                inflaterInstance.reset();
                e.printStackTrace();
                return false;
            }
            inflaterInstance.reset();
            return true;
        }
    }
}