CREATE TABLE `dbs`.`order_delivery_addresses` (
  `order_id` INT NOT NULL,
  `city` VARCHAR(50) NOT NULL,
  `street` VARCHAR(50) NOT NULL,
  `building` VARCHAR(50) NOT NULL,
  `housing` VARCHAR(50) NULL,
  `apartment` VARCHAR(45) NULL)
  CHARACTER SET 'utf8';
