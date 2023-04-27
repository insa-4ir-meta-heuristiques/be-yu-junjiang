package jobshop.solvers;

import jobshop.solvers.neighborhood.Nowicki;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

public class TabooList {

    public Hashtable<Nowicki.Swap, Integer> table;

    public TabooList(){
        this.table = new Hashtable<>();
    }

    /**
     * O(1) Insertion in hashtable without taking into account operations
     * @param tabooTime Time limit for the taboo swap
     * @param swapToRevert Chosen swap to be reverted to create the taboo swap
     */
    public void addTaboo(int tabooTime, Nowicki.Swap swapToRevert){
        // Generate reverse swap
        int task1 = swapToRevert.t2;
        int task2 = swapToRevert.t1;
        Nowicki.Swap swapToInsert = new Nowicki.Swap(swapToRevert.machine,task1,task2);
        this.table.put(swapToInsert,tabooTime);
    }

    /**
     * Updates the counter for every element in the table and removes it if under 0
     */
    public void update(){
        // Iterate through each term and delete those with expired timers
        Integer time;
        Nowicki.Swap swap;
        Hashtable<Nowicki.Swap, Integer> res = new Hashtable<>();
        for (Map.Entry<Nowicki.Swap, Integer> e : this.table.entrySet()){
            swap = e.getKey();
            time = e.getValue();
            if (time-1 > 0){
                res.put(swap,time-1);
            }
        }
        this.table = res;
    }

    /**
     *
     * @param swap Swap to check if it is present in the taboo table
     * @return True if present, False if not.
     */
    public boolean isPresent(Nowicki.Swap swap){
        return this.table.containsKey(swap);
    }

    /**
     *
     * @return A set of all the keys in the hashtable
     */
    public Set<Nowicki.Swap> getSwaps() {
        return table.keySet();
    }

}
