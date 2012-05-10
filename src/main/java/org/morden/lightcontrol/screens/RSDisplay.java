/**
 * 
 */
package org.morden.lightcontrol.screens;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.morden.lightcontrol.LightControl;

/**
 * @author Deacon
 *
 */
public abstract class RSDisplay extends DisplayCore {
	protected List<Block> rsTorches = new ArrayList<Block>();
	
	@Override
    protected boolean init(CommandSender sender, String[] args) {
        if (super.init(sender, args)) {
            LightControl.registerRSDisplayCircuit(this);
            return true;
        }
        return false;
    }
    
    @Override
    public void circuitShutdown() {
        LightControl.deregisterRSDisplayCircuit(this);
    }

    public void onBlockPhysics(BlockPhysicsEvent event) {
        if (rsTorches.contains(event.getBlock())) {
            event.setCancelled(true);
            ((Block)event.getBlock()).setTypeIdAndData(offMaterial.getId(),((Block)event.getBlock()).getData() ,false);
        }
    }
    
    public void onBlockBreak(BlockBreakEvent event) {
        if (rsTorches.contains(event.getBlock())) {
            rsTorches.remove(rsTorches.indexOf(event.getBlock()));
        }
    }
    
    @Override
    public void UpdateBlock(Block block, int value) {
        if (block.getType().equals(onMaterial) && value == 0) {
            if (!rsTorches.contains(block)) {
                rsTorches.add(block);
            }
            block.setTypeIdAndData(offMaterial.getId(), block.getData(), false);
        } else if (block.getType().equals(offMaterial) && value != 0) {
            if (rsTorches.contains(block)) {
                rsTorches.remove(rsTorches.indexOf(block));
            }
            block.setTypeIdAndData(onMaterial.getId(), block.getData(), false);
        } else if (block.getType().equals(offMaterial) && value == 0) {
            if (!rsTorches.contains(block)) {
                rsTorches.add(block);
            }
        }
    }
}
