# Balance-api

Simple API created with Kotlin to serve a frontend of an application to 
calculate the balance of expenses in a group of friends.

The purpose of this project was to learn about Kotlin language.

## How to execute

### The data base

You can use the Dockerfile in the project. Just go to the root and execute:

```
docker build -t mysql-container .
```
To build the container, and:
```
docker run --name mysql -p 3306:3306 -d -e DB_USER=balance -e DB_PASS=123456789 -e DB_NAME=dbbalance mysql-container
```

### The API

Just execute the BalanceApiApplication.kt class when the db is running.
