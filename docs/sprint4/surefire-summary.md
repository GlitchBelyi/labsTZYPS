# surefire-summary.md — Протокол запуску автотестів, Спринт 4

Продукт: «ЕдуПортал» · Модуль: `ua.kpi.tzyapz.eduportal` v1.0.0
Автор: Чупай (ІТ) · Прогін виконав і заповнив числа: Чупай (ІТ), 11.11.2026
Рецензент: Мележик (КТ)

> Статус: числа взято з реального фінального прогону на `main` (всі PR злито).

## 1. Умови запуску

| Параметр | Значення |
|---|---|
| Дата і час запуску | 11.11.2026 |
| Коміт | `main` (фінальна версія) |
| Команди | `mvn clean test` (юніт-тести); `mvn test -DexcludedGroups=none` (разом з UI) |
| JDK / Maven / ОС | OpenJDK 17.0.21 (Temurin) / Maven 3.10.0 / Windows 11 Pro (версія 25H2) |
| Тег | `v1-sprint4` |

## 2. Результати за класами (юніт-тести, `mvn clean test`)

| Клас | Автор | Tests run | Failures | Errors |
|---|---|---|---|---|
| GradeCalculatorBl10Test (BL-10) | Чупай | 14 | 0 | 0 |
| GradeCalculatorTest$EctsLetter (BL-20) | Ромашко | 17 | 1 | 0 |
| GradeCalculatorTest$LatePenalty (BL-30) | Ромашко | 23 | 9 | 0 |
| QuizScorerTest$SingleCorrect (BL-40) | Александрова | 7 | 0 | 0 |
| QuizScorerTest$MultipleCorrect (BL-50) | Александрова | 11 | 2 | 0 |
| QuizScorerScoreTest (BL-60) | Білогривий | 11 | 6 | 0 |
| EnrollmentValidatorBl70Bl80Test$CanEnroll (BL-70) | Мележик | 9 | 2 | 0 |
| EnrollmentValidatorBl70Bl80Test$Violations (BL-80) | Мележик | 23 | 18 | 0 |
| EnrollmentValidatorBl7xTest$KeyMatches (BL-72) | Білогривий | 5 | 0 | 1 |
| EnrollmentValidatorBl7xTest$WithinPeriod (BL-74) | Білогривий | 5 | 1 | 0 |
| EnrollmentValidatorBl7xTest$HasCapacity (BL-76) | Білогривий | 6 | 4 | 0 |
| EnrollmentValidatorBl7xTest$PrerequisiteMet (BL-78) | Білогривий | 6 | 0 | 0 |
| ExampleTest (шаблон модуля) | — | 10 | 0 | 0 |
| **Разом** | | **147** | **43** | **1** |

Підсумок Maven: Tests run: 147, Failures: 43, Errors: 1, Skipped: 0. BUILD FAILURE (очікувано: червоні тести відповідають дефектам v1).

## 3. Юніт-тести, що впали

| Тест | Вимога | Очікувано (SRS 1.1) | Отримано | Issue |
|---|---|---|---|---|
| `GradeCalculatorTest$EctsLetter.lowerBoundOfAIsInclusive` | BL-20 | літера A від 95 включно | B | #25 |
| `GradeCalculatorTest$LatePenalty` (9 рядків) | BL-30 | штраф не більше 50 %, лінійний | складний відсоток, 47.24 | #26 |
| `QuizScorerTest$MultipleCorrect.scoreIsNeverNegative` | BL-50 | бал не менше 0 | -0.1666 та -1.0 | #28 |
| `QuizScorerScoreTest.scoreMatchesSrs` (5 рядків) та `answerIdNotInKeyThrows` | BL-60 | 66.7, 33.3; IAE для зайвого id | 67.0, 33.0; ігнорує зайвий id | #37, #44 |
| `EnrollmentValidatorBl7xTest$KeyMatches.courseKeyNotSet` | BL-72 | false | NullPointerException | #45 |
| `EnrollmentValidatorBl7xTest$WithinPeriod` (1 рядок) | BL-74 | false після кінця | true | #35 |
| `EnrollmentValidatorBl7xTest$HasCapacity` (4 рядки) | BL-76 | false при заповненні, IAE при некоректних | true, немає IAE | #36, #46 |
| `EnrollmentValidatorBl70Bl80Test$CanEnroll` (2 рядки) | BL-70 | false після кінця, false при 30/30 | true | #35, #36 |
| `EnrollmentValidatorBl70Bl80Test$Violations` (18 рядків) | BL-80 | тексти українською | коди KEY/PERIOD | #38 |

## 4. UI-сценарії (`mvn test -DexcludedGroups=none`)

Підсумок разом з UI: Tests run: 156, Failures: 45, Errors: 2, Skipped: 0.

| Сценарій | Статус | Примітка |
|---|---|---|
| UI-01 `LoginUiTest.studentLoginOpensDashboard` (TC-001) | failed | Після входу URL `/`, а не `/my/` (Issue #1) |
| UI-02, UI-03 `CoursesUiTest` (TC-004, TC-005) | passed | 2 з 2 |
| UI-04 `LoginFormUiTest.loginFormHasExpectedElements` | passed | |
| UI-04 `LoginFormUiTest.loginFormTextsMatchSrs` | failed | 3 розбіжності текстів, Issue #2 |
| UI-05 `LoginNegativeUiTest.wrongPasswordStaysOnLoginPage` (TC-002) | error | TimeoutException (не дочекалися повідомлення про помилку за 15 с) |
| UI-06 `NavigationUiTest` (TC-042) | passed | 3 з 3 |
