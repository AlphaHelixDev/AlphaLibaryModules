package de.alphahelix.almschematic;

import de.alphahelix.almfile.SimpleJSONFile;
import de.alphahelix.almutils.Cuboid;
import org.apache.commons.lang3.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

interface UndoSave {
    Material getType();

    byte getData();

    Location getOld();
}

public class SchematicManager {

    private static HashMap<String, ArrayList<UndoSave>> saveMap = new HashMap<>();

    public static void save(Location location1, Location location2, String name) {

        new SchematicFile(new Schematic() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public List<LocationDiff> getBlocks() {
                return SchematicManager.getBlocks(location1, location2);
            }
        });
    }

    public static void paste(String name, Location loc) {
        Schematic schematic = SchematicFile.getSchematic(name);
        ArrayList<UndoSave> save = new ArrayList<>();

        for (Schematic.LocationDiff diff : schematic.getBlocks()) {
            Block toEdit = loc.clone().add(diff.getX(), diff.getY(), diff.getZ()).getBlock();

            save.add(new UndoSave() {
                @Override
                public Material getType() {
                    return toEdit.getType();
                }

                @Override
                public byte getData() {
                    return toEdit.getData();
                }

                @Override
                public Location getOld() {
                    return toEdit.getLocation();
                }
            });

            toEdit.setType(diff.getBlockType());
            toEdit.setData(diff.getBlockData());
        }

        saveMap.put(name, save);
    }

    public static void undo(String name) {
        if (saveMap.containsKey(name))
            for (UndoSave us : saveMap.get(name)) {
                us.getOld().getBlock().setType(us.getType());
                us.getOld().getBlock().setData(us.getData());
            }
    }

    private static ArrayList<Schematic.LocationDiff> getBlocks(Location l1, Location l2) {
        ArrayList<Schematic.LocationDiff> b = new ArrayList<>();
        List<Block> blocks = new Cuboid(l1, l2).getBlocks();

        for (Block block : blocks) {
            if (block.getType() == Material.AIR) continue;

            b.add(new Schematic.LocationDiff() {
                @Override
                public Material getBlockType() {
                    return block.getType();
                }

                @Override
                public byte getBlockData() {
                    return block.getData();
                }

                @Override
                public int getX() {
                    return block.getX() - l1.getBlockX();
                }

                @Override
                public int getY() {
                    return block.getY() - l1.getBlockY();
                }

                @Override
                public int getZ() {
                    return block.getZ() - l1.getBlockZ();
                }
            });
        }

        return b;
    }
}

class SchematicFile extends SimpleJSONFile {

    public SchematicFile(Schematic schematic) {
        super("plugins/AlphaLibary/schematics", schematic.getName() + ".json");
        setValue(schematic.getName(), Base64Coder.encodeString(gson.toJson(schematic)));
    }

    public static Schematic getSchematic(String name) {
        Validate.isTrue(new File("plugins/AlphaLibary/schematics", name + ".json").exists(), "There is no schematic called " + name);

        SimpleJSONFile schemFile = new SimpleJSONFile("plugins/AlphaLibary/schematics", name + ".json");

        String jsonInBase64 = schemFile.getValue(name, String.class);
        String json = Base64Coder.decodeString(jsonInBase64);

        return gson.fromJson(json, Schematic.class);
    }
}