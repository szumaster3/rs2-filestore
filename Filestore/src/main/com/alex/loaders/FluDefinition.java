package com.alex.loaders;

import com.alex.io.InputStream;
import com.alex.store.Store;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

public class FluDefinition {
    private static final HashMap<Long, FluDefinition> cache = new HashMap<>();
    public static Store configClient;
    public final int id;
    public boolean isLoaded = false;
    public int hue;
    public int chroma;
    public int saturation;
    public int luminance;

    private int rgb = 0;

    public int paletteIndex = -1;
    public int scale = 128;

    public boolean aBoolean198 = true;

    public FluDefinition(int id) {
        this.id = id;
        setDefaults();
    }

    public static void init(Store store) {
        configClient = store;
    }

    public static void clearCache() {
        cache.clear();
    }

    public static FluDefinition list(Store store, int id) {

        FluDefinition def = cache.get((long) id);
        if (def != null) {
            return def;
        }

        byte[] data = store.getIndexes()[1].getFile(1, id);

        def = new FluDefinition(id);

        if (data != null) {
            def.decode(new InputStream(data));
            def.isLoaded = true;
        }

        cache.put((long) id, def);

        return def;
    }

    public void load(Store store) {
        byte[] data = store.getIndexes()[1].getFile(1, id);

        if (data != null) {
            decode(new InputStream(data));
            isLoaded = true;
        }
    }

    public void decode(InputStream stream) {
        while (true) {
            int opcode = stream.readUnsignedByte();
            if (opcode == 0) {
                return;
            }
            decodeOpcode(opcode, stream);
        }
    }

    private void decodeOpcode(int opcode, InputStream stream) {
        if (opcode == 1) {
            rgb = stream.readTriByte();
            computeHSL(rgb);
        } else if (opcode == 2) {
            paletteIndex = stream.readUnsignedShort();
            if (paletteIndex == 65535) paletteIndex = -1;
        } else if (opcode == 3) {
            scale = stream.readUnsignedShort();
        } else if (opcode == 4) {
            aBoolean198 = false;
        }
    }

    private void computeHSL(int rgb) {

        double r = ((rgb >> 16) & 0xFF) / 256.0;
        double g = ((rgb >> 8) & 0xFF) / 256.0;
        double b = (rgb & 0xFF) / 256.0;

        double min = Math.min(r, Math.min(g, b));
        double max = Math.max(r, Math.max(g, b));

        double hue = 0;
        double sat;
        double lum = (max + min) / 2.0;

        double chroma = 0;

        if (max != min) {

            double diff = max - min;

            sat = lum < 0.5
                    ? diff / (max + min)
                    : diff / (2.0 - max - min);

            if (max == r) {
                hue = (g - b) / diff + (g < b ? 6 : 0);
            } else if (max == g) {
                hue = (b - r) / diff + 2;
            } else {
                hue = (r - g) / diff + 4;
            }

            hue /= 6.0;

            chroma = lum < 0.5
                    ? diff * lum * 512.0
                    : diff * (1.0 - lum) * 512.0;

        } else {
            sat = 0;
            hue = 0;
        }

        this.hue = clamp((int) (hue * 256.0), 0, 255);
        this.saturation = clamp((int) (lum * 256.0), 0, 255);
        this.luminance = Math.max((int) chroma, 1);
        this.chroma = (int) chroma;
    }

    private int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    private void setDefaults() {
        hue = 0;
        chroma = 0;
        saturation = 0;
        luminance = 0;
        rgb = 0;
        paletteIndex = -1;
        scale = 128;
        aBoolean198 = true;
    }

    public static void print(Store cache, String outputFile) {
        try {
            File file = new File(outputFile);

            if (file.getParentFile() != null)
                file.getParentFile().mkdirs();

            if (!file.exists())
                file.createNewFile();

            int index = 2;
            int archive = 1;

            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {

                int max = cache.getIndexes()[index].getLastFileId(archive);

                for (int id = 0; id <= max; id++) {

                    try {
                        byte[] data = cache.getIndexes()[index].getFile(archive, id);

                        if (data == null || data.length == 0)
                            continue;

                        FluDefinition def = new FluDefinition(id);
                        def.decode(new InputStream(data));
                        def.isLoaded = true;

                        boolean wrote = false;

                        StringBuilder sb = new StringBuilder();
                        sb.append("========== FLU ").append(id).append(" ==========\n");

                        if (def.rgb != 0) {
                            sb.append("rgb = ").append(def.rgb).append("\n");
                            wrote = true;
                        }

                        if (def.paletteIndex != -1) {
                            sb.append("paletteIndex = ").append(def.paletteIndex).append("\n");
                            wrote = true;
                        }

                        if (def.scale != 128) {
                            sb.append("scale = ").append(def.scale).append("\n");
                            wrote = true;
                        }

                        if (!def.aBoolean198) {
                            sb.append("aBoolean198 = false\n");
                            wrote = true;
                        }

                        if (def.hue != 0 || def.saturation != 0 || def.luminance != 0 || def.chroma != 0) {
                            sb.append("===== HSL =====\n");
                            sb.append("hue = ").append(def.hue).append("\n");
                            sb.append("saturation = ").append(def.saturation).append("\n");
                            sb.append("luminance = ").append(def.luminance).append("\n");
                            sb.append("chroma = ").append(def.chroma).append("\n");
                            wrote = true;
                        }

                        if (wrote) {
                            writer.println(sb);
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