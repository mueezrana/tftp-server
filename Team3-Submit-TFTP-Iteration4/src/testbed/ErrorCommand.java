package testbed;

import java.util.HashMap;
import resource.Configurations;
import types.ErrorType;
import types.RequestType;

/**
 * @author Ziqiao Charlie Li
 * 
 * This class represents the type of error that the simulator should generate.
 * This class is used to encapsulate the settings for error simulator
 *
 */
public class ErrorCommand {
	
	private HashMap<String, Integer> mCommandMap;	
	
	/**
	 * Creates a new map of settings
	 */
	public ErrorCommand() {
		this.mCommandMap = new HashMap<>();
	}
	
	/**
	 * Sets the main error code 
	 * 	 1.Illegal TFTP operation 
	 *   2.Unknown transfer ID 
	 *   3.No errors please 
	 *   4.Transmission Error 
	 *   5.Exit 
	 * @param value - user input
	 */
	public void setMainErrorFamily(int value) {
		this.mCommandMap.put(Configurations.MAIN_ERROR, value);
	}
	
	/**
	 * Returns main error code
	 * 
	 * @return int user selection
	 */
	public ErrorType getMainErrorFamily() { 
		return ErrorType.matchErrorByNumber(this.mCommandMap.get(Configurations.MAIN_ERROR));
	}
	
	/**
	 * Gets the sub error selection for each main category if exists
	 * 
	 * @param value - int user input
	 */
	public void setSubErrorFromFamily(int value) {
		this.mCommandMap.put(Configurations.SUB_ERROR, value);
	}
	
	/**
	 * Returns the sub error family
	 * 
	 * @return int
	 */
	public int getSubErrorFromFamily() {
		return this.mCommandMap.get(Configurations.SUB_ERROR);
	}
	
	/**
	 * Sets the transmission error occurrences through a file transmission.
	 * When the transmission has exhausted the value set, no transmission errors
	 * will occur there on after.
	 * 
	 * @param value - number of transmission errors to spawn
	 */
	public void setTransmissionErrorOccurrences(int value) {
		this.mCommandMap.put(Configurations.TE_NUM_PACKETS, value);
	}
	
	/**
	 * Returns the number of occurences
	 * 
	 * @return int
	 */
	public int getTransmissionErrorOccurences() {
		return this.mCommandMap.get(Configurations.TE_NUM_PACKETS);
	}
	
	/**
	 * Sets the frequency of creating transmission errors where the value
	 * 1 represents the amount of good packets to send before an error 
	 * transmission packet is sent
	 * 
	 * @param value - frequency of errors of spawn
	 */
	public void setTransmissionErrorFrequency(int value) {
		this.mCommandMap.put(Configurations.TE_FREQ_ERROR, value);
	}
	
	/**
	 * Sets the frequncy of error, could mean delay time in our case or what ever
	 * the user chooses to set it to
	 * 
	 * @return int
	 */
	public int getTransmissionErrorFrequency() {
		return this.mCommandMap.get(Configurations.TE_FREQ_ERROR);
	}
	
	/**
	 * Gets the frequency key
	 * 
	 * @param value int
	 */
	public void setTransmissionErrorType(int value) {
		this.mCommandMap.put(Configurations.TE_TYPE_ERROR, value);
	}
	
	/**
	 * Gets the request type of the error setting
	 * 
	 * @return RequestType
	 */
	public RequestType getTransmissionErrorType() {
		return RequestType.matchRequestByNumber(this.mCommandMap.get(Configurations.TE_TYPE_ERROR));
	}
}
