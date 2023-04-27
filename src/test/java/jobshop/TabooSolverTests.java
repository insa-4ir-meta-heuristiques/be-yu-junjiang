package jobshop;

import jobshop.Instance;
import jobshop.encodings.Schedule;
import jobshop.solvers.GreedySolver;
import jobshop.solvers.Solver;
import jobshop.solvers.TabooList;
import jobshop.solvers.TabooSolver;
import jobshop.solvers.neighborhood.Nowicki;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

public class TabooSolverTests {
    @Test
    public void listAddTaboo(){
        TabooList expected = new TabooList();
        TabooList result = new TabooList();
        Nowicki.Swap[] swaps = new Nowicki.Swap[5];
        Nowicki.Swap[] swapsExpected = new Nowicki.Swap[5];
        // Generating swaps
        for (int i = 0; i < swaps.length; i++) {
            swaps[i] = new Nowicki.Swap(i,0,1);
        }
        for (int i = 0; i < swapsExpected.length; i++) {
            swapsExpected[i] = new Nowicki.Swap(i,1,0);
        }
        // Filling up the expected
        for (Nowicki.Swap swap : swapsExpected) {
            expected.table.put(swap,-1);
        }
        for (Nowicki.Swap swap : swaps) {
            result.addTaboo(-1,swap);
        }

        Assert.assertEquals(expected.table.size(),result.table.size());
        for (Map.Entry<Nowicki.Swap, Integer> e : result.table.entrySet()) {
            Assert.assertTrue(expected.table.containsKey(e.getKey()));
        }

    }

    @Test
    public void listUpdate(){

        TabooList result = new TabooList();
        Nowicki.Swap[] swaps = new Nowicki.Swap[5];

        // Generating swaps
        for (int i = 0; i < swaps.length; i++) {
            swaps[i] = new Nowicki.Swap(i,0,1);
        }
        // Filling up
        int i = 1;
        for (Nowicki.Swap swap : swaps) {
            result.addTaboo(i,swap);
            i++;
        }
        while(i>=0){
            result.update();
            i--;
        }
        Assert.assertTrue(result.table.isEmpty());

    }

    @Test
    public void listIsPresent(){
        TabooList result = new TabooList();
        Assert.assertFalse(result.isPresent(new Nowicki.Swap(1,1,2)));
        result.addTaboo(5,new Nowicki.Swap(1,1,2));
        Assert.assertTrue(result.isPresent(new Nowicki.Swap(1,2,1)));
    }

    @Test
    public void testTabooSolver() throws IOException {
        Instance instance = Instance.fromFile(Paths.get("instances/aaa3"));
        Solver baseSolver = new GreedySolver(GreedySolver.Priority.EST_SPT);
        TabooSolver solver = new TabooSolver(baseSolver,100,5);
        Optional<Schedule> result = solver.solve(instance,100);
        Assert.assertTrue(result.isPresent());
        System.out.println("============= Taboo ===============");
        System.out.println(result.toString());
    }
}
