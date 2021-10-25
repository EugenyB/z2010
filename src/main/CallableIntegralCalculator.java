package main;

import java.util.concurrent.Callable;
import java.util.function.DoubleUnaryOperator;

public class CallableIntegralCalculator implements Callable<Double> {
    private IntegralCalculator calculator;

    public CallableIntegralCalculator(double a, double b, int n, DoubleUnaryOperator f) {
        calculator = new IntegralCalculator(a, b, n, f);
    }

    @Override
    public Double call() throws Exception {
        return calculator.calculate();
    }
}
