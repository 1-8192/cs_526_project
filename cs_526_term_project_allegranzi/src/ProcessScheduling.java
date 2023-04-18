import net.datastructures.HeapAdaptablePriorityQueue;
import net.datastructures.LinkedQueue;

import java.io.*;
import java.util.Scanner;

public class ProcessScheduling {
    private static int MAX_WAIT_TIME = 30;
    private static String DIR = "user.dir";
    private static String READ_FILE_PATH = "/src/input/process_scheduling_input.txt";
    private static String WRITE_FILE_PATH = "/src/input/process_scheduling_output.txt";
    public static void main(String args[]) throws IOException {
        File file = new File(System.getProperty(DIR) + READ_FILE_PATH);
        Scanner reader = new Scanner(file);

        HeapAdaptablePriorityQueue<Integer, Process> schedulerQueue = new HeapAdaptablePriorityQueue<>();
        HeapAdaptablePriorityQueue<Integer, Process> processQueue = new HeapAdaptablePriorityQueue<>();
        LinkedQueue<Process> helperQueue = new LinkedQueue<>();

        // Reading the processes from the input file and storing them in an adaptable
        // priority queue, keyed on arrival time.
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
        FileWriter writer = new FileWriter(System.getProperty(DIR) + WRITE_FILE_PATH, true);
        String message;
        while (!processQueue.isEmpty() || !schedulerQueue.isEmpty() || runningProcess != null) {
            // The Process queue is already keyed to arrival time, but in case we have 2 processes with the same
            // arrival time, we are setting this while condition to keep adding processes to the scheduler queue
            // as long as the arrival time is less or equal to the current time step.
            while (!processQueue.isEmpty() && processQueue.min().getValue().getArrivalTime() <= time) {
                Process process = processQueue.removeMin().getValue();
                schedulerQueue.insert(process.getPriority(), process);
            }

            if (runningProcess != null) {
                if (!schedulerQueue.isEmpty() && schedulerQueue.min().getKey() < runningProcess.getPriority()) {
                    schedulerQueue.insert(runningProcess.getPriority(), runningProcess);
                    runningProcess = schedulerQueue.removeMin().getValue();
                    totalWaitTime += runningProcess.getWaitTime();
                    message = "Now running Process id = " + runningProcess.getId() +
                            " Arrival = " + runningProcess.getArrivalTime() + "\n" +
                            "Duration = " + runningProcess.getDuration() + "\n" +
                            "Run time left = " + runningProcess.getRunTimeLeft() + "\n" +
                            "at time " + time + "\n";
                    System.out.print(message);
                    writer.write(message);
                    runningProcess.setRunTimeLeft(runningProcess.getRunTimeLeft() - 1);
                    message = "Executed process ID: " + runningProcess.getId() +
                            " at time " + time + " Remaining: " + runningProcess.getRunTimeLeft() +
                            "\n";
                    System.out.print(message);
                    writer.write(message);
                } else {
                    int runTimeLeft = runningProcess.getRunTimeLeft();
                    if (runTimeLeft == 1) {
                        runningProcess.setRunTimeLeft(runningProcess.getRunTimeLeft() - 1);
                        message = "Executed process ID: " + runningProcess.getId() +
                                " at time " + time + " Remaining: " + runningProcess.getRunTimeLeft() +
                                "\n" + "Finished running Process id = " + runningProcess.getId() +
                                " Arrival = " + runningProcess.getArrivalTime() + "\n" +
                                "Duration = " + runningProcess.getDuration() + "\n" +
                                "Run time left = " + runningProcess.getRunTimeLeft() + "\n" +
                                "at time " + time + "\n";
                        System.out.print(message);
                        writer.write(message);
                        runningProcess = null;
                    } else if (runTimeLeft > 1) {
                        runningProcess.setRunTimeLeft(runningProcess.getRunTimeLeft() - 1);
                        message = "Executed process ID: " + runningProcess.getId() +
                                " at time " + time + " Remaining: " + runningProcess.getRunTimeLeft() +
                                "\n";
                        System.out.print(message);
                        writer.write(message);
                    }
                }
            } else if (runningProcess == null && !schedulerQueue.isEmpty()) {
                runningProcess = schedulerQueue.removeMin().getValue();
                totalWaitTime += runningProcess.getWaitTime();
                message = "Now running Process id = " + runningProcess.getId() +
                        " Arrival = " + runningProcess.getArrivalTime() + "\n" +
                        "Duration = " + runningProcess.getDuration() + "\n" +
                        "Run time left = " + runningProcess.getRunTimeLeft() + "\n" +
                        "at time " + time + "\n";
                System.out.print(message);
                writer.write(message);
                runningProcess.setRunTimeLeft(runningProcess.getRunTimeLeft() - 1);
                message = "Executed process ID: " + runningProcess.getId() +
                        " at time " + time + " Remaining: " + runningProcess.getRunTimeLeft() + "\n";
                System.out.print(message);
                writer.write(message);
            }

            while (!schedulerQueue.isEmpty()) {
                Process min = schedulerQueue.removeMin().getValue();
                int wait = (time - min.getArrivalTime()) - (min.getDuration() - min.getRunTimeLeft());
                min.setWaitTime(wait);
                if (wait % MAX_WAIT_TIME == 0 && wait != 0) {
                    min.setPriority(min.getPriority() - 1);
                    message = "Process " + min.getId() + " reached maximum wait time... decreasing priority to "
                            + min.getPriority() + "\n";
                    System.out.print(message);
                    writer.write(message);
                }
                helperQueue.enqueue(min);
            }

            while (!helperQueue.isEmpty()) {
                Process process = helperQueue.dequeue();
                schedulerQueue.insert(process.getPriority(), process);
            }
            time ++;
        }
        double average = (double)totalWaitTime / (double)totalProcesses;
        message = "Average wait time: " + average + "\n";
        System.out.print(message);
        writer.write(message);
        writer.close();
    }
}
