# surefire-summary.md — Протокол запуску автотестів, Спринт 4
 
Автор: Чупай (ІТ) · Продукт: «ЕдуПортал» · Модуль: `ua.kpi.tzyapz.eduportal` v1.0.0
Рецензент: Мележик (КТ)
 
> Статус: чернетка від 05.11.2026. Рядки з «…» заповнюються після остаточного прогону `mvn clean test` на чистому `main`, коли всі автори змерджать свої тести.
 
## 1. Умови запуску
 
| Параметр | Значення |
|---|---|
| Дата і час запуску | 05.11.2026, 11:11 (юніт-тести), 11:30 (UI-01) |
| Коміт / тег | `v1-sprint4`, коміт — після остаточного прогону |
| Команда | `mvn clean test` (юніт-тести); `mvn test -DexcludedGroups=none -Dtest=LoginUiTest` (UI) |
| JDK / Maven / ОС | Temurin 17.0.21 / Maven 3.9.9 / Windows |
| UI-тести | вимкнені за замовчуванням (тег `ui`), потрібні Chrome та інтернет |
 
## 2. Результати за класами
 
Числа беруться з рядків `Tests run: ... in <клас>` у виводі Maven (або з `qa-eduportal/target/surefire-reports/*.txt`).
 
| Клас | Автор | Tests run | Failures | Errors |
|---|---|---|---|---|
| GradeCalculatorTest (BL-10) | Чупай | 14 | 0 | 0 |
| GradeCalculatorTest (BL-20, BL-30) | Ромашко | 40 | 10 | 0 |
| QuizScorerTest (BL-40, BL-50) | Александрова | … | 2 | … |
| QuizScorerTest (BL-60), EnrollmentValidatorTest (BL-72…BL-78) | Білогривий | … | … | … |
| EnrollmentValidatorTest (BL-70, BL-80) | Мележик | … | … | … |
| **Разом** |  | … | … | … |
 
За прогоном 05.11.2026 о 11:11 (без тестів Білогривого й Мележика): Tests run: 68, Failures: 12, Errors: 0, Skipped: 0.
 
## 3. Тести, що впали
 
| Тест | Вимога | Очікувано (SRS 1.1) | Отримано | Issue |
|---|---|---|---|---|
| `GradeCalculatorTest$EctsLetter.lowerBoundOfAIsInclusive` | BL-20 | літера A від 95 включно | B | # |
| `GradeCalculatorTest$LatePenalty.penaltyIsCappedAtFiftyPercent` (5 рядків даних) | BL-30 | штраф не більше −50 % (наприклад, 50,0 для 100 балів) | 59,05; 47,24; 29,52 | # |
| `GradeCalculatorTest$LatePenalty.penaltyIsLinearNotCompound` (4 рядки даних) | BL-30 | −10 % від балу за кожен день (наприклад, 64,0 для 80 і 2 днів) | 64,8; 52,49; 72,9; 43,74 | # |
| `QuizScorerTest$MultipleCorrect.scoreIsNeverNegative` (2 рядки даних) | BL-50 | бал не менше 0 | −0,17; −1,0 | # |
 
Усього 12 червоних тестів (рядків даних), 4 дефекти модуля v1. Червоні тести не вимкнено: кожен відповідає дефекту і посилається на Issue.
 
## 4. UI-сценарії
 
| Сценарій | Статус | Примітка |
|---|---|---|
| UI-01 `LoginUiTest` (TC-001, F-10) | failed | Після входу `student` відкривається головна сторінка `/`, а не панель `/my/`. Відомий дефект demo #1 зі спринта 3, новий Issue не створюється. Запуск 05.11.2026, 11:30 |
 
