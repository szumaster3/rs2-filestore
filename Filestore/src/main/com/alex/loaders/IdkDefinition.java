package com.alex.loaders;

import com.alex.io.InputStream;
import com.alex.io.OutputStream;
import com.alex.store.Store;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class IdkDefinition {
    public final int id;
    public boolean isLoaded = false;
    public int feature = -1;
    public boolean disable = false;
    public int[] model;
    public int[] head = new int[]{-1, -1, -1, -1, -1};
    public short[] recol_s;
    public short[] recol_d;
    public short[] retex_s;
    public short[] retex_d;

    public IdkDefinition(int id) {
        this.id = id;
    }

    public IdkDefinition(Store cache, int id) {
        this.id = id;
        load(cache);
    }

    public void load(Store cache) {
        try {
            byte[] data = cache.getIndexes()[2].getFile(3, id);

            if (data != null) {
                parse(new InputStream(data));
                isLoaded = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parse(InputStream stream) {
        while (true) {
            int opcode = stream.readUnsignedByte();
            if (opcode == 0) {
                break;
            }
            decode(stream, opcode);
        }
    }

    private void decode(InputStream stream, int opcode) {
        int count, i;
        switch (opcode) {
            case 1:
                feature = stream.readUnsignedByte();
                break;
            case 2:
                count = stream.readUnsignedByte();
                model = new int[count];

                for (i = 0; i < count; i++) {
                    model[i] = stream.readUnsignedShort();
                }
                break;
            case 3:
                disable = true;
                break;
            case 40:
                count = stream.readUnsignedByte();
                recol_s = new short[count];
                recol_d = new short[count];
                for (i = 0; i < count; i++) {
                    recol_s[i] = (short) stream.readUnsignedShort();
                    recol_d[i] = (short) stream.readUnsignedShort();
                }
                break;

            case 41:
                count = stream.readUnsignedByte();
                retex_s = new short[count];
                retex_d = new short[count];
                for (i = 0; i < count; i++) {
                    retex_s[i] = (short) stream.readUnsignedShort();
                    retex_d[i] = (short) stream.readUnsignedShort();
                }
                break;
            default:
                if (opcode >= 60 && opcode < 70) {
                    head[opcode - 60] = stream.readUnsignedShort();
                }
                break;
        }
    }

    public byte[] encode() {
        OutputStream stream = new OutputStream();

        if (feature != -1) {
            stream.writeByte(1);
            stream.writeByte(feature);
        }
        if (model != null) {
            stream.writeByte(2);
            stream.writeByte(model.length);

            for (int i = 0; i < model.length; i++) {
                stream.writeShort(model[i]);
            }
        }
        if (disable) {
            stream.writeByte(3);
        }
        if (recol_s != null && recol_d != null) {
            stream.writeByte(40);
            stream.writeByte(recol_s.length);

            for (int i = 0; i < recol_s.length; i++) {
                stream.writeShort(recol_s[i]);
                stream.writeShort(recol_d[i]);
            }
        }
        if (retex_s != null && retex_d != null) {
            stream.writeByte(41);
            stream.writeByte(retex_s.length);

            for (int i = 0; i < retex_s.length; i++) {
                stream.writeShort(retex_s[i]);
                stream.writeShort(retex_d[i]);
            }
        }
        for (int i = 0; i < 5; i++) {
            if (head[i] != -1) {
                stream.writeByte(60 + i);
                stream.writeShort(head[i]);
            }
        }

        stream.writeByte(0);
        byte[] data = new byte[stream.getOffset()];
        stream.setOffset(0);
        stream.getBytes(data, 0, data.length);
        return data;
    }

    public static void print(Store cache, String outputFile) {
        try {
            File file = new File(outputFile);

            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }

            if (!file.exists()) {
                file.createNewFile();
            }

            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {

                int size = cache.getIndexes()[2].getLastFileId(3) + 1;

                for (int id = 0; id < size; id++) {

                    IdkDefinition def = new IdkDefinition(cache, id);

                    if (!def.isLoaded) {
                        continue;
                    }

                    writer.println("========== IDK " + id + " ==========");

                    writer.println("feature = " + def.feature);
                    writer.println("disable = " + def.disable);

                    if (def.model != null) {
                        writer.print("models = ");
                        for (int v : def.model) writer.print(v + " ");
                        writer.println();
                    }

                    writer.print("head = ");
                    for (int i = 0; i < 5; i++) {
                        writer.print(def.head[i] + " ");
                    }
                    writer.println();

                    if (def.recol_s != null) {
                        writer.println("===== RECOLORS =====");
                        for (int i = 0; i < def.recol_s.length; i++) {
                            writer.println(def.recol_s[i] + " -> " + def.recol_d[i]);
                        }
                    }

                    if (def.retex_s != null) {
                        writer.println("===== RETEXTURES =====");
                        for (int i = 0; i < def.retex_s.length; i++) {
                            writer.println(def.retex_s[i] + " -> " + def.retex_d[i]);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}