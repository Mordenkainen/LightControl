package org.morden.lightcontrol;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.morden.lightcontrol.lights.RSLight;
import org.morden.lightcontrol.lights.glasslight;
import org.morden.lightcontrol.lights.lamplight;
import org.morden.lightcontrol.lights.netherlight;
import org.morden.lightcontrol.lights.pumpkinlight;
import org.morden.lightcontrol.lights.rstorchlight;
import org.morden.lightcontrol.lights.torchlight;
import org.morden.lightcontrol.screens.RSDisplay;
import org.morden.lightcontrol.screens.pumpkinscreen;
import org.morden.lightcontrol.screens.rsscreen;
import org.morden.lightcontrol.screens.torchscreen;
import org.morden.lightcontrol.screens.lampscreen;
import org.morden.lightcontrol.screens.glassscreen;
import org.tal.redstonechips.circuit.CircuitLibrary;
import org.tal.redstonechips.RedstoneChips;
import org.bukkit.event.Listener;
import org.tal.redstonechips.circuit.Circuit;



/**
 *
 * @author Dennis Flanagan
 */
public class LightControl extends CircuitLibrary implements Listener {
    private static List<RSLight> rsTorchLightCircuits = new ArrayList<RSLight>();
    private static List<lamplight> rsLampCircuits = new ArrayList<lamplight>();
    private static List<RSDisplay> rsDisplayCircuits = new ArrayList<RSDisplay>();
    private static List<lampscreen> LampDisplayCircuits = new ArrayList<lampscreen>();
    public static RedstoneChips redstonechips;
    
    @SuppressWarnings("unchecked")
	@Override
    public Class<? extends Circuit>[] getCircuitClasses() {
        return new Class[] { glasslight.class, pumpkinlight.class, rstorchlight.class, torchlight.class, netherlight.class, lamplight.class, pumpkinscreen.class, rsscreen.class, torchscreen.class, lampscreen.class, glassscreen.class };
    }
    
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if (!event.isCancelled()) {
            for (RSLight circuit : rsTorchLightCircuits)
                circuit.onBlockPhysics(event);
            for (RSDisplay circuit : rsDisplayCircuits)
                circuit.onBlockPhysics(event);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.isCancelled()) {
            for (RSLight circuit : rsTorchLightCircuits)
                circuit.onBlockBreak(event);
            for (RSDisplay circuit : rsDisplayCircuits)
                circuit.onBlockBreak(event);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockRedstone(BlockRedstoneEvent event) {
    	for (lamplight circuit : rsLampCircuits)
            circuit.onBlockRedstoneEvent(event);
    	for (lampscreen circuit : LampDisplayCircuits)
            circuit.onBlockRedstoneEvent(event);
    }
    
    static public void registerRSTorchLightCircuit(RSLight circuit) {
        if (!rsTorchLightCircuits.contains(circuit))
            rsTorchLightCircuits.add(circuit);
    }

    static public boolean deregisterRSTorchLightCircuit(RSLight circuit) {
        return rsTorchLightCircuits.remove(circuit);
    }
    
    static public void registerRSDisplayCircuit(RSDisplay circuit) {
        if (!rsDisplayCircuits.contains(circuit))
        	rsDisplayCircuits.add(circuit);
    }

    static public boolean deregisterRSDisplayCircuit(RSDisplay circuit) {
        return rsDisplayCircuits.remove(circuit);
    }
    
    static public void registerRSLampCircuit(lamplight circuit) {
        if (!rsLampCircuits.contains(circuit))
        	rsLampCircuits.add(circuit);
    }

    static public boolean deregisterRSLampCircuit(lamplight circuit) {
        return rsLampCircuits.remove(circuit);
    }
    
    static public void registerLampDisplayCircuit(lampscreen circuit) {
        if (!LampDisplayCircuits.contains(circuit))
        	LampDisplayCircuits.add(circuit);
    }

    static public boolean deregisterLampDisplayCircuit(lampscreen circuit) {
        return LampDisplayCircuits.remove(circuit);
    }
    
    @Override
    public void onRedstoneChipsEnable(RedstoneChips instance) {
        instance.getPrefs().registerCircuitPreference(glasslight.class, "dropGlowstone", false);
        instance.getPrefs().registerCircuitPreference(LightControl.class, "lampPostBlocks", "WOOD,LOG,IRON_FENCE,FENCE,NETHER_BRICK,NETHER_FENCE");
        redstonechips = instance;
    }
}
