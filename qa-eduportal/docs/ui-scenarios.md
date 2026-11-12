# Сценарії UI-тестування (ui-scenarios.md) 

* **Продукт:** Навчальна платформа «ЕдуПортал»
* **Спринт:** Спринт 4
* **Автор / Роль:** Тест-аналітик (ТА) — Ромашко
* **Дата складання:** 11 листопада 2026 року
* **Статус:** Рецензовано СМ
* **Охоплення:** документ описує всі вісім автоматизованих UI-кейсів спринту — TC-001, TC-002, TC-004, TC-005, TC-017, TC-018, TC-019, TC-042. Реалізація — JUnit 5 + Selenium WebDriver у пакеті `ua.kpi.tzyapz.eduportal.ui`. Спільна інфраструктура — `BaseUiTest` (ChromeDriver у режимі `--headless=new`, `WebDriverWait` з таймаутом 15 с, тег `ui`).

**Відповідність тест-класів і кейсів:**

| Тест-клас / метод | TC | Вимога |
|---|---|---|
| `CoursesUiTest.guestSeesCourseList` | TC-004 | F-30 |
| `CoursesUiTest.searchWithoutMatchesShowsMessage` | TC-005 | F-30 |
| `LoginUiTest.studentLoginOpensDashboard` | TC-001 | F-10 |
| `LoginNegativeUiTest.wrongPasswordStaysOnLoginPage` | TC-002 | F-10 |
| `LoginFormUiTest.loginFormHasExpectedElements` | TC-017 | GUI-10 |
| `LoginFormUiTest.loginFormTextsMatchSrs` | TC-017 | GUI-10 / SRS 1.1 |
| `LoginForgotPasswordUiTest.forgotPasswordLinkOpensRecoveryPage` | TC-018 | GUI-10 |
| `LoginTitleUiTest.loginPageTitleContainsLogIn` | TC-019 | GUI-10 |
| `NavigationUiTest.guestSeesNavbarAndLoginLink` | TC-042 | GUI-75 |
| `NavigationUiTest.guestHasNoUserMenu` | TC-042 | GUI-75 |
| `NavigationUiTest.languageSwitcherPresent` | TC-042 | GUI-75 |

Запуск: `mvn test -DexcludedGroups=none -Dtest=<Клас>`.

---

## TC-004: Валідація наявності базових курсів на головній сторінці

* **Тест-метод:** `CoursesUiTest.guestSeesCourseList`
* **Вимога:** F-30
* **Опис:** Перевірка того, що на головній сторінці публічної пісочниці Moodle безпосередньо відображаються обов'язкові навчальні курси.
* **URL:** `https://sandbox.moodledemo.net/`
* **Покроковий сценарій:**
  1. **Відкриття сторінки:** `driver.get(BASE_URL + "/")`.
  2. **Перевірка першого курсу:**
     * **Локатор:** `By.linkText("My first course")`
     * **Очікування:** `WebDriverWait` + `ExpectedConditions.visibilityOfElementLocated`
     * **Перевірка:** `assertTrue(first.isDisplayed())`
  3. **Перевірка другого курсу:**
     * **Локатор:** `By.linkText("My second course")`
     * **Очікування:** `WebDriverWait` + `ExpectedConditions.visibilityOfElementLocated`
     * **Перевірка:** `assertTrue(second.isDisplayed())`
* **Очікуваний результат:** Обидва курси ("My first course" та "My second course") успішно знайдено й видимі на головній сторінці без авторизації.

---

## TC-005: Валідація глобального пошуку при введенні неіснуючого запиту

