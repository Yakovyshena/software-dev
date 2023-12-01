package com.mobileapp.mobilelaba1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private EditText coefficientsEditText;
    private EditText epsilonEditText;
    private TextView resultTextView;
    private TextView iterationsTextView;
    private TextView precisionTextView;
    private EditText lowerBoundEditText;
    private EditText upperBoundEditText;
    private RadioGroup methodRadioGroup;
    private int iterationCount1 = 0;
    private int iterationCount2 = 0;
    private int iterationCount3 = 0;
    private String textDivision = "Перше число - значення аргументу, друге - значення функції. \n Division in half method:\n";
    public static final String FILE_NAME = "data.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lowerBoundEditText = findViewById(R.id.lowerBoundEditText);
        upperBoundEditText = findViewById(R.id.upperBoundEditText);
        coefficientsEditText = findViewById(R.id.coefficientsEditText);
        epsilonEditText = findViewById(R.id.epsilonEditText);
        resultTextView = findViewById(R.id.resultTextView);
        iterationsTextView = findViewById(R.id.iterationsTextView);
        precisionTextView = findViewById(R.id.precisionTextView);
        methodRadioGroup = findViewById(R.id.methodRadioGroup);
    }

    public void solveEquation(View view) {
        try {
            // Отримуємо введені дані
            String coefficientsStr = coefficientsEditText.getText().toString();
            double epsilon = Double.parseDouble(epsilonEditText.getText().toString());
            double lowerBound = Double.parseDouble(lowerBoundEditText.getText().toString());
            double upperBound = Double.parseDouble(upperBoundEditText.getText().toString());

            // Розділяємо введені коефіцієнти многочлена
            String[] coefficientsArray = coefficientsStr.split(",");
            double[] coefficients = new double[coefficientsArray.length];

            for (int i = 0; i < coefficientsArray.length; i++) {
                coefficients[i] = Double.parseDouble(coefficientsArray[i].trim());
            }


            double root = 0;
            // Визначте, який RadioButton був обраний
            RadioButton selectedRadioButton = findViewById(methodRadioGroup.getCheckedRadioButtonId());

            // Викликаємо відповідний метод в залежності від обраного RadioButton
            if (selectedRadioButton.getId() == R.id.radioButton1) {
                root = divisionInHalf(lowerBound, upperBound, epsilon, coefficients);
                save(textDivision);
                textDivision = "Перше число - значення аргументу, друге - значення функції. \n Division in half method:\n";
                resultTextView.setText("Корінь (Метод ділення навпіл): " + root);
                iterationsTextView.setText("Кількість ітерацій: " + iterationCount1);
                iterationCount1 = 0;
            } else if (selectedRadioButton.getId() == R.id.radioButton2) {
                root = newtonMethod(lowerBound, upperBound, epsilon, coefficients);
                resultTextView.setText("Корінь (Метод Ньютона): " + root);
                iterationsTextView.setText("Кількість ітерацій: " + iterationCount2);
                iterationCount2 = 0;
            } else if (selectedRadioButton.getId() == R.id.radioButton3) {
                root = newtonMethodModified(lowerBound, upperBound, epsilon, coefficients);
                resultTextView.setText("Корінь (Модифікований метод Ньютона): " + root);
                iterationsTextView.setText("Кількість ітерацій: " + iterationCount3);
                iterationCount3 = 0;
            }
            // Виводимо результат
            precisionTextView.setText("Точність: " + epsilon);
        } catch (Exception e) {
            resultTextView.setText("Помилка: " + e.getMessage());
        }
    }


    public double evaluatePolynomial(double[] coefficients, double x) {
        double result = 0;
        for (int i = 0; i < coefficients.length; i++) {
            result += coefficients[i] * Math.pow(x, coefficients.length - 1 - i);
        }
        return result;
    }

    public double divisionInHalf(double a, double b, double epsilon, double[] coefficients) {

        iterationCount1++;
        double x = (a + b) / 2;
        double f_x = evaluatePolynomial(coefficients, x);
        double f_a = evaluatePolynomial(coefficients, a);
        double f_b = evaluatePolynomial(coefficients, b);
        double a_new;
        double b_new;
        if (Math.signum(f_a) == Math.signum(f_x)) {
            a_new = x;
        } else {
            a_new = a;
        }
        if (Math.signum(f_b) == Math.signum(f_x)) {
            b_new = x;
        } else {
            b_new = b;
        }
        double x_new = (a_new + b_new) / 2;
        String data = x + "\t" + f_x + "\n";
        textDivision += data;
        if (Math.abs(x_new - x) <= 2 * epsilon) {
            return (x_new + x) / 2;
        } else {
            return divisionInHalf(a_new, b_new, epsilon, coefficients);
        }
    }
    public double evaluatePolynomialDerivative(double[] coefficients, double x) {
        double derivative = 0.0;
        int n = coefficients.length; // Найвищий степінь многочлена

        for (int i = 0; i < n - 1; i++) {
            derivative *= x;
            derivative += (n - i - 1) * coefficients[i];
        }

        return derivative;
    }
    public double newtonMethod(double lowerBound, double upperBound, double epsilon, double[] coefficients) throws IOException {

        double x = (lowerBound + upperBound) / 2; // Початкове наближення в середині інтервалу
        double f_x = evaluatePolynomial(coefficients, x);
        double f_prime_x = evaluatePolynomialDerivative(coefficients, x);
        double x_prew = 0;
        String text = "Перше число - значення аргументу, друге - значення функції. \n Newton method:\n" + x + f_x + "\n";
        while (Math.abs(x - x_prew) > epsilon) {
            iterationCount2++;
            x_prew = x;
            x = x - f_x / f_prime_x;
            f_x = evaluatePolynomial(coefficients, x);
            String data = x + "\t" + f_x + "\n";
            text += data;
            f_prime_x = evaluatePolynomialDerivative(coefficients, x);
        }
        save(text);
        return x;
    }

    public double newtonMethodModified(double lowerBound, double upperBound, double epsilon, double[] coefficients) {
        double x = (lowerBound + upperBound) / 2; // Початкове наближення в середині інтервалу
        double f_x = evaluatePolynomial(coefficients, x);
        double f_prime_x_0 = evaluatePolynomialDerivative(coefficients, x);
        double x_prew = 0;
        String text = "Перше число - значення аргументу, друге - значення функції. \n Modified Newton method:\n" + x + f_x + "\n";
        while (Math.abs(x - x_prew) > epsilon) {
            iterationCount3++;
            x_prew = x;
            x = x - f_x / f_prime_x_0;
            f_x = evaluatePolynomial(coefficients, x);
            String data = x + "\t" + f_x + "\n";
            text += data;
        }
        save(text);
        return x;
    }

    public void save(String line) {
        FileOutputStream fos = null;

        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(line.getBytes());
            Toast.makeText(this, "Saved to " + getFilesDir() + "/" + FILE_NAME, Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void openGraphActivity(View view) {
        Intent intent = new Intent(this, GraphActivity.class);
        startActivity(intent);
    }

    public void openAuthorActivity(View view) {
        Intent intent = new Intent(this, AuthorActivity.class);
        startActivity(intent);
    }
}