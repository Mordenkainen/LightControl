package org.morden.lightcontrol;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import org.bukkit.command.CommandSender;
import org.bukkit.block.BlockFace;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.Location;
import org.bukkit.material.MaterialData;
import org.tal.redstonechips.circuit.Circuit;
import org.tal.redstonechips.wireless.Receiver;
import org.tal.redstonechips.util.BitSet7;
import org.tal.redstonechips.util.BitSetUtils;
import org.tal.redstonechips.util.Locations;
import org.tal.redstonechips.RedstoneChips;
import org.tal.redstonechips.util.Locations;


/**
 *
 * @author Dennis Flanagan
 */
public abstract class LightCore extends Circuit {
    private int mode = 0;
    private int addressSize;
    private boolean currentState = false;
    private int currentAddress;
    protected BitSet7 output = null;
    protected static BlockFace[] faces = new BlockFace[] { BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
    protected Material onMaterial = null;
    protected Material offMaterial = null;
    protected ArrayList<ArrayList<Location>> monitoredBlocks = new ArrayList<ArrayList<Location>>();
    
    protected Receiver receiver;
    
    private class ScanParameters {
        public Material onMaterial;
        public Material offMaterial;
        public ArrayList<String> lampPostMaterials;
        public Block origin;
        public BlockFace direction;
        public ArrayList<Location> monitoredBlocks;
        public List<Block> structure;
    }

    private ScanParameters generateParams(Block originBlock, Material offMaterial, Material onMaterial) {
        ScanParameters params = new ScanParameters();
        params.offMaterial = offMaterial;
        params.onMaterial = onMaterial;
        params.structure = new ArrayList<Block>();
        params.lampPostMaterials = new ArrayList<String>(Arrays.asList(LightControl.redstonechips.getPrefs().getPrefs().get("LightControl.lampPostBlocks").toString().split(",")));
        return params;
    }
    
    private void resetScan (Block originBlock, ScanParameters params) {
        params.origin = originBlock;
        params.direction = BlockFace.NORTH;
        params.monitoredBlocks = new ArrayList<Location>();
        params.structure.add(originBlock);
    }  

    private void scan(ScanParameters params) {
        BlockFace direction = params.direction;
        Block origin = params.origin;
        params.direction = Locations.getRightFace(direction);
        checkForLampBlockOnSideFace(params);
        params.direction = Locations.getLeftFace(direction);
        checkForLampBlockOnSideFace(params);
        params.direction = direction;
        checkForLampBlockOnSideFace(params);
        params.direction = direction.getOppositeFace();
        checkForLampBlockOnSideFace(params);
        Block up = origin.getRelative(BlockFace.UP);
        if (!params.structure.contains(up) && ((up.getType()==params.onMaterial) || (up.getType()==params.offMaterial) || (params.lampPostMaterials.contains(up.getType().name())))) {
            params.structure.add(up);
            if ((up.getType()==params.onMaterial) || (up.getType()==params.offMaterial)) {
                params.monitoredBlocks.add(up.getLocation());
            }
            params.direction = direction;
            params.origin = up;
            scan(params);
        }
        Block down = origin.getRelative(BlockFace.DOWN);
        if (!params.structure.contains(down) && ((down.getType()==params.onMaterial) || (down.getType()==params.offMaterial) || (params.lampPostMaterials.contains(down.getType().name())))) {
            params.structure.add(down);
            if ((down.getType()==params.onMaterial) || (down.getType()==params.offMaterial)) {
                params.monitoredBlocks.add(down.getLocation());
            }
            params.direction = direction;
            params.origin = down;
            scan(params);
        }
        params.direction = direction;
        params.origin = origin;
    }
    
    private void checkForLampBlockOnSideFace(ScanParameters params) {
        Block b = params.origin.getRelative(params.direction);
        if (!params.structure.contains(b)) {
            if ((b.getType()==params.onMaterial) || (b.getType()==params.offMaterial) || (params.lampPostMaterials.contains(b.getType().name()))) {
                params.structure.add(b);
                if ((b.getType()==params.onMaterial) || (b.getType()==params.offMaterial)) {
                    params.monitoredBlocks.add(b.getLocation());
                }
                Block origin = params.origin;
                params.origin = b;
                scan(params);
                params.origin = origin;
            }
        }
    }
    
    @Override
    public void inputChange(int inIdx, boolean newLevel) {
        if (mode == 0) {
            if (inIdx == 0) {
                if (newLevel) {
                    output.set(currentAddress, currentState);
                    updateOutputs();
                }
            } else if (inIdx == 1) {
                currentState = newLevel;
            } else {
                currentAddress = BitSetUtils.bitSetToUnsignedInt(inputBits.get(2, addressSize + 2), 0, addressSize);
            }
        } else if (mode == 1) {
            output.set(inIdx, newLevel);
            updateOutputs();
        } else {
            output.set(0, interfaceBlocks.length, newLevel);
            updateOutputs();
        }
    }

    @Override
    protected boolean init(CommandSender sender, String[] args) {
        String channelName = new String();
        boolean wireless = false;
        if (interfaceBlocks.length==0) {
            error(sender, "Expecting at least 1 interface block.");
            return false;
        }
        
        if (inputs.length==0 && args.length==0) {
            error(sender, "Wireless mode requires a channel name.");
            return false;
        }
        
        if (inputs.length == ((int)Math.ceil(Math.log(interfaceBlocks.length)/Math.log(2)) + 2)) {
            mode = 0;
        }
        
        if (interfaceBlocks.length==inputs.length) {
            mode = 1;
        }
        
        if (inputs.length==1) {
            mode = 2;
        }
        
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("addressed")) {
                mode = 0;
                if (args.length > 1) {
                    channelName = args[1];
                    wireless = true;
                }
            } else if (args[0].equalsIgnoreCase("indexed")) {
                mode = 1;
                if (args.length > 1) {
                    channelName = args[1];
                    wireless = true;
                }
            } else if (args[0].equalsIgnoreCase("all")) {
                mode = 2;
                if (args.length > 1) {
                    channelName = args[1];
                    wireless = true;
                }
            } else {
                channelName = args[0];
                wireless = true;
            }
        }
        
