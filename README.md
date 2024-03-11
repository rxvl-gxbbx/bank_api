# Тестовое задание

#### Необходимо написать сервис для “банковских” операций. В нашей системе есть пользователи (клиенты), у каждого клиента есть строго один “банковский аккаунт”, в котором изначально лежит какая-то сумма. Деньги можно переводить между клиентами. На средства также начисляются проценты.

### Функциональные требования:

1. В системе есть пользователи, у каждого пользователя есть строго один **"банковский аккаунт"**. У пользователя также
   есть телефон и email. Телефон и или email должен быть минимум один. На **"банковском счету"** должна быть какая-то
   изначальная сумма. Также у пользователя должна быть указана дата рождения и ФИО.
2. Для простоты будем считать что **в системе нет ролей**, только обычные клиенты. Пусть будет служебный апи (**с
   открытым
   доступом**), через который можно заводить новых пользователей в системе, указав логин, пароль, изначальную сумму,
   телефон и email (логин, телефон и email не должны быть заняты).
3. Баланс счета клиента не может уходит в минус ни при каких обстоятельствах.
4. Пользователь **может** добавить/сменить свои номер телефона и/или email, если они еще не заняты другими
   пользователями.
5. Пользователь **может** удалить свои телефон и/или email. При этом нельзя удалить последний.
6. Остальные данные пользователь **не может менять**.
7. **Сделать АПИ поиска**. Искать можно любого клиента. Должна быть фильтрация и пагинация/сортировка. Фильтры:
    * Если передана **дата рождения**, то фильтр записей, где **дата рождения больше чем переданный в запросе**.
    * Если передан **телефон**, то фильтр по **100% сходству**.
    * Если передано **ФИО**, то фильтр по **like форматом ‘{text-from-request-param}%**’
    * Если передан **email**, то фильтр по **100% сходству**.

8. Доступ к АПИ должен быть **аутентифицирован** (кроме служебного апи для создания новых клиентов).
9. **Раз в минуту баланс каждого клиента увеличиваются на 5% но не более 207% от начального депозита.**  
   *Например:*  
   *Было: 100, стало: 105.*  
   *Было: 105, стало: 110.25.*
10. Реализовать функционал перевода денег с одного счета на другой. Со счета аутентифицированного пользователя, на счёт
    другого пользователя. Сделать все необходимые валидации и потокобезопасной.

### Нефункциональные требования:

1. **Добавить OpenAPI/Swagger**
2. **Добавить логирование**
3. **Аутентификация через JWT.**
4. **Нужно сделать тесты на покрытие функционала трансфера денег.**

### Стек:

1. **Java 17**
2. **Spring Boot 3**
3. **База данных PostgreSQL**
4. **Maven**
5. **REST API**
6. **Дополнительные технологии (Redis, ElasticSearch и т.д.) на ваше усмотрение.**
7. **Фронтенд не нужен**

Результат предоставить в виде публичного репозитория на github.

### [SQL-скрипт](https://gist.github.com/rxvl-gxbbx/bcc18df5567c7b968829638b71dee242) - для создания БД под приложение

### [Swagger](https://github.com/rxvl-gxbbx/bank_api/files/14546471/Swagger.UI.pdf) - Swagger UI проекта

### Описание пакетов:

