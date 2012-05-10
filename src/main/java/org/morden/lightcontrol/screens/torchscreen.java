/**
 * 
 */
package org.morden.lightcontrol.screens;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;

/**
 * @author Deacon
 *
 */
public class torchscreen extends RSDisplay {
	@Override
    protected boolean init(CommandSender sender, String[] args) {
        onMaterial = Material.TORCH;
        offMaterial = Material.REDSTONE_TORCH_OFF;
        return super.init(sender, args);
    }
}
