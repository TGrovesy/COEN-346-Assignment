package coen346assignment3.main;

public class Process implements Runnable{
    /* Variables for process*/
    private int arrivalTime;
    private int burstTime;
    private float remainingTime;
    private float time;
    private volatile Boolean hasCPU = false;
    private Boolean isFinished = false;
    private float quantum = 3000; // Can be constant for this assignment
    private float runTime = 0;
    private float timeToRun;
    private Boolean hasRun = false;
    private int processID;

    /* Constructor */
    Process(int arrivalTime, int burstTime, int processID) {
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
        this.processID = processID;
    }

    @Override
    public void run() {
        while (true) {
            while (!hasCPU || isFinished) Thread.onSpinWait(); // If thread starts but process does not have CPU, wait until given CPU
            timeToRun = Math.min(remainingTime, quantum); // Process will run for either the whole quantum or earlier if time remaining is less than the quantum
            time += timeToRun; // Time increments
            remainingTime -= timeToRun; // Time ran deducted from time remaining for the process to execute
            runTime += timeToRun; // Time to run (time process ran in quantum) added to total time ran

            hasCPU = false; // Process relinquishes CPU back to the scheduler

            if (remainingTime <= 0) { // If the process is done
                isFinished = true; // Process signals to scheduler that it is done execution
            }
        }
    }

    /* Getters */
    public Boolean getFinished() {
        return isFinished;
    }
    public int getArrivalTime() {
        return arrivalTime;
    }
    public float getTimeToRun() {
        return timeToRun;
    }
    public Boolean getHasCPU() {
        return hasCPU;
    }
    public Boolean getHasRun() {
        return hasRun;
    }
    public int getProcessID() {
        return processID;
    }
    public float getRemainingTime() {
        return remainingTime;
    }
    public int getBurstTime() {
        return burstTime;
    }

    /* Setters */
    public void setHasCPU(Boolean hasCPU) {
        this.hasCPU = hasCPU;
    }
    public void setTime(float time) {
        this.time = time;
    }
    public void setHasRun(Boolean hasRun) {
        this.hasRun = hasRun;
    }
}
