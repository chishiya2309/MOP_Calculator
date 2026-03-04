package com.example.mytinh;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    //Hiển thị phép tính
    private TextView tvExpression;

    //Hiển thị kết quả hoặc số đang nhập
    private TextView tvResult;

    private ImageButton btnHistory;

    // Lưu chuỗi số người dùng đang nhập hiện tại
    private String currentNumber = "0";

    // Lưu toán tử hiện tại
    private String operator = "";

    // Lưu giá trị số thứ nhất, NaN dùng để đánh dấu là chưa có giá trị
    private double firstValue = Double.NaN;

    private ArrayList<String> historyList = new ArrayList<>();

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
        btnHistory = findViewById(R.id.btnHistory);
        
        if (tvResult != null) {
            tvResult.setText(currentNumber);
        }

        // Gắn sự kiện cho các nút
        setNumberListeners();
        setOperatorListeners();

        View btnClear = findViewById(R.id.btnClear);
        if (btnClear != null) {
            btnClear.setOnClickListener(v -> {
                currentNumber = "0";
                firstValue = Double.NaN;
                operator = "";
                if (tvResult != null) tvResult.setText("0");
                if (tvExpression != null) {
                    tvExpression.setText("");
                    tvExpression.setVisibility(View.GONE);
                }
            });
        }

        View btnEqual = findViewById(R.id.btnEqual);
        if (btnEqual != null) {
            btnEqual.setOnClickListener(v -> calculate());
        }

        View btnBackspace = findViewById(R.id.btnBackspace);
        if (btnBackspace != null) {
            btnBackspace.setOnClickListener(v -> {
                if (currentNumber != null && !currentNumber.isEmpty() && !currentNumber.equals("0")) {
                    currentNumber = currentNumber.substring(0, currentNumber.length() - 1);
                    if (currentNumber.isEmpty()) {
                        currentNumber = "0";
                    }
                    if (tvResult != null) tvResult.setText(currentNumber);
                }
            });
        }

        View btnDot = findViewById(R.id.btnDot);
        if (btnDot != null) {
            btnDot.setOnClickListener(v -> {
                if (currentNumber.isEmpty() || (operator.isEmpty() && !Double.isNaN(firstValue))){
                    if (operator.isEmpty() && !Double.isNaN(firstValue)) {
                        firstValue = Double.NaN;
                    }
                    currentNumber = "0.";
                    if (tvResult != null) tvResult.setText(currentNumber);
                    return;
                }

                if (!currentNumber.contains(".")){
                    currentNumber += ".";
                    if (tvResult != null) tvResult.setText(currentNumber);
                }
            });
        }

        if (btnHistory != null) {
            btnHistory.setOnClickListener(v -> {
                ArrayList<String> reversedHistory = new ArrayList<>(historyList);
                Collections.reverse(reversedHistory);
                HistoryBottomSheetDialogFragment bottomSheet = HistoryBottomSheetDialogFragment.newInstance(reversedHistory);
                bottomSheet.show(getSupportFragmentManager(), "HistoryBottomSheet");
            });
        }

        setScientificListeners();
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
            if (tvResult != null) tvResult.setText(currentNumber);
        };

        for (int id : numberIds) {
            View v = findViewById(id);
            if (v != null) v.setOnClickListener(listener);
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
            View v = findViewById(id);
            if (v != null) v.setOnClickListener(listener);
        }
    }

    private void updateExpressionWithOperator() {
        if (tvExpression == null) return;
        
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

                String resultString;
                if (result == (long) result) {
                    resultString = String.format("%d", (long) result);
                } else {
                    resultString = String.valueOf(result);
                }

                addHistory(fullExpression.toString() + " = " + resultString);

                fullExpression.append(" =");

                if (tvExpression != null) {
                    tvExpression.setText(fullExpression.toString());
                    tvExpression.setVisibility(View.VISIBLE);
                }

                if (tvResult != null) {
                    if (result == (long) result) {
                        tvResult.setText(String.format("%d", (long) result));
                    } else {
                        tvResult.setText(String.valueOf(result));
                    }
                }

                // Lưu kết quả vào firstValue để dùng tiếp
                firstValue = result;
                currentNumber = "";
                operator = "";
            } catch (ArithmeticException e) {
                if (tvResult != null) tvResult.setText("Error");
                if (tvExpression != null) {
                    tvExpression.setText("");
                    tvExpression.setVisibility(View.GONE);
                }
                firstValue = Double.NaN;
                currentNumber = "0";
                operator = "";
            }
        }
    }

    private void addHistory(String entry) {
        historyList.add(entry);
        if (historyList.size() > 10) {
            historyList.remove(0);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("CURRENT_NUMBER", currentNumber);
        outState.putString("OPERATOR", operator);
        outState.putDouble("FIRST_VALUE", firstValue);
        outState.putStringArrayList("HISTORY", historyList);

        if (tvExpression != null) {
            outState.putString("EXPRESSION_TEXT", tvExpression.getText().toString());
            outState.putInt("EXPRESSION_VISIBILITY", tvExpression.getVisibility());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Phục hồi lại dữ liệu
        currentNumber = savedInstanceState.getString("CURRENT_NUMBER", "0");
        operator = savedInstanceState.getString("OPERATOR", "");
        firstValue = savedInstanceState.getDouble("FIRST_VALUE", Double.NaN);

        historyList = savedInstanceState.getStringArrayList("HISTORY");
        if (historyList == null) {
            historyList = new ArrayList<>();
        }

        // Cập nhật lại UI ngay lập tức
        if (tvResult != null) {
            if (currentNumber.isEmpty() && !Double.isNaN(firstValue)) {
                if (firstValue == (long) firstValue) {
                    tvResult.setText(String.format("%d", (long) firstValue));
                } else {
                    tvResult.setText(String.valueOf(firstValue));
                }
            } else {
                tvResult.setText(currentNumber.isEmpty() ? "0" : currentNumber);
            }
        }
        if (tvExpression != null) {
            tvExpression.setText(savedInstanceState.getString("EXPRESSION_TEXT", ""));
            tvExpression.setVisibility(savedInstanceState.getInt("EXPRESSION_VISIBILITY", View.GONE));
        }
    }

    private void setScientificListeners() {
        // --- PHÉP TOÁN 1 NGÔI ---
        // Thay btnOpenParen/Close bằng btnLog và btnExp
        int[] unaryIds = { R.id.btnSin, R.id.btnCos, R.id.btnTan, R.id.btnSqrt, R.id.btnSquare, R.id.btnLog, R.id.btnExp };

        View.OnClickListener unaryListener = v -> {
            Button b = (Button) v;
            String op = b.getText().toString();

            if (currentNumber.isEmpty() || currentNumber.equals(".")) {
                if (!Double.isNaN(firstValue)) {

                    currentNumber = String.valueOf(firstValue);
                } else {
                    currentNumber = "0";
                }
            }

            try {
                double value = Double.parseDouble(currentNumber);
                double result = calculatorLogic.calculateUnary(value, op);

                if (result == (long) result) {
                    currentNumber = String.format("%d", (long) result);
                } else {
                    currentNumber = String.valueOf(result);
                }
                if (tvResult != null) tvResult.setText(currentNumber);

                if (tvExpression != null) {
                    tvExpression.setText(op + "(" + value + ") = " + currentNumber);
                    tvExpression.setVisibility(View.VISIBLE);
                }

                // Cập nhật lại firstValue để tính tiếp nếu muốn
                firstValue = result;

            } catch (ArithmeticException e) {
                if (tvResult != null) tvResult.setText("Error");
                currentNumber = "";
                firstValue = Double.NaN;
            } catch (Exception e) {
                if (tvResult != null) tvResult.setText("Invalid");
                currentNumber = "";
            }
        };

        for (int id : unaryIds) {
            Button btn = findViewById(id);
            if (btn != null) btn.setOnClickListener(unaryListener);
        }

        // --- HẰNG SỐ (PI) ---
        Button btnPi = findViewById(R.id.btnPi);
        if (btnPi != null) {
            btnPi.setOnClickListener(v -> {
                if (currentNumber.isEmpty() || currentNumber.equals("0")) {
                    // Trường hợp chưa nhập gì, hoặc vừa bấm phép tính (+, -...)
                    currentNumber = String.valueOf(Math.PI);
                } else {
                    // Trường hợp đang có số (vd 6), ta lấy số đó nhân Pi
                    try {
                        double val = Double.parseDouble(currentNumber);
                        currentNumber = String.valueOf(val * Math.PI);
                    } catch (Exception e) {
                        currentNumber = String.valueOf(Math.PI);
                    }
                }
                if (tvResult != null) tvResult.setText(currentNumber);
            });
        }

        // --- LŨY THỪA (^) ---
        Button btnPow = findViewById(R.id.btnPow);
        if (btnPow != null) {
            btnPow.setOnClickListener(v -> {
                String selectedOperator = "^";
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
                    operator = selectedOperator;
                    updateExpressionWithOperator();
                }
            });
        }
    }

}
