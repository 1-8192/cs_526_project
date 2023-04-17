import net.datastructures.HeapAdaptablePriorityQueue;

public class Process {
    private int id;

    private int priority;
    private int arrivalTime;
    private int duration;
    private int waitTime;

    private int runTimeLeft;

    public Process(int id, int priority, int arrivalTime, int duration
    ) {
        this.id = id;
        this.priority = priority;
        this.arrivalTime = arrivalTime;
        this.duration = duration;
        this.waitTime = 0;
        this.runTimeLeft = duration;
    }

    public Process(int[] inputArray) {
        this.id = inputArray[0];
        this.priority = inputArray[1];
        this.duration = inputArray[2];
        this.arrivalTime = inputArray[3];
        this.waitTime = (inputArray.length >= 5) ? inputArray[4] : 0;
        this.runTimeLeft = (inputArray.length >= 6) ? inputArray[5] : inputArray[2];
    }

    public int getId() {
        return this.id;
    }

    public int getPriority() {
        return this.priority;
    }

    public int getArrivalTime() {
        return this.arrivalTime;
    }

    public int getDuration() {
        return this.duration;
    }

    public int getWaitTime() {
        return this.waitTime;
    }

    public int getRunTimeLeft() {return this.runTimeLeft; }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    public void setRunTimeLeft(int runTimeLeft) {
        this.runTimeLeft = runTimeLeft;
    }

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
        builder.append("Wait Time = ");
        builder.append(this.waitTime);
        builder.append(", ");
        builder.append("Run Time Left = ");
        builder.append(this.runTimeLeft);
        return builder.toString();
    }
}
