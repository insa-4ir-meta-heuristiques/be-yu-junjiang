package jobshop.solvers;

import jobshop.Instance;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Schedule;
import jobshop.encodings.Task;

import java.util.ArrayList;
import java.util.Optional;

/** An empty shell to implement a greedy solver. */
public class GreedySolver implements Solver {

    /** All possible priorities for the greedy solver. */
    public enum Priority {
        SPT, LPT, SRPT, LRPT, EST_SPT, EST_LPT, EST_SRPT, EST_LRPT
    }
    public final class Tasklr {

        /**
         * Identifier of the job
         */
        public final int job;

        /**
         * Index of the task inside the job.
         */
        public final int task;

        public final int duree;

        /**
         * Creates a new Task object (job, task).
         */
        public Tasklr(int job, int task, int duree) {
            this.job = job;
            this.task = task;
            this.duree = duree;
        }
    }
    /** Priority that the solver should use. */
    final Priority priority;

    /** Creates a new greedy solver that will use the given priority. */
    public GreedySolver(Priority p) {
        this.priority = p;
    }

   /** public ArrayList<Task> InitTasksToBeDone(Instance instance) {
        ArrayList<Task> res = new ArrayList<Task>();
        for (int i = 0; i < instance.numJobs; i++) {
            res.add(i, new Task(i, -1));
        }
        return res;
    }


    public int computeRemainingTime(int job, Instance instance, ArrayList<Task> jobsLastDoneTasks) {
        int lastDoneTask = jobsLastDoneTasks.get(job).task;
        int remainingTime = 0;
        // Calculate time from remaining tasks
        for (int i = lastDoneTask + 1; i < instance.numTasks; i++) {
            remainingTime += instance.duration(job, i);
        }
        return remainingTime;
    }


    public ArrayList<Task> InitDoableTasks(Instance instance) {
        // Set of tasks
        ArrayList<Task> doableTasks = new ArrayList<>();
        // Initialize possible tasks with the initial tasks from all the jobs
        // Same number of tasks per job
        for (int i = 0; i < instance.numJobs; i++) {
            doableTasks.add(i, new Task(i, 0));
        }
        return doableTasks;
    }


    public void UpdateDoableTasks(Instance instance, ArrayList<Task> doableTasks, Task lastDoneTask) {
        if (lastDoneTask.task < instance.numTasks-1) {
            Task newTask = new Task(lastDoneTask.job, lastDoneTask.task+1);
            doableTasks.set(lastDoneTask.job, newTask);
        } else {
            Task newTask = new Task(lastDoneTask.job, -1);
            doableTasks.set(lastDoneTask.job, newTask);
        }
    }


    public void InitremainingTime(int[] remainingTime) {
        for (int i = 0; i < remainingTime.length; i++) {
            remainingTime[i] = 0;
        }
    }

    @Override
    public Optional<Schedule> solve(Instance instance, long deadline) {
        ResourceOrder sol = new ResourceOrder(instance);




        Task taskToEnqueue = new Task(0,0);
        Tasklr tasklrToEnqueue = new Tasklr(0,0,0);
        ArrayList<Task> TaskList = new ArrayList<Task>();
        ArrayList<Tasklr> TasklrList = new ArrayList<Tasklr>();
        if (priority == Priority.SPT){
            int i = 0;
            for(int jobNumber = 0 ; jobNumber<instance.numJobs ; jobNumber++) {
                TaskList.add(new Task(jobNumber, 0));
            }
            while (i < (instance.numTasks * instance.numJobs)) {
                int mindur = 999999;
                for (int taskn = 0; taskn < TaskList.size(); taskn++) {
                    Task currentTask = TaskList.get(taskn);
                    if (mindur > instance.duration(currentTask)) {
                        mindur = instance.duration(currentTask);
                        taskToEnqueue = currentTask;
                    }
                }
                sol.addTaskToMachine(instance.machine(taskToEnqueue), taskToEnqueue);
                TaskList.remove(taskToEnqueue);
                if (taskToEnqueue.task < (instance.numTasks-1)) {
                    TaskList.add(new Task(taskToEnqueue.job, (taskToEnqueue.task + 1)));
                }
                i++;
            }
        }

        if (priority == Priority.LRPT){
            int n = 0;
            for(int jobNumber = 0 ; jobNumber<instance.numJobs ; jobNumber++) {
                int dure = 0;
                for (int tasknumber = 0; tasknumber<instance.numTasks; tasknumber++) {
                    dure = dure + instance.duration(jobNumber,tasknumber);
                }
                TasklrList.add(new Tasklr(jobNumber, 0, dure));
            }
            while (n < (instance.numTasks * instance.numJobs)) {
                int maxdur = 0;
                for (int taskn = 0; taskn < TasklrList.size(); taskn++) {
                    Tasklr currentTasklr = TasklrList.get(taskn);
                    if (maxdur < currentTasklr.duree) {
                        maxdur = currentTasklr.duree;
                        taskToEnqueue = new Task(currentTasklr.job, currentTasklr.task);
                        tasklrToEnqueue = currentTasklr;
                    }
                }
                sol.addTaskToMachine(instance.machine(taskToEnqueue), taskToEnqueue);
                TasklrList.remove(tasklrToEnqueue);
                if (tasklrToEnqueue.task < (instance.numTasks-1)) {
                    TasklrList.add(new Tasklr(tasklrToEnqueue.job, (tasklrToEnqueue.task + 1), (tasklrToEnqueue.duree-instance.duration(taskToEnqueue))));
                }
                n++;
            }
        }**/
        /**if (priority == Priority.EST_SPT){
            int i = 0;
            ArrayList<Integer> MachineTime = new ArrayList<Integer>();
            ArrayList<Integer> JobTime = new ArrayList<Integer>();
            for(int jobNumber = 0 ; jobNumber<instance.numJobs ; jobNumber++) {
                TaskList.add(new Task(jobNumber, 0));
            }
            while (i < (instance.numTasks * instance.numJobs)) {
                int minstarttime = 999999;
                for (int taskn = 0; taskn < TaskList.size(); taskn++) {
                    Task currentTask = TaskList.get(taskn);
                    int currentstarttime = Schedule.startTime(currentTask.job, currentTask.task);
                    if (minstarttime > currentstarttime) {
                        minstarttime = instance.duration(currentTask);
                        taskToEnqueue = currentTask;
                    }
                }
                sol.addTaskToMachine(instance.machine(taskToEnqueue), taskToEnqueue);
                TaskList.remove(taskToEnqueue);
                if (taskToEnqueue.task < (instance.numTasks-1)) {
                    TaskList.add(new Task(taskToEnqueue.job, (taskToEnqueue.task + 1)));
                }
                i++;
            }
        }**/
       /* return sol.toSchedule();
    }*/
        public ArrayList<Task> InitLastDoneTasks(Instance instance) {
            ArrayList<Task> res = new ArrayList<Task>();
            for (int i = 0; i < instance.numJobs; i++) {
                res.add(i, new Task(i, -1));
            }
            return res;
        }

