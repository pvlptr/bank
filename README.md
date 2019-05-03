# Bank

RESTful API for money transfers between accounts

## Rest API functionality
- create account (with zero balance)

## Prerequisites
- Java 12
- Maven 3+

## Building
    mvn clean install

## Running
    java -jar target\bank-1.0-SNAPSHOT.jar
    
## REST API
### List all accounts
- http://localhost:18081/api/1.0/accounts
- GET
- response example:
```json
    [
        {
            "id": "11111111-1111-1111-1111-111111111111",
            "name": "Main account",
            "balance": 25.15,
            "currency": "EUR"
        },
        {
            "id": "21111111-1111-1111-1111-111111111111",
            "name": "Internet account",
            "balance": 121.33,
            "currency": "EUR"
        },
        {
            "id": "31111111-1111-1111-1111-111111111111",
            "name": "Travel account (North America)",
            "balance": 0.10,
            "currency": "USD"
        }
    ]
```        
### View single account by id
- http://localhost:18081/api/1.0/accounts/11111111-1111-1111-1111-111111111111
- GET
- response example:
```json
    {
        "id": "11111111-1111-1111-1111-111111111111",
        "name": "Main account",
        "balance": 25.15,
        "currency": "EUR"
    }
```
### Create account
- http://localhost:18081/api/1.0/accounts
- POST
- request example:
```json
    {
        "name": "Another new account",
        "currency": "EUR"
    }
```
- response example:
```json
     {
        "id": "50a093f1-84e3-480d-b45a-76b66fbd47a7",
        "name": "Another new account",
        "balance": 0,
        "currency": "EUR"
    }
```        
### Transfer money from one account to another
- http://localhost:18081/api/1.0/transfers
- POST
- request example:
```json
    {
        "fromAccountId": "11111111-1111-1111-1111-111111111111",
        "toAccountId": "21111111-1111-1111-1111-111111111111",
        "amount": 0.15,
        "currency": "EUR"
    }
 ```
- response example (transaction):
```json
    {
        "id": "863ac56b-c152-4bb1-a46a-2470066ac4ce",
        "fromAccountId": "11111111-1111-1111-1111-111111111111",
        "toAccountId": "21111111-1111-1111-1111-111111111111",
        "amount": 0.15,
        "currency": "EUR",
        "created": "2019-04-01T12:35:32.663"
    }
``` 
### List all transactions
- http://localhost:18081/api/1.0/transactions
- GET
- response example (transaction):
```json
    [
        {
            "id": "863ac56b-c152-4bb1-a46a-2470066ac4ce",
            "fromAccountId": "11111111-1111-1111-1111-111111111111",
            "toAccountId": "21111111-1111-1111-1111-111111111111",
            "amount": 0.15,
            "currency": "EUR",
            "created": "2019-04-01T12:35:32.663"
        }
    ]
 ``` 
### View single transaction
- http://localhost:18081/api/1.0/transactions/:id
- GET
- response example (transaction):
```json
    {
        "fromAccountId": "11111111-1111-1111-1111-111111111111",
        "toAccountId": "21111111-1111-1111-1111-111111111111",
        "amount": 0.15,
        "currency": "EUR"
    }
``` 
### Errors
- 404 - Resource not found:
    - Account not found by id
    - Transaction not found by id
    - Unknown REST API url
- 400 - Insufficient funds:
    - Money transfer from one account to another - in account "from" not enough money
- 400 - Invalid operation:
    - Invalid money precision (amount should not have more than 2 digits after decimal point)
    - Invalid money amount (negative)
    - Invalid UUID structure
    - Missing required field
    - Transfer to same account
    - Transfer with currency conversion
    - Max account balance reached (10_000_000_000_000.0)  

### Integration tests for API usage
- AccountRestIT
- TransactionRestIT
- TransferRestIT

## Technologies
- JAVA
- Maven
- SparkFramework - rest end points (with embedded jetty)
- JUnit5 + hamcrest - testing (unit and integration tests)
- inmemory key/value data store implemented using java native inmemory structures 
    (ConcurrentHashMap + synchronized blocks to provide atomicity, consistency and isolation)
- sf4j + logback - logging

