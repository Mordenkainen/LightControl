package org.morden.lightcontrol;

import org.bukkit.command.CommandSender;
import org.bukkit.block.BlockFace;
import org.tal.redstonechips.channel.ReceivingCircuit;
import org.tal.redstonechips.util.BitSet7;
import org.tal.redstonechips.util.BitSetUtils;

/**
 *
 * @author Dennis Flanagan
 */
public abstract class LightCore extends ReceivingCircuit {
	private boolean mode = false;
	private int addressSize;
	private boolean currentState = false;
	private int currentAddress;
	protected BitSet7 output;
	protected static BlockFace[] faces = new BlockFace[] { BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
 
    @Override
    public void inputChange(int inIdx, boolean newLevel) {
		if (mode) {
			output.set(inIdx, newLevel);
			updateOutputs();
		} else {
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
		
		if (interfaceBlocks.length==1) {
            mode = true;
        }
	
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("indexed")) {
				mode = true;
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
			if (mode) {
				if (inputs.length != interfaceBlocks.length) {
					error(sender, "Expecting the same number of inputs and interface blocks.");
					return false;
				}
			} else {
				addressSize = (int)Math.ceil(Math.log(interfaceBlocks.length)/Math.log(2));
				if (inputs.length != addressSize + 2) {
					error(sender, "Expecting " + (addressSize + 2) + " inputs.");
					return false;
				}
			}
		} else {
			try {
				if (!mode) {
					addressSize = (int)Math.ceil(Math.log(interfaceBlocks.length)/Math.log(2));
				}
				this.initWireless(sender, channelName);
			} catch (IllegalArgumentException ie) {
				error(sender, ie.getMessage());
				return false;
			}
		}
		
		output = new BitSet7(interfaceBlocks.length);
		
		return true;
    }

    @Override
    public void receive(BitSet7 bits) {
        if (hasDebuggers()) debug("Received " + BitSetUtils.bitSetToBinaryString(bits, 0, interfaceBlocks.length));
		if (mode) {
			for (int i=0; i<interfaceBlocks.length; i++) {
                output.set(i, bits.get(i));
            }
		} else {
			output.set(BitSetUtils.bitSetToUnsignedInt(bits.get(1, addressSize + 1), 0, addressSize), bits.get(0));
		}
		updateOutputs();
    }

    @Override
    public void circuitShutdown() {
        if (getChannel()!=null) redstoneChips.removeReceiver(this);
    }

    @Override
    public int getChannelLength() {
		if (mode) {
			return interfaceBlocks.length;
		} else {
			return interfaceBlocks.length + 1;
		}
    }
	
	protected abstract void updateOutputs();
}
