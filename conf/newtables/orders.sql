ALTER TABLE `dbs`.`orders`
CHANGE COLUMN `name` `name` VARCHAR(50) NULL DEFAULT NULL ,
ADD COLUMN `last_name` VARCHAR(50) NULL DEFAULT NULL AFTER `name`,
ADD COLUMN `middle_name` VARCHAR(50) NULL DEFAULT NULL AFTER `name`,
ADD COLUMN `comment` TEXT NULL DEFAULT NULL AFTER `last_name`,
ADD COLUMN `delivery_type` VARCHAR(45) NULL DEFAULT NULL AFTER `comment`;