* **Тест-метод:** `CoursesUiTest.searchWithoutMatchesShowsMessage`
* **Вимога:** F-30
* **Опис:** Перевірка коректності обробки пошукового запиту системою Moodle, коли за введеним значенням немає жодних збігів.
* **URL:** `https://sandbox.moodledemo.net/search/index.php?lang=uk`
* **Покроковий сценарій:**
  1. **Відкриття сторінки пошуку:** `driver.get(BASE_URL + "/search/index.php?lang=uk")` — параметр `lang=uk` фіксує мову інтерфейсу незалежно від браузера.
  2. **Очікування поля вводу:**
     * **Локатор:** `css=input[id^='searchinput']`
     * **Очікування:** `ExpectedConditions.visibilityOfElementLocated`.
  3. **Введення пошукового запиту:** `input.sendKeys("T3-Zzzq-9999", Keys.ENTER)` — введення неіснуючого значення й надсилання форми Enter-ом.
  4. **Перевірка результату:**
     * **Дія:** `wait.until(d -> NO_RESULTS.matcher(d.findElement(By.tagName("body")).getText()).find())`.
     * **Шаблон:** `Pattern.compile("Результат відсутній|No results", CASE_INSENSITIVE | UNICODE_CASE)`.
     * **Обробка таймауту:** у разі `TimeoutException` тест падає з виводом перших 300 символів тексту сторінки (для діагностики).
* **Очікуваний результат:** Система виводить повідомлення про те, що результати за запитом `T3-Zzzq-9999` відсутні.

---

## TC-001 / UI-01: Вхід студента відкриває панель /my/ (вимога F-10)

* **Тест-метод:** `LoginUiTest.studentLoginOpensDashboard`
* **Передумови:** Відкрито `https://sandbox.moodledemo.net/login/index.php`. Не авторизований.
* **Кроки:**
  1. Заповнити `#username` значенням `student`.
  2. Заповнити `#password` значенням `sandbox24`.
  3. Натиснути `#loginbtn`.
  4. Дочекатися, поки URL перестане містити `/login/` (`ExpectedConditions.not(urlContains("/login/"))`).
  5. Перевірити `driver.getCurrentUrl()`.
