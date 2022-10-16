import java.util.ArrayList;

public class Main {
    static ArrayList<Float> timer = new ArrayList<>();
    public static void main(String[] args) {
           new TrustpilotMD5(8, true);

    }

    public static void AddTime(float time){
        timer.add(time);

        //kill all threads except the main thread
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        Thread[] threads = new Thread[group.activeCount()];
        group.enumerate(threads);
        for (Thread thread : threads) {
            if (thread != Thread.currentThread()) {
                thread.stop();
            }
        }
        System.out.printf("Time: %.2f ms", time);
        if (timer.size() <= 100) {
            System.out.println(" - " + timer.size() + " - Average: " + (timer.stream().reduce(0f, Float::sum) / timer.size()) + "ms" + " - Max: " + timer.stream().max(Float::compare).get() + "ms" + " - Min: " + timer.stream().min(Float::compare).get() + "ms");
            new TrustpilotMD5(8, false);
        }else {
            System.out.println(" - " + timer.size() + " - Average: " + (timer.stream().reduce(0f, Float::sum) / timer.size()) + "ms" + " - Max: " + timer.stream().max(Float::compare).get() + "ms" + " - Min: " + timer.stream().min(Float::compare).get() + "ms");
            System.exit(0);
        }
    }
}
