package coen346assignment3.scheduler;

import coen346assignment3.process.Process;

import java.util.*;
import java.util.concurrent.Semaphore;

public class Scheduler implements Runnable {

    private final int numProcesses;
    private static long clock = 0;
    private PriorityQueue<Process> arrivalQueue;
    private PriorityQueue<Process> readyQueue;
    private Boolean[] processFinished;
    public static Semaphore cpuSem;
    private final long quantum;
    private static Queue<String> commands;


    /**
     * Constructor.
     *
     * @param processes Array of processes
     * @param numProcesses Number of processes
     * @param quantum Process quantum (time slice)
     */
    public Scheduler(Process[] processes, int numProcesses, long quantum, Queue<String> commands) {
        cpuSem = new Semaphore(2);
        this.numProcesses = numProcesses;
        this.quantum = quantum;
        Scheduler.commands = commands;

        // Create ready queue
        Comparator<Process> remainingTimeCompare = new RemainingTimeComparator();
        readyQueue = new PriorityQueue<>(numProcesses, remainingTimeCompare);

        // Create arrival queue
        Comparator<Process> arrivalTimeCompare = new ArrivalTimeComparator();
        arrivalQueue = new PriorityQueue<>(numProcesses, arrivalTimeCompare);

        // Create arrays for wait times and finished status (initialize to false)
        processFinished = new Boolean[numProcesses];
        Arrays.fill(processFinished, false);

        // Create threads
        Thread[] pThread = new Thread[numProcesses];
        for (int i = 0; i < processes.length; i++) {
            pThread[i] = new Thread(processes[i]);
        }

        // Start all threads
        for (Thread thread : pThread) {
            thread.start();
        }

        addToArrivalQueue(processes); // All processes added to arrival queue
    }

    /**
     * Scheduler code to select and run processes that runs until finished.
     */
    @Override
    public void run() {
        while (!isDone()) {
            if (readyQueue.isEmpty() && !arrivalQueue.isEmpty()) { // If all arrived processes have run but some processes have not arrived yet
                if (arrivalQueue.element().getArrivalTime() > clock)
                    clock = arrivalQueue.element().getArrivalTime(); // Current clock time set to next process' arrival time
            }
            if (!arrivalQueue.isEmpty()) { // If there are still processes that have arrived but not in ready queue
                addToReadyQueue(arrivalQueue); // Add to ready queue
            }
            if (readyQueue.size() > 1) { // Two processes to run (more than one process in ready queue)
                Process[] runProcesses = new Process[2];
                runProcesses[0] = readyQueue.element();
                readyQueue.remove();
                runProcesses[1] = readyQueue.element();
                readyQueue.remove();
                try {
                    runProcess(runProcesses); // Run processes
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else if (readyQueue.size() == 1) { // Only one process to run (in ready queue)
                try {
                    Process runProcess = readyQueue.element();
                    readyQueue.remove();
                     runProcess(runProcess); // Run processes
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Runs a single process.
     *
     * @param process Process to be run
     * @throws InterruptedException Exception from semaphore
     */
    public void runProcess(Process process) throws InterruptedException {
        // Process given CPU access, can now resume execution
        // System.out.println("1 process"); // Debug statement
        process.acquireCPU();

        // Scheduler waits until process indicates that it has paused
        Thread.sleep(100);
        cpuSem.acquire(1);
        clock += quantum; // Global time updated

        // If the process reports to the scheduler that it has finished its execution
        if (process.getFinished()) {
            processFinished[process.getProcessID()] = true; // Process marked as finished
        }
        else {
            readyQueue.add(process); // Otherwise added to ready queue again
        }
        // System.out.println("1 process end"); // Debug statement
    }

    /**
     * Runs two processes concurrently.
     *
     * @param process 2 processes to be run concurrently
     * @throws InterruptedException Exception from semaphore
     */
    public void runProcess(Process[] process) throws InterruptedException {
        // Process given CPU access, can now resume execution
        // System.out.println("2 processes"); // Debug statement
        process[0].acquireCPU();
        process[1].acquireCPU();

        // Scheduler waits until process indicates that it has paused
        Thread.sleep(100);
        cpuSem.acquire(2);
        clock += quantum; // Global time updated

        // If the process reports to the scheduler that it has finished its execution
        if (process[0].getFinished()) {
            processFinished[process[0].getProcessID()] = true; // Process marked as finished
        }
        else if (!process[0].getFinished()) {
            readyQueue.add(process[0]); // Otherwise added to ready queue again
        }
        if (process[1].getFinished()) {
            processFinished[process[1].getProcessID()] = true; // Process marked as finished
        }
        else if (!process[1].getFinished()) {
            readyQueue.add(process[1]); // Otherwise added to ready queue again
        }
        // System.out.println("2 processes end"); // Debug statement
    }

    /**
     * Adds all processes to arrival queue.
     *
     * @param processes Array of processes
     */
    public void addToArrivalQueue(Process[] processes) {
        arrivalQueue.addAll(Arrays.asList(processes).subList(0, numProcesses));
    }

    /**
     * Adds processes to ready queue once they arrive.
     *
     * @param arrivalQueue Arrival Queue
     */
    public void addToReadyQueue (PriorityQueue<Process> arrivalQueue) {
        // Current time is at or after the arrival time
        if (arrivalQueue.element().getArrivalTime() <= clock) {
            readyQueue.add(arrivalQueue.element()); // Added to ready queue
            arrivalQueue.remove(); // Removed from arrival queue
        }
        // Add second process to run concurrently if a second process has arrived
        if (!arrivalQueue.isEmpty()) {
            if (arrivalQueue.element().getArrivalTime() <= clock) {
                readyQueue.add(arrivalQueue.element()); // Added to ready queue
                arrivalQueue.remove(); // Removed from arrival queue
            }
        }
    }

    /**
     * Determines if all processes have completed execution.
     *
     * @return is execution done
     */
    public Boolean isDone() {
        boolean done = true;
        for (int i = 0; i < numProcesses; i++) {
            if (!processFinished[i]) {
                done = false;
                break;
            }
        }
        return done;
    }

    /**
     * Get clock time.
     *
     * @return current clock
     */
    public synchronized static long getClock() {
        return clock;
    }
}