    /**
     * @param job
     * @param instance
     * @param jobsLastDoneTasks Task ArrayList for every last finished tasks for each job
     * @return Returns the remaining processing time for a specific job
     */
    public int computeRemainingTime(int job, Instance instance, ArrayList<Task> jobsLastDoneTasks) {
        int lastDoneTask = jobsLastDoneTasks.get(job).task;
        int remainingTime = 0;
        // Calculate time from remaining tasks
        for (int i = lastDoneTask + 1; i < instance.numTasks; i++) {
            remainingTime += instance.duration(job, i);
        }
        return remainingTime;
    }

    /**
     * @param instance Instance to create list of doable tasks from.
     * @return Task ArrayList of doable Tasks
     */
    public ArrayList<Task> InitDoableTasks(Instance instance) {
        // Set of tasks
        ArrayList<Task> doableTasks = new ArrayList<>();
        // Initialize possible tasks with the initial tasks from all the jobs
        // Same number of tasks per job
        for (int i = 0; i < instance.numJobs; i++) {
            doableTasks.add(i, new Task(i, 0));
        }
        return doableTasks;
    }

    /**
     * @param instance Instance to create list of doable tasks from.
     * @param doableTasks List of remaining doable tasks.
     * @param lastDoneTask The last task which has been done.
     */
    public void UpdateDoableTasks(Instance instance, ArrayList<Task> doableTasks, Task lastDoneTask) {
        if (lastDoneTask.task < instance.numTasks-1) {
            Task newTask = new Task(lastDoneTask.job, lastDoneTask.task+1);
            doableTasks.set(lastDoneTask.job, newTask);
        } else {
            Task newTask = new Task(lastDoneTask.job, -1);
            doableTasks.set(lastDoneTask.job, newTask);
        }
    }

