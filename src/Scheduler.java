import net.datastructures.Entry;
import net.datastructures.HeapAdaptablePriorityQueue;
import net.datastructures.LinkedQueue;

import java.io.*;
import java.util.Iterator;
import java.util.Scanner;

import static constants.SchedulerConstants.*;

/**
 * Main class to run process scheduler simulation. The Scheduler uses a Heap Adaptable Priority Queue to manage
 * processes and execute them in order of priority.
 */
public class Scheduler {
    /**
     * Main heap adaptable priority queue used to store processes by order of priority in the scheduler.
     */
    private HeapAdaptablePriorityQueue<Integer, Process> schedulerQueue;

    /**
     * Heap Adaptable Priority Queue to store processes scanned from an input file.
     */
    private HeapAdaptablePriorityQueue<Integer, Process> inputProcessQueue;

    /**
     * Linked Queue to hold finished processes. Used to calculate total wait time once all
     * processes have finished running.
     */
    private LinkedQueue<Process> finishedProcessQueue;

    /**
     * Variable to hold currently running process.
     */
    private Process runningProcess;

    /**
     * Variable to hold current time step for simulation
     */
    private int time;

    /**
     * Message variable to printing to console and output file.
     */
    private String message;

    /**
     * Constructor takes no args. Instantiates the necessary class variables to run a
     * scheduler simulation.
     */
    public Scheduler() {
        this.schedulerQueue = new HeapAdaptablePriorityQueue<>();
        this.inputProcessQueue = new HeapAdaptablePriorityQueue<>();
        this.finishedProcessQueue = new LinkedQueue<>();
        this.runningProcess = null;
        this.time = 0;
    }

