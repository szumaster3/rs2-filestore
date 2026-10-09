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
import java.util.HashMap;
import java.util.Map;

public final class EnumDefinition {
    public final int id;
    public boolean isLoaded = false;
    public int keyType;
    public int valueType;
    public int defaultInt = 0;
    public String defaultString = "null";

    public Map<Integer, Object> map = new HashMap<>();

    public EnumDefinition(int id) {
        this.id = id;
    }

    public EnumDefinition(Store cache, int id) {
        this.id = id;
        load(cache);
    }

    public void load(Store cache) {
        try {
            byte[] data = cache.getIndexes()[2].getFile(2, id);
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
                keyType = stream.readUnsignedByte();
                break;
            case 2:
                valueType = stream.readUnsignedByte();
                break;
            case 3:
                defaultString = stream.readString();
                break;
            case 4:
                defaultInt = stream.readInt();
                break;
            case 5:
            case 6: {
                int size = stream.readUnsignedShort();
                for (int i = 0; i < size; i++) {
                    int key = stream.readInt();

                    if (opcode == 5) {
                        String value = stream.readString();
                        map.put(key, value);
                    } else {
                        int value = stream.readInt();
                        map.put(key, value);
                    }
                }
                break;
            }
        }
    }

    public byte[] encode() {
        OutputStream stream = new OutputStream();
        if (keyType != 0) {
            stream.writeByte(1);
            stream.writeByte(keyType);
        }
        if (valueType != 0) {
            stream.writeByte(2);
            stream.writeByte(valueType);
        }
        if (defaultString != null && !defaultString.equals("null")) {
            stream.writeByte(3);
            stream.writeString(defaultString);
        }
        if (defaultInt != 0) {
            stream.writeByte(4);
            stream.writeInt(defaultInt);
        }
        if (map != null && !map.isEmpty()) {
            boolean isStringMap = false;
            for (Object v : map.values()) {
                if (v instanceof String) {
                    isStringMap = true;
                    break;
                }
            }

            stream.writeByte(isStringMap ? 5 : 6);
            stream.writeShort(map.size());

            for (Map.Entry<Integer, Object> entry : map.entrySet()) {
                stream.writeInt(entry.getKey());

                if (isStringMap) {
                    stream.writeString((String) entry.getValue());
                } else {
                    stream.writeInt((Integer) entry.getValue());
                }
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

                int size = cache.getIndexes()[2].getLastFileId(2) + 1;

                for (int id = 0; id < size; id++) {

                    EnumDefinition def = new EnumDefinition(cache, id);

                    if (!def.isLoaded) {
                        continue;
                    }

                    writer.println("========== ENUM " + id + " ==========");

                    writer.println("keyType = " + def.keyType);
                    writer.println("valueType = " + def.valueType);
                    writer.println("defaultInt = " + def.defaultInt);
                    writer.println("defaultString = " + def.defaultString);

                    writer.println("map size = " + def.map.size());

                    for (Map.Entry<Integer, Object> entry : def.map.entrySet()) {
                        writer.println(entry.getKey() + " -> " + entry.getValue());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}