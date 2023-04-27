package jobshop;

import jobshop.Instance;
import jobshop.encodings.Schedule;
import jobshop.solvers.DescentSolver;
import jobshop.solvers.GreedySolver;
import jobshop.solvers.Solver;
import jobshop.solvers.neighborhood.Nowicki;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

public class DescentSolverTests {

    @Test
    public void testDescentSolving() throws IOException {
        Instance instance = Instance.fromFile(Paths.get("instances/aaa3"));

        Solver baseSolver = new GreedySolver(GreedySolver.Priority.EST_SPT);
        Nowicki neighborhood = new Nowicki();

        // One solver for each algorithm
        DescentSolver descentSolver = new DescentSolver(neighborhood, baseSolver);
        Optional<Schedule> result = descentSolver.solve(instance, 100);

        // Affichage de chaque solution
        System.out.println("============= Descent ===============");
        Assert.assertTrue(result.isPresent());
        System.out.println(result.toString());
    }

}
