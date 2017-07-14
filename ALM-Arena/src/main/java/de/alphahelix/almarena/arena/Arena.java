package de.alphahelix.almarena.arena;

import de.alphahelix.almarena.ALMArena;
import de.alphahelix.almcore.ALMCore;
import de.alphahelix.almutils.ZIPUtil;
import org.apache.commons.io.FileDeleteStrategy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class Arena implements Serializable {

    private String name;
    private String fileName;
    private ArenaFile.ArenaItem icon;
    private ArrayList<ArenaFile.NotInitLocation> spawns = new ArrayList<>();
    private transient ArrayList<Location> realSpawns = new ArrayList<>();

    public Arena(String name, String fileName, ArenaFile.ArenaItem icon, ArrayList<ArenaFile.NotInitLocation> spawns) {
        this.name = name;
        this.fileName = fileName;
        this.icon = icon;
        this.spawns = spawns;
    }

    /**
     * Gets a instance of an arena by its name
     *
     * @param name the name of the arena
     * @return the instance
     */
    public static Arena getArena(String name) {
        return ALMArena.getArenaFile().getArena(ChatColor.stripColor(name).replace(" ", "_"));
    }

    /**
     * Transforms the arena into a world
     */
    public void loadArena() {
        ZIPUtil.unzip("plugins/AlphaGameLibary/arenas/" + fileName + ".zip", Bukkit.getWorlds().get(0).getWorldFolder().getParent());
        new BukkitRunnable() {
            public void run() {
                Bukkit.createWorld(new WorldCreator(fileName));

                for (ArenaFile.NotInitLocation notInitLocation : getSpawns()) {
                    realSpawns.add(notInitLocation.realize());
                }
            }
        }.runTaskLater(ALMCore.getInstance(), 25);
    }

    /**
     * Deletes the folder of the world
     */
    public void deleteArena() {
        if (Bukkit.getServer().unloadWorld(fileName, false)) {
            try {
                FileDeleteStrategy.FORCE.delete(new File(fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getName() {
        return name;
    }

    public ArrayList<ArenaFile.NotInitLocation> getSpawns() {
        return spawns;
    }

    public ArrayList<Location> getRealSpawns() {
        return realSpawns;
    }

    public ArenaFile.ArenaItem getIcon() {
        return icon;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public String toString() {
        return "Arena{" +
                "name='" + name + '\'' +
                ", fileName='" + fileName + '\'' +
                ", icon=" + icon +
                ", spawns=" + spawns +
                ", realSpawns=" + realSpawns +
                '}';
    }
}

