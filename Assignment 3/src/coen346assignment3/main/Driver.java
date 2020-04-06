package coen346assignment3.main;

import coen346assignment3.process.Process;
import coen346assignment3.scheduler.Scheduler;

import java.io.*;
import java.util.ArrayList;

public class Driver {

	public static void main(String[] args) throws FileNotFoundException {

		// Output file created and System.out set to output to file
		PrintStream output = new PrintStream(new File("output.txt"));
		//System.setOut(output);
		int quantum = 1000;

		// Processes read from file, line from input file stored in array list
		ArrayList<String> inProcesses = readInputFile("processes.txt");
		System.out.println("------Debug Statements------");
		System.out.println(inProcesses); // Debug statement to check if file read properly

		// Number of processes defined
		int processNum = inProcesses.size();

		// Process created and added to array
		Process[] processArray = createProcesses(processNum, inProcesses, quantum);

		// Debug print statement to check that processes created properly
		for (Process p: processArray) {
			System.out.println("Process: " + (p.getProcessID() + 1) + ", Arrival Time: " + p.getArrivalTime() + "ms, Burst Time: " + p.getBurstTime() + "ms");
		}

		// Read memory
		int memSize = readMemorySize("memconfig.txt");
		System.out.println("Memory Size: " + memSize); // Debug statement to check if memory size read properly
		System.out.println("Quantum: " + quantum + "ms"); // Debug statement to check if memory size read properly
		System.out.println("---End of Debug Statements---\n");

		// Processes added to scheduler
		Scheduler scheduler = new Scheduler(processArray, processNum, quantum);

		Thread schedulerThread = new Thread(scheduler);
		schedulerThread.start();
	}

	/**
	 * Reads processes file
	 * @param fileName File path
	 * @return array list of process info
	 */
	public static ArrayList<String> readInputFile(String fileName) {
		ArrayList<String> input = new ArrayList<>();
		try {
			InputStream is = new FileInputStream(fileName); // Input stream from file opened
			BufferedReader br = new BufferedReader(new InputStreamReader(is)); // Line stored in buffered reader
			String readLine; // String storing the line read
			while ((readLine = br.readLine()) != null) {
				input.add(readLine); // Line read added to array list of all processes (lines read)
			}
			br.close();
		} catch (IOException e) { // If file fails to be read, exception generated and error message shown
			e.printStackTrace();
			System.err.println("Error reading file");
		}
		return input;
	}

	/**
	 * Reads the memory size
	 *
	 * @param fileName File path
	 * @return memory size (frames)
	 */
	public static int readMemorySize(String fileName) {
		int frames = 0;
		try {
			InputStream is = new FileInputStream(fileName); // Input stream from file opened
			BufferedReader br = new BufferedReader(new InputStreamReader(is)); // Line stored in buffered reader
			frames = Integer.parseInt(br.readLine());
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error reading file");
		}
		return frames;
	}

	/**
	 * Creates array of processes
	 *
	 * @param processNum Number of processes
	 * @param newProcesses Array of process start and burst time
	 * @param quantum Quantum time
	 * @return array of processes
	 */
	public static Process[] createProcesses(int processNum, ArrayList<String> newProcesses, int quantum) {
		// Array of processes created
		Process[] processes = new Process[processNum];

		// Arrival time and burst time separated and used to create process objects
		for (int i = 0; i < processNum; i++) {
			String[] split = newProcesses.get(i).split(" ");
			int arrivalTime = Integer.parseInt(split[0]);
			int burstTime = Integer.parseInt(split[1]);
			Process p = new Process(arrivalTime, burstTime, i, quantum); // New process created
			processes[i] = p; // New process added to processes array
		}
		return processes;
	}
}
