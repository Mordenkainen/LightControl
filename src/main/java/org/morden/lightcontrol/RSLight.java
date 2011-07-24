package org.morden.lightcontrol;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.Material;
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
			((Block)event.getBlock()).setType(Material.REDSTONE_TORCH_OFF);
		}
	}
	
	public void onBlockBreak(BlockBreakEvent event) {
		if (rsTorches.contains(event.getBlock())) {
			rsTorches.remove(rsTorches.indexOf(event.getBlock()));
		}
	}
}