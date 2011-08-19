package org.morden.lightcontrol;

import org.bukkit.command.CommandSender;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockBreakEvent;

/**
 *
 * @author Dennis Flanagan
 */
public class glasslight extends RSLight {
	@Override
    protected boolean init(CommandSender sender, String[] args) {
		onMaterial = Material.GLOWSTONE;
		offMaterial = Material.GLASS;
		return super.init(sender, args);
	}
	
	@Override
	public void onBlockPhysics(BlockPhysicsEvent event) {
		return;
	}
	
	@Override
	public void onBlockBreak(BlockBreakEvent event) {
		if (rsTorches.contains(event.getBlock())) {
			rsTorches.remove(rsTorches.indexOf(event.getBlock()));
			event.setCancelled(true);
			event.getBlock().setType(Material.AIR);
		}
	}
	
    @Override
	protected void updateBlock(Block block, boolean value) {
		if (block.getType().equals(onMaterial) && !value) {
			block.setType(offMaterial);
			if (rsTorches.contains(block)) {
				rsTorches.remove(rsTorches.indexOf(block));
			}
		} else if (block.getType().equals(offMaterial) && value) {
			block.setType(onMaterial);
			if (!rsTorches.contains(block)) {
				rsTorches.add(block);
			}
		} else if (block.getType().equals(onMaterial) && value) {
			if (!rsTorches.contains(block)) {
				rsTorches.add(block);
			}
		} 
    }
}
