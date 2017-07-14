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

package de.alphahelix.almcore.addons.core;

import java.io.File;

/**
 * This ones need to be extended for a Addon of a certain plugin
 */
public abstract class Addon {
	
	private File dataFolder;
	private AddonDescriptionFile description;
	private ClassLoader loader;
	
	final void init(AddonClassLoader classLoader, File dataFolder, AddonDescriptionFile description) {
		this.dataFolder = dataFolder;
		this.description = description;
		this.loader = classLoader;
		onEnable();
	}
	
	public abstract void onEnable ();
	
	public File getDataFolder() {
		return dataFolder;
	}
	
	public AddonDescriptionFile getDescription() {
		return description;
	}
	
	public ClassLoader getLoader() {
		return loader;
	}
}
