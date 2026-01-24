package com.example.mytinh;

public class CalculatorLogic {

    public double calculate(double first, double second, String operator) throws ArithmeticException {
        switch (operator) {
            case "+":
                return first + second;
            case "-":
                return first - second;
            case "x":
                return first * second;
            case "/":
                if (second == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                return first / second;
            default:
                return second; // or throw exception
        }
    }
}

