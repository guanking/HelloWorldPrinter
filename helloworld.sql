		#################################################
		#												#
        # 		ip:					121.42.158.80		#
        # 		databasename:		helloworld			#
        # 		user:				root				#
        # 		password:			caoguanjie			#
        #												#
        #################################################



###########################################################################
#
#   Create table:user,seller and order. 
#
############################################################################
CREATE SCHEMA `helloworld` DEFAULT CHARACTER SET utf8 ;
use helloworld;
CREATE TABLE `helloworld`.`user` (
	`userphone` VARCHAR(11) NOT NULL,
	`address` LONGTEXT NOT NULL,
	`password` VARCHAR(45) NOT NULL,
	PRIMARY KEY (`userphone`)
);
CREATE TABLE `helloworld`.`seller` (
	`sellerphone` VARCHAR(11) NOT NULL,
	`latitude` DOUBLE NOT NULL,
	`longitude` DOUBLE NOT NULL,
	`message` LONGTEXT NOT NULL,
	PRIMARY KEY (`sellerphone`)
);
CREATE TABLE `helloworld`.`order` (
	`userphone` VARCHAR(11) NOT NULL,
	`sellerphone` VARCHAR(11) NOT NULL,
	`orderid` VARCHAR(12) NOT NULL,
	`paths` LONGTEXT NOT NULL,
	`price` DOUBLE NOT NULL,
	`message` LONGTEXT NOT NULL,
	PRIMARY KEY (`orderid`)
);
ALTER TABLE `helloworld`.`order` 
	ADD INDEX `userphoneprimarykey_idx` (`userphone` ASC),
    ADD INDEX `sellerphoneprimarykey_idx` (`sellerphone` ASC);
ALTER TABLE `helloworld`.`order` 
	ADD CONSTRAINT `userphoneprimarykey` FOREIGN KEY (`userphone`) REFERENCES `helloworld`.`user` (`userphone`)
	ON DELETE CASCADE ON UPDATE CASCADE,
	ADD CONSTRAINT `sellerphoneprimarykey` FOREIGN KEY (`sellerphone`) REFERENCES `helloworld`.`seller` (`sellerphone`)
	ON DELETE CASCADE ON UPDATE CASCADE;
#############################################################################
#
#   Insert one pice of message for each table
#
#############################################################################
INSERT INTO `helloworld`.`user` (`userphone`, `address`, `password`) 
	VALUES ('18405813906', '杭州电子科技大学', '123456');
INSERT INTO `helloworld`.`seller` (`sellerphone`, `latitude`, `longitude`, `message`) 
	VALUES ('18405813906', '12.3', '-90', '{address:杭州电子科技大学}');
INSERT INTO `helloworld`.`order` (`userphone`, `sellerphone`, `orderid`, `paths`, `price`, `message`) 
	VALUES ('18405813906', '18405813906', '201511081245', 'c://TEMP/PATH', '12.4', '杭州电子科技大学');
########################################################################################
#
#  Test the cascade action
#
#########################################################################################
DELETE FROM `helloworld`.`seller` WHERE `sellerphone`='18405813906';
SELECT * FROM helloworld.seller;
SELECT * FROM helloworld.`order`;
DELETE FROM `helloworld`.`user` WHERE `userphone`='18405813906';
SELECT * FROM helloworld.user;
SELECT * FROM helloworld.`order`;
#############################################################################
#
# function for connection
#
#############################################################################
select `order`.*  from `seller`,`order`
	where `seller`.`sellerphone`=`order`.`sellerphone` and `seller`.`sellerphone`='18405813906';
select `order`.*  from `user`,`order`
	where `user`.`userphone`=`order`.`sellerphone` and `user`.`userphone`='18405813906';
    

