package com.alex;

import com.alex.store.Store;
import content.ContentPaths;

import java.io.IOException;

public final class Cache {
    private static Store store;

    private Cache() {}

    public static void init() throws IOException {
        store = new Store("../" + ContentPaths.INSTANCE.getCACHE());
        System.out.println("Initializing cache...");
    }

    public static void init(String path) throws IOException {
        store = new Store(path);
    }

    public static Store getStore() {
        return store;
    }

    public static boolean isInitialized() {
        return store != null;
    }
}