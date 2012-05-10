/**
 * 
 */
package org.morden.lightcontrol.screens;

import java.io.IOException;
import net.eisental.common.parsing.ParsingUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.tal.redstonechips.bitset.BitSet7;
import org.tal.redstonechips.bitset.BitSetUtils;
import org.tal.redstonechips.circuit.Circuit;
import org.tal.redstonechips.memory.Memory;
import org.tal.redstonechips.memory.Ram;
import org.tal.redstonechips.memory.RamListener;
import org.tal.redstonechips.wireless.Receiver;

/**
 * @author Dennis Flanagan
 *
 */
public abstract class DisplayCore extends Circuit implements BlockUpdater{
	private Screen screen;
    private int xWordlength, yWordlength;
    
    private Receiver receiver;
    
    private Ram ram;
    private RamListener ramListener;
    
    private int ramPage = 0, ramPageLength;
    
    protected Material onMaterial = null;
    protected Material offMaterial = null;
    
    @Override
    public void inputChange(int inIdx, boolean state) {
        if (!inputBits.get(0)) return;
        
        if (ram==null) {
            // set pixel
            processPixelInput(inputBits, 1);
        } else {
            // update ram page
            ramPage = BitSetUtils.bitSetToUnsignedInt(inputBits, 1, inputs.length-1);
            if (hasListeners()) debug("Moving to ram page " + ramPage);
            refreshDisplayFromRam();
        }
    }

    class DisplayReceiver extends Receiver {

        @Override
        public void receive(BitSet7 bits) {
            processPixelInput(bits, 0); // set pixel
        }        
    }

    private void processPixelInput(BitSet7 bits, int startIdx) {
        int x = BitSetUtils.bitSetToUnsignedInt(bits, startIdx, xWordlength);
        int y = BitSetUtils.bitSetToUnsignedInt(bits, startIdx+xWordlength, yWordlength);
        int data = BitSetUtils.bitSetToUnsignedInt(bits, startIdx+xWordlength+yWordlength, 1);

        try {
            screen.setPixel(x,y,data, true);
        } catch (IllegalArgumentException ie) {
            if (hasListeners()) debug(ie.getMessage());
        }

        if (hasListeners()) debug("Setting (" + x + ", " + y + ") to " + data);        
    }
    
    class DisplayRamListener implements RamListener {
        @Override
        public void dataChanged(Ram ram, BitSet7 address, BitSet7 data) {
            int state = BitSetUtils.bitSetToUnsignedInt(data, 0, 1);
            int intaddr = BitSetUtils.bitSetToUnsignedInt(address, 0, 32);
            int offset = ramPage * ramPageLength;
            
            if (intaddr >= offset && intaddr < offset + ramPageLength) {
                int idx = intaddr - offset;
                int x = idx % screen.getDescription().addrWidth;
                int y = idx / screen.getDescription().addrWidth;

                try {
                    screen.setPixel(x, y, state, true);
                } catch (IllegalArgumentException ie) {
                    if (hasListeners()) debug(ie.getMessage());
                }

                if (hasListeners()) debug("Setting (" + x + ", " + y + ") to " + data);
            }
        }
    }
    
    private void refreshDisplayFromRam() {
        int offset = ramPage * ramPageLength;
        for (int i=offset; i<offset+ramPageLength; i++) {
            int state = BitSetUtils.bitSetToUnsignedInt(ram.read(i), 0, 1);
            int x = (i-offset) % screen.getDescription().addrWidth;
            int y = (i-offset) / screen.getDescription().addrWidth;
            
            try {
                screen.setPixel(x, y, state, true);
            } catch (IllegalArgumentException ie) {
                if (hasListeners()) debug(ie.getMessage());
            }

            if (hasListeners()) debug("Setting (" + x + ", " + y + ") to " + state);
        }
    }
    
