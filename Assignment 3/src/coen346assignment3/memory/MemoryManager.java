package coen346assignment3.memory;

import java.util.ArrayList;

public class MemoryManager {

	// Main Memory and Virtual Memory
	private ArrayList<Page> virtualMemory = new ArrayList<Page>();
	private Frame[] mainMemory;

	private int mainMemorySize;

	private int mainMemoryPointer = 0;

	public MemoryManager(int memorySize) {
		this.mainMemorySize = memorySize;
		this.mainMemory = new Frame[memorySize];
	}

	/**
	 * Stores Given variable ID and its value to first unassigned spot in main
	 * memory
	 * 
	 * @param variableID
	 * @param value
	 */
	public void memStore(String variableID, int value) {

	}

	/**
	 * Frees up memory location containing this ID
	 * 
	 * @param variableID
	 */
	public void memFree(String variableID) {

	}

	/**
	 * Checks to see if this variable ID exists in memory
	 * 
	 * @param variableID
	 * @return does this ID Exist
	 */
	public boolean memLookup(String variableID) {
		return false;
	}

	/**
	 * Store page in our virtual memory
	 * 
	 * @param page
	 */
	public void StorePage(Page page) {

	}
	
	//Write to VM.txt
	private void WriteToVM(int index, Page page) {
		
	}

	/**
	 * Store frame in our main memory
	 * 
	 * @param frame
	 */
	public boolean StoreFrame(Frame frame) {
		if (!(mainMemoryPointer >= mainMemorySize)) {
			mainMemory[mainMemoryPointer++] = frame;// stores in our main memory array
			return true;//success
		}else {
			//our main memory is full
			System.out.println("Failed! Sorry Main Memory Is Full!");
			return false;//fail
		}
	}

}
