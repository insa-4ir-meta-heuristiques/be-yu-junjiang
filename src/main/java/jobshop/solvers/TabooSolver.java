package jobshop.solvers;

import jobshop.Instance;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Schedule;
import jobshop.solvers.neighborhood.Nowicki;

import java.util.List;
import java.util.Optional;

public class TabooSolver implements Solver {

    final Solver baseSolver;
    final int maxIteration;
    final int tabooTime;

    /** Creates a new descent solver with a given neighborhood and a solver for the initial solution.
     * @param neighborhood Neighborhood object that should be used to generates neighbor solutions to the current candidate.
     * @param baseSolver A solver to provide the initial solution.
     * @param maxIteration
     * @param tabooTime
    **/

    final Nowicki neighborhood = new Nowicki();

    public TabooSolver(Solver solver, int maxIteration, int time){
        this.baseSolver = solver;
        this.maxIteration = maxIteration;
        this.tabooTime = time;
    }


    public Optional<Schedule> solve(Instance instance, long deadline) {
        long start = System.currentTimeMillis();
        long end;
        Optional<Schedule> current = baseSolver.solve(instance,deadline);
        assert current.isPresent();

        // Init best solution
        Schedule bestSolution = current.get();
        Schedule localBestSolution = current.get();
        Optional<Schedule> tabooSolution;
        Schedule bestTabooSolution = null;
        Optional<Schedule> buffer = null;

        // Init iterator variable
        int iterator = 0;

        // Init iterator for swaps
        int indexSwap;
        int indexSelectedSwap;

        // Boolean found a local best
        boolean found;
        boolean foundTabooImprovement = false;

        // Swap for taboo selection
        Nowicki.Swap selectedSwap = null;

        // Create TabooSolver List
        // No need to add current solution because we only keep in memory the taboo swaps
        TabooList tabooList = new TabooList();

        // Swap list
        List<Nowicki.Swap> swaps;
        // Each neighbor is at that same index than its corresponding swap??
        List<ResourceOrder> neighbors;

        while(iterator<=maxIteration){

            found = false;
            // Update tabooList
            tabooList.update();
            iterator++;
            neighbors = neighborhood.generateNeighbors(new ResourceOrder(localBestSolution));
            swaps = neighborhood.allSwaps(new ResourceOrder(localBestSolution));

            // Find best neighbor
            indexSwap = 0;
            indexSelectedSwap = 0;
            for (ResourceOrder neighbor : neighbors) {
                // Check if swap is not part of the taboo list
                if (!tabooList.isPresent(swaps.get(indexSwap))){
                    // Check present
                    buffer = neighbor.toSchedule();
                    if (buffer.isPresent()){
                        // Select best solution
                        if (buffer.get().makespan() < localBestSolution.makespan()) {
                            localBestSolution = buffer.get();
                            indexSelectedSwap = indexSwap;
                            found = true;
                        }
                    }

                }
                indexSwap++;
            }

            // Modifications only if a local best is found
            if (found) {
                // Check for global best
                if (localBestSolution.makespan() < bestSolution.makespan()) {

                    // Find best taboo solution
                    foundTabooImprovement = false;
                    bestTabooSolution = localBestSolution;
                    for (Nowicki.Swap swap : tabooList.getSwaps()) {
                        // Check for non-empty schedule
                        tabooSolution = swap.generateFrom(new ResourceOrder(localBestSolution)).toSchedule();
                        // Compare taboo to best
                        if (tabooSolution.isPresent()){

                            if (tabooSolution.get().makespan() < bestTabooSolution.makespan()){
                                foundTabooImprovement = true;
                                selectedSwap = swap;
                                bestTabooSolution = tabooSolution.get();
                            }

                        }
                    }

                }

                // Checking weather best taboo solution is found
                if (foundTabooImprovement){
                    bestSolution = bestTabooSolution;
                    // Add selected swap to the tabooList
                    tabooList.addTaboo(tabooTime,selectedSwap);
                }else{
                    bestSolution = localBestSolution;
                    // Add selected swap to the tabooList
                    tabooList.addTaboo(tabooTime, swaps.get(indexSelectedSwap));
                }
            }

            end = System.currentTimeMillis();
            if ((end - start)> deadline) {break;}
        }
        return Optional.ofNullable(bestSolution);
    }
}
