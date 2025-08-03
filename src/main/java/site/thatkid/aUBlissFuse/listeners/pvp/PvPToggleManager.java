package site.thatkid.aUBlissFuse.listeners.pvp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class PvPToggleManager {

    private static final String FILE_NAME = "pvpToggle.json";
    private static boolean pvpEnabled = true;
    private static File file;

    public static void init(File dataFolder) {
        file = new File(dataFolder, FILE_NAME);
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        load();
    }

    private static void load() {
        if (!file.exists()) {
            save();
            return;
        }

        try (Reader reader = new FileReader(file)) {
            JsonWrapper wrapper = new Gson().fromJson(reader, JsonWrapper.class);
            pvpEnabled = (wrapper != null) && wrapper.enabled;
        } catch (IOException ex) {
            ex.printStackTrace();
            pvpEnabled = false;
        }
    }

    public static void save() {
        try (Writer writer = new FileWriter(file)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(new JsonWrapper(pvpEnabled), writer);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void setToggled(boolean enabled) {
        pvpEnabled = enabled;
        save();
    }

    public static boolean isToggled() {
        return pvpEnabled;
    }

    private static class JsonWrapper {
        boolean enabled;
        JsonWrapper(boolean enabled) { this.enabled = enabled; }
    }
}
