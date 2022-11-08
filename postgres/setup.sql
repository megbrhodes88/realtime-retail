CREATE SCHEMA elephant;
SET search_path TO elephant;

CREATE EXTENSION postgis;

-- # create and populate products data table
CREATE TABLE products (
	product_id VARCHAR(24) PRIMARY KEY,
    car_year VARCHAR(8),
    car_make VARCHAR(24),
    car_model VARCHAR(24),
    product_name VARCHAR(64),
    product_price INT,
    product_subtitle VARCHAR(65535),
    product_desc VARCHAR(65535)
);

COPY products(product_id,car_year,car_make,car_model,product_name,product_price,product_subtitle,product_desc)
FROM '/data/products.csv'
DELIMITER ','
CSV HEADER;
