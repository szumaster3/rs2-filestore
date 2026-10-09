package com.alex.tools.dump;

import com.alex.store.*;
import com.alex.util.XTEAManager;
import com.alex.util.crypto.Djb2;
import com.alex.utils.CompressionUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public final class MapDumper {

    private static boolean xteasLoaded = false;

    public static void init(Path xteaPath) {
        if (!xteasLoaded) {
            if (!XTEAManager.load(xteaPath)) {
                System.out.println("Failed to load XTEAs!");
            } else {
                System.out.println("XTEAs loaded: " + XTEAManager.allKeys().size());
            }
            xteasLoaded = true;
        }
    }

    public static void dump(Store store, String path, int[] manualKeys) {

        File outDir = new File(path);
        if (!outDir.exists()) outDir.mkdirs();

        Index mapIndex = store.getIndexes()[5];
        if (mapIndex == null || mapIndex.getTable() == null) {
            System.out.println("Maps not found!");
            return;
        }

        ReferenceTable table = mapIndex.getTable();

        Map<Integer, Integer> hashToRegion = new HashMap<>();

        for (int x = 0; x < 256; x++) {
            for (int y = 0; y < 256; y++) {

                int regionId = (x << 8) | y;

                hashToRegion.put(Djb2.hash("m" + x + "_" + y), regionId);
                hashToRegion.put(Djb2.hash("l" + x + "_" + y), regionId);
            }
        }

        Map<Integer, Integer> mapFiles = new HashMap<>();
        Map<Integer, Integer> landFiles = new HashMap<>();

        for (int fileId : table.getValidArchiveIds()) {

            ArchiveReference entry = table.getArchives()[fileId];
            if (entry == null) continue;

            Integer regionId = hashToRegion.get(entry.getNameHash());
            if (regionId == null) continue;

            int x = (regionId >> 8) & 0xFF;
            int y = regionId & 0xFF;

            if (entry.getNameHash() == Djb2.hash("m" + x + "_" + y)) {
                mapFiles.put(regionId, fileId);
            } else {
                landFiles.put(regionId, fileId);
            }
        }

        Set<Integer> regions = new HashSet<>(mapFiles.keySet());
        regions.retainAll(landFiles.keySet());

        System.out.println("Regions found: " + regions.size());

        File mapsDir = new File(outDir, "map_files");
        File landsDir = new File(outDir, "land_files");

        mapsDir.mkdirs();
        landsDir.mkdirs();

        int dumpedMaps = 0;
        int dumpedLands = 0;

        for (int regionId : regions) {

            int[] keys = resolveKeys(regionId, manualKeys);

            int x = (regionId >> 8) & 0xFF;
            int y = regionId & 0xFF;

            String mapName = "m" + x + "_" + y + ".gz";
            String landName = "l" + x + "_" + y + ".gz";

            dumpedMaps += dumpFile(mapIndex, mapFiles.get(regionId), mapsDir, mapName, null);

            if (isNullKeys(keys)) {
                System.out.println("Missing XTEA for region " + regionId);
                continue;
            }

            int result = dumpFile(mapIndex, landFiles.get(regionId), landsDir, landName, keys);

            if (result == 0) {
                System.out.println("Wrong XTEA for region " + regionId + " -> " + Arrays.toString(keys));
            } else {
                dumpedLands += result;
            }
        }

        System.out.println("Dumped maps: " + dumpedMaps);
        System.out.println("Dumped lands: " + dumpedLands);

        print(outDir, regions, mapFiles, landFiles);
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

    private static int dumpFile(Index index, int fileId, File dir, String name, int[] keys) {
        try {
            byte[] data = (keys == null)
                    ? index.getFile(fileId, 0)
                    : index.getFile(fileId, 0, keys);

            if (data == null) return 0;

            Files.write(new File(dir, name).toPath(), CompressionUtils.gzip(data));
            return 1;

        } catch (Exception e) {
            return 0;
        }
    }

    private static void print(File dir,
                              Set<Integer> regions,
                              Map<Integer, Integer> mapFiles,
                              Map<Integer, Integer> landFiles) {

        File file = new File(dir, "map_index.txt");

        try (var writer = Files.newBufferedWriter(file.toPath())) {

            for (int regionId : regions) {

                Integer mapFile = mapFiles.get(regionId);
                Integer landFile = landFiles.get(regionId);

                if (mapFile == null || landFile == null) continue;

                int regionX = (regionId >> 8) & 0xFF;
                int regionY = regionId & 0xFF;

                writer.write(
                        regionId + "," +
                                (regionX * 64) + "," +
                                (regionY * 64) + "," +
                                mapFile + "," +
                                landFile
                );
                writer.newLine();
            }

        } catch (Exception e) {
            System.out.println("Failed to save index: " + e.getMessage());
        }
    }

    public static void verify(Store store) {

        Index mapIndex = store.getIndexes()[5];
        ReferenceTable table = mapIndex.getTable();

        int errors = 0;

        for (int fileId : table.getValidArchiveIds()) {

            ArchiveReference entry = table.getArchives()[fileId];
            if (entry == null) continue;

            try {
                mapIndex.getFile(fileId, 0);
            } catch (Exception e) {
                errors++;
            }
        }

        System.out.println("Verification errors: " + errors);
    }
}