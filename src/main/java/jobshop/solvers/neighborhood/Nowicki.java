package jobshop.solvers.neighborhood;

import jobshop.Instance;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Schedule;
import jobshop.encodings.Task;

import java.util.*;
import java.util.stream.Collectors;

/** Implementation of the Nowicki and Smutnicki neighborhood.
 *
 * It works on the ResourceOrder encoding by generating two neighbors for each block
 * of the critical path.
 * For each block, two neighbors should be generated that respectively swap the first two and
 * last two tasks of the block.
 */
public class Nowicki extends Neighborhood {

    /**
     * A block represents a subsequence of the critical path such that all tasks in it execute on the same machine.
     * This class identifies a block in a ResourceOrder representation.
     * <p>
     * Consider the solution in ResourceOrder representation
     * machine 0 : (0,1) (1,2) (2,2)
     * machine 1 : (0,2) (2,1) (1,1)
     * machine 2 : ...
     * <p>
     * The block with : machine = 1, firstTask= 0 and lastTask = 1
     * Represent the task sequence : [(0,2) (2,1)]
     */
    public static class Block {
        public int getMachine() {
            return machine;
        }

        public int getFirstTask() {
            return firstTask;
        }

        public int getLastTask() {
            return lastTask;
        }

        /**
         * machine on which the block is identified
         */
        public final int machine;
        /**
         * index of the first task of the block
         */
        public final int firstTask;
        /**
         * index of the last task of the block
         */
        public int lastTask;

        /**
         * Creates a new block.
         */
        Block(int machine, int firstTask, int lastTask) {
            this.machine = machine;
            this.firstTask = firstTask;
            this.lastTask = lastTask;
        }

    }

    /**
     * Represents a swap of two tasks on the same machine in a ResourceOrder encoding.
     * <p>
     * Consider the solution in ResourceOrder representation
     * machine 0 : (0,1) (1,2) (2,2)
     * machine 1 : (0,2) (2,1) (1,1)
     * machine 2 : ...
     * <p>
     * The swap with : machine = 1, t1= 0 and t2 = 1
     * Represent inversion of the two tasks : (0,2) and (2,1)
     * Applying this swap on the above resource order should result in the following one :
     * machine 0 : (0,1) (1,2) (2,2)
     * machine 1 : (2,1) (0,2) (1,1)
     * machine 2 : ...
     */
    public static class Swap {
        /**
         * machine on which to perform the swap
         */
        public final int machine;

        /**
         * index of one task to be swapped (in the resource order encoding).
         * t1 should appear earlier than t2 in the resource order.
         */
        public final int t1;

        /**
         * index of the other task to be swapped (in the resource order encoding)
         */
        public final int t2;

        /**
         * Creates a new swap of two tasks.
         */
        public Swap(int machine, int t1, int t2) {
            this.machine = machine;
            if (t1 < t2) {
                this.t1 = t1;
                this.t2 = t2;
            } else {
                this.t1 = t2;
                this.t2 = t1;
            }
        }


        /**
         * Creates a new ResourceOrder order that is the result of performing the swap in the original ResourceOrder.
         * The original ResourceOrder MUST NOT be modified by this operation.
         */
        public ResourceOrder generateFrom(ResourceOrder original) {
            ResourceOrder order = original.copy();
            order.swapTasks(this.machine,this.t1,this.t2);
            return order;

        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Swap swap = (Swap) o;
            return machine == swap.machine && t1 == swap.t1 && t2 == swap.t2;
        }

        @Override
        public int hashCode() {
            return Objects.hash(machine, t1, t2);
        }
    }


    @Override
    public List<ResourceOrder> generateNeighbors(ResourceOrder current) {
        // convert the list of swaps into a list of neighbors (function programming FTW)
        return allSwaps(current).stream().map(swap -> swap.generateFrom(current)).collect(Collectors.toList());

    }

    /**
     * Generates all swaps of the given ResourceOrder.
     * This method can be used if one wants to access the inner fields of a neighbors.
     */
    public List<Swap> allSwaps(ResourceOrder current) {
        List<Swap> neighbors = new ArrayList<>();
        // iterate over all blocks of the critical path
        for (var block : blocksOfCriticalPath(current)) {
            // for this block, compute all neighbors and add them to the list of neighbors
            neighbors.addAll(neighbors(block));
        }
        return neighbors;
    }

    /**
     * Returns a list of all the blocks of the critical path.
     */
    public static List<Block> blocksOfCriticalPath(ResourceOrder order) {
        Optional<Schedule> schedule = order.toSchedule();
        List<Task> criticalPath = schedule.get().criticalPath();
        List<Block> blocks = new ArrayList<>();

        // Variables pour définir les blocks
        int first = -1;
        int last = -1;
        int lastMachine = -1;

        // Pour chaque tâche du chemin critique
        for (Task t : criticalPath) {
            // Si la tâche évaluée fait partie du block actuel on l'ajoute
            if (order.instance.machine(t) == lastMachine) {
                last++;
            }
            // Sinon on va regarder si elle peut être la première tâche d'un block
            else {
                // Si le block construit précédement est plus grand que 1 alors c'est un block et on l'ajoute
                if (last != first) {
                    blocks.add(new Block(lastMachine, first, last));
                }
                // On défini la machine étudiée à celle de la tâche actuelle
                lastMachine = order.instance.machine(t);
                // Puis on va regarder chaque job jusqu'à trouver un job qui utilise la même machine
                // Si c'est le cas on défini un nouveau block et on arrête la boucle
                for (int i=0; i < order.instance.numJobs; i++) {
                    if (order.getTaskOfMachine(lastMachine, i).equals(t)) {
                        first = i;
                        last = first;
                        break;
                    }
                }
            }
        }
        if (last != first) {
            blocks.add(new Block(lastMachine, first, last));
        }
        return blocks;
    }

    /**
     * For a given block, return the possible swaps for the Nowicki and Smutnicki neighborhood
     */
    public List<Swap> neighbors(Block block) {
        List<Swap> swapList = new ArrayList<>();

        if ((block.lastTask - block.firstTask) <= 1) {
            Swap swap = new Swap(block.machine, block.lastTask, block.firstTask);
            swapList.add(swap);
        } else {
            Swap swapFirst = new Swap(block.machine, block.firstTask+1, block.firstTask);
            Swap swapLast = new Swap(block.machine, block.lastTask-1, block.lastTask);
            swapList.add(swapFirst);
            swapList.add(swapLast);
        }

        return swapList;
    }


}

