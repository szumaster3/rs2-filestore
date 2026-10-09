package com.alex.loaders;

import com.alex.io.InputStream;
import com.alex.io.OutputStream;
import com.alex.store.Store;

import java.util.HashMap;
import java.util.Map;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public final class StructDefinition {
    public final int id;
    public boolean isLoaded = false;
    public Map<Integer, Object> params;

    public StructDefinition(int id) {
        this.id = id;
    }

    public StructDefinition(Store cache, int id) {
        this.id = id;
        load(cache);
    }

    public void load(Store cache) {
        try {
            byte[] data = cache.getIndexes()[2].getFile(26, id);

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
        if (opcode != 249) {
            return;
        }

        int count = stream.readUnsignedByte();
        if (params == null) {
            params = new HashMap<>();
        }
        for (int i = 0; i < count; i++) {
            boolean isString = stream.readUnsignedByte() == 1;
            int key = stream.readTriByte();
            if (isString) {
                params.put(key, stream.readString());
            } else {
                params.put(key, stream.readInt());
            }
        }
    }

    public int getInt(int key, int defaultValue) {
        if (params == null) return defaultValue;

        Object value = params.get(key);
        return value instanceof Integer ? (int) value : defaultValue;
    }

    public String getString(int key, String defaultValue) {
        if (params == null) return defaultValue;

        Object value = params.get(key);
        return value instanceof String ? (String) value : defaultValue;
    }

    public byte[] encode() {
        OutputStream out = new OutputStream();

        if (params != null && !params.isEmpty()) {

            out.writeByte(249);
            out.writeByte(params.size());

            for (Map.Entry<Integer, Object> entry : params.entrySet()) {

                Object value = entry.getValue();

                if (value instanceof String) {
                    out.writeByte(1);
                    out.writeTriByte(entry.getKey());
                    out.writeString((String) value);
                } else {
                    out.writeByte(0);
                    out.writeTriByte(entry.getKey());
                    out.writeInt((Integer) value);
                }
            }
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

            int archive = 26;

            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {

                int max = cache.getIndexes()[2].getLastFileId(archive);

                for (int id = 0; id <= max; id++) {

                    try {
                        byte[] data = cache.getIndexes()[2].getFile(archive, id);
                        if (data == null) continue;

                        StructDefinition def = new StructDefinition(id);
                        def.parse(new InputStream(data));

                        if (def.params == null || def.params.isEmpty())
                            continue;

                        writer.println("========== STRUCT " + id + " ==========");

                        for (Map.Entry<Integer, Object> e : def.params.entrySet()) {
                            Object v = e.getValue();

                            if (v instanceof String) {
                                writer.println(e.getKey() + " (string) = \"" + v + "\"");
                            } else {
                                writer.println(e.getKey() + " (int) = " + v);
                            }
                        }

                    } catch (Exception ignored) {
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}