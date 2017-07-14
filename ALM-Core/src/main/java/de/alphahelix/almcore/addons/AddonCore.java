package de.alphahelix.almcore.addons;


import de.alphahelix.almcore.addons.core.AddonManager;
import de.alphahelix.almcore.addons.core.SimpleAddonManager;

import java.io.File;
import java.util.logging.Level;

public class AddonCore {

    private static File addonFolder;
    private static AddonLogger logger;
    private static AddonManager addonManager;

    public static File getAddonFolder() {
        return addonFolder;
    }

    public static AddonLogger getLogger() {
        return logger;
    }

    public static AddonManager getAddonManager() {
        return addonManager;
    }

    public static void enable() {
        logger = new AddonLogger();

        addonFolder = new File("plugins/UHC/Addons");
        if (!addonFolder.exists())
            addonFolder.mkdirs();

        getLogger().log(Level.INFO, "Loading addons...");
        addonManager = new SimpleAddonManager(getAddonFolder(), new AddonCore());
        addonManager.loadAddons();

        getLogger().log(Level.INFO, "Successfully loaded " + addonManager.getAddons().length + " Addons");
    }
}
