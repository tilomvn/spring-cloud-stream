# Application Description
The application provide the api for retrieving bit coin spot price of the specific currency like NZD, USD,...

# 1. How to build and run application
## 1.1 How to build
* Project built by SpringBoot Microservice Framework with Maven Build. Goto root folder of project and type cmd below:
  * mvnw clean install
* After install successfull, we will get file cryptocurrency-exchange-0.0.1-SNAPSHOT.jar in folder cryptocurrency-exchange\target
## 1.2 How to run
### 1.2.1 How to start application
* Before you run application, make sure you have already installed Kafka Server on your environment(default port: 9092).
* Type cmd below to run application:
  * cd cryptocurrency-exchange\target
  * java -jar cryptocurrency-exchange-0.0.1-SNAPSHOT.jar
* After started application, the topic coinexchange-in-0, coin-exchange-out-0 would be created on kafka server.
### 1.2.2 How to run
* Access to apis for running app: http://localhost:4050/coinexchange/{transaction}/{currency}/{amount}.
* We can also send message with format {transaction}/{currency}/{amount} to topic coinexchange-in-0 by kafka console producer.
* Message format: transaction: [buy or sell], currency:[USD, NZD,...], amount: number[1,2,...]
* Reference to test evidence in mock folder for more detail.
# 2. Coinbase API Using
* I am using link https://developers.coinbase.com/docs/wallet/guides/price-data to research and then get the coinbase endpoint https://api.coinbase.com/v2/
    because it don't require authentication, good for developing.
* It supports 3 api for retrieving spot price:
  * How many money of specified currency for 1 BTC: https://api.coinbase.com/v2/prices/spot?currency=NZD
  * How many money of specified currency for buying 1 BTC: https://api.coinbase.com/v2/prices/buy?currency=NZD
  * How many money of specified currency for selling 1 BTC: https://api.coinbase.com/v2/prices/sell?currency=NZD
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
# Others
* The integration test of coding could not be done. It run ok but could not get expected result. Still researching. Please reference to manual integration test evidence in folder mock.
* Please check the bonus point: the application supports other currency and the streaming data should be retrieved within 1 second.s

