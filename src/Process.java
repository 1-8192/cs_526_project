/**
 * Class to represent Process object that the Scheduler will execute in order.
 */
public class Process {

    /**
     * Process ID
     */
    private int id;

    /**
     * Process Priority. Should be used as key in Scheduler adaptable Priority queue.
     */
    private int priority;

    /**
     * Time process should be added to schedule queue. Should be used as key in initial process
     * adaptable priority queue.
     */
    private int arrivalTime;

    /**
     * Duration of process.
     */
    private int duration;

    /**
     * Not a requirement, but for ease of use saving the time the process has waited to this variable.
     */
    private int waitTime;

    /**
     * Another convenience variable to store the amount of time left to run.
     */
    private int runTimeLeft;

    /**
     * Standard class Constructor.
     *
     * @param id          process id
     * @param priority    process priority
     * @param arrivalTime time to add to queue
     * @param duration    duration of process
     */
    public Process(int id, int priority, int arrivalTime, int duration
    ) {
        this.id = id;
        this.priority = priority;
        this.arrivalTime = arrivalTime;
        this.duration = duration;
        // initializing the non-required variables below.
        this.waitTime = 0;
        this.runTimeLeft = duration;
    }

    /**
     * Constructor for array input.
     * @param inputArray input Array of variables to initialize. Please ensure array indexes are keyed to:
     *                   0 => id
     *                   1 => priority
     *                   2 => duration
     *                   3 => arrivalTime
     *                   4 => waitTime (optional)
     *                   5 => runTimeLeft (optional)
     */
    public Process(int[] inputArray) {
        this.id = inputArray[0];
        this.priority = inputArray[1];
        this.duration = inputArray[2];
        this.arrivalTime = inputArray[3];
        this.waitTime = (inputArray.length >= 5) ? inputArray[4] : 0;
        this.runTimeLeft = (inputArray.length >= 6) ? inputArray[5] : inputArray[2];
    }

    /**
     * ID getter
     *
     * @return process ID
     */
    public int getId() {
        return this.id;
    }

    /**
     * Priority getter
     *
     * @return process Priority
     */
    public int getPriority() {
        return this.priority;
    }

    /**
     * ArrivalTime getter
     *
     * @return process arrival time
     */
    public int getArrivalTime() {
        return this.arrivalTime;
    }

    /**
     * Duration getter
     *
     * @return process duration
     */
    public int getDuration() {
        return this.duration;
    }

    /**
     * waitTime getter
     *
     * @return process wait time
     */
    public int getWaitTime() {
        return this.waitTime;
    }

    /**
     * runTimeLeft getter
     *
     * @return process run time left
     */
    public int getRunTimeLeft() {return this.runTimeLeft; }

    /**
     * Duration setter
     *
     * @param duration the new duration
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * Priority setter
     *
     * @param priority the new priority
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * waitTime setter
     *
     * @param waitTime the new wait time
     */
    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    /**
     * runTimeLeft setter
     *
     * @param runTimeLeft the new run time left
     */
    public void setRunTimeLeft(int runTimeLeft) {
        this.runTimeLeft = runTimeLeft;
    }

    /**
     * The simulation needs to print process info, so we need a toString() method.
     *
     * @return string representation of instance
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Id = ");
        builder.append(this.id);
        builder.append(", ");
        builder.append("Priority = ");
        builder.append(this.priority);
        builder.append(", ");
        builder.append("Duration = ");
        builder.append(this.duration);
        builder.append(", ");
        builder.append("Arrival Time = ");
        builder.append(this.arrivalTime);
        builder.append(", ");
        // Not required or present in sample output for the assignment, but I thought we might as well
        // show these variables, too.
        builder.append("Wait Time = ");
        builder.append(this.waitTime);
        builder.append(", ");
        builder.append("Run Time Left = ");
        builder.append(this.runTimeLeft);
        return builder.toString();
    }
}
