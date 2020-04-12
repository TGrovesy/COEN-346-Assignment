package coen346assignment3.process;

import coen346assignment3.memory.MemoryManager;
import coen346assignment3.scheduler.Scheduler;

import java.util.concurrent.TimeUnit;

public class Process implements Runnable{

    private final long arrivalTime;
    private final long burstTime;
    private long remainingTime;
    private volatile Boolean hasCPU = false;
    private Boolean isFinished = false;
    private final int quantum;
    private Boolean hasRun = false;
    private final int processID;

    /**
     * Constructor.
     *
     * @param arrivalTime Process arrival time
     * @param burstTime Process burst (run) time
     * @param processID Process ID
     * @param quantum Process quantum (time slice)
     */
    public Process(int arrivalTime, int burstTime, int processID, int quantum) {
        this.arrivalTime = TimeUnit.MILLISECONDS.convert(arrivalTime, TimeUnit.SECONDS); // Convert to ms
        this.burstTime = TimeUnit.MILLISECONDS.convert(burstTime, TimeUnit.SECONDS); // Convert to ms
        this.remainingTime = this.burstTime;
        this.processID = processID;
        this.quantum = quantum;
    }

    /**
     * Running process.
     */
    @Override
    public void run() {
        while (!isFinished) { // When process is finished, thread will close
            while (!hasCPU) Thread.onSpinWait();
            long clock = Scheduler.getClock();
            if (!hasRun) {
                // Process starts up for first time
                hasRun = true;
                System.out.println("Clock: " + clock + ", Process " + (processID + 1) + ": Started");
            }
            System.out.println("Clock: " + clock + ", Process " + (processID + 1) + ": Resumed");
            long timeRan = Math.min(remainingTime, quantum); // Process will run for either the whole quantum or earlier if time remaining is less than the quantum
            remainingTime -= timeRan; // Time ran deducted from time remaining for the process to execute
            long remainingQuantum = timeRan;
            long commandTime = commandTime();
            long commandClock = clock;
            while (commandTime <= remainingQuantum) {
                remainingQuantum -= commandTime;
                commandClock += commandTime;
                runCommand(clock, commandClock);
                commandTime = commandTime();
            }
            clock += timeRan;
            System.out.println("Clock: " + clock + ", Process " + (processID + 1) + ": Paused");
            if (remainingTime <= 0) { // If the process is done
                isFinished = true; // Process signals to scheduler that it is done execution
                System.out.println("Clock: " + clock + ", Process " + (processID + 1) + ": Finished");
            }
            releaseCPU(); // CPU given back to the scheduler, process will pause
        }
    }

    /**
     * Runs the next command.
     * @param clock Clock
     * @param commandClock Clock when command execution done
     */
    public void runCommand(long clock, long commandClock) {
        if (!Scheduler.commands.isEmpty()) {
            String[] command = Scheduler.commands.element();
            Scheduler.commands.remove();
            switch (command[0]) {
                case "Store":
                    System.out.println("Clock: " + clock + ", Process " + (processID + 1) + ", STORE"); // Debug
                    //MemoryManager.memStore(command[1], Integer.parseInt(command[2]), commandClock, processID);
                    break;
                case "Release":
                    System.out.println("Clock: " + clock + ", Process " + (processID + 1) + ", RELEASE"); // Debug
                    //MemoryManager.memFree(command[1], commandClock, processID);
                    break;
                case "Lookup":
                    System.out.println("Clock: " + clock + ", Process " + (processID + 1) + ", LOOKUP"); // Debug
                    //MemoryManager.memLookup(command[1], commandClock, processID);
                    break;
                default:
                    System.out.println("Clock: " + clock + ", Process " + (processID + 1) + ", Incorrect Command Called");
                    break;
            }
        }
    }

    /**
     * Method for process to acquire CPU.
     */
    public void acquireCPU() {
        hasCPU = true;
    }

    /**
     * Method for process to release CPU to the scheduler.
     */
    public void releaseCPU() {
        hasCPU = false;
        Scheduler.cpuSem.release(1); // Process notifies scheduler to continue execution
    }

    /**
     * Returns random value between 400 and 600
     * @return command execution time
     */
    public long commandTime() {
        return (long) (Math.random() * (600 - 400)) + 400;
    }

    /**
     * Returns value indicating process has finished execution.
     * @return if process finished execution
     */
    public Boolean getFinished() {
        return isFinished;
    }

    /**
     * Returns process arrival time.
     * @return arrival time
     */
    public long getArrivalTime() {
        return arrivalTime;
    }

    /**
     * Returns process ID.
     * @return process ID
     */
    public int getProcessID() {
        return processID;
    }

    /**
     * Returns process remaining execution time.
     * @return remaining time of execution
     */
    public long getRemainingTime() {
        return remainingTime;
    }

    /**
     * Returns process burst time.
     * @return burst time
     */
    public long getBurstTime() {
        return burstTime;
    }

}
