# Pronouncing

This is a server to keep track of songs.


## Dependencies

* Java 1.11
* Maven 3
* Postgresql 9 or higher


## Instructions

1. Download the code
```bash
git clone https://github.com/UCL/PopChat-BE.git
```

1. Create a popchat database
```bash
createdb popchat
createuser -P popchat # Make sure password is "popchat"
```

1. Run the code

```bash
mvn spring-boot:run
```

1. Or run the tests

```bash
mvn test
```
