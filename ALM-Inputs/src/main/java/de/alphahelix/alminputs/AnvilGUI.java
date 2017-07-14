/*
 *
 *  * Copyright (C) <2017>  <AlphaHelixDev>
 *  *
 *  *       This program is free software: you can redistribute it under the
 *  *       terms of the GNU General Public License as published by
 *  *       the Free Software Foundation, either version 3 of the License.
 *  *
 *  *       This program is distributed in the hope that it will be useful,
 *  *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  *       GNU General Public License for more details.
 *  *
 *  *       You should have received a copy of the GNU General Public License
 *  *       along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package de.alphahelix.alminputs;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AnvilGUI extends InputGUI {

    @Override
    public void openGUI(Player p) {
        Inventory anvil = Bukkit.createInventory(p, InventoryType.ANVIL);

        anvil.setItem(0, new ItemStack(Material.PAPER));
        p.openInventory(anvil);
    }

    public void openGUI(Player p, ItemStack stack) {
        Inventory anvil = Bukkit.createInventory(p, InventoryType.ANVIL);

        anvil.setItem(0, stack);
        p.openInventory(anvil);
    }
}
