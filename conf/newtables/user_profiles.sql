ALTER TABLE `dbs`.`user_profiles`
ADD COLUMN `city` VARCHAR(45) NULL AFTER `address`,
ADD COLUMN `street` VARCHAR(45) NULL AFTER `city`,
ADD COLUMN `building` VARCHAR(45) NULL AFTER `street`,
ADD COLUMN `housing` VARCHAR(45) NULL AFTER `building`,
ADD COLUMN `apartment` VARCHAR(45) NULL AFTER `housing`,
ADD COLUMN `user_last_name` VARCHAR(50) NULL DEFAULT NULL AFTER `user_name`,
ADD COLUMN `user_middle_name` VARCHAR(50) NULL DEFAULT NULL AFTER `user_last_name`;

