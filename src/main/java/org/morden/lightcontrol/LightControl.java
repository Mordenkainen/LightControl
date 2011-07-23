package org.morden.lightcontrol;

import org.tal.redstonechips.RedstoneChips;
import org.tal.redstonechips.circuit.CircuitLibrary;

/**
 *
 * @author Dennis Flanagan
 */
public class LightControl extends CircuitLibrary {
    @Override
    public Class[] getCircuitClasses() {
        return new Class[] { glasslight.class };
    }
}
