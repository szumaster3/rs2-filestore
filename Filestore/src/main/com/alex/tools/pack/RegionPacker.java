package com.alex.tools.pack;

import com.alex.store.Index;
import com.alex.store.Store;
import com.alex.util.XTEAManager;
import content.ContentPaths;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Region transfer tool.
 * <a href="https://rune-server.org/threads/592-map-data-xteas.698452/">Source</a>
 * @author _jordan
 */
public class RegionPacker {
	public static final int[] REGIONS = { 10291 };

	private static final Path MAPS_DIR = Paths.get("Assets/maps/");

	private static Index fromIndex;
	private static Index toIndex;

	public static void main(String[] args) {
		try {
			XTEAManager.load(Paths.get(ContentPaths.INSTANCE.getXTEAS()));

			var toCache = new Store(ContentPaths.INSTANCE.getCACHE());

			//fromIndex = fromCache.getIndexes()[5];
			toIndex = toCache.getIndexes()[5];

			for (int regionId : REGIONS) {

				//transfer(regionId);
				load(regionId);
				// m50_50.dat
				// um50_50.dat
				// l50_50.dat
				// ul50_50.dat
				// n50_50.dat
				toIndex.rewriteTable();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Done: " + Arrays.toString(REGIONS));
	}

	private static void transfer(int regionId) {

		int regionX = (regionId >> 8) * 64;
		int regionY = (regionId & 0xff) * 64;

		int[] xteas = XTEAManager.lookup(regionId);

		put("m", regionX, regionY,
				fromIndex.getFile(fromIndex.getArchiveId(getName("m", regionX, regionY))));
		put("um", regionX, regionY,
				fromIndex.getFile(fromIndex.getArchiveId(getName("um", regionX, regionY))));

		put("l", regionX, regionY,
				fromIndex.getFile(fromIndex.getArchiveId(getName("l", regionX, regionY)), 0, xteas));

		put("ul", regionX, regionY,
				fromIndex.getFile(fromIndex.getArchiveId(getName("ul", regionX, regionY)), 0, xteas));

		String n = getName("n", regionX, regionY);
		byte[] data = fromIndex.getFile(fromIndex.getArchiveId(n), 0);
		if (data == null)
			data = toIndex.getFile(toIndex.getArchiveId(n), 0);

		put(n, data);
	}

	private static void load(int regionId) {

		int regionX = (regionId >> 8) * 64;
		int regionY = (regionId & 0xff) * 64;

		String[] types = {
				"m",
				//"um",
				"l",
				//"ul",
				//"n"
		};


		for (String type : types) {
			String name = getName(type, regionX, regionY);
			byte[] data = load(name);
			put(name, data);
		}
	}

	private static void put(String name, byte[] data) {
		if (data == null) return;

		int archiveId = toIndex.getArchiveId(name);
		if (archiveId == -1)
			archiveId = toIndex.getTable().getValidArchiveIds().length;

		boolean result = toIndex.putFile(
				archiveId,
				0,
				2,
				data,
				null,
				false,
				false,
				name.toLowerCase().hashCode(),
				-1
		);

		System.out.println(name + ", " + result);
	}

	private static void put(String prefix, int x, int y, byte[] data) {
		put(getName(prefix, x, y), data);
	}

	private static String getName(String prefix, int regionX, int regionY) {
		return prefix + ((regionX >> 3) / 8) + "_" + ((regionY >> 3) / 8);
	}

	private static byte[] load(String name) {
		try {
			Path file = MAPS_DIR.resolve(name + ".dat");
			if (Files.exists(file)) {
				return Files.readAllBytes(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}