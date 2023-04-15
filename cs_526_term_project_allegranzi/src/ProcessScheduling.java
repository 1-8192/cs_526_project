import net.datastructures.HeapAdaptablePriorityQueue;
import net.datastructures.LinkedQueue;
import net.datastructures.PriorityQueue;
import net.datastructures.Queue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Scanner;

public class ProcessScheduling {
    static int MAX_WAIT_TIME = 30;
    public static void main(String args[]) throws FileNotFoundException {
        File file = new File(System.getProperty("user.dir") + "/src/input/process_scheduling_input.txt");
        Scanner reader = new Scanner(file);

        HeapAdaptablePriorityQueue<Integer, Process> schedulerQueue = new HeapAdaptablePriorityQueue<>();
        HeapAdaptablePriorityQueue<Integer, Process> processQueue = new HeapAdaptablePriorityQueue<>();
        LinkedQueue<Process> helperQueue = new LinkedQueue<>();

        // Reading the processes from the input file
        System.out.println("Building Process Queue...");
        while (reader.hasNextLine()) {
            String line = reader.nextLine().toString();
            String[] inputArray = line.split(" ");
            int[] inputArrayInt  = new int[4];
            int j = 0;
            for (int i = 0; i < inputArray.length; i++) {
                inputArrayInt[i] = Integer.valueOf(inputArray[i]);
            }
            Process process = new Process(inputArrayInt);
            System.out.println(process.toString());
            processQueue.insert(process.getArrivalTime(), process);
        }

        reader.close();
        int totalProcesses = processQueue.size();
        int totalWaitTime = 0;
        // Actual simulation starts here
        Process runningProcess = null;
        int time = 0;
        while (!processQueue.isEmpty() || !schedulerQueue.isEmpty()) {
            if (!processQueue.isEmpty()) {
                if (processQueue.min().getValue().getArrivalTime() <= time) {
                    Process process = processQueue.removeMin().getValue();
                    schedulerQueue.insert(process.getPriority(), process);
                }
            }
            if (!schedulerQueue.isEmpty()) {
                if (runningProcess == null) {
                    runningProcess = schedulerQueue.removeMin().getValue();
                    totalWaitTime += runningProcess.getWaitTime();
                    System.out.println("Executed process ID: " + runningProcess.getId() +
                            " at time " + time + " Remaining: " + runningProcess.getDuration());
                } else if (runningProcess.getDuration() == 1) {
                    System.out.println("Finished running Process id = " + runningProcess.getId() +
                            " Arrival = " + runningProcess.getArrivalTime() + "\n" +
                            "Duration = " + runningProcess.getDuration() + "\n" +
                            "Run time left = 0\n" +
                            "at time " + time);
                    runningProcess = null;
                } else if (runningProcess.getDuration() > 1) {
                    runningProcess.setDuration(runningProcess.getDuration() - 1);
                    System.out.println("Executed process ID: " + runningProcess.getId() +
                            " at time " + time + " Remaining: " + runningProcess.getDuration());
                }
                while (!schedulerQueue.isEmpty()) {
                    Process min = schedulerQueue.removeMin().getValue();
                    int wait = time - min.getArrivalTime();
                    min.setWaitTime(wait);
                    if (wait > MAX_WAIT_TIME) {
                        min.setPriority(min.getPriority() - 1);
                    }
                    helperQueue.enqueue(min);
                }

                while (!helperQueue.isEmpty()) {
                    Process process = helperQueue.dequeue();
                    schedulerQueue.insert(process.getPriority(), process);
                }

                if (!schedulerQueue.isEmpty() && runningProcess != null
                        && schedulerQueue.min().getKey() < runningProcess.getPriority()) {
                    schedulerQueue.insert(runningProcess.getPriority(), runningProcess);
                    runningProcess = null;
                }
            }
            time ++;
        }
        int average = totalWaitTime / totalProcesses;
        System.out.println("Average wait time: " + average);
    }
}
