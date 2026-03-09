package com.playerhighlight;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerHighlightClient implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("playerhighlight");
    public static HighlightConfig config;
    public static boolean highlightEnabled = true;

    private static KeyMapping toggleKey;

    @Override
    public void onInitializeClient() {
        LOGGER.info("[PlayerHighlight] Загружен для Minecraft 1.21.11! Нажми H для переключения.");

        config = HighlightConfig.load();
        highlightEnabled = config.enabled;

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.playerhighlight.toggle",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                "category.playerhighlight"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.consumeClick()) {
                highlightEnabled = !highlightEnabled;
                config.enabled = highlightEnabled;
                config.save();

                if (client.player != null) {
                    client.player.displayClientMessage(
                            Component.literal("§b[PlayerHighlight] §r" +
                                    (highlightEnabled ? "§aВключён ✔" : "§cВыключён ✘")),
                            true
                    );
                }
            }

            if (client.level == null) return;

            for (Player player : client.level.players()) {
                boolean isSelf = (player == client.player);
                boolean shouldGlow = highlightEnabled && (!isSelf || config.highlightSelf);
                player.setGlowingTag(shouldGlow);
            }
        });
    }
}
