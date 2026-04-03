package io.github.gamezconz.core;

import java.util.prefs.Preferences;

public class AppConfig {
    private static final Preferences prefs = Preferences.userNodeForPackage(AppConfig.class);
    private static final String DICOM_ROOT_KEY = "dicom_root_dir";
    private static final String LANGUAGE_KEY = "app_language";

    public static String getDicomRoot() {
        return prefs.get(DICOM_ROOT_KEY, ""); 
    }

    public static void setDicomRoot(String path) {
        prefs.put(DICOM_ROOT_KEY, path);
    }

    // --- NUEVO: Gestión de idioma ---
    public static String getLanguage() {
        return prefs.get(LANGUAGE_KEY, "en"); // Inglés por defecto
    }

    public static void setLanguage(String lang) {
        prefs.put(LANGUAGE_KEY, lang);
    }
} 