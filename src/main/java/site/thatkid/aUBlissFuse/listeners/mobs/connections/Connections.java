package site.thatkid.aUBlissFuse.listeners.mobs.connections;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Connections {
    public static Map<UUID, EntityConnections> connectionsMap = new HashMap<>();

    public static boolean isConnected(UUID uuid, String type) {
        EntityConnections conn = connectionsMap.get(uuid);
        if (conn == null) return false;

        return switch (type.toLowerCase()) {
            case "villager" -> conn.villager;
            case "chicken" -> conn.chicken;
            case "irongolem" -> conn.ironGolem;
            default -> false;
        };
    }

    public static void saveConnections(File file) {
        try (Writer writer = new FileWriter(file)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(connectionsMap, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadConnections(File file) {
        if (!file.exists()) return;

        try (Reader reader = new FileReader(file)) {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<UUID, EntityConnections>>() {}.getType();
            connectionsMap = gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
