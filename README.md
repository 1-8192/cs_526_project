# cs_526_project
Final Project for BU MET CS 526  
Author: Alessandro Allegranzi

## Project Questions

### Explain your choice for the data structure you used for D in this project

For a data structure in D, I stuck with using the HeapAdaptablePriorityQueue provided in data structures. I inserted the processes keyed to arrival time, so that in the simulation I could simply check the first entry in the queue without needing to traverse a whole data structure to find processes with the earliest arrival times. Multiple processes may have the same arrival time, but even so, removing the first few elements from the adaptable queue is still faster than a full list search, since a removal or minimum check is O(log n), and a full list search is O(n).

### For processes that had equal priority, it may have been better to execute the process with earlier arrival time instead of choosing arbitrarily. At a high level, how would you have to modify your project to accommodate this?

Instead of automatically removing the first entry in the Adaptable Priority Queue, I would have to remove the entries with the current time step as the key. I would then need to compare the arrival times of the processes and choose the earliest one. This could be done reasonably efficiently through sorting, as the list probably would not be very large. Another option could be to remove all the process from the schedule queue with the same priority and insert them into another priority queue keyed to arrival time, and remove the minimum entry from there. Then the remaining entries would need to be re-inserted back into the scheduler queue. The latter option brings in more space complexity with another priority queue though.

### What other changes could you consider

Some general refactoring of the code would be very useful in making the simulation code more organized and easier to read. My current solution has some messy conditionals, and there is likely some opportunity to simplify everything.
Storing the finished processes in a queue and adding all the wait times at the end of the program also seems space and time expensive. I originally tried adding the wait times to a counter variable any time a process finished running, but couldn’t quite get the average to be correct by the end.

## Local Build Instructions

Copy repository locally. Using an IDE of your choice, run the [ProcessScheduling](src/ProcessScheduling.java) file.
