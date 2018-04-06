
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- Dumping database structure for accountingdb
DROP DATABASE IF EXISTS accountingdb_ngac;
CREATE DATABASE IF NOT EXISTS `accountingdb_ngac` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `accountingdb_ngac`;

DROP TABLE IF EXISTS `accounting_table`;
-- Dumping structure for table accountingdb_ngac.accounting_table
CREATE TABLE IF NOT EXISTS `accounting_table` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `SessionID` int(11) NOT NULL,
  `RequestingEntity` varchar(200) NOT NULL,
  `Consumer` varchar(200) NOT NULL,
  `Producer` varchar(200) NOT NULL,
  `ServiceName` varchar(200) NOT NULL,
  `InboundRequests` int(11) NOT NULL,
  `OutResponses` int(11) NOT NULL,
  `SessionStartTime` bigint(11) NOT NULL,
  `SessionEndTime` bigint(11) NOT NULL,
  `MinRequestSize` int(11) NOT NULL,
  `MaxRequestSize` int(11) NOT NULL,
  `IPaddressChange` int(11) NOT NULL,
  `TerminationCause` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY (`SessionID`, `RequestingEntity`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='This table contains the accounting information';
