package com.creativedurability;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Menyimpan status on/off durability-di-Creative ke sebuah file config
 * ({@code config/creativedurability.properties}), supaya pengaturan ini
 * tetap ada setelah server/game di-restart.
 *
 * Sengaja tidak memakai SavedData/PersistentState bawaan Minecraft, karena
 * API itu terikat per-world dan lebih rawan berubah antar versi. File
 * properties biasa jauh lebih stabil dan berlaku untuk seluruh server.
 */
public final class CreativeDurabilitySettings {
    private static final String FILE_NAME = "creativedurability.properties";
    private static final String KEY_ENABLED = "enabled";

    private static volatile boolean enabled = false;

    private CreativeDurabilitySettings() {
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean value) {
        enabled = value;
        save();
    }

    /** Dipanggil sekali saat mod diinisialisasi untuk membaca config lama (jika ada). */
    public static void load() {
        Path path = configPath();
        if (!Files.exists(path)) {
            save();
            return;
        }

        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(path)) {
            props.load(in);
            enabled = Boolean.parseBoolean(props.getProperty(KEY_ENABLED, "false"));
        } catch (IOException e) {
            CreativeDurabilityMod.LOGGER.warn(
                    "Gagal membaca config Creative Durability, memakai default (nonaktif).", e);
            enabled = false;
        }
    }

    private static void save() {
        Path path = configPath();
        Properties props = new Properties();
        props.setProperty(KEY_ENABLED, Boolean.toString(enabled));

        try {
            Files.createDirectories(path.getParent());
            try (OutputStream out = Files.newOutputStream(path)) {
                props.store(out, "Pengaturan Creative Durability. Bisa diedit manual saat server mati.");
            }
        } catch (IOException e) {
            CreativeDurabilityMod.LOGGER.warn("Gagal menyimpan config Creative Durability.", e);
        }
    }

    private static Path configPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
    }
}
