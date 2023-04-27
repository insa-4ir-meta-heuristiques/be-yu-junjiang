package jobshop.solvers;

import jobshop.Instance;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Schedule;
import jobshop.solvers.neighborhood.Neighborhood;
import jobshop.solvers.neighborhood.Nowicki;
import jobshop.solvers.BasicSolver;

import java.util.List;
import java.util.Optional;

/** An empty shell to implement a descent solver. */
public class DescentSolver implements Solver {

    final Neighborhood neighborhood;
    final Solver baseSolver;

    /** Creates a new descent solver with a given neighborhood and a solver for the initial solution.
     *
     * @param neighborhood Neighborhood object that should be used to generates neighbor solutions to the current candidate.
     * @param baseSolver A solver to provide the initial solution.
     */
    public DescentSolver(Neighborhood neighborhood, Solver baseSolver) {
        this.neighborhood = neighborhood;
        this.baseSolver = baseSolver;
    }

    @Override
    public Optional<Schedule> solve(Instance instance, long deadline) {
        // On trouve la solution du solver actuel
        Optional<Schedule> schedule = this.baseSolver.solve(instance, deadline);

        // Tant qu'on ne trouve pas de voisin améliorant ou de timeout on continue
        boolean foundSolution = false;
        int time = 0;
        while (!foundSolution && time <= 100) {
            // find neighbours thanks to the resource order
            List<ResourceOrder> neighborhood = this.neighborhood.generateNeighbors(new ResourceOrder(schedule.get()));

            // initialisation
            int bestNeighbor = -1;
            int bestNeighborSpan = -1;
            int bestMakespan = schedule.get().makespan();

            // itération sur chaque voisin
            for (int i=0; i<neighborhood.size(); i++) {
                int currentSpan = neighborhood.get(i).toSchedule().get().makespan();
                // trouver le meilleur voisin
                if (currentSpan < bestNeighborSpan || bestNeighborSpan == -1) {
                    bestNeighborSpan = currentSpan;
                    bestNeighbor = i;
                }
            }
            // si on trouve une meilleur solution alors on met fin à la boucle
            if (bestNeighborSpan >= bestMakespan) {
                foundSolution = true;
            }
            else {
                schedule = neighborhood.get(bestNeighbor).toSchedule();
            }
            time++;
        }

        return schedule;
    }

}
