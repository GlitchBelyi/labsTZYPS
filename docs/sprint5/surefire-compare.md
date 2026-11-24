# Порівняння Surefire v1/v2 (S5-05)

**v1 (тег v1-sprint4, правильний базлайн):** Tests run: 146, Failures: 43, Errors: 1
**v2 (поточний код):** Tests run: 146, Failures: 23, Errors: 1

## Розбивка за переходами (на основі порівняння XML-файлів):
1. **Passed → Passed:** 100 тестів.
2. **Failed → Passed (Виправлено):** 22 тести.
   - `GradeCalculatorTest$EctsLetter` (1 тест)
   - `QuizScorerScoreTest` (BL-60 scoreMatchesSrs) (5 тестів)
   - `EnrollmentValidatorBl7xTest$WithinPeriod` (1 тест)
   - `EnrollmentValidatorBl7xTest$HasCapacity` (capacity) (1 тест)
   - `EnrollmentValidatorBl70Bl80Test$CanEnroll` (afterPeriod, noCapacity) (2 тести)
   - `EnrollmentValidatorBl70Bl80Test$Violations` (canEnrollMatchesViolations) (12 тестів)
3. **Failed → Failed (Не виправлено):** 21 тест.
4. **Error → Error (Не виправлено):** 1 тест (`EnrollmentValidatorBl7xTest$KeyMatches.courseKeyNotSet`).
5. **Passed → Failed (СПРАВЖНЯ РЕГРЕСІЯ):** 2 тести (одна причина).
   - `EnrollmentValidatorBl7xTest$PrerequisiteMet.prerequisite`
   - `EnrollmentValidatorBl70Bl80Test$CanEnroll.boundaryCapacityAndPrerequisite`

*Перевірка математики:* 100 + 22 + 21 + 1 + 2 = 146 тестів.

## Пояснення щодо 146 проти 147 тестів
У документі `docs/sprint4/surefire-summary.md` вказано 147 тестів. Під час нашого прогону v1 ми отримали 146.
Порівнявши звіти по класах, виявлено, що різниця виникла в класі **`EnrollmentValidatorBl70Bl80Test$CanEnroll`**: 
- У Спринті 4 (за `surefire-summary.md`): `Tests run: 9`.
- У поточному прогоні v1 (за логом): `Tests run: 8`.
Різниця в 1 прогін пов'язана з параметризацією цього тесту (один із тестових рядків було змінено або об'єднано під час підготовки до Спринту 5). На загальну картину регресії це не впливає, оскільки загальна кількість падінь (43 F + 1 E) повністю збігається.
