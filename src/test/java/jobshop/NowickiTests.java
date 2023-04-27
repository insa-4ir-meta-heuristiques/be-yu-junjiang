package jobshop;

import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Schedule;
import jobshop.solvers.GreedySolver;
import jobshop.solvers.neighborhood.Nowicki;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class NowickiTests {


    @Test
    public void testBlocksCriticalPath() throws IOException {
        Instance instance = Instance.fromFile(Paths.get("instances/aaa3"));
        GreedySolver solver = new GreedySolver(GreedySolver.Priority.EST_SPT);
        Optional<Schedule> resultRandom = solver.solve(instance,100);
        ResourceOrder order = new ResourceOrder(resultRandom.get());

        Nowicki nowicki = new Nowicki();

        List<Nowicki.Block> blocks = nowicki.blocksOfCriticalPath(order);
    }

    @Test
    public void testNeighbors() throws IOException {
        Instance instance = Instance.fromFile(Paths.get("instances/aaa3"));
        GreedySolver solverRandom = new GreedySolver(GreedySolver.Priority.EST_SPT);
        Optional<Schedule> resultRandom = solverRandom.solve(instance,100);
        ResourceOrder order = new ResourceOrder(resultRandom.get());

        Nowicki nowicki = new Nowicki();

        List<Nowicki.Block> blocks = nowicki.blocksOfCriticalPath(order);

        List<Nowicki.Swap> swapList = nowicki.neighbors(blocks.get(0));
    }
}
