/**
 * 
 */
package org.morden.lightcontrol.screens;

import org.bukkit.block.Block;

/**
 * @author Deacon
 *
 * Interface to represent the function used to updae a block from off to on and vice versa
 *
 */
public interface BlockUpdater {
	public void UpdateBlock(Block block, int state);
}