    /**
     * @param remainingTime List to be initialized
     */
    public void InitremainingTime(int[] remainingTime) {
        for (int i = 0; i < remainingTime.length; i++) {
            remainingTime[i] = 0;
        }
    }

    /**
     * @param instance    The current instance
     * @param doableTasks Task ArrayList of doable tasks
     * @return Shortest processing time task in the set
     */
    public Task SPTTask(Instance instance, ArrayList<Task> doableTasks) {
        int min = Integer.MAX_VALUE;
        int index = 0;
        int duration;
        for (int i = 0; i < doableTasks.size(); i++) {
            if (doableTasks.get(i).task >= 0) {
                duration = instance.duration(doableTasks.get(i));
                if (duration < min && duration >= 0) {
                    min = duration;
                    index = i;
                }
            }
        }
        return doableTasks.get(index);
    }

    /**
     * @param instance          The current instance
     * @param doableTasks       Task ArrayList of doable tasks
     * @param jobsLastDoneTasks Task ArrayList for every last finished tasks for each job
     * @return Longest remaining processing time job's task is returned
     */
    public Task LRPTTask(Instance instance, ArrayList<Task> doableTasks, ArrayList<Task> jobsLastDoneTasks) {
        // Compute job remaining time
        int max = Integer.MIN_VALUE;
        Task longest = new Task(-1,-1);
        int currentTime;
        // Compute remaining time for each job that has a doable Task
        for (Task doableTask : doableTasks) {
            currentTime = computeRemainingTime(doableTask.job, instance, jobsLastDoneTasks);
            if (currentTime > max) {
                max = currentTime;
                longest = doableTask;
            }
        }
        return longest;
    }


    public void InitESTArrays(Instance instance, ArrayList<Integer> finishingTimeMachines, ArrayList<Integer> jobCurrentTime) {
        for (int i = 0; i < instance.numJobs; i++) {
            jobCurrentTime.add(0);
        }
        for (int i = 0; i < instance.numTasks; i++) {
            finishingTimeMachines.add(0);
        }
    }


    /**
     *
     * @param instance The current instance
     * @param doableTasks ArrayList of the currently doable tasks
     * @param finishingTimeMachines ArrayList of the finishing time for each machine
     * @param jobCurrentTime    ArrayList of the current starting time for each job
     * @return ArrayList of the starting times for each doable task
     */
    public ArrayList<Est> computeAvailableTime(Instance instance, ArrayList<Task> doableTasks, ArrayList<Integer> finishingTimeMachines, ArrayList<Integer> jobCurrentTime) {
        ArrayList<Est> time = new ArrayList<>();
        int taskFreeTime;
        int machineFreeTime;
        int machine;
        int availableTime;
        for (Task doableTask : doableTasks) {
            // Retrieve finishing time for the machine

            if (doableTask.task >= 0) {
                machine = instance.machine(doableTask);
                machineFreeTime = finishingTimeMachines.get(machine);
                // Retrieve current starting time for the task
                taskFreeTime = jobCurrentTime.get(doableTask.job);
                availableTime = Integer.max(taskFreeTime, machineFreeTime);
                int job = doableTasks.indexOf(doableTask);

                time.add(new Est(doableTask,availableTime));

            }
        }
        return time;
    }


    /**
     * @param instance              The current instance
     * @param doableTasks           Task ArrayList of doable tasks
     * @param finishingTimeMachines Last finishing time for each machine
     * @param jobCurrentTime        Current finishing time for the task for each job
     * @return Shortest processing time and nearest task is returned
     */
    public Est EST_SPTTask(Instance instance, ArrayList<Task> doableTasks, ArrayList<Integer> finishingTimeMachines, ArrayList<Integer> jobCurrentTime) {
        ArrayList<Task> filteredTasks = new ArrayList<>();
        // Compute starting time for doable Tasks
        ArrayList<Est> time = computeAvailableTime(instance, doableTasks, finishingTimeMachines, jobCurrentTime);
        // Find minimum times
        int min = Integer.MAX_VALUE;
        for (Est t : time) {
            if (min > t.startingTime) {
                min = t.startingTime;
            }
        }
        // Add all tasks with the same minimum starting time
        // Works because time is indexed with doableTasks !
        for (Est Est : time) {
            if (Est.startingTime == min){
                filteredTasks.add(Est.task);
            }
        }

        // Run the SPT choice with the filtered Array
        Task chosenTask =  SPTTask(instance, filteredTasks);
        Est result = new Est(chosenTask,min);
        return result;

    }

