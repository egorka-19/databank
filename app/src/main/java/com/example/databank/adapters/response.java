package com.example.databank.adapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.databank.R;
import com.example.databank.UI.Users.Reviews_end_Activity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class response extends RecyclerView.Adapter<response.MyViewHolder> {

    private List<String> dataList;
    private Map<String, List<String>> productCategories;
    public String category, phone;
    private Random random = new Random();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public response(List<String> dataList, Map<String, List<String>> productCategories) {
        this.dataList = dataList;
        this.productCategories = productCategories;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_for_response, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        category = dataList.get(0);// Извлечь категорию из предыдущего экрана
        String productId = "Ae6c4RVBRLQWpvZLXMAE"; // извлечь ID товара из переменной
        String userId = "89828249233"; // извлечь ID пользователя из переменной

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
                "Энергоэффективность"));

        productCategories.put("бытовая техника", createCharacteristicsList(
                "Мощность",
                "Уровень шума",
                "Энергоэффективность",
                "Качество сборки",
                "Удобство управления",
                "Надежность",
                "Вместимость",
                "Наличие дополнительных функций",
                "Гарантийный срок",
                "Дизайн"));

        productCategories.put("ремонт и строительство", createCharacteristicsList(
                "Качество материала",
                "Устойчивость к воздействию окружающей среды",
                "Надежность",
                "Легкость в установке",
                "Сравнение цен",
                "Долговечность",
                "Эстетические характеристики",
                "Безопасность использования",
                "Наличие сертификатов",
                "Экологичность"));

        productCategories.put("одежда", createCharacteristicsList(
                "Качество материала",
                "Комфорт",
                "Стиль",
                "Сезонность",
                "Размерный ряд",
                "Цветовая палитра",
                "Устойчивость к износу",
                "Наличие подкладки",
                "Легкость ухода",
                "Соответствие размеру"));

        productCategories.put("красота", createCharacteristicsList(
                "Качество ингредиентов",
                "Удобство применения",
                "Эффективность действия",
                "Наличие аллергических реакций",
                "Дизайн упаковки",
                "Срок хранения",
                "Аромат",
                "Наличие сертификатов",
                "Безопасность для кожи",
                "Эко-дружественность"));

        productCategories.put("автотовары", createCharacteristicsList(
                "Качество материалов",
                "Легкость установки",
                "Совместимость с маркой/моделью",
                "Безопасность",
                "Энергоэффективность",
                "Надежность",
                "Гарантийный срок",
                "Производительность",
                "Устойчивость к износу",
                "Общая стоимость эксплуатации"));

        productCategories.put("детские товары", createCharacteristicsList(
                "Безопасность материалов",
                "Возрастные ограничения",
                "Удобство использования",
                "Эстетичность",
                "Качество сборки",
                "Наличие сертификатов",
                "Возможность стирки/чистки",
                "Стереоэффекты (в игрушках)",
                "Наличие гарантийного срока",
                "Развивающая функция"));

        productCategories.put("творчество", createCharacteristicsList(
                "Качество материалов",
                "Удобство работы",
                "Разнообразие цветов",
                "Эко-френдли материалы",
                "Кreativность",
                "Способности к смешиванию",
                "Обратная отзывчивость",
                "Степень сложности",
                "Объем/количество в упаковке",
                "Долговечность"));

        productCategories.put("здоровье", createCharacteristicsList(
                "Качество ингредиентов",
                "Удобство применения",
                "Эффективность",
                "Наличие побочных эффектов",
                "Наличие сертификата",
                "Способ применения",
                "Срок годности",
                "Дозировка",
                "Рекомендации по применению",
                "Цена"));

        productCategories.put("спорт и отдых", createCharacteristicsList(
                "Качество материалов",
                "Удобство использования",
                "Функциональность",
                "Вес",
                "Компактность",
                "Наличие дополнительных функций",
                "Устойчивость к воздействию",
                "Энергоэффективность",
                "Гарантия качества",
                "Разделы и конструкции"));

        productCategories.put("продукты питания", createCharacteristicsList(
                "Срок годности",
                "Качество ингредиентов",
                "Пищевая ценность",
                "Упаковка",
                "Состав",
                "Наличие аллергенов",
                "Место производства",
                "Эко-дружественность",
                "Дополнительные добавки",
                "Смесь вкусов"));

        productCategories.put("книги", createCharacteristicsList(
                "Качество печати",
                "Издание",
                "Тематика",
                "Удобство чтения",
                "Объем",
                "Цена",
                "Автор",
                "Издательство",
                "Обложка",
                "Иллюстрации"));

        List<String> characteristics = productCategories.get(category);

        Collections.shuffle(characteristics);
        List<String> result = new ArrayList<>();
        // Добавляем элементы в результат, пока не будет собрано нужное количество
        int index = 0;
        while (result.size() <= 3) {

            if (index >= characteristics.size()) {
                index = 0;
            }
            result.add(characteristics.get(index));
            index++;
        }

        String characteristic1 = result.get(0);
        String characteristic2 = result.get(1);
        String characteristic3 = result.get(2);

        // Установка заголовков для SeekBar
        holder.seekbar1Title.setText(characteristic1);
        holder.seekbar2Title.setText(characteristic2);
        holder.seekbar3Title.setText(characteristic3);

        // Логика для обработки нажатия на кнопку отправки
        holder.sendBtn.setOnClickListener(v -> {
            // Получение значений прогресса из SeekBar'ов
            int rating1 = holder.seekBar1.getProgress();
            int rating2 = holder.seekBar2.getProgress();
            int rating3 = holder.seekBar3.getProgress();

            // Пример вывода или отправки данных в базу
            //Toast.makeText(holder.itemView.getContext(), "Данные отправлены: " + ratings.toString(), Toast.LENGTH_SHORT).show();

            // отправка этих данных в базу данных
            float rating = holder.rating.getRating(); // извлечь
            int rat = (int) rating;
            // Получаем текущую дату
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            Map<String, Object> characteristicRatings = new HashMap<>();
            characteristicRatings.put(characteristic1, rating1 + 1);
            characteristicRatings.put(characteristic2, rating2 + 1);
            characteristicRatings.put(characteristic3, rating3 + 1);

            // Добавляем остальные характеристики, если их нет в оценках (по умолчанию можно ставить 0)
            for (String characteristic : characteristics) {
                if (!(characteristicRatings.containsKey(characteristic))){
                    characteristicRatings.putIfAbsent(characteristic, 0);
                }

            }
            Intent intent = new Intent(holder.itemView.getContext(), Reviews_end_Activity.class);
            intent.putExtra("rating", rat);
            intent.putExtra("rating1", rating1 + 1);
            intent.putExtra("rating2", rating2 + 1);
            intent.putExtra("rating3", rating3 + 1);
            intent.putExtra("category", category);
            intent.putExtra("characteristic1", characteristic1);
            intent.putExtra("characteristic2", characteristic2);
            intent.putExtra("characteristic3", characteristic3);
            holder.itemView.getContext().startActivity(intent);


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

        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    // Функция для получения случайной характеристики
    private String getRandomCharacteristic(List<String> characteristics) {
        return characteristics.get(random.nextInt(characteristics.size()));
    }

    private List<String> createCharacteristicsList(String... characteristics) {
        List<String> list = new ArrayList<>();
        for (String characteristic : characteristics) {
            list.add(characteristic);
        }
        return list;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView seekbar1Title, seekbar2Title, seekbar3Title;
        SeekBar seekBar1, seekBar2, seekBar3;
        Switch switchElement;
        RatingBar rating;

        ImageButton sendBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            seekbar1Title = itemView.findViewById(R.id.seekbar1_title);
            seekbar2Title = itemView.findViewById(R.id.seekbar2_title);
            seekbar3Title = itemView.findViewById(R.id.seekbar3_title);
            seekBar1 = itemView.findViewById(R.id.seekBar1);
            seekBar2 = itemView.findViewById(R.id.seekBar2);
            seekBar3 = itemView.findViewById(R.id.seekBar3);
            switchElement = itemView.findViewById(R.id.switch_element);
            sendBtn = itemView.findViewById(R.id.send_btn);
            rating = itemView.findViewById(R.id.ratingBar);
        }
    }
}
