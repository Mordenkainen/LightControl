package org.morden.lightcontrol.lights;

import org.bukkit.command.CommandSender;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 *
 * @author Dennis Flanagan
 */
public class pumpkinlight extends LightCore {
    @Override
    protected boolean init(CommandSender sender, String[] args) {
        onMaterial = Material.JACK_O_LANTERN;
        offMaterial = Material.PUMPKIN;
        return super.init(sender, args);
    }
    
    @Override
    protected void updateBlock(Block block, boolean value) {
        if (block.getType().equals(onMaterial) && !value) {
            block.setTypeIdAndData(offMaterial.getId(), block.getData(), false);
        } else if (block.getType().equals(offMaterial) && value) {
            block.setTypeIdAndData(onMaterial.getId(), block.getData(), false);
        } 
    }
}
