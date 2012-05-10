package org.morden.lightcontrol.screens;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.morden.lightcontrol.LightControl;

public class lampscreen extends DisplayCore {
protected List<Block> rsLamps = new ArrayList<Block>();
	
    @Override
    protected boolean init(CommandSender sender, String[] args) {
        onMaterial = Material.REDSTONE_LAMP_ON;
        offMaterial = Material.REDSTONE_LAMP_OFF;
        LightControl.registerLampDisplayCircuit(this);
        return super.init(sender, args);
    }
    
    @Override
    public void circuitShutdown() {
        LightControl.deregisterLampDisplayCircuit(this);
    }
    
    public void onBlockRedstoneEvent(BlockRedstoneEvent event) {
            if (rsLamps.contains(event.getBlock())){
            	event.setNewCurrent(15);
            }
    }
    
    @Override
    public void UpdateBlock(Block block, int value) {
        if (block.getType().equals(onMaterial) && value == 0) {
            block.setType(offMaterial);
            if (rsLamps.contains(block)) {
            	rsLamps.remove(rsLamps.indexOf(block));
            }
        } else if (block.getType().equals(offMaterial) && value != 0) {
            block.setType(onMaterial);
            if (!rsLamps.contains(block)) {
            	rsLamps.add(block);
            }
        } else if (block.getType().equals(onMaterial) && value != 0) {
            if (!rsLamps.contains(block)) {
            	rsLamps.add(block);
            }
        } 
    }
}