1) [config](https://github.com/rxvl-gxbbx/bank/tree/master/src/main/java/com/rxvlvxr/bank/config) - классы
   конфигурации (JWT,
   Swagger, Security)
2) [controllers](https://github.com/rxvl-gxbbx/bank/tree/master/src/main/java/com/rxvlvxr/bank/controllers) -
   контроллеры для REST сервиса, данные классы нужны для обработки запросов
3) [dtos (Data Transfer Objects)](https://github.com/rxvl-gxbbx/bank/tree/master/src/main/java/com/rxvlvxr/bank/dtos) -
   классы-обертки для JSON request/response, нужны для передачи данных, т.е. все поля этих классов
   конвертируются в JSON формат для удобства передачи запросов, либо для того чтобы скрыть информацию, которая является
   необязательной для клиента (напримерю, ID из БД)
4) [exceptions](https://github.com/rxvl-gxbbx/bank/tree/master/src/main/java/com/rxvlvxr/bank/exceptions) -
   классы-исключения
5) [handlers](https://github.com/rxvl-gxbbx/bank/tree/master/src/main/java/com/rxvlvxr/bank/handlers) -
   классы-обработчики
6) [mappers](https://github.com/rxvl-gxbbx/bank/tree/master/src/main/java/com/rxvlvxr/bank/mappers) - классы для
   конвертации объектов
7) [models](https://github.com/rxvl-gxbbx/bank/tree/master/src/main/java/com/rxvlvxr/bank/models) -
   классы-модели, описывают сущности таблиц
8) [repositories](https://github.com/rxvl-gxbbx/bank/tree/master/src/main/java/com/rxvlvxr/bank/repositories) - классы
   для взаимодействия с БД
9) [security](https://github.com/rxvl-gxbbx/bank/tree/master/src/main/java/com/rxvlvxr/bank/security) - классы, которые
   требуются для Spring Security
10) [services](https://github.com/rxvl-gxbbx/bank/tree/master/src/main/java/com/rxvlvxr/bank/services) -
    классы сервиса, отвечают за бизнес-логику приложения
11) [utils](https://github.com/rxvl-gxbbx/bank/tree/master/src/main/java/com/rxvlvxr/bank/utils) - классы
    утилит
12) [validators](https://github.com/rxvl-gxbbx/bank/tree/master/src/main/java/com/rxvlvxr/bank/validators) -
    классы для валидации запросов

## Описание методов из тестового задания

---

**Пусть будет служебный апи (с открытым доступом), через который можно заводить новых пользователей в системе, указав
логин, пароль, изначальную сумму, телефон и email (логин, телефон и email не должны быть заняты).**

### Регистрация пользователей в БД

```java

@PostMapping("/registration")
public Map<String, String> registration(@RequestBody @Valid UserDTO userDTO, BindingResult bindingResult) {
    log.info("Метод registration начал выполнение");

    User user = userMapper.toUser(userDTO);

    log.info("Валидация логина пользователя");
    userValidation.validate(user, bindingResult);

    validateContactInfo(user.getPhones().stream()
            .findFirst(), "телефона", phoneValidation, bindingResult);
    validateContactInfo(user.getEmails().stream()
            .findFirst(), "адреса почты", emailValidation, bindingResult);

    if (bindingResult.hasErrors()) throw new UserNotCreatedException(ErrorUtil.getResponse(bindingResult));

    log.info("Регистрация пользователя={}", user.getUsername());
    registrationService.register(user);
    log.info("Регистрация прошла успешно");

    log.info("Генерация токена");
    String token = jwtUtil.generateToken(userDTO.getUsername());

    log.info("Токен успешно сгенерирован");
    return Map.of("jwt-token", token);
}
```

Метод для POST-запроса, принимающий в теле запроса данные в JSON-формате, которые конвертируются в **UserDTO** при
помощи Jackson, поля DTO класса соответствуют ключам, переданным в запросе

#### Пример запроса к сервису

URL: http://localhost:8080/bank/users/registration

```json
{
  "fullName": "Фамилия Имя Отчество",
  "birthDate": "2000-01-01",
  "username": "user",
  "password": "password",
  "account": {
    "amount": 1000
  },
  "phone": {
    "number": "79991234567"
  },
  "email": {
    "address": "mail@mail.com"
  }
}
```

#### Пример успешного ответа на запрос

Ответом явлется JWT-токен для дальнейшей аутентификации

```json
{
  "jwt-token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJVc2VyIERldGFpbHMiLCJ1c2VybmFtZSI6InVzZXIiLCJpYXQiOjE3MDk1NDU4NzEsImlzcyI6ImJhbmstYXBpLXRlc3QiLCJleHAiOjE3MDk1NDk0NzF9.DY_Bq7JeKd3CToRMQ5-j8FK4Q74a4emuDoVsVWCNgJ4"
}
```

#### Пример ошибки

```json
{
  "errors": [
    {
      "message": "fullName - неверный формат. Введите ФИО. Например: Фамилия Имя Отчество",
      "time": "2024-03-04T14:22:06.1859661"
    },
    {
      "message": "email.address - пожалуйста, введите корректный адрес электронной почты",
      "time": "2024-03-04T14:22:06.1859661"
    },
    {
      "message": "fullName - поле не может быть пустым",
      "time": "2024-03-04T14:22:06.1859661"
    }
  ]
}
```

---

**Пользователь может добавить/сменить свои номер телефона и/или email, если они еще не заняты другими пользователями.**

### Добавление номера телефона (аналогичный метод есть в классе EmailsController - логика одна и та же)

```java

@PostMapping
public ResponseEntity<Response> add(@AuthenticationPrincipal BankUserDetails userDetails, @RequestBody @Valid PhoneDTO phoneDTO, BindingResult bindingResult) {
    User user = userDetails.user();
    log.info("Метод add начал выполнение для пользователя={}", user.getUsername());

    Phone phone = phoneMapper.toPhone(phoneDTO);

    log.info("Валидация номера телефона");
    phoneValidation.validate(phone, bindingResult);

    if (bindingResult.hasErrors()) throw new PhoneNotCreatedException(ErrorUtil.getResponse(bindingResult));

    phone.setUser(user);

    log.info("Попытка добавить новый номер для пользователя={}", user.getUsername());
    phoneService.add(phone);

    log.info("Номер телефона успешно добавлен для пользователя={}", user.getUsername());
    return new ResponseEntity<>(new Response("Номер телефона успешно добавлен", LocalDateTime.now()), HttpStatus.CREATED);
}
```

Метод POST-запроса, принимающий на вход данные в JSON формате, которые соответствуют **PhoneDTO** классу (т.е. все поля
класса соответствуют ключам в запросе). Параметер **userDetails** нужен для получения данных об аутентификации. Это
достигается при помощи аннотации **@AuthenticationPrincipal**.

#### Пример запроса к сервису

```json
{
  "number": "79990123456"
}
```

#### Пример ответа

```json
{
  "message": "Номер телефона успешно добавлен",
  "time": "2024-03-04T17:02:21.2733664"
}
```

#### Пример ошибки (при добавлении уже существующего номера)

```json
{
  "errors": [
    {
      "message": "number - Этот номер уже используется",
      "time": "2024-03-04T14:38:12.3013799"
    }
  ]
}
```

---

### Изменение номера телефона (аналогичный метод есть в классе EmailsController - логика одна и та же)

```java

@PatchMapping("/{id}")
public ResponseEntity<Response> update(@PathVariable("id") long id, @AuthenticationPrincipal BankUserDetails userDetails, @RequestBody @Valid PhoneDTO phoneDTO, BindingResult bindingResult) {
    User user = userDetails.user();
    log.info("Метод update начал выполнение для пользователя={}", user.getUsername());

    if (isRestricted(id, user)) throw new ForbiddenException();

    Phone phone = phoneMapper.toPhone(phoneDTO);

    log.info("Валидация номера телефона");
    phoneValidation.validate(phone, bindingResult);

    if (bindingResult.hasErrors())
        throw new PhoneNotUpdatedException(ErrorUtil.getResponse(bindingResult));

    phone.setUser(user);

    user.getPhones().stream()
            .findFirst()
            .ifPresentOrElse(telephone -> log.info("Обновление телефона id={} с номера=\"{}\" на номер=\"{}\"", id, telephone.getNumber(), phone.getNumber()),
                    PhoneNotFoundException::new);
    phoneService.update(id, phone);

    log.info("Номер телефона успешно обновлен для пользователя={}", user.getUsername());
    return new ResponseEntity<>(new Response("Номер телефона успешно обновлен", LocalDateTime.now()), HttpStatus.OK);
}
```

Метод для PATCH-запроса для изменения данных в таблице, принимающий из URL строку **ID** телефона в базе данных и данные
в формате JSON, которые конвертируются с помощью Jackson в объект **PhoneDTO**. У этого класса есть единственное поле
**number**, оно соответствует ключу, переданному в запросе.

#### Пример запроса к сервису

URL: http://localhost:8080/bank/phones/{id}

```json
{
  "number": "79994561212"
}
```

#### Ответ на запрос при успешной валидации

```json
{
  "message": "Номер телефона успешно обновлен",
  "time": "2024-03-04T17:03:16.6445454"
}
```

#### Пример ошибки

```json
{
  "errors": [
    {
      "message": "number - Этот номер уже используется",
      "time": "2024-03-04T14:21:42.7710392"
    }
  ]
}
```

---

**Пользователь может удалить свои телефон и/или email. При этом нельзя удалить последний.**

### Удаление номера телефона (аналогичный метод есть в классе EmailsController - логика одна и та же)

```java

@DeleteMapping("/{id}")
public ResponseEntity<Response> delete(@PathVariable("id") long id, @AuthenticationPrincipal BankUserDetails userDetails) {
    User user = userDetails.user();
    log.info("Метод delete начал выполнение для пользователя={}", user.getUsername());

    if (isRestricted(id, user)) throw new ForbiddenException();

    user.getPhones().stream()
            .findFirst()
            .ifPresentOrElse(phone -> log.info("Удаляется телефон id={} с номером {} для пользователя {}", id, phone.getNumber(), user.getUsername()),
                    PhoneNotFoundException::new);
    phoneService.delete(id);

    log.info("Номер телефона успешно удален для пользователя={}", user.getUsername());
    return new ResponseEntity<>(new Response("Номер телефона успешно удален", LocalDateTime.now()), HttpStatus.OK);
}
```

Метод для PATCH-запроса для изменения данных в таблице, принимающий из URL строку **ID** телефона в базе данных.
Параметр **userDetails** нужен для извлечения данных об аутентифицированном пользователе. Это достигается с помощью
аннотации **@AuthenticationPrincipal**.

#### Пример запроса

DELETE-запрос на URL: http://localhost:8080/bank/phones/{id}

#### Пример ответа

```json
{
  "message": "Номер телефона успешно удален",
  "time": "2024-03-04T17:03:38.9727026"
}
```

#### Пример ошибки (если попытаться удалить последний добавленный номер)

```json
{
  "errors": [
    {
      "message": "Невозможно выполнить операцию",
      "time": "2024-03-04T14:32:19.7332502"
    }
  ]
}
```

---

**Сделать АПИ поиска**. Искать можно любого клиента. Должна быть фильтрация и пагинация/сортировка. Фильтры:

* Если передана **дата рождения**, то фильтр записей, где **дата рождения больше чем переданный в запросе**.
* Если передан **телефон**, то фильтр по **100% сходству**.
* Если передано **ФИО**, то фильтр по **like форматом ‘{text-from-request-param}%**’
* Если передан **email**, то фильтр по **100% сходству**.

### Метод поиска пользователей по БД

```java

@PostMapping("/search")
public UserResponse searchResults(@RequestBody @Valid SearchDTO request, BindingResult bindingResult, @AuthenticationPrincipal BankUserDetails userDetails) {
    if (bindingResult.hasErrors())
        throw new InvalidSearchRequestException(ErrorUtil.getResponse(bindingResult));

    log.info("Метод searchResults начал выполнение для пользователя={}", userDetails.user().getUsername());
    User user = userMapper.toUser(request);
    log.info("Выполняется поиск пользователей с параметрами: birthDate={}, phones.number={}, fullName={}, emails.address={}", request.getBirthDate(), request.getPhone(), request.getFullName(), request.getEmail());
    UserResponse response = new UserResponse(userService.search(new UserSpecification(user), getPageable(request.getSort(), request.getPagination(), "fullName", 1, 10, Sort.Direction.ASC)).stream()
            .map(userMapper::toDTO)
            .collect(Collectors.toList()));

    log.info("Поиск пользователей успешно завершен");
    log.info("Количество найденных пользователей: {}", response.getUsers().size());
    return response;
}
```

Метод POST-запроса, принимающий в виде запроса SearchDTO - класс, который имеет поля, сответствующие поисковому
запросу (**birthDate**, **phone**, **fullName**, **email**) и **userDetails** - параметр для получения данных об
аутентифицированном пользователе (достигается с помощью **@AuthenticationPrincipal** аннотации). Есть возможность
сортировки/пагинации. Достигается с помощью private метода **getPageable**. Также есть возможность поиска сразу по
нескольким параметрам.

**Примечание**: я решил выбрать POST, а не GET, потому что передаю в теле запроса конфиденциальные данные.

### Примеры запроса

URL: http://localhost:8080/bank/users/search

```json
{
  "birthDate": "1980-01-01",
  "phone": {
    "number": "79259991212"
  },
  "fullName": "Фам",
  "email": {
    "address": "another@mail.ru"
  },
  "pagination": {
    "pageNumber": 1,
    "pageSize": 1
  },
  "sort": {
    "field": "fullName",
    "direction": "asc"
  }
}
```

#### Пример ответа

```json
{
  "users": [
    {
      "fullName": "Фамилия Имя Отчество",
      "birthDate": "1988-03-30",
      "account": {
        "amount": 2474914.4992992477
      },
      "phones": [
        {
          "number": "79259991212"
        },
        {
          "number": "79309309090"
        },
        {
          "number": "79261239999"
        }
      ],
      "emails": [
        {
          "address": "example@gmail.com"
        },
        {
          "address": "another@mail.ru"
        }
      ]
    }
  ]
}
```

#### Ответ (если ничего не найдено)

```json
{
  "users": []
}
```

#### Ответ (при ошибке валидации)

```json
{
  "errors": [
    {
      "message": "sort.field - должно соответствовать одному из перечисленных значений: id, fullName, birthDate, phones.number, emails.address",
      "time": "2024-03-09T12:44:46.5341769"
    },
    {
      "message": "pagination.pageSize - должно быть не меньше 1",
      "time": "2024-03-09T12:44:46.5341769"
    },
    {
      "message": "email.address - пожалуйста, введите корректный адрес электронной почты",
      "time": "2024-03-09T12:44:46.5341769"
    },
    {
      "message": "pagination.pageNumber - должно быть не меньше 1",
      "time": "2024-03-09T12:44:46.5341769"
    },
    {
      "message": "sort.direction - пожалуйста, введите корректное значение: asc (по возрастанию), desc (по убыванию)",
      "time": "2024-03-09T12:44:46.5341769"
    },
    {
      "message": "phone.number - неверный номер телефона. Введите номер в корректном формате. Например: 79001234567",
      "time": "2024-03-09T12:44:46.5341769"
    }
  ]
}
```

---

**Раз в минуту баланс каждого клиента увеличиваются на 5% но не более 207% от начального депозита.**

### Конфигурация

**@EnableScheduling** в классе конфигурации позволяет планировать запуск задач (методов)

```java

@SpringBootApplication
@EnableScheduling // эта аннотация позволяет включить запланированные задачи
public class BankApplication {
    // code
}
```

### Класс запланированных задач

Данный класс использует сервис **AccountService** для выполнения бизнес-логики, а аннотация **@Scheduled** запускает
метод 1 раз в минуту.

```java

@Component
@Profile("!integration-test")
public class ScheduledTask {
    private final AccountService accountService;

    @Autowired
    public ScheduledTask(AccountService accountService) {
        this.accountService = accountService;
    }

    @Scheduled(fixedRate = 60_000) // указываем время запланированного запуска (соответствует 1 минуте)
    public void increaseBalance() {
        accountService.increaseBalanceForAllAccounts();
    }
}
```

Является безопасным для транзакций благодаря аннотации над методом findAll() в AccountRepository

```java

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    // позволяет делать SELECT ... FOR UPDATE
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @NonNull
    List<Account> findAll();

    // code...
}
```

---

**Реализовать функционал перевода денег с одного счета на другой. Со счета аутентифицированного пользователя, на счёт
другого пользователя. Сделать все необходимые валидации и потокобезопасной.**

### Перевод денег (метод контроллера)

```java

@PatchMapping("/{id}/transfer")
public ResponseEntity<Response> transfer(@PathVariable("id") long id, @AuthenticationPrincipal BankUserDetails userDetails,
                                         @RequestBody @Valid TransferDTO request, BindingResult bindingResult) {
    User user = userDetails.user();
    log.info("Метод transfer начал выполнение для пользователя={}", user.getUsername());

    long accountId = user.getAccount().getId();
    // если ID аккаунта аутентифицированного пользователя не соответствует ID в URL, то выбрасывается исключение
    if (accountId != id) throw new ForbiddenException();
    // нельзя переводить на свой счет
    if (accountId == request.getAccountId()) throw new NotAllowedException();
    // если есть ошибки при валидации, то будет брошено исключение
    if (bindingResult.hasErrors()) throw new TransferNotProcessedException(ErrorUtil.getResponse(bindingResult));

    log.info("Попытка перевода с аккаунта id={} на аккаунт id={}, сумма перевода={}", id, request.getAccountId(), request.getAmount());
    accountService.transfer(id, request.getAccountId(), request.getAmount());

    log.info("Перевод от пользователя={} на сумму={} выполнен успешно", user.getUsername(), request.getAmount());
    return new ResponseEntity<>(new Response("Перевод выполнен успешно", LocalDateTime.now()), HttpStatus.OK);
}
```

Метод PATCH-запроса, в URL передается ID банковского счета, в качестве параметров принимает **TransferDTO** - класс,
соответствующий ключам, переданным в теле запроса в формате JSON, а именно **accountId** (аккаунт, на который будет
осуществлен перевод) и **amount** (сумма денег для перевода). Так же как и в других методах требующих аутентификации
передается параметр **userDetails**.

### Логика метода transfer из сервиса AccountService

```java
// объявляем что выполнение метода будет в рамках транзакции
// синхронизируем метод (делаем его потокобезопасным) с помощью synchronized
@Transactional
public synchronized void transfer(long fromId, long toId, double amount) {
    // если в базе данных нет банковских аккаунтов по заданным ID, то выбрасываем исключение
    Account accountFrom = accountRepository.findById(fromId).orElseThrow(AccountNotFoundException::new);
    Account accountTo = accountRepository.findById(toId).orElseThrow(AccountNotFoundException::new);
    // если в аккаунте, из которого исходит перевод недостаточно средств, то выбрасывается исключение
    if (amount > accountFrom.getAmount())
        throw new NotEnoughFundsException(accountFrom.getAmount());
    // удаляем сумму из исходящего счета и добавляем в принимающий счет
    accountFrom.setAmount(accountFrom.getAmount() - amount);
    accountTo.setAmount(accountTo.getAmount() + amount);

    accountRepository.flush();
}
```

Чтобы избежать состояние гонки в сигнатуру метода было добавлено ключевое слово **synchronized**, теперь этот метод не
будет выполняться одновременно из разных потоков, но этого недостаточно для успешных транзакций (переводов), поэтому в
**AccountRepository** на методы, которые соответствуют SELECT я поставил следующие аннотации...

```java

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    // code ...

    // SELECT ... FOR UPDATE
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Account> findById(long id);
}
```

При **SELECT**-е прочитанные строки будут заблокированы до тех пор пока не завершится транзакция, т.е. их не
смогут читать из других потоков. Теперь переводы потокобезопасные и проходят все тесты.

#### Пример запроса

```json
{
  "accountId": 150,
  "amount": 1000
}
```

#### Пример ответа

```json
{
  "message": "Перевод выполнен успешно",
  "time": "2024-03-04T17:05:39.8344266"
}
```

#### Пример ошибки (если недостаточно средств)

```json
{
  "errors": [
    {
      "message": "Недостаточно средств на счете",
      "time": "2024-03-04T15:44:59.0216489"
    }
  ]
}
```
