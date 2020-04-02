package coen346assignment3.main;

import java.util.concurrent.TimeUnit;

public class Process implements Runnable{
    /* Variables for process*/
    private long arrivalTime;
    private long burstTime;
    private long remainingTime;
    volatile Boolean hasCPU = false;
    private Boolean isFinished = false;
    private int quantum;
    private Boolean hasRun = false;
    private int processID;
    private long timeRan;

    /* Constructor */
    Process(int arrivalTime, int burstTime, int processID, int quantum) {
        this.arrivalTime = TimeUnit.MILLISECONDS.convert(arrivalTime, TimeUnit.SECONDS); // Convert to ms
        this.burstTime = TimeUnit.MILLISECONDS.convert(burstTime, TimeUnit.SECONDS); // Convert to ms
        this.remainingTime = this.burstTime;
        this.processID = processID;
        this.quantum = quantum;
    }

    @Override
    public void run() {
        while (!isFinished) {
            while (!hasCPU) Thread.onSpinWait();
            long time = Scheduler.getTime();
            if (!hasRun) {
                // Process knows that it has run
                hasRun = true;
                System.out.println("Clock: " + time + ", Process " + (processID + 1) + ": Started");
            }
            System.out.println("Clock: " + time + ", Process " + (processID + 1) + ": Resumed");
            timeRan = Math.min(remainingTime, quantum); // Process will run for either the whole quantum or earlier if time remaining is less than the quantum
            remainingTime -= timeRan; // Time ran deducted from time remaining for the process to execute
            time += timeRan;
            System.out.println("Clock: " + time + ", Process " + (processID + 1) + ": Paused");
            if (remainingTime <= 0) { // If the process is done
                isFinished = true; // Process signals to scheduler that it is done execution
                System.out.println("Clock: " + time + ", Process " + (processID + 1) + ": Finished");
            }
            releaseCPU();
        }
    }

    /* Method for process to acquire CPU */
    public void acquireCPU() {
        hasCPU = true;
    }

    /* Method for process to release CPU to the scheduler */
    public void releaseCPU() {
        hasCPU = false;
        Scheduler.cpuSem.release(1); // Process notifies scheduler to continue execution
    }

    /* Getters */
    public Boolean getFinished() {
        return isFinished;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }

    public int getProcessID() {
        return processID;
    }

    public long getRemainingTime() {
        return remainingTime;
    }

    public long getTimeRan() {
        return timeRan;
    }

    public long getBurstTime() {
        return burstTime;
    }

}
