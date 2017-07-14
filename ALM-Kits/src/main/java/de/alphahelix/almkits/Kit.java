package de.alphahelix.almkits;

import com.google.common.base.Objects;
import de.alphahelix.almkits.events.KitReceiveEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.Arrays;
import java.util.WeakHashMap;

public class Kit implements Serializable {

    private static WeakHashMap<String, Kit> kits = new WeakHashMap<>();

    private String name;
    private String rawName;
    private int price;
    private ItemStack icon;
    private ItemStack[] items;

    public Kit(String name, int price, ItemStack icon, ItemStack... items) {
        this.name = name;
        this.rawName = ChatColor.stripColor(name).replace(" ", "_");
        this.price = price;
        this.icon = icon;
        this.items = items;

        kits.put(rawName, this);
    }

    public static Kit getKitByName(String name) {
        if (kits.containsKey(name))
            return kits.get(name);
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRawName() {
        return rawName;
    }

    public void setRawName(String rawName) {
        this.rawName = rawName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public Kit setIcon(ItemStack icon) {
        this.icon = icon;
        return this;
    }

    public ItemStack[] getItems() {
        return items;
    }

    public void giveItems(Player p) {
        for (ItemStack itemStack : items) {
            if (itemStack == null) continue;
            p.getInventory().addItem(itemStack);
        }
        Bukkit.getPluginManager().callEvent(new KitReceiveEvent(p, this));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Kit kit = (Kit) o;
        return getPrice() == kit.getPrice() &&
                Objects.equal(getName(), kit.getName()) &&
                Objects.equal(getRawName(), kit.getRawName()) &&
                Objects.equal(getIcon(), kit.getIcon());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName(), getRawName(), getPrice(), getIcon());
    }

    @Override
    public String toString() {
        return "Kit{" +
                "name='" + name + '\'' +
                ", rawName='" + rawName + '\'' +
                ", price=" + price +
                ", icon=" + icon +
                ", items=" + Arrays.toString(items) +
                '}';
    }
}
