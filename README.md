# PlayTech
PlayTech Assignment

Implement client and server in Java. 
## Description 
Server offers service for player wallet (balance).
Wallet state (balance) should be managed in memory (3rd party solution may not be used).
Balance is backed up in database (hsql or sqlite). 
When balance is not in memory, it is loaded from database and any changes are done in memory. 
Player record in database is created on demand. 

There is a periodical background process to write changes from memory to database. 
  Constraints on balance changes: 
  
  • Balance cannot be less than 0. 
  • If transaction exists (is duplicate), then previous response is returned. Check may take into consideration only 1000 latest transactions. 
  • If balance change is bigger than configured limit, then change is denied (explained further below). 
  • If player is in blacklist, then change is denied (explained further below). Configuration (balance change limit and player blacklist) must be taken from external source. This can be file, database, external component etc. Client itself is a server that offers gameplay logic. Specific gameplay will not be implemented, client can just generate random balance updates. Specific communication protocol between client and server is not specified (custom protocol can be invented). Server must write proper log information, where at least IN/OUT per player must be grep’able. 
  
  ## Commands between servers: 
    client->server: username, transaction id, balance change 
    server->client: transaction id, error code, balance version, balance change, balance after change 
    Database structure PLAYER(USERNAME, BALANCE_VERSION, BALANCE)
  ## Documentation: 
  
  • Describe shortly the implementation aspects. 
  • If some features are not implemented, point out the reasons   Building and Packaging: 
  • The solution can be built using any commonly available build framework (e.g. Maven, Gradle, Ant). Providing project-local wrappers for running the build in-place are appreciated. 
  • As a part of the build, the solution should be packaged into Docker containers. Do not publish the solution to a public Docker repository; providing Dockerfiles and docker-compose or instructions on how to build and run the containerized solution should be enoug
  
  ## Architecture

![Diagram](https://github.com/nithinprasad/PlayTech/blob/main/boot-32_5.jpeg?raw=true)
  
  
  Sample Request For Credit
  ```
  {
    "userName": "nithin2",
    "transactionId": "456",
    "balance": "123"
  }
  ```
  Sample Request For Debit
  ```
  {
    "userName": "nithin2",
    "transactionId": "456",
    "balance": "-123"
  }
  ```
  
  Sample Response
  ```
  {
    "transactionId": "456",
    "balanceVersion": "1",
    "errorCode": null,
    "balanceChange": 0,
    "balanceAfterChange": 123
  }
  ```
List Of Error codes
* BALANCE_CANNOT_BE_LESS_THAN_ZERO,
* BALANCE_CAANOT_BE_MORE_THAN_CONFIGURED_LIMIT,
* USER_IS_CURRENTLY_BLOCKED,
* DUPLICATE_TRANSACTION
	
Screenshots
![Diagram](https://github.com/nithinprasad/PlayTech/blob/main/client1.png?raw=true)
![Diagram](https://github.com/nithinprasad/PlayTech/blob/main/client2.png?raw=true)


