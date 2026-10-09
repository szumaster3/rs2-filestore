package com.alex.loaders;

import com.alex.io.InputStream;
import com.alex.io.OutputStream;
import com.alex.store.Store;
import com.alex.utils.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class FloDefinition {
    public final int id;
    public boolean isLoaded = false;
    public int color = 0;
    public int textureId = -1;
    public boolean occlude = true;
    public boolean blockShadow = true;
    public boolean allowUnderwater = true;
    public int minimapColor = 0;
    public int brightness = 128;
    public int hue = 8;
    public int saturation = 16;
    public int minimapShape = -1;

    public FloDefinition(int id) {
        this.id = id;
    }

    public FloDefinition(Store cache, int id) {
        this(cache, id, true);
    }

    public FloDefinition(Store cache, int id, boolean load) {
        this.id = id;

        this.setDefaults();

        if (load) {
            load(cache);
        }
    }

    public void load(Store cache) {
        try {
            byte[] data = cache.getIndexes()[2].getFile(4, this.id);

            if (data != null) {
                this.parse(new InputStream(data));
                this.isLoaded = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parse(InputStream stream) {
        while (true) {
            int opcode = stream.readUnsignedByte();
            if (opcode == 0) break;
            decode(stream, opcode);
        }
    }

    private void setDefaults() {
        color = 0;
        textureId = -1;
        occlude = true;
        blockShadow = true;
        allowUnderwater = true;
        brightness = 128;
        hue = 8;
        saturation = 16;
        minimapColor = 0;
        minimapShape = -1;
    }

    private void decode(InputStream stream, int opcode) {
        switch (opcode) {
            case 1:
                int rgb = stream.readTriByte();
                color = rgbToHsl(rgb);
                break;
            case 2:
                textureId = stream.readUnsignedByte();
                break;
            case 3:
                textureId = stream.readUnsignedShort();
                if (textureId == 65535) textureId = -1;
                break;
            case 5:
                allowUnderwater = false;
                break;
            case 7:
                minimapColor = rgbToHsl(stream.readTriByte());
                break;
            case 8:
                stream.readTriByte();
                break;
            case 9:
                brightness = stream.readUnsignedShort();
                break;
            case 10:
                blockShadow = false;
                break;
            case 11:
                hue = stream.readUnsignedByte();
                break;
            case 12:
                occlude = true;
                break;
            case 13:
                saturation = stream.readTriByte();
                break;
            case 14:
                minimapShape = stream.readUnsignedByte();
                break;
        }
    }

    public byte[] encode() {
        OutputStream stream = new OutputStream();

        if (color != 0) {
            stream.writeByte(1);
            stream.writeTriByte(color);
        }

        if (textureId != -1 && textureId < 256) {
            stream.writeByte(2);
            stream.writeByte(textureId);
        } else if (textureId != -1) {
            stream.writeByte(3);
            stream.writeShort(textureId);
        }

        if (!allowUnderwater) {
            stream.writeByte(5);
        }

        if (minimapColor != 0) {
            stream.writeByte(7);
            stream.writeTriByte(minimapColor);
        }

        if (brightness != 128) {
            stream.writeByte(9);
            stream.writeShort(brightness);
        }

        if (!blockShadow) {
            stream.writeByte(10);
        }

        if (hue != 8) {
            stream.writeByte(11);
            stream.writeByte(hue);
        }

        if (occlude) {
            stream.writeByte(12);
        }

        if (saturation != 16) {
            stream.writeByte(13);
            stream.writeTriByte(saturation);
        }

        if (minimapShape != -1) {
            stream.writeByte(14);
            stream.writeByte(minimapShape);
        }

        stream.writeByte(0);

        byte[] data = new byte[stream.getOffset()];
        stream.setOffset(0);
        stream.getBytes(data, 0, data.length);

        return data;
    }

    public static int rgbToHsl(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        r /= 255.0;
        g /= 255.0;
        b /= 255.0;

        double max = Math.max(r, Math.max(g, b));
        double min = Math.min(r, Math.min(g, b));

        double h = 0, s, l = (max + min) / 2;

        if (max == min) {
            h = s = 0;
        } else {
            double d = max - min;

            s = l > 0.5 ? d / (2.0 - max - min) : d / (max + min);

            if (max == r) {
                h = (g - b) / d + (g < b ? 6 : 0);
            } else if (max == g) {
                h = (b - r) / d + 2;
            } else {
                h = (r - g) / d + 4;
            }

            h /= 6;
        }

        int ih = (int)(h * 255);
        int is = (int)(s * 255);
        int il = (int)(l * 255);

        return (ih << 16) | (is << 8) | il;
    }

    public static void print(Store cache, String outputFile) {
        try {
            File file = new File(outputFile);
            if (file.getParentFile() != null) file.getParentFile().mkdirs();
            if (!file.exists()) file.createNewFile();

            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {

                int size = cache.getIndexes()[2].getLastFileId(2) + 1;

                for (int id = 0; id < size; id++) {

                    FloDefinition def = new FloDefinition(cache, id, false);
                    def.load(cache);

                    if (!def.isLoaded) continue;

                    writer.println("========== FLO " + id + " ==========");

                    for (Field field : def.getClass().getDeclaredFields()) {
                        if (Modifier.isStatic(field.getModifiers())) continue;

                        field.setAccessible(true);

                        Object value = field.get(def);

                        if (value == null) continue;

                        if (value instanceof Number) {
                            long num = ((Number) value).longValue();
                            if (num == 0 || num == -1) continue;
                        }

                        writer.println(field.getName() + " = " + value);
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}