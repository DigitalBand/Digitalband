CREATE  TABLE `dbs`.`questions` (
  `questionId` INT NOT NULL AUTO_INCREMENT ,
  `productId` INT NOT NULL ,
  `email` VARCHAR(100) NOT NULL ,
  `type` VARCHAR(45) NOT NULL DEFAULT 'availability' ,
  PRIMARY KEY (`questionId`) );

  ALTER TABLE `dbs`.`questions` ADD COLUMN `status` VARCHAR(45) NOT NULL DEFAULT 'unanswered'  AFTER `type` ;
