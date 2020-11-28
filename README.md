# Lunch Microservice

The service provides an endpoint that will determine, from a set of recipes, what I can have for lunch at a given date, 
based on my fridge ingredient's expiry date, so that I can quickly decide what I’ll be having to eat, and the ingredients 
required to prepare the meal.

## Prerequisites

* [Java 11 Runtime](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
* [Docker](https://docs.docker.com/get-docker/) & [Docker-Compose](https://docs.docker.com/compose/install/)

*Note: Docker is used for the local MySQL database instance, feel free to use your own instance or any other SQL database and insert data from lunch-data.sql script* 

## Configuration

The application supports certain runtime environment variables that can either configure its components or change its behaviour aspects.

DB configuration
- DB_DRIVER_TYPE
- DB_HOST
- DB_PORT
- DB_NAME
- DB_USER
- DB_PASSWORD

There is a choice between two implementations of the same service: criteria- and native-query based recipe filters. 
While the Criteria API allows for a more object-oriented and domain-aware API, in this particular scenario it performs 
a bit worse than the native SQL query implementation.

This choice depends on one of the Spring profiles enabled:
- **criteria-query**
- **native-query**
  
Here is an example of a start command with a complete set of options passed to it:
```
export DB_DRIVER_TYPE=postgres 
export DB_USER=postgres 
export DB_PASSWORD= 
export DB_NAME=lunch 
export DB_PORT=5432 
export DB_HOST=localhost 

java -Dspring.active.profiles=native-query -jar ./java-tech-task-0.0.1-SNAPSHOT-jar-with-dependencies.jar 
```

### Test

To execute tests, run:
```
mvn test
```

### Run

1. Start database:

    ```
    docker-compose up -d
    ```
   
2. Add test data from  `sql/lunch-data.sql` to the database. Here's a helper script if you prefer:


    ```
    CONTAINER_ID=$(docker inspect --format="{{.Id}}" lunch-db)
    ```
    
    ```
    docker cp sql/lunch-data.sql $CONTAINER_ID:/lunch-data.sql
    ```
    
    ```
    docker exec $CONTAINER_ID /bin/sh -c 'mysql -u root -prezdytechtask lunch </lunch-data.sql'
    ```
    
3. Run Springboot LunchApplication

### Distribute

In order to create a distributable ZIP package of the application, run (triggers tests execution as well):
```
mvn package
```

If successful, a ZIP file with the runnable JAR file will be created at: 
```
target/java-tech-task-${project.version}.zip
```