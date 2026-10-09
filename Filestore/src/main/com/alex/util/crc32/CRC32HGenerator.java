package com.alex.util.crc32;

import java.util.zip.CRC32;

/**
 * Utility class for generating CRC32 hashes.
 * <p>
 * Uses a shared CRC32 instance for efficiency, synchronized to allow thread-safe access.
 * </p>
 */
public final class CRC32HGenerator {

    /**
     * Shared CRC32 instance used for calculating hashes
     */
    private static final CRC32 CRC32Instance = new CRC32();

    /**
     * Computes the CRC32 hash of the entire byte array.
     *
     * @param data the input byte array
     * @return the 32-bit CRC32 hash
     */
    public static int getHash(byte[] data) {
        return getHash(data, 0, data.length);
    }

    /**
     * Computes the CRC32 hash of a portion of a byte array.
     *
     * @param data   the input byte array
     * @param offset the starting index in the array
     * @param length the number of bytes to include
     * @return the 32-bit CRC32 hash
     */
    public static int getHash(byte[] data, int offset, int length) {
        synchronized (CRC32Instance) {
            CRC32Instance.update(data, offset, length);
            int hash = (int) CRC32Instance.getValue();
            CRC32Instance.reset();
            return hash;
        }
    }
}