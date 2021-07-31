# Application Description
The application provide the api for retrieving bit coin spot price of the specific currency like NZD, USD,...

# 1. How to build and run application
## 1.1 How to build
* Project built by SpringBoot Microservice Framework with Maven Build. Goto root folder of project and type cmd below:
  * mvnw clean install
* After install successfull, we will get file cryptocurrency-exchange-0.0.1-SNAPSHOT.jar in folder cryptocurrency-exchange\target
## 1.2 How to run
### 1.2.1 How to start application
* Type cmd below to run application:
  * cd cryptocurrency-exchange\target
  * java -jar cryptocurrency-exchange-0.0.1-SNAPSHOT.jar
### 1.2.2 How to run
* Application streaming to cloud kafka development duck, see info in application.properties.Use the cloud kafka console to check.
# 2. Coinbase API Using
* I am using link https://developers.coinbase.com/docs/wallet/guides/price-data to research and then get the coinbase endpoint https://api.coinbase.com/v2/
    because it don't require authentication, good for developing.
* The link https://exchange.blockchain.com/api/#request provides endpoint https://api.blockchain.com/exchange/ with required authentication. I saw it not only includes api key to request headers, also using specific library to hash message and includes hashed message to request header.
# 3. Application Design / Patterns
* Coding is following SOLID principles with focusing on Single Responsible and Open Close Principle. Also applying Composite Design Pattern.
* A CoinbaseExchangeService class includes SpotPriceService class, ProfitFactor class, ApiClient class.
  * ApiClient class responsible for retrieving spot price data from coinbase api.
  * SpotPriceService class responsible for retrieving spot price data from coinbase api, store data to memory for second retrieve data of streaming request.
  * ProfitFactor class responsible for random profit factor.
  * CoinbaseExchangeService calls SpotPriceService, ProfitFactor for getting data and calculating price.
* Input validation
  * Invalid Transaction: when transaction is not buy or sell
  * Invalid Amount: when amount is not a number
# 4. Solution Estimation
* Research Coinbase API: 3 hours
* Research Spring Cloud Stream, Stream to Kafka: 8 hours
* Document: 30 minutes
* Build And Test: 8 hours
* Total: 19.5 hours