    /**
     * @param instance              The current instance
     * @param doableTasks           Task ArrayList of doable tasks
     * @param jobsLastDone          Task ArrayList for every last finished tasks for each job
     * @param finishingTimeMachines Last finishing time for each machine
     * @param jobCurrentTime        Current finishing time for the task for each job
     * @return Nearest and longest remaining processing time job's task is returned
     */
    public Est EST_LRPTTask(Instance instance, ArrayList<Task> doableTasks, ArrayList<Task> jobsLastDone, ArrayList<Integer> finishingTimeMachines, ArrayList<Integer> jobCurrentTime) {

        ArrayList<Task> filteredTasks = new ArrayList<>();
        // Compute starting time for doable Tasks
        ArrayList<Est> time = computeAvailableTime(instance, doableTasks, finishingTimeMachines, jobCurrentTime);
        // Find minimum times
        int min = Integer.MAX_VALUE;
        for (Est t : time) {
            if (min > t.startingTime) {
                min = t.startingTime;
            }
        }
        // Add all tasks with the same minimum starting time
        // Works because time is indexed with doableTasks !
        for (Est Est : time) {
            if (Est.startingTime == min){
                filteredTasks.add(Est.task);
            }
        }
        Task chosenTask =  LRPTTask(instance, filteredTasks,jobsLastDone);
        Est result = new Est(chosenTask,min);
        return result;
    }

    protected boolean noJobLeft(int jobs, ArrayList<Task> doableTasks) {
        boolean result = true;
        for (int i=0; i<jobs; i++) {
            if (doableTasks.get(i).task != -1) {
                result = false;
            }
        }

        return result;
    }

    @Override
    public Optional<Schedule> solve(Instance instance, long deadline) {
        // Solution is represented by a resource order object
        Est res;
        Task currentTask = new Task(0, 0);
        ResourceOrder ro = new ResourceOrder(instance);
        Optional<Schedule> schedule;
        int machine;
        // Set of tasks -> 1 task for each job
        ArrayList<Task> doableTasks = InitDoableTasks(instance);
        ArrayList<Task> lastDoneTasks = InitLastDoneTasks(instance);

        ArrayList<Integer> finishingTimeMachines = new ArrayList<>();
        ArrayList<Integer> jobCurrentTime = new ArrayList<>();

        InitESTArrays(instance,finishingTimeMachines,jobCurrentTime);

        int calc;

        boolean noRemainingJobs = false;

        while (!noRemainingJobs) {
            // Choisir tache appropriée
            switch (this.priority) {
                case SPT:
                    currentTask = SPTTask(instance, doableTasks);
                    break;
                case LRPT:
                    currentTask = LRPTTask(instance, doableTasks, lastDoneTasks);
                    break;
                case EST_SPT:
                    res = EST_SPTTask(instance, doableTasks, finishingTimeMachines, jobCurrentTime);
                    //System.out.println("res :" + res);
                    currentTask = res.getTask();
                    // Update finishing time for the job
                    calc = res.getStartingTime() + instance.duration(currentTask);
                    jobCurrentTime.set(currentTask.job, calc);
                    // Update finishing time for the machine
                    finishingTimeMachines.set(instance.machine(currentTask), calc);
                    break;
                case EST_LRPT:
                    res = EST_LRPTTask(instance, doableTasks, lastDoneTasks, finishingTimeMachines, jobCurrentTime);
                    currentTask = res.getTask();
                    // Update finishing time for the job
                    calc = res.getStartingTime() + instance.duration(currentTask);
                    jobCurrentTime.set(currentTask.job, calc);
                    // Update finishing time for the machine
                    finishingTimeMachines.set(instance.machine(currentTask), calc);
                    break;

            }

            // Assigner cette tache correspondante dans le ressource order
            machine = instance.machine(currentTask);
            ro.addTaskToMachine(machine, currentTask);

            // mettre a jour l'ensemble des taches faisables
            UpdateDoableTasks(instance, doableTasks, currentTask);
            // Mettre a jour les taches deja faites
            lastDoneTasks.set(currentTask.job, currentTask);

            noRemainingJobs = noJobLeft(instance.numJobs, doableTasks);
        }

        return ro.toSchedule();
    }

}

