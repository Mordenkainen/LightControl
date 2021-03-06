package org.morden.lightcontrol.lights;

import org.bukkit.command.CommandSender;
import org.bukkit.block.BlockFace;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 *
 * @author Dennis Flanagan
 */
public class netherlight extends LightCore {
    @Override
    protected boolean init(CommandSender sender, String[] args) {
        onMaterial = Material.NETHERRACK;
        offMaterial = Material.NETHERRACK;
        return super.init(sender, args);
    }
    
    @Override
    protected void updateBlock(Block block, boolean value) {
        if (block.getType().equals(Material.NETHERRACK)) {
            Block fireBlock = block.getRelative(BlockFace.UP);
            if (value) {
                fireBlock.setType(Material.FIRE);
            } else {
                fireBlock.setType(Material.AIR);
            }
        }
    }
}
