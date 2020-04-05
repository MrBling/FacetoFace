# Host: localhost  (Version 5.7.21)
# Date: 2020-04-05 19:34:26
# Generator: MySQL-Front 6.1  (Build 1.23)


#
# Structure for table "record"
#

DROP TABLE IF EXISTS `record`;
CREATE TABLE `record` (
  `Id` int(5) unsigned NOT NULL DEFAULT '0',
  `classes` varchar(20) DEFAULT NULL,
  `Record_class` varchar(50) DEFAULT NULL,
  `PId` int(4) DEFAULT NULL,
  `name` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `PId` (`PId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Data for table "record"
#

INSERT INTO `record` VALUES (0,'ruan2','5.1.2',1,'赵帅斌');

#
# Structure for table "ruan1"
#

DROP TABLE IF EXISTS `ruan1`;
CREATE TABLE `ruan1` (
  `PId` int(4) NOT NULL DEFAULT '0',
  `name` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`PId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Data for table "ruan1"
#


#
# Structure for table "ruan2"
#

DROP TABLE IF EXISTS `ruan2`;
CREATE TABLE `ruan2` (
  `PId` int(4) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`PId`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

#
# Data for table "ruan2"
#

INSERT INTO `ruan2` VALUES (1,'赵帅斌'),(2,'殷天豪'),(5,'罗锦程');