        if (!wireless) {
            if (mode == 0) {
                addressSize = (int)Math.ceil(Math.log(interfaceBlocks.length)/Math.log(2));
                if (inputs.length != addressSize + 2) {
                    error(sender, "Expecting " + (addressSize + 2) + " inputs for Addressed Mode.");
                    return false;
                }
            } else if (mode == 1) {
                if (inputs.length != interfaceBlocks.length) {
                    error(sender, "Expecting the same number of inputs and interface blocks for Indexed Mode.");
                    return false;
                }
            } else {
                if (inputs.length > 1 ) {
                    error(sender, "Expecting only one input block for All Mode.");
                    return false;
                }
            }
        } else {
            try {
                int len;
                if (mode == 0) {
                    addressSize = (int)Math.ceil(Math.log(interfaceBlocks.length)/Math.log(2));
                    len = addressSize + 2;
                } else if (mode == 1) {
                    len = interfaceBlocks.length;
                } else len = 1;
                receiver = new LightCoreReceiver();
                receiver.init(sender, channelName, len, this);
            } catch (IllegalArgumentException ie) {
                error(sender, ie.getMessage());
                return false;
            }
        }
        
        ScanParameters params = generateParams(interfaceBlocks[0].getLocation().getBlock(), offMaterial, onMaterial);
        for (int i = 0; i < interfaceBlocks.length; i++) {
            resetScan(interfaceBlocks[i].getLocation().getBlock(), params);
            scan(params);
            monitoredBlocks.add(params.monitoredBlocks);
        }
        
        output = new BitSet7(interfaceBlocks.length);
        
        updateOutputs();
        
