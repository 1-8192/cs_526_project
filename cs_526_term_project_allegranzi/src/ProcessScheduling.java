import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class ProcessScheduling {
    public static void main(String args[]) throws FileNotFoundException {
        File file = new File(System.getProperty("user.dir") + "/src/input/process_scheduling_input.txt");
        Scanner reader = new Scanner(file);

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
        }
    }
}