* **Очікуваний результат:** URL містить `/my/` (інформаційна панель студента).
* **Примітка:** Очікувано **червоний** на demo — після входу відкривається головна сторінка `/`, а не `/my/` (відомий дефект demo, BUG-#1). Наявність «Мої курси» в цьому кейсі не перевіряється.

---

## TC-002 / UI-05: Негативний вхід — правильне ім'я, неправильний пароль (вимога F-10)

* **Тест-метод:** `LoginNegativeUiTest.wrongPasswordStaysOnLoginPage`
* **Передумови:** Відкрито `https://sandbox.moodledemo.net/login/index.php`. Не авторизований.
* **Кроки:**
  1. Заповнити `#username` значенням `student`.
  2. Заповнити `#password` свідомо неправильним значенням `Wrong-pass-9` (етика demo — реальних облікових даних не використовуємо, один вхід на тест).
  3. Натиснути `#loginbtn`.
  4. Дочекатися появи повідомлення про помилку `ERROR_ALERT = .loginerrors .alert-danger` (локатор за класом, не за текстом — мова demo нестабільна, BUG-#2).
  5. Перевірити URL, видимість помилки, збереження імені й очищення пароля.
* **Очікуваний результат:**
  * URL містить `/login/` і **не** містить `/my/`.
  * Відображається повідомлення про помилку.
  * Поле `#username` містить `student`.
  * Поле `#password` порожнє (значення очищено).

---

## TC-017 / UI-04: Склад форми входу (вимога GUI-10)

**Тест-методи:** `LoginFormUiTest.loginFormHasExpectedElements`, `LoginFormUiTest.loginFormTextsMatchSrs`
**Передумови:** Відкрито сторінку входу `https://sandbox.moodledemo.net/login/index.php`. Не авторизований.
**Кроки (`loginFormHasExpectedElements`):**
1. Перевірити наявність поля імені (`#username`).
2. Перевірити поле пароля (`#password`), атрибут `type="password"`.
3. Перевірити наявність кнопки входу (`#loginbtn`).
4. Перевірити наявність кнопки «Увійти як гість» (`#loginguestbtn`).
5. Перевірити наявність посилання «Забули пароль?» (`a[href*='forgot_password']`).
6. Перевірити наявність перемикача показу пароля (`[data-action='togglesensitive'], .toggle-sensitive-btn`).
7. Перевірити наявність перемикача мови (`a[data-bs-toggle='dropdown'][aria-haspopup='true']`).
**Кроки (`loginFormTextsMatchSrs`):**
8. Перевірити тексти написів на сторінці («З поверненням», «Забули пароль?», «Увійти як гість»).

**Очікуваний результат:**
Кроки 1-7: усі елементи присутні на сторінці, локатори унікальні.
Крок 8: тексти відповідають SRS 1.1 (очікувано **червоний** через Issue #2, оскільки на demo інтерфейс англійською).

---

## TC-018 / UI-08: Посилання «Забули пароль?» відкриває сторінку відновлення

* **Тест-метод:** `LoginForgotPasswordUiTest.forgotPasswordLinkOpensRecoveryPage`
* **Вимога:** GUI-10
* **Передумови:** Відкрито `https://sandbox.moodledemo.net/login/index.php`. Не авторизований. Сторінку не змінюємо, акаунт не використовуємо, форму відновлення не надсилаємо (етика demo).
* **Кроки:**
  1. Відкрити сторінку входу.
  2. Дочекатися наявності посилання `a[href*='/login/forgot_password.php']` (локатор не залежить від мови інтерфейсу).
  3. Клікнути на посилання.
  4. Дочекатися, поки URL міститиме `/login/forgot_password.php`.
* **Очікуваний результат:** Відкрито сторінку відновлення пароля; `driver.getCurrentUrl()` містить `/login/forgot_password.php`.

---

## TC-019 / UI-07: Заголовок вкладки сторінки входу

* **Тест-метод:** `LoginTitleUiTest.loginPageTitleContainsLogIn`
* **Вимога:** GUI-10
* **Передумови:** Відкрито `https://sandbox.moodledemo.net/login/index.php`. Не авторизований.
* **Кроки:**
  1. Відкрити сторінку входу.
  2. Дочекатися, поки `driver.getTitle()` не стане непорожнім.
  3. Перевірити заголовок.
* **Очікуваний результат:** Title містить «Log in» **або** «Увійти». Мова demo нестабільна, тому допускаються обидва варіанти.

---

## TC-042 / UI-06: Верхня панель гостя (вимога GUI-75)

**Тест-методи:** `NavigationUiTest.guestSeesNavbarAndLoginLink`, `NavigationUiTest.guestHasNoUserMenu`, `NavigationUiTest.languageSwitcherPresent`
**Передумови:** Відкрито `https://sandbox.moodledemo.net/`. Не авторизований. Локатори не залежать від мови (id, href, структура).

**Кроки (`guestSeesNavbarAndLoginLink`):**
1. Відкрити головну сторінку.
2. Дочекатися появи `nav.navbar`.
3. Перевірити наявність посилання `a[href*='/login/index.php']`.

**Кроки (`guestHasNoUserMenu`):**
1. Відкрити головну сторінку.
2. Дочекатися появи `nav.navbar`.
3. Перевірити, що `#user-menu-toggle` **відсутній** (`findElements(...).isEmpty()`).

**Кроки (`languageSwitcherPresent`):**
1. Відкрити головну сторінку.
2. Дочекатися появи `nav.navbar`.
3. Перевірити наявність `#lang-menu-toggle`.

**Очікуваний результат:**
- `guestSeesNavbarAndLoginLink`: навігаційна панель і посилання на форму входу присутні.
- `guestHasNoUserMenu`: меню користувача для гостя відсутнє. **Очікувано червоний** — на demo гість бачить меню користувача (відомі дефекти demo, #5, #6).
- `languageSwitcherPresent`: перемикач мови присутній на панелі гостя.
