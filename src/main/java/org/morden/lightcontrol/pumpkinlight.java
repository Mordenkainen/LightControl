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
public class pumpkinlight extends LightCore {
	private static BlockFace[] faces = new BlockFace[] { BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
	
    @Override
	protected void updateOutputs() {
		 for (int i = 0; i < interfaceBlocks.length; i++)
            updateBlocks(interfaceBlocks[i], output.get(i));
	}
	
    private void updateBlocks(Location origin, boolean value) {
        for (BlockFace face : faces) {
            Location f = Locations.getFace(origin, face);
			Block block = world.getBlockAt(f);
            if (block.getType().equals(Material.JACK_O_LANTERN) && !value) {
				block.setType(Material.PUMPKIN);
            } else if (block.getType().equals(Material.PUMPKIN) && value) {
				block.setType(Material.JACK_O_LANTERN);
            } 
        }
    }
}
