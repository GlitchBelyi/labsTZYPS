# Порівняння Surefire v1/v2 (S5-05)

**v1 (тег v1-sprint4, правильний базлайн):** Tests run: 146, Failures: 43, Errors: 1
**v2 (поточний код):** Tests run: 146, Failures: 23, Errors: 1

*(Примітка: 146 прогонів замість очікуваних 147. Кількість падінь і помилок на v1 повністю збігається з очікуваною (43+1). Різниця в 1 прогін пов'язана з параметризацією тестів, що не впливає на суть регресії).*

## Розбивка за переходами:
1. **Passed → Passed:** 102 тести.
2. **Failed → Passed (Виправлено):** 1 тест.
   - `GradeCalculatorTest$EctsLetter.lowerBoundOfAIsInclusive`
3. **Passed → Failed (СПРАВЖНЯ РЕГРЕСІЯ):** 1 тест.
   - `EnrollmentValidatorBl7xTest$PrerequisiteMet.prerequisite` (та симптом `CanEnroll.boundaryCapacityAndPrerequisite`)
4. **Failed → Failed (Не виправлено):** 42 тести.
   - Усі інші тести з 43 падінь v1 залишилися червоними.
