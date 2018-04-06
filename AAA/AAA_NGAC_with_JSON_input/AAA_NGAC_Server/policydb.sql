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

-- Dumping database structure for policydb
DROP DATABASE IF EXISTS policydb_ngac;
CREATE DATABASE IF NOT EXISTS `policydb_ngac` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `policydb_ngac`;


--
-- Table structure for table `arrowheadcloud`
--

DROP TABLE IF EXISTS `arrowheadcloud`;

CREATE TABLE `arrowheadcloud` (
  `cloud_id` int(11) NOT NULL AUTO_INCREMENT,
  `authenticationInfo` varchar(255) DEFAULT NULL,
  `cloudName` varchar(255) DEFAULT NULL,
  `operator` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`cloud_id`),
  UNIQUE KEY `UK3u1wgfsf3ayneoxt5264me60p` (`operator`,`cloudName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;




DROP TABLE IF EXISTS `node`;
-- Dumping structure for table policydb.node
CREATE TABLE IF NOT EXISTS `node` (
  `node_id` int(11) NOT NULL AUTO_INCREMENT,
  `nodetype_id` int(11) NOT NULL,
  `name` varchar(200) DEFAULT NULL,
  `description` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`node_id`),
  KEY `node_type_id_idx` (`nodetype_id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='This table contains all the nodes in the graph';

-- Data exporting was unselected.

--
-- Table structure for table `arrowheadsystem`
--

DROP TABLE IF EXISTS `arrowheadsystem`;

CREATE TABLE `arrowheadsystem` (
  `system_id` int(11) NOT NULL AUTO_INCREMENT,
  `IPAddress` varchar(255) DEFAULT NULL,
  `authenticationInfo` varchar(255) DEFAULT NULL,
  `port` varchar(255) DEFAULT NULL,
  `node_id` int (11) DEFAULT NULL,
  `systemName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`system_id`),
  CONSTRAINT `fk_node_id` FOREIGN KEY (`node_id`) REFERENCES `node` (`node_id`) ON DELETE CASCADE
  -- CONSTRAINT `fk_name` FOREIGN KEY (`name`) REFERENCES `node` (`name`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `arrowheadservice`
--

DROP TABLE IF EXISTS `arrowheadservice`;
CREATE TABLE `arrowheadservice` (
  `service_id` int(11) NOT NULL AUTO_INCREMENT,
  `metaData` varchar(255) DEFAULT NULL,
  `serviceName` varchar(255) DEFAULT NULL,
  `set_id` int (11) DEFAULT NULL,
   PRIMARY KEY (`service_id`),
   CONSTRAINT `fk_set_id` FOREIGN KEY (`set_id`) REFERENCES `operation_set` (`set_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `node_type`;
-- Dumping structure for table policydb.node_type
CREATE TABLE IF NOT EXISTS `node_type` (
  `nodetype_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `description` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`nodetype_id`),
  KEY `idx_node_type_description` (`description`),
  KEY `idx_node_type_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='This table contains node types';

DROP TABLE IF EXISTS `assignment`;
-- Dumping structure for table policydb.assignment
CREATE TABLE IF NOT EXISTS `assignment` (
  `assignment_id` int(11) NOT NULL AUTO_INCREMENT,
  `start_node_id` int(11) DEFAULT NULL,
  `end_node_id` int(11) DEFAULT NULL,
  `depth` int(11) DEFAULT NULL,
  `assignment_path_id` int(2) DEFAULT NULL,
  PRIMARY KEY (`assignment_id`),
  KEY `end_node_id_idx` (`end_node_id`),
  KEY `fk_start_node_id_idx` (`start_node_id`),
  KEY `idx_all_columns` (`start_node_id`,`depth`,`assignment_path_id`,`end_node_id`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='This table stores assignment relations';

-- Data exporting was unselected.

DROP TABLE IF EXISTS `assignment_path`;
-- Dumping structure for table policydb.assignment_path
CREATE TABLE IF NOT EXISTS `assignment_path` (
  `assignment_path_id` int(11) NOT NULL AUTO_INCREMENT,
  `node_id` int(11) NOT NULL,
  PRIMARY KEY (`assignment_path_id`),
  KEY `fk_assignment_node_id` (`node_id`),
  CONSTRAINT `fk_assignment_node_id` FOREIGN KEY (`node_id`) REFERENCES `node` (`node_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.

DROP TABLE IF EXISTS `association`;
-- Dumping structure for table policydb.association
CREATE TABLE IF NOT EXISTS `association` (
  `association_id` int(11) NOT NULL AUTO_INCREMENT,
  `ua_id` int(11) NOT NULL,
  `operation_id` int(11) NOT NULL,
  `oa_id` int(11) NOT NULL,
  PRIMARY KEY (`association_id`),
  CONSTRAINT `fk_opset_id` FOREIGN KEY (`operation_id`) REFERENCES `operation` (`operation_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `operation`;
-- Dumping structure for table policydb.operation
CREATE TABLE IF NOT EXISTS `operation` (
  `operation_id` int(11) NOT NULL AUTO_INCREMENT,
  `operation_type_id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `description` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`operation_id`),
  UNIQUE KEY `operation_id_UNIQUE` (`operation_id`),
  KEY `operation_type_id_idx` (`operation_type_id`),
  KEY `idx_operation_name` (`name`),
  CONSTRAINT `fk_operation_type_id` FOREIGN KEY (`operation_type_id`) REFERENCES `operation_type` (`operation_type_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Operation';

-- Data exporting was unselected.

DROP TABLE IF EXISTS `operation_set`;
-- Dumping structure for table policydb.operation_set
CREATE TABLE IF NOT EXISTS `operation_set` (
  `set_id` int(11) NOT NULL AUTO_INCREMENT, 
  `operation_node_id` int(11) NOT NULL,
  `operation_id` int(11) NOT NULL,
  PRIMARY KEY (`set_id`),
  KEY `fk_op_set_operation_id_idx` (`operation_id`),
  CONSTRAINT `fk_op_set_operation_id` FOREIGN KEY (`operation_id`) REFERENCES `operation` (`operation_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_operation_set_details_node_id` FOREIGN KEY (`operation_node_id`) REFERENCES `node` (`node_id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='This table contains the information for User operation node';

-- Data exporting was unselected.

DROP TABLE IF EXISTS `operation_type`;
-- Dumping structure for table policydb.operation_type
CREATE TABLE IF NOT EXISTS `operation_type` (
  `operation_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`operation_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Operation types';

-- Data exporting was unselected.