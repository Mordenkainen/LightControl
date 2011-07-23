package org.tal.basiccircuits;


import org.bukkit.command.CommandSender;
import org.bukkit.block.BlockFace;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.tal.redstonechips.channel.ReceivingCircuit;
import org.tal.redstonechips.util.BitSet7;
import org.tal.redstonechips.util.BitSetUtils;
import org.tal.redstonechips.util.Locations;

/**
 *
 * @author Dennis Flanagan
 */
public class lighttest extends LightControl {
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
            BlockState state = block.getState();
            if (block.getType().equals(Material.GLOWSTONE) && !value) {
				state.setType(Material.GLASS);
				state.update(true);
            } else if (block.getType().equals(Material.GLASS) && value) {
				state.setType(Material.GLOWSTONE);
				state.update(true);
            } 
        }
    }
}
