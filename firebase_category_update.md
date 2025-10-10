# Обновление названий категорий в Firebase

## Инструкция по обновлению категорий в Firebase Firestore:

### 1. Войти в Firebase Console
- Перейти на https://console.firebase.google.com/
- Выбрать проект databank

### 2. Перейти в Firestore Database
- В левом меню выбрать "Firestore Database"
- Найти коллекцию "HomeCategory"

### 3. Обновить документы категорий
Найти и обновить следующие документы:

**Документ 1:**
- Поле `name`: изменить на "Учебные"
- Поле `type`: оставить как есть (например, "educational")

**Документ 2:**
- Поле `name`: изменить на "Бытовые" 
- Поле `type`: оставить как есть (например, "household")

**Документ 3:**
- Поле `name`: изменить на "Другие"
- Поле `type`: оставить как есть (например, "other")

### 4. Проверить результат
После обновления в Firebase, категории в приложении будут отображаться как:
- Все (статическая категория)
- Учебные
- Бытовые  
- Другие

### Альтернативный способ через код:
Если нужно добавить код для автоматического обновления, можно использовать:

```java
// В ThemainscreenFragment.java добавить метод для обновления категорий
private void updateCategoryNames() {
    Map<String, Object> updates = new HashMap<>();
    updates.put("name", "Учебные");
    db.collection("HomeCategory").document("educational_id").update(updates);
    
    updates.put("name", "Бытовые");
    db.collection("HomeCategory").document("household_id").update(updates);
    
    updates.put("name", "Другие");
    db.collection("HomeCategory").document("other_id").update(updates);
}
```
