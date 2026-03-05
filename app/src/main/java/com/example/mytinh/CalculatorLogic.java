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
            case "^":
                return Math.pow(first, second);
            default:
                return second;

        }
    }

    // Xử lý các phép toán 1 ngôi (sin, cos, tan, √, x²)
    public double calculateUnary(double value, String operator) throws ArithmeticException {
        double result = 0;
        // Sai số epsilon để làm tròn các kết quả lượng giác (ví dụ sin 180 = 0)
        double epsilon = 1e-10;

        switch (operator) {
            case "sin":
                result = Math.sin(Math.toRadians(value));
                return Math.abs(result) < epsilon ? 0.0 : result;

            case "cos":
                result = Math.cos(Math.toRadians(value));
                return Math.abs(result) < epsilon ? 0.0 : result;

            case "tan":
                // Bắt lỗi tan(90), tan(270) vì nó tiến tới vô cực
                if (Math.abs(value % 180) == 90) {
                    throw new ArithmeticException("Tan undefined");
                }
                result = Math.tan(Math.toRadians(value));
                return Math.abs(result) < epsilon ? 0.0 : result;

            case "√":
                // Bắt lỗi người dùng nhập số âm rồi bấm căn
                if (value < 0) {
                    throw new ArithmeticException("Negative Square Root");
                }
                return Math.sqrt(value);

            case "x²":
                return value * value;
            case "log":
                if (value <= 0) throw new ArithmeticException("Log undefined");
                return Math.log10(value);
            case "exp":
                return Math.exp(value);
            default:
                return value;
        }
    }
}

