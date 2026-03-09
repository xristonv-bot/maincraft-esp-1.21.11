package com.playerhighlight;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class HighlightConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH =
            FabricLoader.getInstance().getConfigDir().resolve("playerhighlight.json");

    public boolean enabled = true;
    public boolean pulseEffect = true;
    public boolean highlightSelf = false;
    public int colorR = 0;
    public int colorG = 120;
    public int colorB = 255;

    public static HighlightConfig load() {
        if (CONFIG_PATH.toFile().exists()) {
            try (FileReader reader = new FileReader(CONFIG_PATH.toFile())) {
                HighlightConfig cfg = GSON.fromJson(reader, HighlightConfig.class);
                if (cfg != null) return cfg;
            } catch (IOException e) {
                PlayerHighlightClient.LOGGER.error("[PlayerHighlight] Не удалось загрузить конфиг", e);
            }
        }
        HighlightConfig cfg = new HighlightConfig();
        cfg.save();
        return cfg;
    }

    public void save() {
        try (FileWriter writer = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            PlayerHighlightClient.LOGGER.error("[PlayerHighlight] Не удалось сохранить конфиг", e);
        }
    }
}
