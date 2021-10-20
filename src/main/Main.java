package main;

public class Main {
    private double result = 0;
    private int finished = 0;

    public static void main(String[] args) {
        Main main = new Main();
        main.run();
    }

    private void run() {
        double a = 0;
        double b = Math.PI;
        int n = 1000_000_000;
        int nThreads = 50;
        long start = System.currentTimeMillis();
//        IntegralCalculator calc = new IntegralCalculator(a, b, n, Math::sin);
//        double result = calc.calculate();
        double delta = (b-a)/nThreads;
        for (int i = 0; i < nThreads; i++) {
            ThreadingIntegralCalculator calculator = new ThreadingIntegralCalculator(a + i * delta, a + (i + 1) * delta, n / nThreads, Math::sin, this);
            new Thread(calculator).start();
        }
        synchronized (this) {
            try {
                while (finished < nThreads) {
                    wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long finish = System.currentTimeMillis();
        System.out.println("result = " + result);
        System.out.println(finish-start);
    }

    public synchronized void sendResult(double v) {
        result += v;
        finished++;
        notify();
    }
}
