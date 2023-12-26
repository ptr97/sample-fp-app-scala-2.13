# Product Offer Aggregation Service

It's an application that aggregates offers for a product.
An offer has two attributes: a floating point "price" and a string "productCode".
A "product offer aggregation" has the following attributes: min, max, average price of the aggregated offers, and count of offers.
An aggregation can be in two states, "open" or "closed".
Only aggregations with more than N offers can be closed (where N is configurable and defaults to 3).

Acceptance criteria:
1. A closed aggregation does not accept any more offers.
2. The service's API should allow:
   - querying for an aggregation for a given product code,
   - closing the aggregation,
   - supplying offers for the aggregation.
3. The offers to aggregate should be batched or streamed.


## Prerequisites

In order to run an application you have to have:
- [Scala 2.13](https://www.scala-lang.org/download/2.13.12.html)
- Java 11 or higher e.g. [Adoptium/Temurin](https://adoptium.net/),
- [sbt](https://www.scala-sbt.org/),
- [Docker](https://www.docker.com/).


## Run the application

In order to run the application use attached script:
- `docker` must be running in the background
- add execute permission to file `run.sh`:
    ```bash
    chmod +x run.sh
    ```
- in order to start the http app use:
    ```bash
    ./run.sh app
    ```
- in order to all tests (tests also requires running `docker`) use:
    ```bash
    ./run.sh tests
    ```
- in order to clean all `docker` artifacts use:
    ```bash
    ./run.sh clean
    ```

Http application starts on `localhost:9000` by default, but you configure its host and port in `src/main/resources/application.conf`.

## API

Application allows to:
- get product aggregation by product code:
  - `GET /products/{product-code}`
      ```curl
      curl --request GET \
      --url http://localhost:9000/products/product-1
      ```
- modify product state:
  - `PUT /products/{product-code}`
      ```curl
      curl --request PUT \
      --url http://localhost:9000/products/product-1 \
      --header 'Content-Type: application/json' \
      --data '{
        "state": "closed"
      }'
      ```
  - allowed states are: `open` and `closed`
  - you can always change product state to `open`
  - you can change product state to `closed` only ih the product has enough offers
- add offer for product:
   - `POST /products/{product-code}`
      ```curl
      curl --request POST \
      --url http://localhost:9000/products/product-1 \
      --header 'Content-Type: application/json' \
      --data '{
        "price": 150
      }'
      ```
   - you can add offer only for the product in `open` state

## Test data:

In database there are few products with codes from `product-1` to `product-10`. 
