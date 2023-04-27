package jobshop.solvers;

import jobshop.Instance;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Schedule;
import jobshop.encodings.Task;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

public class GreedyRandomSolver extends GreedySolver{

    /**
     * Number of trials for the randomization
     */
    final int repeat;


    /**
     * Creates a new random greedy solver that will use the given priority.
     */
    public GreedyRandomSolver(Priority p,int repeat) {
        super(p);
        this.repeat = repeat;
    }

    public ArrayList<Task> extractDoable(ArrayList<Task> doableTasks){
        ArrayList<Task> res = new ArrayList<>();
        for (Task doableTask : doableTasks) {
            if(doableTask.task != -1){
                res.add(doableTask);
            }
        }
        return res;
    }




    @Override
    public Optional<Schedule> solve(Instance instance, long deadline){
        // Solution is represented by a resource order object
        Est res;
        Task currentTask = new Task(0, 0);
        ResourceOrder ro = new ResourceOrder(instance);
        Optional<Schedule> schedule;
        Optional<Schedule> toReturn;
        Schedule best = null;
        int machine;
        // Set of tasks -> 1 task for each job
        ArrayList<Task> doableTasks = InitDoableTasks(instance);
        ArrayList<Task> lastDoneTasks = InitLastDoneTasks(instance);

        ArrayList<Integer> finishingTimeMachines = new ArrayList<>();
        ArrayList<Integer> jobCurrentTime = new ArrayList<>();

        InitESTArrays(instance,finishingTimeMachines,jobCurrentTime);

        int calc;

        boolean noRemainingJobs = false;

        int count = 0;

        int minMakespan = Integer.MAX_VALUE;

        Random rand = new Random();
        int random;
        int randomIndex;
        ArrayList<Task> sublist;

        while(count < this.repeat){
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
                    default:
                        // lots of things, hopefully not

                }


                // Ajouter de l'aleatoire
                sublist = extractDoable(doableTasks);
                random = rand.nextInt(100);
                if (random >= 95){
                    // select a task from the doable tasks
                    randomIndex = rand.nextInt(sublist.size());
                    currentTask = sublist.get(randomIndex);
                }

                machine = instance.machine(currentTask);
                ro.addTaskToMachine(machine,currentTask);



                // mettre a jour l'ensemble des taches faisables
                UpdateDoableTasks(instance, doableTasks, currentTask);
                // Mettre a jour les taches deja faites
                lastDoneTasks.set(currentTask.job, currentTask);

                noRemainingJobs = noJobLeft(instance.numJobs, doableTasks);
            }

            // Search for best result finishing time wise
            schedule = ro.toSchedule();

            if (schedule.isPresent()){
                if (minMakespan > schedule.get().makespan()){
                    minMakespan = schedule.get().makespan();
                    best = schedule.get();
                }
            }
            count++;
        }
        toReturn = Optional.ofNullable(best);
        return toReturn;
    }




}