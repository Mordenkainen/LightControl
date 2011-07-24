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
public class torchlight extends RSLight {
		
    @Override
	protected void updateOutputs() {
		 for (int i = 0; i < interfaceBlocks.length; i++)
            updateBlocks(interfaceBlocks[i], output.get(i));
	}
	
    private void updateBlocks(Location origin, boolean value) {
        for (BlockFace face : faces) {
            Location f = Locations.getFace(origin, face);
			Block block = world.getBlockAt(f);
            if (block.getType().equals(Material.TORCH) && !value) {
				if (!rsTorches.contains(block)) {
					rsTorches.add(block);
				}
				block.setType(Material.REDSTONE_TORCH_OFF);
            } else if (block.getType().equals(Material.REDSTONE_TORCH_OFF) && value) {
				if (rsTorches.contains(block)) {
					rsTorches.remove(rsTorches.indexOf(block));
				}
				block.setType(Material.TORCH);
            } 
        }
    }
}