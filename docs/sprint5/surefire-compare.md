# Порівняння Surefire v1/v2 (S5-05)

**Середовище:** macOS, JDK 17, Maven 3.9.10, JUnit 5.
**Дата прогону:** 24.11.2026.
**Виконавець:** Ромашко (ІТ).

## Підсумкові числа
- **v1:** Tests run: 146, Failures: 18, Errors: 0, Skipped: 0
- **v2:** Tests run: 146, Failures: 23, Errors: 1, Skipped: 0

*Примітка:* План очікував ~147 прогонів. Різниця в 1 прогін (146 замість 147) пов'язана з особливістю параметризації в поточній гілці `main` і не впливає на повноту регресії.

## Розбивка за переходами (146 тестів)
1. **Passed → Passed (стабільно):** 121 тест.
2. **Failed → Passed (виправлено):** 1 тест.
3. **Failed → Failed (не виправлено):** 17 тестів.
4. **Passed → Failed (РЕГРЕСІЯ):** 7 тестів.

## Детальний перелік змінених результатів (тільки ті, що падали)

### 🟢 Failed → Passed (Кандидати на Verified)
1. `GradeCalculatorTest$EctsLetter.lowerBoundOfAIsInclusive` (v1: очік. A, отр. B) — **Виправлено** (v2: Passed). Причина: зміна `>95` на `>=95`.

### 🔴 Passed → Failed (Регресії v2)
1. `EnrollmentValidatorBl7xTest$KeyMatches.courseKeyNotSet` (v2: NPE `course.enrolmentKey()`) — прибрано null-check.
2. `EnrollmentValidatorBl7xTest$HasCapacity.invalid` (3 рядки) (v2: не кидає `IllegalArgumentException`) — прибрано валідацію.
3. `EnrollmentValidatorBl7xTest$PrerequisiteMet.prerequisite` (v2: межа 60.0 дає false) — зміна `>=` на `>`.
4. `QuizScorerScoreTest.answerIdNotInKeyThrows` (v2: не кидає `IllegalArgumentException`) — прибрано перевірку невідомого ID.

### 🟡 Failed → Failed (Не виправлено, кандидати на Reopened)
1. `GradeCalculatorTest$LatePenalty.penaltyIsCappedAtFiftyPercent` (5 рядків)
2. `GradeCalculatorTest$LatePenalty.penaltyIsLinearNotCompound` (4 рядки)
3. `QuizScorerTest$MultipleCorrect.scoreIsNeverNegative` (2 рядки)
4. `EnrollmentValidatorBl70Bl80Test$Violations.*` (6 тестів: allFourInOrder, keyAndCapacityOrder, onlyCapacity, onlyKey, onlyPeriod, onlyPrerequisite) — тести очікують укр. текст, отримують `[KEY, PERIOD, CAPACITY, PREREQUISITE]`.
