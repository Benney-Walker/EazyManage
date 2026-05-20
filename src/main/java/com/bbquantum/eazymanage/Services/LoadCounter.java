package com.bbquantum.eazymanage.Services;

public class LoadCounter {

    private final int total;

    private int count = 0;

    private boolean completed = false;

    public LoadCounter(int total) {
        this.total = total;
    }

    /**
     * One leaf has reported in. Increments the count; fires {@code onComplete}
     * exactly once when the total is reached. {@code leafFailure}, if non-null,
     * is run unconditionally — preserving the legacy quirk where the
     * orchestrator's failure toast fires on every tick that supplies it.
     */
    public void tick(Runnable onComplete, Runnable leafFailure) {
        count++;
        if (count >= total && !completed) {
            completed = true;
            if (onComplete != null) onComplete.run();
        }
        if (leafFailure != null) leafFailure.run();
    }

    public int total() { return total; }
    public int count() { return count; }
    public boolean isComplete() { return completed; }
}
