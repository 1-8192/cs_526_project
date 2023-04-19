import net.datastructures.Entry;
import net.datastructures.HeapAdaptablePriorityQueue;
import net.datastructures.LinkedQueue;

import java.io.*;
import java.util.Iterator;
import java.util.Scanner;

import static constants.SchedulerConstants.*;

public class Scheduler {

    private HeapAdaptablePriorityQueue<Integer, Process> schedulerQueue;
    private HeapAdaptablePriorityQueue<Integer, Process> processQueue;
    private LinkedQueue<Process> finishedProcessQueue;

    public Scheduler() {
        this.schedulerQueue = new HeapAdaptablePriorityQueue<>();
        this.processQueue = new HeapAdaptablePriorityQueue<>();
        this.finishedProcessQueue = new LinkedQueue<>();
    }

    public void runSimulation() throws IOException {
        // Initializing variables for the simulation
        Process runningProcess = null;
        int time = 0;
        File file = new File(System.getProperty(DIR) + READ_FILE_PATH);
        Scanner reader = new Scanner(file);
        FileWriter writer = new FileWriter(System.getProperty(DIR) + WRITE_FILE_PATH, true);
        String message;

        // Loading processes from input file. Then closing reader since we are done with it.
        loadProcessesFromInput(reader, writer);
        reader.close();

        // If the input file was empty, we exit the program.
        if (processQueue.isEmpty()) {
            System.out.println("No processes found in input file. Quitting Scheduler");
            writer.write("No processes found in input file. Quitting Scheduler");
            System.exit(0);
        }

        // Main while loop for simulation
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
                    message = "Now running Process id = " + runningProcess.getId() + "\n" +
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
                                "\n" + "Finished running Process id = " + runningProcess.getId() + "\n" +
                                " Arrival = " + runningProcess.getArrivalTime() + "\n" +
                                "Duration = " + runningProcess.getDuration() + "\n" +
                                "Run time left = " + runningProcess.getRunTimeLeft() + "\n" +
                                "at time " + time + "\n";
                        System.out.print(message);
                        writer.write(message);
                        finishedProcessQueue.enqueue(runningProcess);
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
                message = "Now running Process id = " + runningProcess.getId() + "\n" +
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

            // The instructions mention using another data structure to contain changing processes given the possible
            // priority change. I opted to just iterate over the adaptable queue to update wait time, and priority if
            // necessary, to save on space complexity for the new data structure. The priority/key change was not
            // problematic in testing. Since the priority would only lower, I don't think there's any danger of hitting
            // the "same" entry twice with the iterator after a priority shift.
            if (!schedulerQueue.isEmpty()) {
                Iterator itr = schedulerQueue.iterator();
                Entry entry;
                Process currProcess;
                int wait;
                while (itr.hasNext()) {
                    entry = (Entry) itr.next();
                    currProcess = (Process) entry.getValue();
                    wait = (time - currProcess.getArrivalTime() + 1) - (currProcess.getDuration() - currProcess.getRunTimeLeft());
                    currProcess.setWaitTime(wait);
                    if (currProcess.getWaitTime() != 0 && currProcess.getWaitTime() % MAX_WAIT_TIME == 0) {
                        currProcess.setPriority(currProcess.getPriority() - 1);
                        message = "Process " + currProcess.getId() + " reached maximum wait time... decreasing priority to "
                                + currProcess.getPriority() + "\n";
                        System.out.print(message);
                        writer.write(message);
                        // Replacing value before we replace the key
                        schedulerQueue.replaceValue(entry, currProcess);
                        schedulerQueue.replaceKey(entry, currProcess.getPriority());
                    } else {
                        schedulerQueue.replaceValue(entry, currProcess);
                    }
                }
            }
            time ++;
        }
        // After main simulation loop finishes, we need to calculate the avergae wait time
        double average = calculateAverageWaitTime(finishedProcessQueue);
        message = "Average wait time: " + average + "\n";
        System.out.print(message);
        writer.write(message);
        // Finally closing the writer
        writer.close();
    }

    /**
     * Loads Processes from an input script and inserts them in a heap adaptable queue.
     *
     * @param reader file Scanner
     * @param writer file Writer
     *
     * @throws IOException
     */
    private void loadProcessesFromInput(Scanner reader, Writer writer) throws IOException {

        // Reading the processes from the input file and storing them in an adaptable
        // priority queue, keyed on arrival time.
        System.out.println("Building Process Queue...");
        writer.write("Building Process Queue...");
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
            writer.write(process.toString());
            processQueue.insert(process.getArrivalTime(), process);
        }
    }

    /**
     * Calucaltes the average wait time for all processes in a queue.
     *
     * @param processQueue the process queue we need the average for.
     *
     * @return the average wait time of processes in the queue
     */
    private double calculateAverageWaitTime(LinkedQueue<Process> processQueue) {
        double waitTime = 0;
        double totalProcesses = processQueue.size();
        Process test;
        while (!processQueue.isEmpty()) {
            test = processQueue.dequeue();
            waitTime += test.getWaitTime();
        }
        return waitTime / totalProcesses;
    }
}
