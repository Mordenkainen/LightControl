package org.morden.lightcontrol;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockBreakEvent;

/**
 *
 * @author Dennis Flanagan
 */
public abstract class RSLight extends LightCore {
    protected List<Block> rsTorches = new ArrayList<Block>();
    
    @Override
    protected boolean init(CommandSender sender, String[] args) {
        if (super.init(sender, args)) {
            LightControl.registerRSTorchLightCircuit(this);
            return true;
        }
        return false;
    }
    
    @Override
    public void circuitShutdown() {
        LightControl.deregisterRSTorchLightCircuit(this);
    }
    
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if (rsTorches.contains(event.getBlock())) {
            event.setCancelled(true);
            ((Block)event.getBlock()).setType(offMaterial);
        }
    }
    
    public void onBlockBreak(BlockBreakEvent event) {
        if (rsTorches.contains(event.getBlock())) {
            rsTorches.remove(rsTorches.indexOf(event.getBlock()));
        }
    }
    
    @Override
    protected void updateBlock(Block block, boolean value) {
        if (block.getType().equals(onMaterial) && !value) {
            if (!rsTorches.contains(block)) {
                rsTorches.add(block);
            }
            block.setType(offMaterial);
        } else if (block.getType().equals(offMaterial) && value) {
            if (rsTorches.contains(block)) {
                rsTorches.remove(rsTorches.indexOf(block));
            }
            block.setType(onMaterial);
        } else if (block.getType().equals(offMaterial) && !value) {
            if (!rsTorches.contains(block)) {
                rsTorches.add(block);
            }
        }
    }
}