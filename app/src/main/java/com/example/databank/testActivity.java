package com.example.databank;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class testActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // Пример значений переменных
        String category = "электроника"; // Категория из переменной
        String productId = "Ae6c4RVBRLQWpvZLXMAE"; // ID товара из переменной
        String userId = "89225164775"; // ID пользователя из переменной
        int rating = 4; // Общая оценка

        // Получаем текущую дату
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Предположим, что у нас есть словарь с категориями и характеристиками
        Map<String, List<String>> productCategories = new HashMap<>();
        productCategories.put("электроника", createCharacteristicsList(
                "Качество экрана",
                "Производительность",
                "Автономность",
                "Соответствие описанию",
                "Качество звука",
                "Наличие гарантийного обслуживания",
                "Размер и вес",
                "Удобство использования",
                "Наличие обновлений",
                "Энергоэффективность"
        ));

        // Подгружаем характеристики из словаря productCategories для категории "электроника"
        List<String> characteristics = productCategories.get(category);

        // Заполняем оценки для характеристик (эти данные можно будет подтягивать откуда-то)
        Map<String, Object> characteristicRatings = new HashMap<>();
        characteristicRatings.put("Качество экрана", 5);
        characteristicRatings.put("Производительность", 4);
        characteristicRatings.put("Автономность", 3);

        // Добавляем остальные характеристики, если их нет в оценках (по умолчанию можно ставить 0)
        for (String characteristic : characteristics) {
            characteristicRatings.putIfAbsent(characteristic, 0);
        }

        // Формируем данные для записи в Firestore
        Map<String, Object> reviewData = new HashMap<>();
        reviewData.put("rating", rating); // Общая оценка
        reviewData.put("date", currentDate); // Текущая дата
        reviewData.put("characteristicRatings", characteristicRatings); // Оценки по характеристикам

        // Записываем данные в Firestore
        db.collection("ProductCategories")
                .document(category) // Категория (например, "электроника")
                .collection(productId) // ID товара
                .document(userId) // ID пользователя
                .set(reviewData)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Данные успешно добавлены!"))
                .addOnFailureListener(e -> Log.w("Firestore", "Ошибка при добавлении данных", e));
    }
    private List<String> createCharacteristicsList(String... characteristics) {
        List<String> list = new ArrayList<>();
        for (String characteristic : characteristics) {
            list.add(characteristic);
        }
        return list;
    }
}

