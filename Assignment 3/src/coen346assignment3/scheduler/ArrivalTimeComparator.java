package coen346assignment3.scheduler;

import coen346assignment3.process.Process;

import java.util.Comparator;

public class ArrivalTimeComparator implements Comparator<Process> {

    /**
     * Compares arrival times and puts earliest arrival time at head of queue.
     *
     * @param p1 Process 1
     * @param p2 Process 2
     * @return Process has higher or lower arrival time
     */
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