    /**
     * Method to run the simulation for the project.
     *
     * @throws IOException
     */
    public void runSimulation() throws IOException {
        // Initializing filer, scanner, and writers to set up reading the input processes and
        // writing to an output file.
        File file = new File(System.getProperty(DIR) + READ_FILE_PATH);
        Scanner reader = new Scanner(file);
        FileWriter writer = new FileWriter(System.getProperty(DIR) + WRITE_FILE_PATH, true);

        // Loading processes from input file. Then closing reader since we are done with it.
        loadProcessesFromInput(reader, writer);
        reader.close();

        // If the input file was empty, we exit the program.
        if (inputProcessQueue.isEmpty()) {
            message = "No processes found in input file. Quitting Scheduler";
            System.out.println(message);
            writer.write(message);
            writer.close();
            System.exit(0);
        }

        // Printing the Maximum Wait time
        message = "\n" + "Maximum Wait Time = " + MAX_WAIT_TIME;
        System.out.println(message);
        writer.write(message);

        // Main while loop for simulation. If all queues are empty, and there is no running Process, we are done.
        while (!inputProcessQueue.isEmpty() || !schedulerQueue.isEmpty() || runningProcess != null) {
            /**
             * Running a process for the time step
             */

            // The Process queue is already keyed to arrival time, but in case we have 2 processes with the same
            // arrival time, we are setting this while condition to keep adding processes to the scheduler queue
            // as long as the arrival time is less or equal to the current time step.
            while (!inputProcessQueue.isEmpty() && inputProcessQueue.min().getValue().getArrivalTime() <= time) {
                Process process = inputProcessQueue.removeMin().getValue();
                // Inserting process into the Schedule queue keyed to the process' priority.
                schedulerQueue.insert(process.getPriority(), process);
            }

            // Set of conditions to run in case we already have a process that is being run.
            if (runningProcess != null) {
                // If the minimum entry in the scheduler queue has a lower priority than the currently running
                // process, we move the process back into the queue, and start running the new one. Then print
                // the appropriate messages. The exception is if the current process is finishing in this time step.
                if (!schedulerQueue.isEmpty() && schedulerQueue.min().getKey() < runningProcess.getPriority() &&
                runningProcess.getRunTimeLeft() > 1) {
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
                // Else we keep running the current process without switching for one with a higher priority.
                } else {
                    int runTimeLeft = runningProcess.getRunTimeLeft();
                    // if the runTimeLeft variable is 1, we are done with running the process in this time step.
                    if (runTimeLeft <= 1) {
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
                        // Adding the finished process for the finished process queue, for calculating average wait
                        // time at the end of the simulation.
                        finishedProcessQueue.enqueue(runningProcess);
                        // clearing out the running process variable.
                        runningProcess = null;
                    // Else we keep running the process.
                    } else if (runTimeLeft > 1) {
                        runningProcess.setRunTimeLeft(runningProcess.getRunTimeLeft() - 1);
                        message = "Executed process ID: " + runningProcess.getId() +
                                " at time " + time + " Remaining: " + runningProcess.getRunTimeLeft() +
                                "\n";
                        System.out.print(message);
                        writer.write(message);
                    }
                }
            // If we are not running any process, and the scheduler queue is not empty, we remove the
            // first entry in the scheduler queue, which is the highest priority process.
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

            /**
             * Updating wait and priority times
             */
            // I opted to just iterate over the adaptable queue to update wait time, and priority if
            // necessary. I tested out another version using a linked queue to hold values, and then re-insert into
            // the scheduler queue, but there was no difference in result. I opted to save on the space complexity
            // and not use an additional data structure. Since the priority would only lower, I don't think there's
            // any danger of hitting the "same" entry twice with the iterator after a priority shift.
            if (!schedulerQueue.isEmpty()) {
                Iterator itr = schedulerQueue.iterator();
                Entry entry;
                Process currProcess;
                int wait;
                // Iterating through the iterator entries
                while (itr.hasNext()) {
                    entry = (Entry) itr.next();
                    currProcess = (Process) entry.getValue();
                    // calculating the wait by first subtracting the arrival time from the current time step. Then we
                    // subtract the difference between the process' duration, and the amount of time it has left, to
                    // exclude the time it has run from the wait time for the process.
                    wait = (time - currProcess.getArrivalTime() + 1) - (currProcess.getDuration()
                            - currProcess.getRunTimeLeft());
                    currProcess.setWaitTime(wait);
                    // Checking to see if the process has waited a max wait time cycle, by using the modulo operator.
                    // Also checking that the wait time isn't 0. For this case we are updating both the value and the
                    // key for the process.
                    if (currProcess.getWaitTime() != 0 && currProcess.getWaitTime() % MAX_WAIT_TIME == 0) {
                        currProcess.setPriority(currProcess.getPriority() - 1);
                        message = "Process " + currProcess.getId() + " reached maximum wait time... decreasing " +
                                "priority to " + currProcess.getPriority() + "\n";
                        System.out.print(message);
                        writer.write(message);
                        // Replacing value before we replace the key
                        schedulerQueue.replaceValue(entry, currProcess);
                        schedulerQueue.replaceKey(entry, currProcess.getPriority());
                    // Else we only update the entry value with the updated wait time.
                    } else {
                        schedulerQueue.replaceValue(entry, currProcess);
                    }
                }
            }
            // Finally move forward one time step
            time ++;
        }

        /**
         * Final average wait time calculation
         */

        double average = calculateAverageWaitTime(finishedProcessQueue);
        message = "Average wait time: " + average + "\n";
        System.out.print(message);
        writer.write(message);
        // Finally closing the writer
        writer.close();
    }

    /**
     * Loads Processes from an input script and inserts them into a heap adaptable priority queue.
     *
     * @param reader file Scanner
     * @param writer file Writer
     *
     * @throws IOException
     */
    private void loadProcessesFromInput(Scanner reader, Writer writer) throws IOException {

        // Reading the processes from the input file and storing them in an adaptable
        // priority queue, keyed on arrival time.
        message = "Building Process Queue...";
        System.out.println(message);
        writer.write(message);
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
            writer.write(process.toString() + "\n");
            inputProcessQueue.insert(process.getArrivalTime(), process);
        }
    }

    /**
     * Calculates the average wait time for all processes in a queue.
     *
     * @param processQueue the process queue we need the average for.
     *
     * @return the average wait time of processes in the queue
     */
    private double calculateAverageWaitTime(LinkedQueue<Process> processQueue) {
        double waitTime = 0;
        double totalProcesses = processQueue.size();
        Process process;
        while (!processQueue.isEmpty()) {
            process = processQueue.dequeue();
            waitTime += process.getWaitTime();
        }
        return waitTime / totalProcesses;
    }
}
