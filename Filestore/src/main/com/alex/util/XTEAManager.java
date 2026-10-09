package com.alex.util;

import com.google.gson.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages XTEA keys for map regions.
 */
public final class XTEAManager {

    private static final Logger logger = Logger.getLogger(XTEAManager.class.getName());

    public static final int[] NULL_KEYS = new int[4];
    private static final Map<Integer, int[]> regionKeys = new HashMap<>();

    private XTEAManager() { }

    /**
     * Returns the XTEA keys for a given region, or NULL_KEYS if missing.
     */
    public static int[] lookup(int regionId) {
        return regionKeys.getOrDefault(regionId, NULL_KEYS);
    }

    /**
     * Returns an unmodifiable view of all loaded keys.
     */
    public static Map<Integer, int[]> allKeys() {
        return Collections.unmodifiableMap(regionKeys);
    }

    /**
     * Loads XTEA keys from a json file.
     *
     * @param jsonPath path to json file
     * @return true if keys were loaded successfully
     */
    public static boolean load(Path jsonPath) {
        regionKeys.clear();

        try {
            String content = Files.readString(jsonPath);
            JsonObject root = JsonParser.parseString(content).getAsJsonObject();
            JsonArray xteas = root.getAsJsonArray("xteas");

            if (xteas == null) {
                logger.severe("Missing 'xteas' array in JSON.");
                return false;
            }

            for (JsonElement elem : xteas) {
                JsonObject entry = elem.getAsJsonObject();

                String regionStr = entry.get("regionId").getAsString().trim();
                int regionId;

                try {
                    regionId = Integer.parseInt(regionStr);
                } catch (NumberFormatException e) {
                    logger.warning("Invalid regionId: " + regionStr);
                    continue;
                }

                String keysRaw = entry.get("keys").getAsString().trim();
                String[] split = keysRaw.split(",");

                if (split.length != 4) {
                    logger.warning("Invalid key length for region " + regionId + ": " + keysRaw);
                    continue;
                }

                int[] keys = new int[4];
                boolean valid = true;

                for (int i = 0; i < 4; i++) {
                    try {
                        keys[i] = Integer.parseInt(split[i].trim());
                    } catch (NumberFormatException e) {
                        logger.warning("Invalid key value in region " + regionId + ": " + keysRaw);
                        valid = false;
                        break;
                    }
                }

                if (!valid) continue;

                regionKeys.put(regionId, keys);
            }

            logger.info("Loaded " + regionKeys.size() + " XTEA keys.");
            return !regionKeys.isEmpty();

        } catch (IOException | JsonParseException e) {
            logger.log(Level.SEVERE, "Failed to load XTEA keys", e);
            return false;
        }
    }

    /**
     * Dumps all keys to text files in a directory.
     */
    public static void dump(Path outputDir) {
        try {
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }

            for (var entry : regionKeys.entrySet()) {
                int region = entry.getKey();
                int[] keys = entry.getValue();
                Path file = outputDir.resolve(region + ".txt");
                Files.writeString(file, Arrays.toString(keys));
            }

            logger.info(() -> "Dumped XTEA keys to " + outputDir);

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to dump XTEA keys", e);
        }
    }

    public static void main(String[] args) {
        Path jsonFile = Paths.get("../Server/data/configs/xteas.json");
        Path outputDir = Paths.get("../Dumps/maps/xteas/", "txt");

        if (load(jsonFile)) {
            dump(outputDir);
        } else {
            logger.severe("Failed to load XTEA keys.");
        }
    }
}
