/*
SQLyog Community v13.3.0 (64 bit)
MySQL - 10.4.32-MariaDB : Database - estrandyante
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`estrandyante` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;

USE `estrandyante`;

/*Table structure for table `choices` */

DROP TABLE IF EXISTS `choices`;

CREATE TABLE `choices` (
  `AnsID` int(11) NOT NULL AUTO_INCREMENT,
  `Answer` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`AnsID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `choices` */

insert  into `choices`(`AnsID`,`Answer`) values 
(1,'A'),
(2,'B'),
(3,'C'),
(4,'D');

/*Table structure for table `questionnaires` */

DROP TABLE IF EXISTS `questionnaires`;

CREATE TABLE `questionnaires` (
  `QuestionID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `QDescription` varchar(255) DEFAULT NULL,
  `QAChoice` varchar(255) DEFAULT NULL,
  `QBChoice` varchar(255) DEFAULT NULL,
  `QCChoice` varchar(255) DEFAULT NULL,
  `QDChoice` varchar(255) DEFAULT NULL,
  `AnsID` int(25) NOT NULL,
  `StrandID` int(10) unsigned NOT NULL,
  PRIMARY KEY (`QuestionID`),
  KEY `AnsID` (`AnsID`),
  KEY `StrandID` (`StrandID`),
  CONSTRAINT `questionnaires_ibfk_2` FOREIGN KEY (`AnsID`) REFERENCES `choices` (`AnsID`),
  CONSTRAINT `questionnaires_ibfk_3` FOREIGN KEY (`StrandID`) REFERENCES `strands` (`StrandID`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


DROP TABLE IF EXISTS `roles`;

CREATE TABLE `roles` (
  `RoleID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `RoleName` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`RoleID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `roles` */

insert  into `roles`(`RoleID`,`RoleName`) values 
(1,'Admin'),
(2,'Student'),
(3,'Faculty');

/*Table structure for table `strands` */

DROP TABLE IF EXISTS `strands`;

CREATE TABLE `strands` (
  `StrandID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `StrandName` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`StrandID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `strands` */

insert  into `strands`(`StrandID`,`StrandName`) values 
(1,'STEM'),
(2,'ABM'),
(3,'HUMSS'),
(4,'ICT');

/*Table structure for table `students` */

DROP TABLE IF EXISTS `students`;

CREATE TABLE `students` (
  `StudID` int(11) NOT NULL AUTO_INCREMENT,
  `UserID` int(11) NOT NULL,
  `Email` varchar(80) NOT NULL,
  `Fname` varchar(80) NOT NULL,
  `Lname` varchar(80) NOT NULL,
  `Mname` varchar(80) NOT NULL,
  `Ename` varchar(80) DEFAULT NULL,
  `Birthdate` date NOT NULL,
  `RecommendedStrand` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`StudID`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `UserID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Email` varchar(256) DEFAULT NULL,
  `Username` varchar(256) NOT NULL,
  `Password` varchar(80) NOT NULL,
  `RoleID` int(10) unsigned NOT NULL,
  PRIMARY KEY (`UserID`),
  KEY `RoleID` (`RoleID`),
  CONSTRAINT `users_ibfk_1` FOREIGN KEY (`RoleID`) REFERENCES `roles` (`RoleID`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
