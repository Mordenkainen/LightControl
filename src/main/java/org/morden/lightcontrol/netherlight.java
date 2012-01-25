package org.morden.lightcontrol;

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
        onMaterial = Material.FIRE;
        offMaterial = Material.AIR;
        return super.init(sender, args);
    }
    
    @Override
    protected void updateBlock(Block block, boolean value) {
        if (block.getType().equals(Material.NETHERRACK)) {
            Block fireBlock = block.getFace(BlockFace.UP);
            if (value) {
                fireBlock.setType(onMaterial);
            } else {
                fireBlock.setType(offMaterial);
            }
        }
    }
}
