package com.alex.tools.pack;

import com.alex.store.Index;
import com.alex.store.Store;
import com.alex.util.XTEAManager;
import com.alex.utils.CompressionUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public final class MapPacker {

    private static boolean xteasLoaded = false;

    public static void init(Path xteaPath) {
        if (xteasLoaded) return;

        if (!XTEAManager.load(xteaPath)) {
            System.out.println("Failed to load XTEAs!");
        } else {
            System.out.println("XTEAs loaded: " + XTEAManager.allKeys().size());
        }

        xteasLoaded = true;
    }

    public static void packRegion(Store cache,
                                  String path,
                                  int regionId,
                                  int mapId,
                                  int objId,
                                  int[] manualKeys) {

        Index index = cache.getIndexes()[5];

        File mapFile = getFile(path + "/map_files", mapId);
        File landFile = getFile(path + "/land_files", objId);

        try {
            if (mapFile != null) {
                byte[] mapData = read(mapFile);
                index.putFile(mapId, 0, mapData);
                System.out.println("MAP packed: " + mapId);
            }

            if (landFile != null || objId != -1) {

                byte[] landData = (landFile != null)
                        ? read(landFile)
                        : new byte[0];

                int[] keys = resolveKeys(regionId, manualKeys);

                if (isNullKeys(keys)) {
                    System.out.println("No XTEA for region " + regionId);
                } else {
                    System.out.println("XTEA: " + Arrays.toString(keys));
                }

                index.putFile(objId, 0, landData, keys);

                System.out.println("LAND packed: " + objId);
            }

            System.out.println("Done region " + regionId);

        } catch (Exception e) {
            System.out.println("Failed region " + regionId + ": " + e.getMessage());
        }
    }

    private static byte[] read(File file) throws Exception {
        byte[] data = Files.readAllBytes(file.toPath());

        if (file.getName().endsWith(".gz")) {
            data = CompressionUtils.gunzip(data);
        }

        return data;
    }

    private static File getFile(String dir, int id) {
        File gz = new File(dir, id + ".gz");
        if (gz.exists()) return gz;

        File dat = new File(dir, id + ".dat");
        if (dat.exists()) return dat;

        return null;
    }

    private static int[] resolveKeys(int regionId, int[] manualKeys) {
        if (manualKeys != null && manualKeys.length == 4) {
            return manualKeys;
        }
        return XTEAManager.lookup(regionId);
    }

    private static boolean isNullKeys(int[] keys) {
        return Arrays.equals(keys, XTEAManager.NULL_KEYS);
    }

    public static int[] parseKeys(String input) {
        if (input == null || input.isEmpty()) return null;

        String[] split = input.split(",");
        if (split.length != 4) return null;

        int[] keys = new int[4];
        try {
            for (int i = 0; i < 4; i++) {
                keys[i] = Integer.parseInt(split[i].trim());
            }
            return keys;
        } catch (Exception e) {
            return null;
        }
    }
}