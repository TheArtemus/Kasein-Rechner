package com.example.kaseinrechner;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MainActivity extends AppCompatActivity {

    private EditText milkVolumeEditText;
    private RadioGroup anlageRadioGroup;
    private Button calculateButton;
    private TextView resultTextView;

    // Объекты, содержащие времена для каждого типа Anlage
    private final double[] anlageTimes = {20.0 / 60, 22.0 / 60, 42.0 / 60};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        milkVolumeEditText = findViewById(R.id.milkVolumeEditText);
        anlageRadioGroup = findViewById(R.id.anlageRadioGroup);
        calculateButton = findViewById(R.id.calculateButton);
        resultTextView = findViewById(R.id.resultTextView);

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateMilkTime();
            }
        });

        milkVolumeEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    // Скрыть клавиатуру
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }

                    // Вычислить время
                    calculateMilkTime();
                    return true;
                }
                return false;
            }
        });
    }

    private void calculateMilkTime() {
        String milkInput = milkVolumeEditText.getText().toString();

        // Проверяем, ввел ли пользователь количество молока
        if (milkInput.isEmpty()) {
            milkVolumeEditText.setError("Bitte geben Sie eine Zahl ein."); // Вежливое сообщение об ошибке
            return;
        }

        // Преобразуем введённое количество молока в число
        double milch = Double.parseDouble(milkVolumeEditText.getText().toString());
        double ergebnis;

        // Получаем выбранный элемент радиогруппы
        int selectedAnlage = anlageRadioGroup.getCheckedRadioButtonId();

        // Проверяем, выбрана ли радиокнопка
        if (selectedAnlage == -1) {
            // Если нет, показываем сообщение об ошибке
            resultTextView.setText("Bitte wählen Sie eine Option aus!"); // Сообщение об ошибке
            return; // Завершаем метод
        }

        // Определяем индекс выбранной радиокнопки
        int anlageIndex = (selectedAnlage == R.id.anlage1) ? 0 :
                (selectedAnlage == R.id.anlage2) ? 1 : 2;

        // Вычисляем результат
        ergebnis = milch / anlageTimes[anlageIndex];

        // Вычисляем часы и минуты
        int hoursToAdd = (int) Math.floor(ergebnis / 60);
        int minutesToAdd = (int) ergebnis % 60;

        LocalTime currentTime = LocalTime.now();
        LocalTime newTime = currentTime.plusHours(hoursToAdd).plusMinutes(minutesToAdd);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        String result = String.format("Du hast noch %.0f Minuten Milch!\nAktuelle Zeit: %s\nNeue Milchtank Uhrzeit: %s",
                ergebnis, currentTime.format(formatter), newTime.format(formatter)).replace('.', ',');

        resultTextView.setText(result); // Выводим результат
    }
}
