package com.alex.store;

import com.alex.utils.Utils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Represents a main data file with an associated index file.
 * <p>
 * Supports reading and writing archives stored in fixed-size blocks with optional new protocol.
 * </p>
 */
public final class MainFile {

    public static final int IDX_BLOCK_LEN = 6;
    public static final int HEADER_LEN = 8;
    public static final int EXPANDED_HEADER_LEN = 10;
    public static final int BLOCK_LEN = 512;
    public static final int EXPANDED_BLOCK_LEN = 510;
    public static final int TOTAL_BLOCK_LEN = 520;

    /**
     * Temporary buffer used for reading and writing blocks
     */
    private static final ByteBuffer tempBuffer = ByteBuffer.allocateDirect(TOTAL_BLOCK_LEN);

    private final int id;
    private final FileChannel index;
    private final FileChannel data;
    private final boolean newProtocol;

    public MainFile(int id, RandomAccessFile dataFile, RandomAccessFile indexFile, boolean newProtocol) throws IOException {
        this.id = id;
        this.data = dataFile.getChannel();
        this.index = indexFile.getChannel();
        this.newProtocol = newProtocol;
    }

    public Archive getArchive(int archiveId) {
        return getArchive(archiveId, null);
    }

    public Archive getArchive(int archiveId, int[] keys) {
        byte[] data = getArchiveData(archiveId);
        return data == null ? null : new Archive(archiveId, data, keys);
    }

    public byte[] getArchiveData(int archiveId) {
        synchronized (data) {
            try {
                tempBuffer.clear().limit(IDX_BLOCK_LEN);
                index.read(tempBuffer, (long) archiveId * IDX_BLOCK_LEN);
                tempBuffer.flip();
                int length = Utils.readMedium(tempBuffer);
                int block = Utils.readMedium(tempBuffer);

                if (length <= 0 || block <= 0 || block > data.size() / TOTAL_BLOCK_LEN) return null;

                ByteBuffer fileBuffer = ByteBuffer.allocate(length);
                int remaining = length;
                int chunk = 0;
                int blockLen = (newProtocol && archiveId > 0xFFFF) ? EXPANDED_BLOCK_LEN : BLOCK_LEN;
                int headerLen = (newProtocol && archiveId > 0xFFFF) ? EXPANDED_HEADER_LEN : HEADER_LEN;

                while (remaining > 0) {
                    if (block == 0) return null;

                    int readSize = Math.min(remaining, blockLen);
                    tempBuffer.clear().limit(readSize + headerLen);
                    data.read(tempBuffer, (long) block * TOTAL_BLOCK_LEN);
                    tempBuffer.flip();

                    int currentFile, currentChunk, nextBlock, currentIndex;
                    if (newProtocol && archiveId > 0xFFFF) {
                        currentFile = tempBuffer.getInt();
                        currentChunk = tempBuffer.getShort() & 0xFFFF;
                        nextBlock = Utils.readMedium(tempBuffer);
                        currentIndex = tempBuffer.get() & 0xFF;
                    } else {
                        currentFile = tempBuffer.getShort() & 0xFFFF;
                        currentChunk = tempBuffer.getShort() & 0xFFFF;
                        nextBlock = Utils.readMedium(tempBuffer);
                        currentIndex = tempBuffer.get() & 0xFF;
                    }

                    if ((archiveId != currentFile && archiveId <= 0xFFFF) || chunk != currentChunk || id != currentIndex)
                        return null;
                    if (nextBlock < 0 || nextBlock > data.size() / TOTAL_BLOCK_LEN) return null;

                    fileBuffer.put(tempBuffer);
                    remaining -= readSize;
                    block = nextBlock;
                    chunk++;
                }

                byte[] result = new byte[length];
                fileBuffer.flip();
                fileBuffer.get(result);
                return result;

            } catch (Exception e) {
                return null;
            }
        }
    }

    public boolean putArchive(Archive archive) {
        return putArchiveData(archive.getId(), archive.getData());
    }

    public boolean putArchiveData(int archiveId, byte[] archive) {
        ByteBuffer buffer = ByteBuffer.wrap(archive);
        boolean done = putArchiveData(archiveId, buffer, archive.length, true);
        if (!done) {
            done = putArchiveData(archiveId, buffer, archive.length, false);
        }
        return done;
    }

