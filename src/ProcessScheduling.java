import java.io.*;

public class ProcessScheduling {

    /**
     * Class with main method for the Process Scheduler Program.
     * Final project for CS 526.
     *
     * @param args main method params
     *
     * @throws IOException
     */
    public static void main(String args[]) throws IOException {
        Scheduler scheduler = new Scheduler();
        scheduler.runSimulation();
    }
}
