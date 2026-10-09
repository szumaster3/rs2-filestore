package com.alex.loaders;

import com.alex.io.InputStream;
import com.alex.io.OutputStream;
import com.alex.store.Store;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public final class ParamDefinition {
    public final int id;
    public boolean isLoaded = false;
    public int type;
    public int defaultInt;
    public String defaultString;

    public ParamDefinition(int id) {
        this.id = id;
    }

    public ParamDefinition(Store cache, int id) {
        this.id = id;
        load(cache);
    }

    public void load(Store cache) {
        try {
            byte[] data = cache.getIndexes()[2].getFile(11, id);

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
        switch (opcode) {
            case 1:
                type = stream.readUnsignedByte();
                break;
            case 2:
                defaultInt = stream.readInt();
                break;
            case 5:
                defaultString = stream.readString();
                break;
        }
    }

    public boolean isString() {
        return type == 115;
    }

    public byte[] encode() {
        OutputStream out = new OutputStream();
        if (type != 0) {
            out.writeByte(1);
            out.writeByte(type);
        }
        if (defaultInt != 0) {
            out.writeByte(2);
            out.writeInt(defaultInt);
        }
        if (defaultString != null) {
            out.writeByte(5);
            out.writeString(defaultString);
        }

        out.writeByte(0);
        byte[] data = new byte[out.getOffset()];
        out.setOffset(0);
        out.getBytes(data, 0, data.length);
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

            int archive = 11;

            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {

                int max = cache.getIndexes()[2].getLastFileId(archive);

                for (int id = 0; id <= max; id++) {

                    try {
                        byte[] data = cache.getIndexes()[2].getFile(archive, id);
                        if (data == null) continue;

                        ParamDefinition def = new ParamDefinition(id);
                        def.parse(new InputStream(data));
                        if (def.type == 0 && def.defaultInt == 0 && def.defaultString == null)
                            continue;

                        writer.println("========== PARAM " + id + " ==========");

                        if (def.type != 0)
                            writer.println("type = " + def.type);

                        if (def.defaultInt != 0)
                            writer.println("defaultInt = " + def.defaultInt);

                        if (def.defaultString != null)
                            writer.println("defaultString = " + def.defaultString);

                        writer.println("isString = " + def.isString());

                    } catch (Exception ignored) {
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}