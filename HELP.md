Spring Boot [КУРС]
https://www.youtube.com/playlist?list=PL3Ur78l82EFD8OKSulH3NaK1As4G7YWMJ
Ilya Lisov

2026.10.31 - начал делать с начала:
Самые новые версии джавы и зависимостей,
сделаю более правильный гит.

Приложение: tasklist, авторизация, добавление тасков
Работа со своими тасками (CRUD)

Технологии:
Репозиторий - JDBC
Собственные SQL-запросы => MyBatis с конфигурацией через XML
Авторизация: JWT token
Security expressions - проверка пользователя на доступ к енд-поинтам
Валидация над DTO: MapStruct - для более новых версий https://www.baeldung.com/java-mapstruct-lombok 
Spring Data JPA - миграции
Liquibase
Swagger
Docker

end-points:
авторизация
добавление таски
изменять статус задачи
удалять задачу

// =======================
// Видео1
пакет Domain: пользователь + роли + задача Task, статус задачи
Репозиторий - классы для доступа к БД 
controller -> service -> repository (3 уровня)

@SpringBootApplication = @SpringBootConfiguration + @EnableAutoConfiguration + @ComponentScan
Все компоненты, сервисы и репозитории будут автоматически находиться и связываться Спрингом

@Data (Lombok) = getters, setters, requiredArsConstructor, toString, equalsAndHashCode, value

enum ROLE = ROLE_USER + ROLE_ADMIN - начинаются с ROLE - это надо для спринга

extends RuntimeEception - делаем исключение unchecked, чтобы его можно было выбрасывать
ResourceMappingException - ошибки при маппинге в JDBC
AccessDeniedException - чтобы user.id==1 не смог обратиться к task.userId==2

Naming:
repository: findBy...
service: getBy...

Optional - может возвращать User | null - обёртка, которая не даст забыть проверить на пустое значение
repo возвращает Optional<User> <-> User | null
service возвращает User | exception

Методы в репозитории делают ОДНУ операцию - глупые/простые методы - умеют мало чего (ходят в БД и делают одну операцию)
  
Аннотации @Repository + @Service + @Component работают одинаково
никаких различий, кроме потенциально БУДУЩИХ, в обработке спрингом нет
аннотация repository улавливает исключения при работе с бд
@Repository, @Service, and @Controller are specializations of @Component for more specific use cases

JwtResponse не надо валидировать, т.к мы сами его создаём

@JsonProperty(access = JsonProperty.Access.WRITE_ONLY) - можем только принимать пароль, но не отдавать его в ответе

Long id = создаётся автоматически
ResourceNotFoundException - не нашли что-то по id
ResourceMappingException - ошибки когда достаём объект из БД
AccessDeniedException - имеет оим пользователь право доступа к ч-л
Mappers = ResultSet => Наши объекты

Репозитории:
Все методы - глупые / простые - ходят в БД и делают 1 операцию
Методы называются find...
В сервисах методы называются get...

@Repository + @Service + @Component - работают одинаково, но названы по-разному (возможно, чтобы потом было удобнее разрабатывать Spring)

Обращение в БД идёт всегда только через сервис:
TaskService => UserService => UserRepository... (ошибки обрабатываются в сервисе)

DTO - объект для передачи данных по интернету

JwtRequest - принимает username + password => авторизация 

MapStruct: Dto => Entity - Например, User => UserDto, UserDto => User

Jakarta/@NotNull - поля не могут быть нулевыми (в body ответа фронта)
JwtResponse - это наш ответ - его нет смысла валидировать, т.к сами его создаём
@NotNull(message = "Id must be not null.", groups = OnUpdate.class) - при создании у пользователя нет id, поэтому он @NotNull только при обновлении данных
@JsonProperty(access = JsonProperty.Access.WRITE_ONLY) - пароль только принимаем, но не отправляем в ответе назад

@Autowired писать не надо, т.к есть @RequiredArgsConstructor - спринг сам всё свяжет

---
DB
Не надо использовать public-схема, т.к она нужна для самой БД для её информации.
Использование публичной схемы небезопасно.

Spring Data JPA - библиотека, которая позволяет делать миграции при старте (application.yaml => sql + init + ...)
schema.sql - один раз надо создать схему и таблицы
demo_data.sql - создаём данные
BIGSERIAL = LONG + автоинкрементируется

Реализация сервисов, реализация репозиториев - JDBC + MyBatis

// =======================
// Видео2

Spring Boot Security

https://bcrypt-generator.com/
10 раз
12345 (это пароль пользователя внутри demo_data.sql)

JwtEntity - пользователь для Спринга
JwtEntityFactory - создаём JwtEntity из пользователя

