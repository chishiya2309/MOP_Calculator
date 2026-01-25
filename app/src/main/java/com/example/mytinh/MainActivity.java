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
    private String currentNumber = "";

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

        // Gắn sự kiện cho các nút
        setNumberListeners();
        setOperatorListeners();

        findViewById(R.id.btnClear).setOnClickListener(v -> {
            currentNumber = "";
            firstValue = Double.NaN;
            operator = "";
            tvResult.setText("0");
            tvExpression.setText("");
            tvExpression.setVisibility(View.GONE);
        });

        findViewById(R.id.btnEqual).setOnClickListener(v -> calculate());

        findViewById(R.id.btnBackspace).setOnClickListener(v -> {
            if (currentNumber != null && !currentNumber.isEmpty()) {
                currentNumber = currentNumber.substring(0, currentNumber.length() - 1);
                if (currentNumber.isEmpty()) {
                    tvResult.setText("0");
                } else {
                    tvResult.setText(currentNumber);
                }
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
            currentNumber += b.getText().toString();
            tvResult.setText(currentNumber);
        };

        for (int id : numberIds) {
            findViewById(id).setOnClickListener(listener);
        }
    }

    private void setOperatorListeners() {
        int[] operatorIds = { R.id.btnAdd, R.id.btnSubtract, R.id.btnMultiply, R.id.btnDivide };

        View.OnClickListener listener = v -> {
            if (!currentNumber.isEmpty()) {
                if (!Double.isNaN(firstValue)) {
                    calculate();
                } else {
                    firstValue = Double.parseDouble(currentNumber);
                }
                operator = ((Button) v).getText().toString();
                currentNumber = "";

                updateExpressionWithOperator();
            }
        };

        for (int id : operatorIds) {
            findViewById(id).setOnClickListener(listener);
        }
    }

    private void updateExpressionWithOperator() {
        StringBuilder expression = new StringBuilder();

        // Hiển thị số thứ nhất + toán tử
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

                // Hiển thị phép tính đầy đủ với trường hợp nhấn dấu bằng
                StringBuilder fullExpression = new StringBuilder();

                // Số thứ nhất
                if (firstValue == (long) firstValue) {
                    fullExpression.append(String.format("%d", (long) firstValue));
                } else {
                    fullExpression.append(String.valueOf(firstValue));
                }


                fullExpression.append(" ").append(operator).append(" ");

                // Số thứ hai
                if (secondValue == (long) secondValue) {
                    fullExpression.append(String.format("%d", (long) secondValue));
                } else {
                    fullExpression.append(String.valueOf(secondValue));
                }

                // Dấu bằng
                fullExpression.append(" =");

                tvExpression.setText(fullExpression.toString());
                tvExpression.setVisibility(View.VISIBLE);

                // Hiển thị kết quả
                if (result == (long) result) {
                    tvResult.setText(String.format("%d", (long) result));
                } else {
                    tvResult.setText(String.valueOf(result));
                }

                firstValue = result;
                currentNumber = "";
                operator = "";
            }catch (ArithmeticException e) {
                tvResult.setText("Error");
                tvExpression.setText("");
                tvExpression.setVisibility(View.GONE);
                firstValue = Double.NaN;
                currentNumber = "";
                operator = "";
            }
        }
    }
}