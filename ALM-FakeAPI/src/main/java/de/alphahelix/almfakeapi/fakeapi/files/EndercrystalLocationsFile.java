/*
 * Copyright (C) <2017>  <AlphaHelixDev>
 *
 *       This program is free software: you can redistribute it under the
 *       terms of the GNU General Public License as published by
 *       the Free Software Foundation, either version 3 of the License.
 *
 *       This program is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU General Public License for more details.
 *
 *       You should have received a copy of the GNU General Public License
 *       along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.alphahelix.almfakeapi.fakeapi.files;


import de.alphahelix.almfakeapi.fakeapi.instances.FakeEndercrystal;
import de.alphahelix.almfile.SimpleJSONFile;

public class EndercrystalLocationsFile extends SimpleJSONFile {

    public EndercrystalLocationsFile() {
        super("plugins/AlphaLibary", "fake_endercrystals.json");
    }

    public void addEndercrystalToFile(FakeEndercrystal fakeEndercrystal) {
        addValuesToList("Endercrystals", fakeEndercrystal);
    }

    public FakeEndercrystal[] getFakeEndercrystalsFromFile() {
        if (jsonContains("Endercrystals"))
            return getListValues("Endercrystals", FakeEndercrystal[].class);
        return new FakeEndercrystal[]{};
    }
}
