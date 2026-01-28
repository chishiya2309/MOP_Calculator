package com.example.mytinh;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    //Hiển thị phép tính
    private TextView tvExpression;

    //Hiển thị kết quả hoặc số đang nhập
    private TextView tvResult;

    // Lưu chuỗi số người dùng đang nhập hiện tại
    private String currentNumber = "0";

    // Lưu toán tử hiện tại
    private String operator = "";

    // Lưu giá trị số thứ nhất, NaN dùng để đánh dấu là chưa có giá trị
    private double firstValue = Double.NaN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Chế độ hiển thị tràn viền
        EdgeToEdge.enable(this);
        // Gắn file giao diện XML vào code Java
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Gán TextView từ ID trong layout
        tvExpression = findViewById(R.id.tvExpression);
        tvResult = findViewById(R.id.tvResult);
        tvResult.setText(currentNumber);

        // Gắn sự kiện cho các nút
        setNumberListeners();
        setOperatorListeners();

        findViewById(R.id.btnClear).setOnClickListener(v -> {
            currentNumber = "0";
            firstValue = Double.NaN;
            operator = "";
            tvResult.setText("0");
            tvExpression.setText("");
            tvExpression.setVisibility(View.GONE);
        });

        findViewById(R.id.btnEqual).setOnClickListener(v -> calculate());

        findViewById(R.id.btnBackspace).setOnClickListener(v -> {
            if (currentNumber != null && !currentNumber.isEmpty() && !currentNumber.equals("0")) {
                currentNumber = currentNumber.substring(0, currentNumber.length() - 1);
                if (currentNumber.isEmpty()) {
                    currentNumber = "0";
                }
                tvResult.setText(currentNumber);
            }
        });

        findViewById(R.id.btnDot).setOnClickListener(v -> {
            if (currentNumber.isEmpty() || (operator.isEmpty() && !Double.isNaN(firstValue))){
                if (operator.isEmpty() && !Double.isNaN(firstValue)) {
                    firstValue = Double.NaN;
                }
                currentNumber = "0.";
                tvResult.setText(currentNumber);
                return;
            }

            if (!currentNumber.contains(".")){
                currentNumber += ".";
                tvResult.setText(currentNumber);
            }
        });
    }

    private void setNumberListeners() {
        int[] numberIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        };

        View.OnClickListener listener = v -> {
            Button b = (Button) v;
            String digit = b.getText().toString();

            // Nếu đang là "0" hoặc vừa có kết quả từ phép tính trước
            if (currentNumber.equals("0") || (currentNumber.isEmpty() && operator.isEmpty())) {
                if (operator.isEmpty() && !Double.isNaN(firstValue)) {
                    firstValue = Double.NaN;
                }
                currentNumber = digit;
            } else {
                currentNumber += digit;
            }
            tvResult.setText(currentNumber);
        };

        for (int id : numberIds) {
            findViewById(id).setOnClickListener(listener);
        }
    }

    private void setOperatorListeners() {
        int[] operatorIds = { R.id.btnAdd, R.id.btnSubtract, R.id.btnMultiply, R.id.btnDivide };

        View.OnClickListener listener = v -> {
            String selectedOperator = ((Button) v).getText().toString();
            if (!currentNumber.isEmpty()) {
                if (!Double.isNaN(firstValue)) {
                    calculate();
                } else {
                    firstValue = Double.parseDouble(currentNumber);
                }
                operator = selectedOperator;
                currentNumber = "";
                updateExpressionWithOperator();
            } else if (!Double.isNaN(firstValue)) {
                // Sử dụng kết quả vừa tính được để thực hiện phép tính mới
                operator = selectedOperator;
                updateExpressionWithOperator();
            }
        };

        for (int id : operatorIds) {
            findViewById(id).setOnClickListener(listener);
        }
    }

    private void updateExpressionWithOperator() {
        StringBuilder expression = new StringBuilder();

        if(!Double.isNaN(firstValue)) {
            if(firstValue == (long) firstValue) {
                expression.append(String.format("%d", (long) firstValue));
            } else {
                expression.append(String.valueOf(firstValue));
            }
        }

        if(!operator.isEmpty()) {
            expression.append(" ").append(operator).append(" ");
        }

        tvExpression.setText(expression.toString());
        tvExpression.setVisibility(View.VISIBLE);
    }

    private CalculatorLogic calculatorLogic = new CalculatorLogic();

    private void calculate() {
        if(!Double.isNaN(firstValue) && !currentNumber.isEmpty()) {
            double secondValue = Double.parseDouble(currentNumber);
            double result = 0;

            try {
                result = calculatorLogic.calculate(firstValue, secondValue, operator);

                StringBuilder fullExpression = new StringBuilder();
                if (firstValue == (long) firstValue) {
                    fullExpression.append(String.format("%d", (long) firstValue));
                } else {
                    fullExpression.append(String.valueOf(firstValue));
                }

                fullExpression.append(" ").append(operator).append(" ");

                if (secondValue == (long) secondValue) {
                    fullExpression.append(String.format("%d", (long) secondValue));
                } else {
                    fullExpression.append(String.valueOf(secondValue));
                }
                fullExpression.append(" =");

                tvExpression.setText(fullExpression.toString());
                tvExpression.setVisibility(View.VISIBLE);

                if (result == (long) result) {
                    tvResult.setText(String.format("%d", (long) result));
                } else {
                    tvResult.setText(String.valueOf(result));
                }

                // Lưu kết quả vào firstValue để dùng tiếp
                firstValue = result;
                currentNumber = "";
                operator = "";
            } catch (ArithmeticException e) {
                tvResult.setText("Error");
                tvExpression.setText("");
                tvExpression.setVisibility(View.GONE);
                firstValue = Double.NaN;
                currentNumber = "0";
                operator = "";
            }
        }
    }
}
