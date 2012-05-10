/**
 * 
 */
package org.morden.lightcontrol.screens;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.morden.lightcontrol.LightControl;

/**
 * @author Deacon
 *
 */
public class glassscreen extends RSDisplay {
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
            Map<String, Object> prefs = LightControl.redstonechips.getPrefs().getPrefs();
            if (Boolean.parseBoolean(prefs.get("glasslight.dropGlowstone").toString()) == false) {
                event.setCancelled(true);
                event.getBlock().setType(Material.AIR);
            }
        }
    }
    
    @Override
    public void UpdateBlock(Block block, int value) {
        if (block.getType().equals(onMaterial) && value == 0) {
            block.setType(offMaterial);
            if (rsTorches.contains(block)) {
                rsTorches.remove(rsTorches.indexOf(block));
            }
        } else if (block.getType().equals(offMaterial) && value != 0) {
            block.setType(onMaterial);
            if (!rsTorches.contains(block)) {
                rsTorches.add(block);
            }
        } else if (block.getType().equals(onMaterial) && value != 0) {
            if (!rsTorches.contains(block)) {
                rsTorches.add(block);
            }
        } 
    }
}
