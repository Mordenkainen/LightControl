/**
 * 
 */
package org.morden.lightcontrol.screens;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;

/**
 * @author Deacon
 *
 */
public class pumpkinscreen extends DisplayCore {
	@Override
    protected boolean init(CommandSender sender, String[] args) {
        onMaterial = Material.JACK_O_LANTERN;
        offMaterial = Material.PUMPKIN;
        return super.init(sender, args);
    }
	
    @Override
    public void UpdateBlock(Block block, int value) {
        if (block.getType().equals(onMaterial) && value == 0) {
        	block.setTypeIdAndData(offMaterial.getId(), block.getData(), false);
        } else if (block.getType().equals(offMaterial) && value != 0) {
        	block.setTypeIdAndData(onMaterial.getId(), block.getData(), false);
        } 
    }
}
