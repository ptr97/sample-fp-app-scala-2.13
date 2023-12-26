CREATE TYPE PRODUCT_STATE AS ENUM ('open', 'closed');

CREATE TABLE IF NOT EXISTS products (
  code                  VARCHAR(128)    PRIMARY KEY,
  name                  VARCHAR(512)    NOT NULL,
  state                 PRODUCT_STATE   NOT NULL,
  min_offers_to_close   SMALLINT        NOT NULL CHECK (min_offers_to_close >= 0) DEFAULT 3
);

CREATE TABLE IF NOT EXISTS product_offers (
  product_code  VARCHAR(128)    NOT NULL,
  price         NUMERIC(12, 2)  NOT NULL,
  CONSTRAINT fk_product_code FOREIGN KEY(product_code) REFERENCES products(code)
);
