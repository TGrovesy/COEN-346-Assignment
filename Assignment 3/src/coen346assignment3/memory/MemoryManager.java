package coen346assignment3.memory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class MemoryManager {

	// Main Memory and Virtual Memory
	private static ArrayList<Page> virtualMemory = new ArrayList<Page>();
	private static int virtualMemoryTailPointer = 0;
	private static Frame[] mainMemory;

	private static int mainMemorySize;
	private static int mainMemoryPointer = 0;
	private static int usedMainMemory = 0;

	public MemoryManager(int memorySize) {
		mainMemorySize = memorySize;
		mainMemory = new Frame[memorySize];
	}

	/**
	 * Stores Given variable ID and its value to first unassigned spot in main
	 * memory
	 * 
	 * @param variableID
	 * @param value
	 */
	public static void memStore(String variableID, int value) {
		if (usedMainMemory < mainMemorySize) {// Main memory is not full store as frame
			for (int i = 0; i < mainMemory.length; i++) {// find free place to store
				if (mainMemory[i] == null || mainMemory[i].GetVariableID().equals(variableID)) {// space free
					mainMemory[i] = new Frame(variableID, value);
					usedMainMemory++;// Increase amount of memory used
					System.out.println("Wrote to Main Memory"); // TODO remove debug print
					return;// stored successfuly so exit
				}
			}
			// space wasnt free (should never reach this point)
			// TODO Deal with exception
		} else {// Main Memory Full Store as page
			Page pageToStore = new Page(variableID, value);
			StorePage(pageToStore);
		}
	}

	/**
	 * Frees up memory location containing this ID
	 * 
	 * @param variableID
	 */
	public static void memFree(String variableID) {
		// check the main memory first
		for (int i = 0; i < mainMemory.length; i++) {
			if (mainMemory[i] != null) {//ensures memory location has something
				if (mainMemory[i].GetVariableID().equals(variableID)) {// found
					mainMemory[i] = null; // delete what is at memory location
					System.out.println("Deleted Memory in Main Memory"); // TODO remove debug print
					return;// been removed no need to access virtual memory
				}
			}
		}
		try {

			String newVm = "";
			Scanner vmScan = new Scanner(new File("vm.txt"));
			while (vmScan.hasNext()) {
				String line = vmScan.next();
				String[] lineSplit = line.split(",");
				if (lineSplit[1].equals(variableID)) {
					newVm += lineSplit[0] + ",null,null\n";// adds in line at correct location
					continue;
				}
				newVm += line + "\n"; // copies in old line
			}
			vmScan.close();// close the file

			// write vm
			PrintWriter vmWriter = new PrintWriter(new File("vm.txt"));
			vmWriter.write(newVm);
			vmWriter.close();// ensure the file is closed
			System.out.println("Deleted Memory in VM"); // TODO remove debug print
		} catch (FileNotFoundException e) {
			System.out.println("Virtual Memory Not Found!");
		}
	}

	/**
	 * Checks to see if this variable ID exists in memory
	 * 
	 * @param variableID
	 * @return does this ID Exist
	 */
	public static boolean memLookup(String variableID) {
		// check main memory first
		boolean freeSpot = false;
		int lastAccessedIndex = 0;

		for (int i = 0; i < mainMemory.length; i++) {// ensures our last accessed index doesn point to null if the
														// memory location was freed up
			if (mainMemory[i] != null) {
				lastAccessedIndex = i;
				break;
			}
		}

		for (int i = 0; i < mainMemory.length; i++) {

			if (mainMemory[i] != null) {// prevents null comparison if memory location was freed
				if (mainMemory[i].lastAccess.getTime() <= mainMemory[lastAccessedIndex].lastAccess.getTime()) {// if the
																												// time
																												// is
																												// less
																												// then
																												// it is
																												// older
																												// because
																												// of
																												// how
																												// system
																												// time
																												// works
					// new older index
					lastAccessedIndex = i;
				}
				if (mainMemory[i].GetVariableID().equals(variableID)) {
					return true;
				}
			} else {
				// free spot, TODO read and swap
				freeSpot = true;
			}
		}

		try {// check virtual memory
			Scanner vmScan = new Scanner(new File("vm.txt"));
			while (vmScan.hasNext()) {
				String line = vmScan.next();
				String[] lineSplit = line.split(",");
				if (lineSplit[1].equals(variableID)) {
					// TODO Memory Swap
					memFree(variableID);// removes current virtua storage of variable which will be in virtual memory
					System.out.println("SWAP: Variable " + mainMemory[lastAccessedIndex].GetVariableID()
							+ " with Variable " + lineSplit[1]);
					mainMemory[lastAccessedIndex] = new Frame(lineSplit[1], Integer.parseInt(lineSplit[2]));

					vmScan.close();
					return true;
				}
			}
			vmScan.close();

		} catch (FileNotFoundException e) {
			System.out.println("Could not find in virtul memory!");
		}

		return false;
	}

	/**
	 * Store page in our virtual memory Pages stored as CSV and each page has its
	 * own line index, variableID, value
	 * 
	 * @param page
	 */
	public static void StorePage(Page page) {
		virtualMemory.add(page);
		String memoryLocValue = virtualMemoryTailPointer + "," + page.GetVariableID() + "," + page.GetValue();
		StoreToVM(memoryLocValue);
		virtualMemoryTailPointer++;// increase tail pointer
	}

	// Write to VM.txt
	/**
	 * Writes our desired value to store to our virtual memory
	 * 
	 * @param memoryLocValue
	 */
	private static void StoreToVM(String memoryLocValue) {
		try {
			String newVm = "";
			boolean memoryAdded = false;
			Scanner vmScan = new Scanner(new File("vm.txt"));
			String[] memoryLocDelimeted = memoryLocValue.split(",");
			while (vmScan.hasNext()) {
				String line = vmScan.next();
				String[] lineSplit = line.split(",");
				if (!memoryAdded) {
					if (lineSplit[0].equals(memoryLocDelimeted[0]) || lineSplit[1].equals(memoryLocDelimeted[1])) {
						newVm += memoryLocValue + "\n";// adds in line at correct location
						memoryAdded = true;
						continue;
					}
				}
				newVm += line + "\n"; // copies in old line
			}
			vmScan.close();// close the file
			if (!memoryAdded) {// if our new memory hasnt yet been added store it
				newVm += memoryLocValue + "\n";
			}
			PrintWriter vmWriter = new PrintWriter(new File("vm.txt"));
			vmWriter.write(newVm);
			vmWriter.close();// ensure the file is closed
			System.out.println("Wrote to VM"); // TODO remove debug print
		} catch (FileNotFoundException e) {
			System.out.println("Virtual Memory Not Found!");
		}
	}

	/**
	 * Store frame in our main memory
	 * 
	 * @param frame
	 */
	public static boolean StoreFrame(Frame frame) {
		if (!(mainMemoryPointer >= mainMemorySize)) {
			mainMemory[mainMemoryPointer++] = frame;// stores in our main memory array
			return true;// success
		} else {
			// our main memory is full
			System.out.println("Failed! Sorry Main Memory Is Full!");
			return false;// fail
		}
	}

}
