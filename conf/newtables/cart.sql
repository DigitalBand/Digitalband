


ALTER TABLE `shopping_items` CHANGE COLUMN `userId` `userId` INT(11) NOT NULL DEFAULT 0  ;
ALTER TABLE `shopping_items` CHANGE COLUMN `unitPrice` `unitPrice` DOUBLE NULL  ;
ALTER TABLE `cart` ADD COLUMN `updateDate` TIMESTAMP NULL  AFTER `userId` ;


ALTER TABLE `cart` CHANGE COLUMN `userId` `userId` INT(11) NULL
, DROP INDEX `cartcol_UNIQUE` ;

ALTER TABLE `dbs`.`products` CHANGE COLUMN `brandId` `brandId` INT(11)
  NOT NULL  ;

  ALTER TABLE orders ADD COLUMN `email` VARCHAR(85) NULL  AFTER `status` , ADD COLUMN `phone` VARCHAR(45) NULL  AFTER `email` , ADD COLUMN `name` VARCHAR(45) NULL  AFTER `phone` , ADD COLUMN `address` VARCHAR(255) NULL  AFTER `name` ;

