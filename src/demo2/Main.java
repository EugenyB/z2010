package demo2;

import java.util.concurrent.*;

public class Main {
    private int count = 0;
    public static void main(String[] args) {
        Main main = new Main();
        main.run();
    }

    private void run() {

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

//            ScheduledFuture<String> future = scheduledExecutorService.schedule(() -> {
//                System.out.println("Calling...");
//                return "Called!";
//            }, 5, TimeUnit.SECONDS);
//            System.out.println("res = " + future.get());
            scheduledExecutorService.scheduleAtFixedRate(()->{
                System.out.println("count = " + (++count));
            }, 3, 5, TimeUnit.SECONDS);

    }
}
