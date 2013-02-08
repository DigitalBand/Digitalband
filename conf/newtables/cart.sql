CREATE  TABLE `dbs`.`cart` (
  `cartId` INT NOT NULL AUTO_INCREMENT ,
  `createdDate` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ,
  `userId` INT NOT NULL ,
  PRIMARY KEY (`cartId`) ,
  UNIQUE INDEX `cartcol_UNIQUE` (`userId` ASC) );

  ALTER TABLE `dbs`.`shopping_items` ADD COLUMN `cartId` INT NULL  AFTER `unitPrice` ;

ALTER TABLE `dbs`.`shopping_items` CHANGE COLUMN `userId` `userId` INT(11) NOT NULL DEFAULT 0  ;
ALTER TABLE `dbs`.`shopping_items` CHANGE COLUMN `unitPrice` `unitPrice` DOUBLE NULL  ;
