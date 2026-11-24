# Регресійні дефекти v2 (S5-10)

**Дата:** 24.11.2026
**Виконавець:** Ромашко (ІТ)
**Рецензент:** Білогривий

У результаті порівняння Surefire v1/v2 (S5-05) виявлено **4 нові регресійні дефекти**. 
Тести, які проходили на v1 (Passed), почали падати на v2 (Failed / Error). Це означає, що розробники під час виправлення старих дефектів зламали робочу логіку.

Усі дефекти зареєстровано у GitHub Issues. Кожен отримав мітку `regression` та має заголовок із префіксом `[регресія v2]`.

---

## 1. NPE при перевірці ключа курсу
- **Issue:** #62 
- **Тест:** `EnrollmentValidatorBl7xTest$KeyMatches.courseKeyNotSet`
- **Результат v1:** Passed | **Результат v2:** Error (NullPointerException)
- **Severity:** Critical | **Priority:** P1
- **Порушена вимога:** BL-72
- **Причина:** У v2 видалено перевірку `if (course == null || course.enrolmentKey() == null)`. 
- **Фактичний результат:** Виникає NPE замість повернення `false`.

## 2. Відсутня валідація місткості курсу
- **Issue:** #63
- **Тест:** `EnrollmentValidatorBl7xTest$HasCapacity.invalid` (3 параметризовані рядки)
- **Результат v1:** Passed | **Результат v2:** Failed
- **Severity:** Major | **Priority:** P2
- **Порушена вимога:** BL-76
- **Причина:** Видалено перевірку `capacity <= 0 || enrolled < 0`.
- **Фактичний результат:** Метод не кидає `IllegalArgumentException`.

## 3. Зламано межу складання пререквізиту (60.0)
- **Issue:** #64
- **Тест:** `EnrollmentValidatorBl7xTest$PrerequisiteMet.prerequisite`
- **Результат v1:** Passed | **Результат v2:** Failed
- **Severity:** Major | **Priority:** P2
- **Порушена вимога:** BL-70
- **Причина:** Змінено умову `grade >= PREREQUISITE_PASS` на `grade > PREREQUISITE_PASS`.
- **Фактичний результат:** Студент із балом рівно 60.0 не проходить перевірку (`expected: <true> but was: <false>`).

## 4. Відсутня перевірка ID відповіді в ключі тесту
- **Issue:** #65
- **Тест:** `QuizScorerScoreTest.answerIdNotInKeyThrows`
- **Результат v1:** Passed | **Результат v2:** Failed
- **Severity:** Major | **Priority:** P2
- **Порушена вимога:** BL-60
- **Причина:** Видалено блок валідації `keyIds.contains(ansId)`.
- **Фактичний результат:** Метод не кидає `IllegalArgumentException`, а мовчки ігнорує зайвий ID.

---

## Висновок для звіту
Виявлені регресії блокують критерій **К4** (Відкриті регресійні дефекти = 0). 
Рішення про випуск v2 без виправлення цих дефектів буде **no-go**, оскільки порушено безумовний критерій готовності.
