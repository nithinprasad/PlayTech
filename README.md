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
  
  Code can run via

```python
./mvnw spring-boot:run
```
client ui is available at [http://localhost:8080/](http://localhost:8080/)


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

### Websocket Config

Cors are disabled and allow only for same origin


```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }
}
```

### Websocket Controller
```java
@Controller
public class WalletController {

	@Autowired
	WalletUpdateService service;
	
    @MessageMapping("/update")
    @SendTo("/topic/public")
    public WalletUpdateResponse register(@Payload WalletUpdateInput input, SimpMessageHeaderAccessor headerAccessor) {
    	MDC.put("username",input.getUserName());
    	WalletUpdateResponse updateResponse= service.updateWallet(input);
		MDC.clear();
		return  updateResponse;
    }

}
```

### Logging

Format
```
 ${CONSOLE_LOG_PATTERN:-%clr(SERVER){green} %clr(%d{${LOG_DATEFORMAT_PATTERN:-yyyy-MM-dd HH:mm:ss.SSS}}){faint} %clr(${LOG_LEVEL_PATTERN:-%5p}) %clr(${PID:- }){magenta} %clr(%X{username:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}}
```
Sample
```
SERVER 2022-02-15 15:09:14.956  INFO 67464 nithin --- [nboundChannel-9] c.p.n.p.s.service.WalletUpdateService    : OutGoing Response WalletUpdateResponse(transactionId=null, balanceVersion=2, errorCode=null, balanceChange=11, balanceAfterChange=22)

```
Can grep logs by username in above case its nithin


### Enviornment

Default Running on port `8080`

#### Enviornment Varibale
| Name     | Default Value  | Describtion   |
|--------- |----------------|---------------|
|  spring.datasource.url |jdbc:hsqldb:mem:testdb;DB_CLOSE_DELAY=-1   | hostname/service where hsql server is running  |
|  spring.datasource.username|sa   | username    |
|  spring.datasource.password|   | password    |
|  server.port| 8080  |   default port  |
|  limit.maxlimit| 10000  |   maximum configurable limit  |
|  scheduler.interval| 100000  |  Scheduler interval for periodic backup  |

### Docker File 
Can build a docker image by
```
docker build -t nithinprasad549/playtech .
```
Alternative can build docker image by
```
./mvnw spring-boot:build-image
```

Docker compose file
```
 version: '3'
 services:
  playtech:
    image: nithinprasad549/playtech
    environment:
     - spring.datasource.url=jdbc:hsqldb:mem:testdb;DB_CLOSE_DELAY=-1
     - spring.datasource.username=sa
     - limit.maxlimit=10000
     - scheduler.interval=100000
```


Screenshots
![Diagram](https://github.com/nithinprasad/PlayTech/blob/main/client1.png?raw=true)
![Diagram](https://github.com/nithinprasad/PlayTech/blob/main/client2.png?raw=true)


