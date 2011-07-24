package org.morden.lightcontrol;

import org.bukkit.command.CommandSender;
import org.bukkit.block.BlockFace;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.tal.redstonechips.util.Locations;

/**
 *
 * @author Dennis Flanagan
 */
public class netherlight extends LightCore {
	
    @Override
	protected void updateOutputs() {
		 for (int i = 0; i < interfaceBlocks.length; i++)
            updateBlocks(interfaceBlocks[i], output.get(i));
	}
	
    private void updateBlocks(Location origin, boolean value) {
        for (BlockFace face : faces) {
            Location f = Locations.getFace(origin, face);
			Block block = world.getBlockAt(f);
            if (block.getType().equals(Material.NETHERRACK)) {
				Block fireBlock = block.getFace(BlockFace.UP);
				if (value) {
					fireBlock.setType(Material.FIRE);
				} else {
					fireBlock.setType(Material.AIR);
				}
			}
        }
    }
}
