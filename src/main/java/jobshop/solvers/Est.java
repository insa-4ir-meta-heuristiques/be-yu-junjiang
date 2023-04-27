package jobshop.solvers;

import jobshop.encodings.Task;

import java.util.Objects;

public class Est {

    final int startingTime;
    final Task task;

    public Est(Task task,int startingTime){
        this.startingTime = startingTime;
        this.task = task;
    }

    public int getStartingTime(){
        return this.startingTime;
    }

    public Task getTask() {
        return this.task;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Est)) return false;
        Est Est = (Est) o;
        return getStartingTime() == Est.getStartingTime() && Objects.equals(getTask(), Est.getTask());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStartingTime(), getTask());
    }

    @Override
    public String toString(){
        return ("Starting Time: "+Integer.toString(startingTime) +" Task: "+ task.toString());
    }
}
