package com.alex.loaders;

import com.alex.Cache;
import com.alex.io.InputStream;
import com.alex.io.OutputStream;
import com.alex.store.Store;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public final class SeqDefinition {
    public int id;
    public int[] frames;
    public int[] frameDelay;
    public boolean[] frameGroup;
    public int[][] soundEffect;
    public int[] frameSet;
    public int replayOff = -1;
    public int replayCount = 99;
    public int loopType = -1;
    public int moveType = -1;
    public int exactMove = 2;
    public int priority = 5;
    public int mainHand = -1;
    public int offHand = -1;
    public boolean stretches;
    public boolean tween;
    public boolean alpha;
    public boolean loaded;

    public SeqDefinition(int id) {
        this.id = id;
    }

    public boolean load() {
        try {
            byte[] data = Cache.getStore().getIndexes()[20].getFile(id >>> 7, id & 0x7F, null);
            if (data == null) return false;

            decode(data);
            postDecode();
            loaded = true;
            return true;

        } catch (Exception e) {
            System.out.println("SEQ load fail id=" + id);
            e.printStackTrace();
            return false;
        }
    }

    private void decode(byte[] data) {
        InputStream in = new InputStream(data);
        while (true) {
            int opcode = in.readUnsignedByte();
            if (opcode == 0) break;
            switch (opcode) {
                case 1: {
                    int count = in.readUnsignedShort();
                    frameDelay = new int[count];
                    frames = new int[count];
                    for (int i = 0; i < count; i++)
                        frameDelay[i] = in.readUnsignedShort();
                    for (int i = 0; i < count; i++)
                        frames[i] = in.readUnsignedShort() << 16;
                    for (int i = 0; i < count; i++)
                        frames[i] += in.readUnsignedShort();
                    break;
                }
                case 2:
                    replayOff = in.readUnsignedShort();
                    break;
                case 3: {
                    frameGroup = new boolean[256];
                    int len = in.readUnsignedByte();

                    for (int i = 0; i < len; i++)
                        frameGroup[in.readUnsignedByte()] = true;
                    break;
                }
                case 4:
                    stretches = true;
                    break;
                case 5:
                    priority = in.readUnsignedByte();
                    break;
                case 6:
                    mainHand = in.readUnsignedShort();
                    break;
                case 7:
                    offHand = in.readUnsignedShort();
                    break;
                case 8:
                    replayCount = in.readUnsignedByte();
                    break;
                case 9:
                    loopType = in.readUnsignedByte();
                    break;
                case 10:
                    moveType = in.readUnsignedByte();
                    break;
                case 11:
                    exactMove = in.readUnsignedByte();
                    break;
                case 12: {
                    int len = in.readUnsignedByte();
                    frameSet = new int[len];
                    for (int i = 0; i < len; i++)
                        frameSet[i] = in.readUnsignedShort() << 16;
                    for (int i = 0; i < len; i++)
                        frameSet[i] += in.readUnsignedShort();
                    break;
                }
                case 13: {
                    int len = in.readUnsignedShort();
                    soundEffect = new int[len][];
                    for (int i = 0; i < len; i++) {
                        int size = in.readUnsignedByte();
                        if (size > 0) {
                            soundEffect[i] = new int[size];
                            soundEffect[i][0] = in.readMedium();
                            for (int j = 1; j < size; j++)
                                soundEffect[i][j] = in.readUnsignedShort();
                        }
                    }
                    break;
                }
                case 14:
                    alpha = true;
                    break;
                case 15:
                    tween = true;
                    break;
            }
        }
    }

    public byte[] encode() {
        OutputStream out = new OutputStream();

        if (frameDelay != null && frames != null) {
            out.writeByte(1);
            out.writeShort(frameDelay.length);

            for (int i = 0; i < frameDelay.length; i++)
                out.writeShort(frameDelay[i]);

            for (int i = 0; i < frames.length; i++)
                out.writeShort(frames[i] >> 16);

            for (int i = 0; i < frames.length; i++)
                out.writeShort(frames[i] & 0xFFFF);
        }

        if (replayOff != -1) {
            out.writeByte(2);
            out.writeShort(replayOff);
        }

        if (frameGroup != null) {
            out.writeByte(3);

            int count = 0;
            for (boolean b : frameGroup)
                if (b) count++;

            out.writeByte(count);

            for (int i = 0; i < frameGroup.length; i++)
                if (frameGroup[i])
                    out.writeByte(i);
        }

        if (stretches) {
            out.writeByte(4);
        }

        if (priority != 5) {
            out.writeByte(5);
            out.writeByte(priority);
        }

        if (mainHand != -1) {
            out.writeByte(6);
            out.writeShort(mainHand);
        }

        if (offHand != -1) {
            out.writeByte(7);
            out.writeShort(offHand);
        }

        if (replayCount != 99) {
            out.writeByte(8);
            out.writeByte(replayCount);
        }

        if (loopType != -1) {
            out.writeByte(9);
            out.writeByte(loopType);
        }

        if (moveType != -1) {
            out.writeByte(10);
            out.writeByte(moveType);
        }

        if (exactMove != 2) {
            out.writeByte(11);
            out.writeByte(exactMove);
        }

        if (frameSet != null) {
            out.writeByte(12);
            out.writeByte(frameSet.length);

            for (int i = 0; i < frameSet.length; i++)
                out.writeShort(frameSet[i] >> 16);

            for (int i = 0; i < frameSet.length; i++)
                out.writeShort(frameSet[i] & 0xFFFF);
        }

        if (soundEffect != null) {
            out.writeByte(13);
            out.writeShort(soundEffect.length);

            for (int i = 0; i < soundEffect.length; i++) {
                if (soundEffect[i] == null) {
                    out.writeByte(0);
                    continue;
                }

                out.writeByte(soundEffect[i].length);
                out.writeMedium(soundEffect[i][0]);

                for (int j = 1; j < soundEffect[i].length; j++)
                    out.writeShort(soundEffect[i][j]);
            }
        }

        if (alpha) out.writeByte(14);
        if (tween) out.writeByte(15);

        out.writeByte(0);

        byte[] data = new byte[out.getOffset()];
        out.setOffset(0);
        out.getBytes(data, 0, data.length);
        return data;
    }

    public void postDecode() {
        if (loopType == -1)
            loopType = (frameGroup == null) ? 0 : 2;

        if (moveType == -1)
            moveType = (frameGroup == null) ? 0 : 2;
    }

    public static void print(Store cache, String filePath) {
        try {
            File file = new File(filePath);

            if (file.getParentFile() != null)
                file.getParentFile().mkdirs();

            try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
                int indexId = 20;
                int maxArchive = cache.getIndexes()[indexId].getLastFileId(0);
                for (int archive = 0; archive <= maxArchive; archive++) {
                    int maxFile = cache.getIndexes()[indexId].getLastFileId(archive);
                    for (int fileId = 0; fileId <= maxFile; fileId++) {
                        try {
                            byte[] data = cache.getIndexes()[indexId].getFile(archive, fileId, null);

                            if (data == null)
                                continue;

                            int id = (archive << 7) | fileId;

                            SeqDefinition seq = new SeqDefinition(id);

                            if (!seq.load())
                                continue;

                            out.println("========== SEQ " + id + " ==========");
                            out.println("frames=" + (seq.frames != null ? seq.frames.length : 0));
                            out.println("delay=" + (seq.frameDelay != null ? seq.frameDelay.length : 0));
                            out.println("priority=" + seq.priority);
                            out.println("loopType=" + seq.loopType);
                            out.println("moveType=" + seq.moveType);
                            out.println("tween=" + seq.tween);
                            out.println("alpha=" + seq.alpha);
                        } catch (Exception e) {
                            System.out.println("SEQ dump fail archive=" + archive + " file=" + fileId);
                            e.printStackTrace();
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}