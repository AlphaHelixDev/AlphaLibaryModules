package de.alphahelix.almutils;

import org.bukkit.entity.Player;

import java.util.WeakHashMap;

public class PlayerMap<V> extends WeakHashMap<String, V> {
    public void put(Player p, V obj) {
        put(p.getName(), obj);
    }

    public V get(Player p) {
        return get(p.getName());
    }

    public Player[] getKeys() {
        return Util.makePlayerArray(keySet());
    }
}
