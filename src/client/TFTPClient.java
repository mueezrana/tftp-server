package client;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

import helpers.Keyboard;
import networking.ClientNetworking;
import resource.*;
import testbed.ErrorChecker;
import testbed.TFTPErrorMessage;
import types.*;

/**
 * @author Team 3
 *
 *         This class represents a TFTP console application for interfacing with
 */
public class TFTPClient {

	private boolean isClientAlive = true;
	private final String CLASS_TAG = "<TFTP Client>";
	private int mPortToSendTo;
	private InetAddress mAddressToSendTo;
	private int mode;

	// by default the logger is set to VERBOSE level
	private Logger logger;

	// Error checker
	ErrorChecker errorChecker = null;

	public static void main(String[] args) {
		TFTPClient vClient = new TFTPClient();
		vClient.initialize();
	}

	private Logger getVerbosity() {
		int v;
		do {
			System.out.println("Logging should be (1) silent or (2) verbose?");
			v = Keyboard.getInteger();
		} while (v != 1 && v != 2);
		
		return (v == 2 ? Logger.SILENT : Logger.VERBOSE);
	}
	
	private String getClientFilePath() {
		String path = null;
		do {
			System.out.print("Enter a directory to use as your default directory to write from: \n");
			path = Keyboard.getString();
		} while (!(new File(path).isDirectory()));
		
		if (!path.endsWith("/"))
			path += "/";
		
		return path;
	}
	
	/**
	 * This function initializes the client's functionality and block the rest
	 * of the program from running until a exit command was given.
	 */
	public void initialize() {
		setLogLevel();
		logger.setClassTag(this.CLASS_TAG);
		Scanner scan = new Scanner(System.in);
		ClientNetworking net = null;
		
		
		try {
			mode = getSendPort();
			try {
				if (mode == 1) {
					this.mPortToSendTo = Configurations.SERVER_LISTEN_PORT;
					while(true) {
						System.out.println("Enter valid host ip:"); 
						try {
							String ip = Keyboard.getString();
							this.mAddressToSendTo = InetAddress.getByName(ip);
							break;
						} catch (UnknownHostException e) {
							System.out.println("Not a valid host, try again.");
						}
					}
				} else {
					this.mPortToSendTo = Configurations.ERROR_SIM_LISTEN_PORT;
					this.mAddressToSendTo = InetAddress.getLocalHost();
				}
			} catch (UnknownHostException e) {
				System.err.println("Could not find host at the address you entered.");
			}

			int optionSelected = 0;

			while (isClientAlive) {
				System.out.println(UIStrings.MENU_CLIENT_SELECTION);

				try {
					optionSelected = Keyboard.getInteger();
				} catch (NumberFormatException e) {
					optionSelected = 0;
				}
				errorChecker = null;
				switch (optionSelected) {
				case 1:
					// Read file
					net = new ClientNetworking();
					String readFileName;
					while (true) {
						logger.print(logger, Strings.PROMPT_ENTER_FILE_NAME);
						readFileName = Keyboard.getString();
						if (ErrorChecker.isValidFilename(readFileName)) {
							break;
						}
						System.out.println("Invalid entry. Try again.\n");
					}

					try {
						TFTPErrorMessage result;
						do {
							result = net.generateInitRRQ(readFileName, this.mPortToSendTo, this.mAddressToSendTo, this.logger);
							if (result.getType() != ErrorType.NO_ERROR)
								break;
							if (result.getType() == ErrorType.NOT_DEFINED)
								break;
							result = net.receiveFile();
						} while (result == null);
						if (result.getType() == ErrorType.NO_ERROR || result.getType() == ErrorType.NOT_DEFINED) {
							logger.print(Logger.VERBOSE, Strings.TRANSFER_SUCCESSFUL);
						} else {
							logger.print(Logger.ERROR, Strings.TRANSFER_FAILED);
							logger.print(Logger.ERROR, result.getString());
						}
					} catch (Exception e) {
						if (logger == Logger.VERBOSE)
							e.printStackTrace();

						logger.print(Logger.ERROR, Strings.TRANSFER_FAILED);
					}
					break;
				case 2:
					// Write file
					String clientFilePath = getClientFilePath();
					net = new ClientNetworking();

					logger.print(logger, Strings.PROMPT_FILE_NAME_PATH);
					String writeFileNameOrFilePath = Keyboard.getString();

					if (!writeFileNameOrFilePath.contains("/")) { // We were only given a filename.
						//System.out.println("Given only filename.");
						writeFileNameOrFilePath = clientFilePath + writeFileNameOrFilePath;
						//System.out.println(writeFileNameOrFilePath);
					}
					
					TFTPErrorMessage result = null;
					File f = new File(writeFileNameOrFilePath);
					
					if (!f.exists() || f.isDirectory()) {
						logger.print(Logger.ERROR, Strings.FILE_NOT_EXIST + ": " + writeFileNameOrFilePath);
						break;
					}
					
					try {
						result = net.generateInitWRQ(writeFileNameOrFilePath, this.mPortToSendTo, this.mAddressToSendTo, this.logger);
					
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (result == null)
						break;
					if ((result.getType() == ErrorType.NO_ERROR)
							|| (result.getType() == ErrorType.SORCERERS_APPRENTICE)) {
						result = net.sendFile();
						if (result.getType() == ErrorType.NO_ERROR) {
							logger.print(Logger.VERBOSE, Strings.TRANSFER_SUCCESSFUL);
						} else {
							logger.print(Logger.ERROR, result.getString());
						}
					} else {
						logger.print(Logger.ERROR, Strings.TRANSFER_FAILED);
						logger.print(Logger.ERROR, result.getString());

					}
					break;
				case 3:
					// shutdown client
					isClientAlive = !isClientAlive;
					logger.print(Logger.VERBOSE, Strings.EXIT_BYE);
					break;

				default:
					logger.print(Logger.ERROR, Strings.ERROR_INPUT);
					break;
				}
			}
		} finally {
			scan.close();
		}
	}

	/**
	 * This function sets the client to use the error simulator or not
	 * 
	 * @return mode 1 is do not use and 2 is use.
	 */
	private int getSendPort() {
		while (true) {
			System.out.println(UIStrings.CLIENT_MODE);

			int mode = Keyboard.getInteger();

			if (mode == 1) {
				return mode;
			} else if (mode == 2) {
				return mode;
			} else {
				logger.print(Logger.ERROR, Strings.ERROR_INPUT);
			}

		}
	}
	
	/**
	 * This function only prints the client side selection menu
	 */
	private void setLogLevel() {

		int optionSelected;

		while (true) {
			System.out.println(UIStrings.CLIENT_LOG_LEVEL_SELECTION);

			try {
				optionSelected = Keyboard.getInteger();
			} catch (NumberFormatException e) {
				optionSelected = 0;
			}

			if (optionSelected == 1) {
				this.logger = Logger.VERBOSE;
				break;
			} else if (optionSelected == 2) {
				this.logger = Logger.SILENT;
				break;
			} else {
				Logger temp = Logger.ERROR;
				temp.print(Logger.ERROR, Strings.ERROR_INPUT);
			}
		}
	}
}
