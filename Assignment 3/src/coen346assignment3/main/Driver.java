package coen346assignment3.main;

import java.io.*;
import java.util.ArrayList;

public class Driver {

	public static void main(String[] args) throws FileNotFoundException {

		// Processes read from file, line from input file stored in array list
		ArrayList<String> inProcesses = readInput("processes.txt");
		System.out.println(inProcesses); // Debug statement to check if file read properly

		// Number of processes defined
		int processNum = inProcesses.size();
		Process[] processArray = createProcesses(processNum, inProcesses);

		// Output file created and System.out set to output to file
		/*PrintStream output = new PrintStream(new File("output.txt"));
		System.setOut(output);*/

	}


	/* Method to read input file line by line */
	public static ArrayList<String> readInput(String fileName) {
		ArrayList<String> inLine = new ArrayList<>();
		try {
			InputStream is = new FileInputStream(fileName); // Input stream from file opened
			BufferedReader br = new BufferedReader(new InputStreamReader(is)); // Line stored in buffered reader
			String readLine; // String storing the line read
			while ((readLine = br.readLine()) != null) {
				inLine.add(readLine); // Line read added to array list of all processes (lines read)
			}
			br.close();
		} catch (Exception e) { // If file fails to be read, exception generated and error message shown
			e.printStackTrace();
			System.err.println("Error reading file");
		}
		return inLine;
	}

	/* Method to create process objects from info from file and return array of processes */
	public static Process[] createProcesses(int processNum, ArrayList<String> newProcesses) {
		// Array of processes created
		Process[] processes = new Process[processNum];

		// Arrival time and burst time separated and used to create process objects
		for (int i = 0; i < processNum; i++) {
			String[] split = newProcesses.get(i).split(" ");
			int arrivalTime = Integer.parseInt(split[0]);
			int burstTime = Integer.parseInt(split[1]);
			Process p = new Process(arrivalTime, burstTime, i); // New process created
			processes[i] = p; // New process added to processes array
		}
		return processes;
	}
}
