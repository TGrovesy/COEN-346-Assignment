package coen346assignment3.main;

import java.sql.Time;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Scheduler implements Runnable {

    /* Variables for scheduler */
    private int numProcesses;
    private long time = 0;
    private long startTime = System.currentTimeMillis();
    private PriorityQueue<Process> arrivalQueue;
    private PriorityQueue<Process> readyQueue;
    private Boolean[] processFinished;
    public Semaphore p_sem;

    Scheduler(Process[] processes, int numProcesses) {
        p_sem = new Semaphore(1);
        this.numProcesses = numProcesses;

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

        for (Thread thread : pThread) {
            thread.start();
        }

        addToArrivalQueue(processes); // All processes added to arrival queue
    }

    @Override
    public void run() {
        // Run the processes until all are done
        while (!isDone()) {
            if (readyQueue.isEmpty() && !arrivalQueue.isEmpty()) { // If all arrived processes have run but some processes have not arrived yet
                if (arrivalQueue.element().getArrivalTime() > time)
                    time = arrivalQueue.element().getArrivalTime(); // Current time set to next process' arrival time
            }
            if (!arrivalQueue.isEmpty()) { // If there are still processes that have arrived but not in ready queue
                addToReadyQueue(arrivalQueue); // Add to ready queue
            }
            if (!readyQueue.isEmpty()) // Temporary fix
            {
                try {
                    runProcess(readyQueue.element()); // Run process with shorted remaining time
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /* Method that executes a process for its quantum*/
    void runProcess(Process process) throws InterruptedException {
        long schedulerTime = time;
        // If process has never started, start the process
        if (!process.getHasRun()) {
            // Process knows that it has run
            process.setHasRun(true);
            System.out.println("Clock: " + schedulerTime + ", Process " + (process.getProcessID() + 1) + ": Started");
        }

        // Process given CPU access, can now resume execution
        process.acquireCPU(this);
        System.out.println("Clock: " + schedulerTime + ", Process " + (process.getProcessID() + 1) + ": Resumed");

        // Scheduler waits until process indicates that it has paused
        //while (process.hasCPU) Thread.onSpinWait();
        p_sem.acquire();
        schedulerTime += process.getTimeRan();
        System.out.println("Clock: " + schedulerTime + ", Process " + (process.getProcessID() + 1) + ": Paused");

        // If the process reports to the scheduler that it has finished its execution
        if (process.getFinished()) {
            System.out.println("Clock: " + schedulerTime + ", Process " + (process.getProcessID() + 1) + ": Finished");
            processFinished[process.getProcessID()] = true; // Process marked as finished
            if (!readyQueue.isEmpty()) // Temporary fix
                readyQueue.remove(); // Process removed from ready queue
        }
        time += schedulerTime - time;
    }

    /* Method that adds all processes entering scheduler to the arrival queue */
    void addToArrivalQueue(Process[] processes) {
        arrivalQueue.addAll(Arrays.asList(processes).subList(0, numProcesses));
    }

    /* Method that adds processes to the ready queue once they have arrived */
    void addToReadyQueue (PriorityQueue<Process> arrivalQueue) {
        // Current time is at or after the arrival time
        if (arrivalQueue.element().getArrivalTime() <= time) {
            readyQueue.add(arrivalQueue.element()); // Added to ready queue
            arrivalQueue.remove(); // Removed from arrival queue
        }
    }

    /* Method that determines whether all processes have completed */
    Boolean isDone() {
        boolean done = true;
        for (int i = 0; i < numProcesses; i++) {
            if (!processFinished[i]) {
                done = false;
                break;
            }
        }
        return done;
    }

}

/* Comparator to implement ready queue (sort by time remaining) */
class RemainingTimeComparator implements Comparator<Process>{

    @Override
    public int compare(Process p1, Process p2) {
        // If selected process has less time left than the one being compared
        if (p1.getRemainingTime() < p2.getRemainingTime())
            return -1;
            // If selected process has more time left than the one being compared
        else if (p1.getRemainingTime() > p2.getRemainingTime())
            return 1;
        else // If remaining times are the same, prioritize process that has been in system longer
            return Long.compare(p1.getArrivalTime(), p2.getArrivalTime());
    }
}

/* Comparator to implement arrival queue (sort by arrival time) */
class ArrivalTimeComparator implements Comparator<Process>{

    @Override
    public int compare(Process p1, Process p2) {
        // If selected process has less time left than the one being compared
        if (p1.getArrivalTime() < p2.getArrivalTime())
            return -1;
            // If selected process has more time left than the one being compared
        else if (p1.getArrivalTime() > p2.getArrivalTime())
            return 1;
        else // If two processes have same arrival times, prioritize one with least remaining time (burst time)
            return Float.compare(p1.getRemainingTime(), p2.getRemainingTime());
    }
}