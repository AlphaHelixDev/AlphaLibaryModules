/*
 *     Copyright (C) <2016>  <AlphaHelixDev>
 *
 *     This program is free software: you can redistribute it under the
 *     terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.alphahelix.almreflections.nms;

import de.alphahelix.almreflections.reflection.ReflectionUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


public class SimpleTablist {

    private static Constructor<?> chatComponentText, ppoPlayerListHeaderFooter;

    static {
        try {
            chatComponentText = ReflectionUtil.getNmsClass("ChatComponentText").getConstructor(String.class);
            ppoPlayerListHeaderFooter = ReflectionUtil.getNmsClass("PacketPlayOutPlayerListHeaderFooter").getConstructor();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set the tablist of the {@link Player}
     *
     * @param p      you want to change the tablist for
     * @param header what stands above the players
     * @param footer what stands below the players
     */
    public static void setTablistHeaderFooter(Player p, String header, String footer) {

        if (header == null)
            header = "";
        if (footer == null)
            footer = "";

        try {
            Object headerComponent = chatComponentText.newInstance(ChatColor.translateAlternateColorCodes('&', header));
            Object footerComponent = chatComponentText.newInstance(ChatColor.translateAlternateColorCodes('&', footer));

            Object packetPlayOutPlayerListHeaderFooter = ppoPlayerListHeaderFooter.newInstance();

            ReflectionUtil.SaveField h = ReflectionUtil.getDeclaredField("a", packetPlayOutPlayerListHeaderFooter.getClass());
            ReflectionUtil.SaveField f = ReflectionUtil.getDeclaredField("b", packetPlayOutPlayerListHeaderFooter.getClass());

            h.set(packetPlayOutPlayerListHeaderFooter, headerComponent, true);
            f.set(packetPlayOutPlayerListHeaderFooter, footerComponent, true);

            ReflectionUtil.sendPacket(p, packetPlayOutPlayerListHeaderFooter);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | SecurityException e) {
            e.printStackTrace();
        }
    }
}