bcrypt - шифрование паролей - bcrypt-generator.com + 10 раундов
Каждый по новому сгенерированный хеш будет отличаться. То есть, нельзя сравнить хеши напрямую, а надо сравнивать через алгоритм. Что делает его более безопасным.

JwtEntity - храним пользователя для спринга
JwtEntityFactory - создаём User -> JwtEntity, связка нашей логики и логики SpringSecurity
JwtUserDetailsService - авторизация пользователя
JwtTokenProvider - создаёт, проверяет - обеспечивает взаимодействие с токенами = авторизация через AccessToken + RefreshToken
JwtProperties - просто достаёт данные из application.yaml

JWT token авторизация
В токене хранится информация о пользователе.
Токен подписывается с помощью секретного слова/строки на сервере.
Только сервер может создавать такие токены. Прочитать и расшифровать токен может кто угодно.

Мы будем хранить в токене username, id и список ролей - refresh token.

Сервер создаёт Access + Refresh токены и отправляем их на фронт. 
Когда пользователь делает запрос, то в header запроса добавляет bearer ${token}.
BE по токену проверяет (FilterService) и пропускает пользователя дальше или нет.

Access токен живёт мало - потому что он часто бегает между FE и BE - его можно перехватить и подписать им свои вредные запросы
Refresh token живёт долго
    - если Access token не валиден, то BE возвращает unauthorized и не пропускает запрос
    - FE отправляет refresh token == запрос на обновление access токена
    - создаются новые access + refresh и отправляются на фронт - токены обновляются
    - идёт заново изначальный запрос

secret создаётся:
base64 encoder base64encode.net => ключ в application.yaml

SELECT * FROM tasklist3.users;
SELECT * FROM tasklist3.users_roles;
SELECT * FROM tasklist3.tasks;
SELECT * FROM tasklist3.users_tasks;

// =======================
// Видео3

Сервисы и репозитории

Порядок общения внутри приложения:
Контроллер -> Сервис -> Репозиторий -> База данных
Сервис -> другой сервис

SQL-запрос -> исполняем с помощью стейтментов

Стейтменты:
обычный - небезопасный, медленный, подвержен инъекциям
prepared - собирается форма, значения подставляются
callable - находятся на БД

везде инжектим интерфейсы, а не классы, которые расширяют данные интерфейсы
Инжектим интерфейсы потому что логика сервиса в его интерфейсе - все реализации имеют те же методы, соответственно мы должны использовать интерфейс. Согласно чистому коду системы взаимодействуют через интерфейсы, которые реализуются уже конкретными классами
В данном случае у нас один бин этого класса (реализация интерфейса - класс якобы наследуемый от интерфейса в контексте спринга), если бы их было два или больше, то приложение бы не запустилось в нашем случае.
Чтобы все сработало, необходимо использовать @Qualifier либо же инжектить конкретный класс.

Везде одна и та же ошибка -- Connection и PreparedStatement не закрываются в случае эксепшена при обращении к БД.

// =======================
// Видео4

MyBatis - Реализация репозитория (~JDBC)

Hibernate - мэпит объекты на таблицы в БД
MyBatis - мэпит строки на объекты в БД

Будем писать мапперы с помощью XML, т.к. так удобнее делать селекты и мапить с другими объектами
------------
Liquibase
миграции
позволяет заполнять БД данными при старте
------------
Swagger
http://localhost:8080/swagger-ui/index.html

// =======================
// Видео5

Security expression - проверка есть у пользователя права доступа ук данному энд-поинту
2 способа

Docker
запустить докер
Dockerfile - конфигурация, которая собирает приложение + копирует в контейнер + запускает
docker-compose.yaml
maven - tasklist - lifecycle - 1) clean, 2) package
terminal - docker compose up
docker compose up --build - чтобы пересобрал все контейнеры (если указан postgres:5432, а он подключается к localhost:5433 и падает liquibase)
теперь работает из контейнера (работает сваггер если приложение не запущено)

resources / db-init - эта папка будет добавляться в докер-контейнер - будет инициализировать БД ликвибейзом - ликвибейз будет создавать схему








# Read Me First
The following was discovered as part of building this project:

* The original package name 'com.example.tasklist3 ' is invalid and this project uses 'com.example.tasklist3' instead.

# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/4.1.2/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/4.1.2/maven-plugin/build-image.html)
* [Spring Web](https://docs.spring.io/spring-boot/4.1.2/reference/web/servlet.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/4.1.2/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Spring Data JDBC](https://docs.spring.io/spring-boot/4.1.2/reference/data/sql.html#data.sql.jdbc)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Using Spring Data JDBC](https://github.com/spring-projects/spring-data-examples/tree/main/jdbc/basics)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.

