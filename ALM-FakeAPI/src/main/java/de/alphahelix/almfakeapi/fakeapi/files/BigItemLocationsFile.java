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


import de.alphahelix.almfakeapi.fakeapi.instances.FakeBigItem;
import de.alphahelix.almfile.SimpleJSONFile;

public class BigItemLocationsFile extends SimpleJSONFile {

    public BigItemLocationsFile() {
        super("plugins/AlphaLibary", "fake_bigitems.json");
    }

    public void addBigItemToFile(FakeBigItem fakeBigItem) {
        addValuesToList("BigItems", fakeBigItem);
    }

    public FakeBigItem[] getFakeBigItemFromFile() {
        if (jsonContains("BigItems"))
            return getListValues("BigItems", FakeBigItem[].class);
        return new FakeBigItem[]{};
    }
}
