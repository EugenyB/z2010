package main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    private double result = 0;
    private int finished = 0;

    ReentrantLock lock = new ReentrantLock();
    Condition condition = lock.newCondition();

    public static void main(String[] args) {
        Main main = new Main();
//        main.run();
//        main.run1();
//        main.run2();
        main.run3();
    }

    private void run3() {
        double a = 0;
        double b = Math.PI;
        int n = 1000_000_000;
        int nThreads = 1000;
        long start = System.currentTimeMillis();
        double delta = (b-a)/nThreads;
        List<Callable<Double>> callables = new ArrayList<>();
        for (int i = 0; i < nThreads; i++) {
            callables.add(new CallableIntegralCalculator(a + delta * i, a + delta * (i+1), n/nThreads, Math::sin));
        }
        ExecutorService es = Executors.newWorkStealingPool();
        try {
            double result = es.invokeAll(callables).stream().map(f -> {
                try {
                    return f.get();
                } catch (Exception e) {
                    throw new IllegalStateException();
                }
            }).mapToDouble(x -> x).sum();
            long finish = System.currentTimeMillis();
            System.out.println("result = " + result);
            System.out.println(finish-start);
        } catch (InterruptedException e) { }
    }

    private void run2() {
        double a = 0;
        double b = Math.PI;
        int n = 1000_000_000;
        int nThreads = 500;
        long start = System.currentTimeMillis();
        double delta = (b-a)/nThreads;
        List<Future<Double>> futures = new ArrayList<>();
        ExecutorService es = Executors.newFixedThreadPool(500);
        for (int i = 0; i < nThreads; i++) {
            Future<Double> future = es.submit(new CallableIntegralCalculator(a + delta * i, a + delta * (i + 1), n / nThreads, Math::sin));
            futures.add(future);
        }
        es.shutdown();
        try {
            for (Future<Double> future : futures) {
                result += future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            //e.printStackTrace();
        }
        long finish = System.currentTimeMillis();
        System.out.println("result = " + result);
        System.out.println(finish-start);
    }

    private void run1() {
        double a = 0;
        double b = Math.PI;
        int n = 1000_000_000;
        int nThreads = 50;
        long start = System.currentTimeMillis();
        double delta = (b-a)/nThreads;
        for (int i = 0; i < nThreads; i++) {
            ThreadingIntegralCalculator calculator = new ThreadingIntegralCalculator(a + i * delta, a + (i + 1) * delta, n / nThreads, Math::sin, this);
            new Thread(calculator).start();
        }
        lock.lock();
        try {
            while (finished < nThreads) {
                condition.await();
            }
        } catch (InterruptedException e) {
            // e.printStackTrace(); Guarded suspension
        } finally {
            lock.unlock();
        }
        long finish = System.currentTimeMillis();
        System.out.println("result = " + result);
        System.out.println(finish-start);
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

    public void sendResult1(double v) {
        lock.lock();
        try {
            result += v;
            finished++;
            condition.signal();
        } finally {
            lock.unlock();
        }
    }
}