        return true;
    }

    class LightCoreReceiver extends Receiver {
        @Override
        public void receive(BitSet7 bits) {
            if (hasDebuggers()) debug("Received " + BitSetUtils.bitSetToBinaryString(bits, 0, getChannelLength()));
            if (mode == 0) {
                output.set(BitSetUtils.bitSetToUnsignedInt(bits.get(1, addressSize + 1), 0, addressSize), bits.get(0));
            } else if (mode == 1) {
                for (int i=0; i<interfaceBlocks.length; i++) {
                    output.set(i, bits.get(i));
                }
            } else {
                output.set(0, interfaceBlocks.length, bits.get(0));
            }
            updateOutputs();
        }        
    }
    
    @Override
    protected boolean isStateless() {
        return false;
    }
    
    @Override
    public void setInternalState(Map<String, String> state) {
        BitSet7 outputState = BitSetUtils.mapToBitSet(state, "output") ;
        if (outputState!=null) {
            output = outputState;
        }
        if (state.containsKey("monitoredBlocks")) {
            monitoredBlocks = getBlocksArray(state.get("monitoredBlocks"));
        }
        updateOutputs();
    }

    @Override
    public Map<String, String> getInternalState() {
        Map<String,String> state = new HashMap<String, String>();
        BitSetUtils.bitSetToMap(state, "output", output, interfaceBlocks.length);
        state.put("monitoredBlocks", makeList(monitoredBlocks).toString());
        return state;
    }
    
    protected void updateOutputs() {
        for (int i = 0; i < interfaceBlocks.length; i++) {
            for (int j = 0; j < monitoredBlocks.get(i).size(); j++) {
                Block block = world.getBlockAt(monitoredBlocks.get(i).get(j));
                updateBlock(block, output.get(i));
            }
        }
    }
    
    private List<Integer> makeBlockList(Location l) {
        List<Integer> list = new ArrayList<Integer>();
        list.add(l.getBlockX());
        list.add(l.getBlockY());
        list.add(l.getBlockZ());
        return list;
    }
    
    private List<List<Integer>> makeBlockListsList(ArrayList<Location> vs) {
        List<List<Integer>> list = new ArrayList<List<Integer>>();
        if(!vs.isEmpty())
            for (int i = 0; i < vs.size(); i++)
                list.add(makeBlockList(vs.get(i)));
        return list;
    }
    
    
    private Object makeList(ArrayList<ArrayList<Location>> vs) {
        List<List<List<Integer>>> list = new ArrayList<List<List<Integer>>>();
        if(!vs.isEmpty())
            for (int i = 0; i < vs.size(); i++)
                list.add(makeBlockListsList(vs.get(i)));
        return list;
     }
    
    private ArrayList<ArrayList<Location>> getBlocksArray(String list) {
        ArrayList<ArrayList<Location>> locations = new ArrayList<ArrayList<Location>>();
        int opencount;
        int closeloc;
        String tmp1 = list.substring(1);
        while (tmp1.charAt(0) != ']') {
            tmp1 = tmp1.substring(tmp1.indexOf('['));
            opencount = -1;
            closeloc = 0;
            while (opencount != 0) {
                if (tmp1.charAt(closeloc) == '[') {
                    if (opencount == -1)
                        opencount++;
                    opencount++;
                }
                if (tmp1.charAt(closeloc) == ']')
                    opencount--;
                closeloc++;
            }
            locations.add(locationParser(tmp1.substring(0,closeloc)));
            tmp1 = tmp1.substring(closeloc);
        }
        return locations;
    }
    
    private ArrayList<Location> locationParser (String locations){
        ArrayList<Location> interfaceLocations = new ArrayList<Location>();
        locations = locations.substring(1).replace("[", "").replace("], ", ";").replace("]]", "");
        if (locations.charAt(0) == ']')
            return interfaceLocations;
        String[] tmpLocations = locations.split(";");
        for (String location: tmpLocations) {
            String[] coords = location.split(",");
            interfaceLocations.add(new Location(world, Integer.decode(coords[0].trim()), Integer.decode(coords[1].trim()), Integer.decode(coords[2].trim())));
        }
        return interfaceLocations;
    }
    
    protected abstract void updateBlock(Block block, boolean value);
}
