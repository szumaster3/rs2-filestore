package com.alex.utils;

import com.alex.io.InputStream;
import com.alex.io.OutputStream;
import com.alex.store.Store;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.*;

public final class Utils {

    private Utils() {
    }

    public static byte[] cryptRSA(byte[] data, BigInteger exponent, BigInteger modulus) {
        return new BigInteger(data).modPow(exponent, modulus).toByteArray();
    }

    public static String getJagString(InputStream stream) {
        int first = stream.readUnsignedByte();
        if (first == 0) {
            return "";
        }
        return (char) first + getString(stream);
    }

    private static String getString(InputStream stream) {
        StringBuilder bldr = new StringBuilder();
        int b;
        while (true) {
            b = stream.readUnsignedByte();
            if (b == 0) break;
            bldr.append((char) b);
        }
        return bldr.toString();
    }

    public static byte[] getArchivePacketData(int indexId, int archiveId, byte[] archive) {
        OutputStream stream = new OutputStream(archive.length + 4);

        stream.writeByte(indexId);
        stream.writeShort(archiveId);
        stream.writeByte(0);
        stream.writeInt(archive.length);

        int offset = 8;

        for (int i = 0; i < archive.length; i++) {
            if (offset == 512) {
                stream.writeByte(-1);
                offset = 1;
            }

            stream.writeByte(archive[i]);
            offset++;
        }

        byte[] data = new byte[stream.getOffset()];
        stream.setOffset(0);
        stream.getBytes(data, 0, data.length);
        return data;
    }

    public static int readMedium(ByteBuffer buffer) {
        return ((buffer.get() & 0xFF) << 16) | ((buffer.get() & 0xFF) << 8) | (buffer.get() & 0xFF);
    }

    public static void writeMedium(ByteBuffer buffer, int value) {
        buffer.put((byte) (value >> 16));
        buffer.put((byte) (value >> 8));
        buffer.put((byte) value);
    }

    public static int getNameHash(String name) {
        return name.toLowerCase(Locale.getDefault()).hashCode();
    }

    public static int getItemDefinitionsSize(Store store) {
        int lastArchiveId = store.getIndexes()[19].getLastArchiveId();
        return lastArchiveId * 256 + store.getIndexes()[19].getValidFilesCount(lastArchiveId);
    }

    public static int getNPCDefinitionsSize(Store store) {
        int lastArchiveId = store.getIndexes()[18].getLastArchiveId();
        return lastArchiveId * 128 + store.getIndexes()[18].getValidFilesCount(lastArchiveId);
    }

    public static final int getObjectDefinitionsSize(Store store) {
        int lastArchiveId = store.getIndexes()[16].getLastArchiveId();
        return lastArchiveId * 256 + store.getIndexes()[16].getValidFilesCount(lastArchiveId);
    }

    public static int getRenderAnimationDefinitionsSize(Store store) {
        int lastArchiveId = store.getIndexes()[2].getLastArchiveId();
        return lastArchiveId * 128 + store.getIndexes()[2].getValidFilesCount(lastArchiveId);
    }
}
