package org.morden.lightcontrol.screens;

import org.bukkit.Location;
import org.bukkit.Material;

/**
 *
 * @author Tal Eisenberg
 * Modified by Dennis Flanagan
 */
public class ScreenDescription {
    public Location origin;
    public int physicalWidth, physicalHeight, addrWidth, addrHeight;
    public int pixelWidth, pixelHeight;
    public Screen.Axis widthAxis, heightAxis;
    public Material onMaterial;
    public Material offMaterial;
    public BlockUpdater blockUpdater;
}