    @Override
    protected boolean init(CommandSender sender, String[] args) {
        String channel = null;
        int[] size = null;
        
        String[] split = args[0].split("x");
        if (split.length==2 && ParsingUtils.isInt((split[0])) && ParsingUtils.isInt((split[1]))) {
            size = new int[] { Integer.parseInt(split[0]), Integer.parseInt(split[1]) };
        }        
        
        int start = (size==null?0:1);
        if (args.length>start) {
            
            
            for (int i=start; i<args.length; i++) {
	            if (args[i].startsWith("$")) {
	                try {
	                    ram = (Ram)Memory.getMemory(args[i].substring(1), Ram.class);
	                } catch (IllegalArgumentException e) {
	                    error(sender, e.getMessage());
	                } catch (IOException e) {
	                    error(sender, e.getMessage());
	                }
	            } else if (channel==null) {
	                if (args[i].startsWith("#"))
	                    channel = args[i].substring(1);
	                else channel = args[i];
	            } else error(sender, "Invalid argument: " + args[i]);
            }
        }
        
        if (interfaceBlocks.length!=2) {
            error(sender, "Expecting 2 interface blocks. One block in each of 2 opposite corners of the display.");
            return false;
        }
        
        try {
            if (size!=null)
                screen = Screen.generateScreen(interfaceBlocks[0].getLocation(), interfaceBlocks[1].getLocation(),
                        size[0], size[1], onMaterial, offMaterial, this);
            else 
                screen = Screen.generateScreen(interfaceBlocks[0].getLocation(), interfaceBlocks[1].getLocation(),
                		onMaterial, offMaterial, this);

            if (ram!=null) ramPageLength = screen.getDescription().addrWidth * screen.getDescription().addrHeight;
            
            info(sender, "Successfully scanned display. ");
            info(sender, "The screen is " + 
                    Math.abs(screen.getDescription().physicalWidth) + "m wide, " + 
                    Math.abs(screen.getDescription().physicalHeight) + "m high. Each pixel is " + 
                    Math.abs(screen.getDescription().pixelWidth) + "m on " + 
                    Math.abs(screen.getDescription().pixelHeight) + "m.");            
            if (ram!=null) info(sender, "Reading pixel data from memory: " + ram.getId());
        } catch (IllegalArgumentException ie) {
            error(sender, ie.getMessage());
            return false;
        }
        
        // expecting 1 clock, enough pins for address width, enough pins for address height, and a pin for data.
        xWordlength = screen.getXLength(); 
        yWordlength = screen.getYLength(); 

        if (channel==null && ram==null) {
            int expectedInputs = 2 + xWordlength + yWordlength;
            if (inputs.length!=expectedInputs && (inputs.length!=0 || channel==null)) {
                error(sender, "Expecting " + expectedInputs + " inputs. 1 clock input, " + xWordlength + " x address input(s)" + (yWordlength!=0?", " + yWordlength + "y address input(s)":"") + 
                        ", and 1 data input.");
                return false;
            } 

            if (sender instanceof Player) {
                info(sender, "inputs: clock - 0, x: 1-" + xWordlength + (yWordlength!=0?", y: " + (xWordlength+1) + "-" + 
                        (xWordlength+yWordlength):"") + ", data: " + (xWordlength+yWordlength+1) + "-" + 
                        (xWordlength+yWordlength+1) + ".");
            }
        } else if (channel!=null) {
            try {
                int len = xWordlength+yWordlength+1;
                receiver = new DisplayReceiver();
                receiver.init(sender, channel, len, this);
            } catch (IllegalArgumentException ie) {
                error(sender, ie.getMessage());
                return false;
            }
        } else if (ram!=null) {
            ramListener = new DisplayRamListener();
            ram.addListener(ramListener);
        }

        if (sender instanceof Player) screen.clear();
        
        if (ram!=null) refreshDisplayFromRam();
        
        return true;
    }
    
    public void UpdateBlock(Block block, int state) {
    }
}
