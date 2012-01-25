package org.morden.lightcontrol;

import org.bukkit.command.CommandSender;
import org.bukkit.Material;
import org.bukkit.material.Pumpkin;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

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
        BlockState state = block.getState();
        if (block.getType().equals(onMaterial) && !value) {
            Pumpkin data = (Pumpkin)(state.getData());
            state.setType(offMaterial);
            state.setData(data);
            state.update(true);
        } else if (block.getType().equals(offMaterial) && value) {
            Pumpkin data = (Pumpkin)(state.getData());
            state.setType(onMaterial);
            state.setData(data);
            state.update(true);
        } 
    }
}
