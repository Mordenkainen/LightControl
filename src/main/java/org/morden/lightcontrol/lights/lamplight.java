package org.morden.lightcontrol.lights;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.morden.lightcontrol.LightControl;

public class lamplight extends LightCore {
	protected List<Block> rsLamps = new ArrayList<Block>();
	
    @Override
    protected boolean init(CommandSender sender, String[] args) {
        onMaterial = Material.REDSTONE_LAMP_ON;
        offMaterial = Material.REDSTONE_LAMP_OFF;
        LightControl.registerRSLampCircuit(this);
        return super.init(sender, args);
    }
    
    @Override
    public void circuitShutdown() {
        LightControl.deregisterRSLampCircuit(this);
    }
    
    public void onBlockRedstoneEvent(BlockRedstoneEvent event) {
            if (rsLamps.contains(event.getBlock())){
            	event.setNewCurrent(15);
            }
    }
    
    @Override
    protected void updateBlock(Block block, boolean value) {
        if (block.getType().equals(onMaterial) && !value) {
            block.setType(offMaterial);
            if (rsLamps.contains(block)) {
            	rsLamps.remove(rsLamps.indexOf(block));
            }
        } else if (block.getType().equals(offMaterial) && value) {
            block.setType(onMaterial);
            if (!rsLamps.contains(block)) {
            	rsLamps.add(block);
            }
        } else if (block.getType().equals(onMaterial) && value) {
            if (!rsLamps.contains(block)) {
            	rsLamps.add(block);
            }
        } 
    }
}
