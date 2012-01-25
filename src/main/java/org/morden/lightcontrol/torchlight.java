package org.morden.lightcontrol;

import org.bukkit.command.CommandSender;
import org.bukkit.Material;

/**
 *
 * @author Dennis Flanagan
 */
public class torchlight extends RSLight {
    @Override
    protected boolean init(CommandSender sender, String[] args) {
        onMaterial = Material.TORCH;
        offMaterial = Material.REDSTONE_TORCH_OFF;
        return super.init(sender, args);
    }
}