    public boolean putArchiveData(int archiveId, ByteBuffer archive, int size, boolean exists) {
        synchronized (data) {
            try {
                int block = 0;

                if (exists) {
                    if ((long) archiveId * IDX_BLOCK_LEN + IDX_BLOCK_LEN > index.size()) return false;

                    tempBuffer.clear().limit(IDX_BLOCK_LEN);
                    index.read(tempBuffer, (long) archiveId * IDX_BLOCK_LEN);
                    tempBuffer.flip().position(3); // skip length
                    block = Utils.readMedium(tempBuffer);
                    if (block <= 0 || block > data.size() / TOTAL_BLOCK_LEN) return false;
                } else {
                    block = (int) ((data.size() + TOTAL_BLOCK_LEN - 1) / TOTAL_BLOCK_LEN);
                    if (block == 0) block = 1;
                }

                tempBuffer.clear();
                Utils.writeMedium(tempBuffer, size);
                Utils.writeMedium(tempBuffer, block);
                tempBuffer.flip();
                index.write(tempBuffer, (long) archiveId * IDX_BLOCK_LEN);

                int remaining = size;
                int chunk = 0;
                int blockLen = (newProtocol && archiveId > 0xFFFF) ? EXPANDED_BLOCK_LEN : BLOCK_LEN;
                int headerLen = (newProtocol && archiveId > 0xFFFF) ? EXPANDED_HEADER_LEN : HEADER_LEN;

                while (remaining > 0) {
                    int nextBlock = 0;

                    if (exists) {
                        tempBuffer.clear().limit(headerLen);
                        data.read(tempBuffer, (long) block * TOTAL_BLOCK_LEN);
                        tempBuffer.flip();

                        int currentFile, currentChunk, currentIndex;
                        if (newProtocol && archiveId > 0xFFFF) {
                            currentFile = tempBuffer.getInt();
                            currentChunk = tempBuffer.getShort() & 0xFFFF;
                            nextBlock = Utils.readMedium(tempBuffer);
                            currentIndex = tempBuffer.get() & 0xFF;
                        } else {
                            currentFile = tempBuffer.getShort() & 0xFFFF;
                            currentChunk = tempBuffer.getShort() & 0xFFFF;
                            nextBlock = Utils.readMedium(tempBuffer);
                            currentIndex = tempBuffer.get() & 0xFF;
                        }

                        if ((archiveId != currentFile && archiveId <= 0xFFFF) || chunk != currentChunk || id != currentIndex)
                            return false;
                        if (nextBlock < 0 || nextBlock > data.size() / TOTAL_BLOCK_LEN) return false;
                    }

                    if (nextBlock == 0) {
                        exists = false;
                        nextBlock = (int) ((data.size() + TOTAL_BLOCK_LEN - 1) / TOTAL_BLOCK_LEN);
                        if (nextBlock == 0) nextBlock = 1;
                        if (nextBlock == block) nextBlock++;
                    }

                    if (remaining <= blockLen) nextBlock = 0;

                    tempBuffer.clear();
                    if (newProtocol && archiveId > 0xFFFF) {
                        tempBuffer.putInt(archiveId);
                        tempBuffer.putShort((short) chunk);
                        Utils.writeMedium(tempBuffer, nextBlock);
                        tempBuffer.put((byte) id);
                    } else {
                        tempBuffer.putShort((short) archiveId);
                        tempBuffer.putShort((short) chunk);
                        Utils.writeMedium(tempBuffer, nextBlock);
                        tempBuffer.put((byte) id);
                    }

                    int blockSize = Math.min(remaining, blockLen);
                    archive.limit(archive.position() + blockSize);
                    tempBuffer.put(archive);
                    tempBuffer.flip();
                    data.write(tempBuffer, (long) block * TOTAL_BLOCK_LEN);

                    remaining -= blockSize;
                    block = nextBlock;
                    chunk++;
                }

                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    public int getId() {
        return id;
    }

    public int getArchivesCount() throws IOException {
        synchronized (index) {
            return (int) (index.size() / IDX_BLOCK_LEN);
        }
    }
}