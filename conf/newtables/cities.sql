ALTER TABLE `dbs`.`cities` 
ADD COLUMN `delivery` TEXT CHARACTER SET 'utf8' NULL AFTER `domain`,
ADD COLUMN `payment` TEXT CHARACTER SET 'utf8' NULL AFTER `delivery`;

# add prefix
ALTER TABLE `dbs`.`cities`
ADD COLUMN `prefix` VARCHAR(45) NULL DEFAULT NULL AFTER `phone`;