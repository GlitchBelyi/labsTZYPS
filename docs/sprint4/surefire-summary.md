# surefire-summary.md — Протокол запуску автотестів, Спринт 4

Продукт: «ЕдуПортал» · Модуль: `ua.kpi.tzyapz.eduportal` v1.0.0
Автор чернетки: Чупай (ІТ) · Прогін виконав і заповнив числа: Мележик (КТ), 11.11.2026
Рецензент: призначається командою (не КТ, бо числа заповнював КТ)

> Статус: числа взято з реального прогону на поточному `main` (без тестів BL-60, BL-72…BL-78 і UI-05, їх ще немає в `main`). Після злиття цих тестів прогін треба повторити на чистому клоні, поставити тег `v1-sprint4` і оновити таблицю.

## 1. Умови запуску

| Параметр | Значення |
|---|---|
| Дата і час запуску | 11.11.2026, 13:39 UTC |
| Коміт | `1f2b2d1` (`main` + тимчасовий файл запуску, код і тести ідентичні `main`) |
| Команди | `mvn clean test` (юніт-тести); `mvn test -DexcludedGroups=none` (разом з UI) |
| JDK / Maven / ОС | OpenJDK 17.0.21 (Temurin) / Maven 3.10.0 / Ubuntu (GitHub Actions) |
| UI-тести | вимкнені за замовчуванням (тег `ui`), запускалися з Chrome на demo `sandbox.moodledemo.net` |
| Тег | `v1-sprint4` ще не поставлено (чекає на остаточний прогін після злиття решти тестів) |

## 2. Результати за класами (юніт-тести, `mvn clean test`)

| Клас | Автор | Tests run | Failures | Errors |
|---|---|---|---|---|
| GradeCalculatorBl10Test (BL-10) | Чупай | 14 | 0 | 0 |
| GradeCalculatorTest$EctsLetter (BL-20) | Ромашко | 17 | 1 | 0 |
| GradeCalculatorTest$LatePenalty (BL-30) | Ромашко | 23 | 9 | 0 |
| QuizScorerTest$SingleCorrect (BL-40) | Александрова | 7 | 0 | 0 |
| QuizScorerTest$MultipleCorrect (BL-50) | Александрова | 11 | 2 | 0 |
| EnrollmentValidatorBl70Bl80Test$CanEnroll (BL-70) | Мележик | 9 | 2 | 0 |
| EnrollmentValidatorBl70Bl80Test$Violations (BL-80) | Мележик | 23 | 18 | 0 |
| ExampleTest (шаблон модуля) | — | 10 | 0 | 0 |
| BL-60, BL-72, BL-74, BL-76, BL-78 | Білогривий | немає в `main` | — | — |
| **Разом** |  | **114** | **32** | **0** |

Підсумок Maven: Tests run: 114, Failures: 32, Errors: 0, Skipped: 0, BUILD FAILURE (очікувано: червоні тести відповідають дефектам v1).

## 3. Юніт-тести, що впали

| Тест | Вимога | Очікувано (SRS 1.1) | Отримано | Issue |
|---|---|---|---|---|
| `GradeCalculatorTest$EctsLetter.lowerBoundOfAIsInclusive` | BL-20 | літера A від 95 включно | B | #25 |
| `GradeCalculatorTest$LatePenalty.penaltyIsCappedAtFiftyPercent` (5 рядків даних) | BL-30 | штраф не більше 50 % (наприклад, 50,0; 40,0; 25,0) | 59,05; 47,24; 29,52 | #26 |
| `GradeCalculatorTest$LatePenalty.penaltyIsLinearNotCompound` (4 рядки даних) | BL-30 | −10 % від балу за кожну добу | складний відсоток | #26 |
| `QuizScorerTest$MultipleCorrect.scoreIsNeverNegative` (2 рядки даних) | BL-50 | бал не менше 0 | від'ємний бал | #28 |
| `EnrollmentValidatorBl70Bl80Test$CanEnroll.afterPeriod` | BL-74, BL-70 | false після кінця періоду | true | #35 |
| `EnrollmentValidatorBl70Bl80Test$CanEnroll.noCapacity` | BL-76, BL-70 | false при заповненому курсі | true | #36 |
| `EnrollmentValidatorBl70Bl80Test$Violations` (6 методів) і `canEnrollMatchesViolations` (12 рядків даних) | BL-80 | повідомлення українською та їх кількість за SRS 1.1 | коди `KEY`/`PREREQUISITE` замість текстів; порушення періоду й місткості повертаються порожнім списком | #38 (інтерпретація, питання замовнику Q1) |

Разом 32 червоні тести (рядки даних): 1 + 9 + 2 + 2 + 18. Червоні тести не вимкнено: кожен відповідає дефекту і посилається на Issue.

## 4. UI-сценарії (`mvn test -DexcludedGroups=none`)

Підсумок разом з UI: Tests run: 122, Failures: 33, Errors: 1, Skipped: 0.

| Сценарій | Статус | Примітка |
|---|---|---|
| UI-01 `LoginUiTest.studentLoginOpensDashboard` (TC-001) | error | Після спроби входу за 15 с URL лишився `https://sandbox.moodledemo.net/login/index.php?loginredirect=1`. Чи це дефект demo (дефект #1 зі спринта 3), чи збій середовища, треба перевірити повторним запуском на demo |
| UI-03/UI-04 `LoginFormUiTest.loginFormHasExpectedElements` (TC-017) | passed | |
| UI-04 LoginFormUiTest.loginFormTextsMatchSrs (TC-017) | failed | 3 розбіжності текстів форми входу з GUI-10; дефект зафіксовано в Issue #2 |
| UI-02/UI-03 `CoursesUiTest` (TC-004, TC-005) | passed | 2 з 2 |
| UI-06 `NavigationUiTest` (TC-042) | passed | 3 з 3 |
| UI-05 `LoginNegativeUiTest` (TC-002) | немає в `main` | чекає на PR Білогривого |

## 5. Що лишилося до фінального прогону

1. Злити в `main` тести BL-60, BL-72…BL-78 і UI-05.
2. Чупай: чистий клон, `mvn clean test` і `mvn test -DexcludedGroups=none` на Windows, оновити таблицю в п. 2, поставити тег `v1-sprint4`.
3. Створити Issue для `LoginFormUiTest.loginFormTextsMatchSrs` і повторно перевірити UI-01.
