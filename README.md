# Pronouncing

This is a server to keep track of songs.


## Dependencies

* Java 1.11, although the project can be built for other Java versions
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
    mvn spring-boot:run [-Djava.version=<Your Java Version>]
    ```
    * You can check the server is live by opening a browser on `http://localhost:8080/words/rhymes-with/bee`.

1. Or run the tests

    ```bash
    mvn -Dspring.profiles.active=test [-Djava.version=<Your Java Version>] test
    ```

## Set up options

You can set set up options in `src/main/resources/application.properties`

The following options are used to configure the application:

 - `spring.datasource.url`: The connection string for the database (eg. jdbc:postgresql://localhost:5432/popchat)
 - `spring.datasource.username`: The database username
 - `spring.datasource.password`: The database password
 - `initialSongDataDirectory`: The directory to look for song jsons to preload the database. `/` is `src/main/resources`
 - `salt.algorithm`: The algorithm to use the generate random salts. Supported options are: `SHA1PRNG`
 - `hash.algorithm`: The hashing algorithm to use. Supported options are: `PBFDF2`
 - `salt.length`: Length of the salt
 - `hash.length`: Length of the hash
 - `hash.iterations`: Number of hashing iterations.
 
 Note that the length of salt + length of hash must be <= 600.

### Song JSON format

Example Song JSON file

```json
{
 "artist": "Artist name",
 "title": "Song title",
 "year": 2015,
 "video": "URL to the video",
 "lyrics": "[00:20.500]Sing song lyrics in LRC format"
}
```

### Endpoints Documentation

Documentation about the endpoints is automatically generated with Swagger.
It can be accessed at `/swagger-ui.html`, e.g. http://localhost:8080/swagger-ui.html.
The raw Swagger config data can be accessed at `/v2/api-docs`.
