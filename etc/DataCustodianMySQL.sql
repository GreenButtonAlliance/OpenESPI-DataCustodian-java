CREATE DATABASE  IF NOT EXISTS `datacustodian` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `datacustodian`;
-- MySQL dump 10.13  Distrib 5.5.32, for debian-linux-gnu (x86_64)
--
-- Host: 127.0.0.1    Database: datacustodian
-- ------------------------------------------------------
-- Server version	5.5.32

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

--
-- Table structure for table `time_configurations`
--

DROP TABLE IF EXISTS `time_configurations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `time_configurations` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created` datetime DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `updated` datetime DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `dstEndRule` tinyblob,
  `dstOffset` bigint(20) NOT NULL,
  `dstStartRule` tinyblob,
  `tzOffset` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `usage_points`
--

DROP TABLE IF EXISTS `usage_points`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usage_points` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created` datetime DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `updated` datetime DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `roleFlags` tinyblob,
  `status` smallint(6) DEFAULT NULL,
  `local_time_parameters_id` bigint(20) DEFAULT NULL,
  `retail_customer_id` bigint(20) DEFAULT NULL,
  `serviceCategory_kind` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_6525f2b83b154175bf274c74945` (`uuid`),
  UNIQUE KEY `UK_dedb0ea7bfce4996aa9d0517ee9` (`uuid`),
  KEY `FK_cc7a145f5d734e88b7ba6c2f667` (`local_time_parameters_id`),
  KEY `FK_f97142548c4341edb4bf22c2499` (`retail_customer_id`),
  KEY `FK_cdfff9f92edd49da8c76112d05a` (`serviceCategory_kind`),
  KEY `FK_ac007303f57d4a4a8b03e976117` (`local_time_parameters_id`),
  KEY `FK_bc0572530d8d4c2ca525e7e1578` (`retail_customer_id`),
  KEY `FK_f50c03f9d914448696bd643fa24` (`serviceCategory_kind`),
  CONSTRAINT `FK_f50c03f9d914448696bd643fa24` FOREIGN KEY (`serviceCategory_kind`) REFERENCES `service_categories` (`kind`),
  CONSTRAINT `FK_ac007303f57d4a4a8b03e976117` FOREIGN KEY (`local_time_parameters_id`) REFERENCES `time_configurations` (`id`),
  CONSTRAINT `FK_bc0572530d8d4c2ca525e7e1578` FOREIGN KEY (`retail_customer_id`) REFERENCES `retail_customers` (`id`),
  CONSTRAINT `FK_cc7a145f5d734e88b7ba6c2f667` FOREIGN KEY (`local_time_parameters_id`) REFERENCES `time_configurations` (`id`),
  CONSTRAINT `FK_cdfff9f92edd49da8c76112d05a` FOREIGN KEY (`serviceCategory_kind`) REFERENCES `service_categories` (`kind`),
  CONSTRAINT `FK_f97142548c4341edb4bf22c2499` FOREIGN KEY (`retail_customer_id`) REFERENCES `retail_customers` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `interval_readings`
--

DROP TABLE IF EXISTS `interval_readings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `interval_readings` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created` datetime DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `updated` datetime DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `cost` bigint(20) DEFAULT NULL,
  `duration` bigint(20) DEFAULT NULL,
  `start` bigint(20) DEFAULT NULL,
  `value` bigint(20) DEFAULT NULL,
  `interval_block_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_aaa00f348e42404285786f630eb` (`interval_block_id`),
  KEY `FK_5bc51af37183407281bc49f2079` (`interval_block_id`),
  CONSTRAINT `FK_5bc51af37183407281bc49f2079` FOREIGN KEY (`interval_block_id`) REFERENCES `interval_blocks` (`id`),
  CONSTRAINT `FK_aaa00f348e42404285786f630eb` FOREIGN KEY (`interval_block_id`) REFERENCES `interval_blocks` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1341 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reading_qualities`
--

DROP TABLE IF EXISTS `reading_qualities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reading_qualities` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created` datetime DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `updated` datetime DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `quality` varchar(255) DEFAULT NULL,
  `interval_reading_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_a835c81ddce946b9af111546821` (`interval_reading_id`),
  CONSTRAINT `FK_a835c81ddce946b9af111546821` FOREIGN KEY (`interval_reading_id`) REFERENCES `interval_readings` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `service_categories`
--

DROP TABLE IF EXISTS `service_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service_categories` (
  `kind` bigint(20) NOT NULL,
  PRIMARY KEY (`kind`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `subscriptions`
--

DROP TABLE IF EXISTS `subscriptions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `subscriptions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created` datetime DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `updated` datetime DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `retail_customer_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_dc6851a7218243d4a840311d879` (`retail_customer_id`),
  CONSTRAINT `FK_dc6851a7218243d4a840311d879` FOREIGN KEY (`retail_customer_id`) REFERENCES `retail_customers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `electric_power_usage_summaries`
--

DROP TABLE IF EXISTS `electric_power_usage_summaries`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `electric_power_usage_summaries` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created` datetime DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `updated` datetime DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `billLastPeriod` bigint(20) DEFAULT NULL,
  `billToDate` bigint(20) DEFAULT NULL,
  `billingPeriod_duration` bigint(20) DEFAULT NULL,
  `billingPeriod_start` bigint(20) DEFAULT NULL,
  `costAdditionalLastPeriod` bigint(20) DEFAULT NULL,
  `currency` varchar(255) DEFAULT NULL,
  `currentBillingPeriodOverAllConsumption_powerOfTenMultiplier` varchar(255) DEFAULT NULL,
  `currentBillingPeriodOverAllConsumption_timeStamp` bigint(20) DEFAULT NULL,
  `currentBillingPeriodOverAllConsumption_uom` varchar(255) DEFAULT NULL,
  `currentBillingPeriodOverAllConsumption_value` bigint(20) DEFAULT NULL,
  `currentDayLastYearNetConsumption_powerOfTenMultiplier` varchar(255) DEFAULT NULL,
  `currentDayLastYearNetConsumption_timeStamp` bigint(20) DEFAULT NULL,
  `currentDayLastYearNetConsumption_uom` varchar(255) DEFAULT NULL,
  `currentDayLastYearNetConsumption_value` bigint(20) DEFAULT NULL,
  `currentDayNetConsumption_powerOfTenMultiplier` varchar(255) DEFAULT NULL,
  `currentDayNetConsumption_timeStamp` bigint(20) DEFAULT NULL,
  `currentDayNetConsumption_uom` varchar(255) DEFAULT NULL,
  `currentDayNetConsumption_value` bigint(20) DEFAULT NULL,
  `currentDayOverallConsumption_powerOfTenMultiplier` varchar(255) DEFAULT NULL,
  `currentDayOverallConsumption_timeStamp` bigint(20) DEFAULT NULL,
  `currentDayOverallConsumption_uom` varchar(255) DEFAULT NULL,
  `currentDayOverallConsumption_value` bigint(20) DEFAULT NULL,
  `peakDemand_powerOfTenMultiplier` varchar(255) DEFAULT NULL,
  `peakDemand_timeStamp` bigint(20) DEFAULT NULL,
  `peakDemand_uom` varchar(255) DEFAULT NULL,
  `peakDemand_value` bigint(20) DEFAULT NULL,
  `previousDayLastYearOverallConsumption_powerOfTenMultiplier` varchar(255) DEFAULT NULL,
  `previousDayLastYearOverallConsumption_timeStamp` bigint(20) DEFAULT NULL,
  `previousDayLastYearOverallConsumption_uom` varchar(255) DEFAULT NULL,
  `previousDayLastYearOverallConsumption_value` bigint(20) DEFAULT NULL,
  `previousDayNetConsumption_powerOfTenMultiplier` varchar(255) DEFAULT NULL,
  `previousDayNetConsumption_timeStamp` bigint(20) DEFAULT NULL,
  `previousDayNetConsumption_uom` varchar(255) DEFAULT NULL,
  `previousDayNetConsumption_value` bigint(20) DEFAULT NULL,
  `previousDayOverallConsumption_powerOfTenMultiplier` varchar(255) DEFAULT NULL,
  `previousDayOverallConsumption_timeStamp` bigint(20) DEFAULT NULL,
  `previousDayOverallConsumption_uom` varchar(255) DEFAULT NULL,
  `previousDayOverallConsumption_value` bigint(20) DEFAULT NULL,
  `qualityOfReading` varchar(255) DEFAULT NULL,
  `ratchetDemand_powerOfTenMultiplier` varchar(255) DEFAULT NULL,
  `ratchetDemand_timeStamp` bigint(20) DEFAULT NULL,
  `ratchetDemand_uom` varchar(255) DEFAULT NULL,
  `ratchetDemand_value` bigint(20) DEFAULT NULL,
  `ratchetDemandPeriod_duration` bigint(20) DEFAULT NULL,
  `ratchetDemandPeriod_start` bigint(20) DEFAULT NULL,
  `statusTimeStamp` bigint(20) NOT NULL,
  `usage_point_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_b0db3f8e25814ec6acdb1db5d2b` (`usage_point_id`),
  CONSTRAINT `FK_b0db3f8e25814ec6acdb1db5d2b` FOREIGN KEY (`usage_point_id`) REFERENCES `usage_points` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `retail_customers`
--

DROP TABLE IF EXISTS `retail_customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `retail_customers` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `enabled` tinyint(1) NOT NULL,
  `first_name` varchar(30) NOT NULL,
  `last_name` varchar(30) NOT NULL,
  `password` varchar(100) DEFAULT NULL,
  `role` varchar(255) NOT NULL,
  `username` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `meter_readings`
--

DROP TABLE IF EXISTS `meter_readings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `meter_readings` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created` datetime DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `updated` datetime DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `reading_type_id` bigint(20) DEFAULT NULL,
  `usage_point_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_34df1a8cd9a84542ae396a607cc` (`reading_type_id`),
  KEY `FK_0c6f4682e4214089b87dd1dcdc4` (`usage_point_id`),
  KEY `FK_6d83eb5357e842c6907807d5a55` (`reading_type_id`),
  KEY `FK_388a5d2a7dbc40eb9b92686a7c1` (`usage_point_id`),
  CONSTRAINT `FK_388a5d2a7dbc40eb9b92686a7c1` FOREIGN KEY (`usage_point_id`) REFERENCES `usage_points` (`id`),
  CONSTRAINT `FK_0c6f4682e4214089b87dd1dcdc4` FOREIGN KEY (`usage_point_id`) REFERENCES `usage_points` (`id`),
  CONSTRAINT `FK_34df1a8cd9a84542ae396a607cc` FOREIGN KEY (`reading_type_id`) REFERENCES `reading_types` (`id`),
  CONSTRAINT `FK_6d83eb5357e842c6907807d5a55` FOREIGN KEY (`reading_type_id`) REFERENCES `reading_types` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reading_types`
--

DROP TABLE IF EXISTS `reading_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reading_types` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created` datetime DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `updated` datetime DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `accumulationBehaviour` varchar(255) DEFAULT NULL,
  `aggregate` varchar(255) DEFAULT NULL,
  `rational_denominator` decimal(19,2) DEFAULT NULL,
  `rational_numerator` decimal(19,2) DEFAULT NULL,
  `commodity` varchar(255) DEFAULT NULL,
  `consumptionTier` varchar(255) DEFAULT NULL,
  `cpp` varchar(255) DEFAULT NULL,
  `currency` varchar(255) DEFAULT NULL,
  `dataQualifier` varchar(255) DEFAULT NULL,
  `flowDirection` varchar(255) DEFAULT NULL,
  `interharmonic_denominator` decimal(19,2) DEFAULT NULL,
  `interharmonic_numerator` decimal(19,2) DEFAULT NULL,
  `intervalLength` bigint(20) DEFAULT NULL,
  `kind` varchar(255) DEFAULT NULL,
  `measuringPeriod` varchar(255) DEFAULT NULL,
  `phase` varchar(255) DEFAULT NULL,
  `powerOfTenMultiplier` varchar(255) DEFAULT NULL,
  `timeAttribute` varchar(255) DEFAULT NULL,
  `tou` varchar(255) DEFAULT NULL,
  `uom` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `authorizations`
--

DROP TABLE IF EXISTS `authorizations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `authorizations` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created` datetime DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `updated` datetime DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `access_token` varchar(255) NOT NULL,
  `authorizationServer` varchar(255) DEFAULT NULL,
  `resource` varchar(255) NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `thirdParty` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `interval_blocks`
--

DROP TABLE IF EXISTS `interval_blocks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `interval_blocks` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created` datetime DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `updated` datetime DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `duration` bigint(20) DEFAULT NULL,
  `start` bigint(20) DEFAULT NULL,
  `meter_reading_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_911727cb833949b0aa3a2c6d86a` (`meter_reading_id`),
  KEY `FK_7c703705ef07408eae7928f5ec7` (`meter_reading_id`),
  CONSTRAINT `FK_7c703705ef07408eae7928f5ec7` FOREIGN KEY (`meter_reading_id`) REFERENCES `meter_readings` (`id`),
  CONSTRAINT `FK_911727cb833949b0aa3a2c6d86a` FOREIGN KEY (`meter_reading_id`) REFERENCES `meter_readings` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `electric_power_quality_summaries`
--

DROP TABLE IF EXISTS `electric_power_quality_summaries`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `electric_power_quality_summaries` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created` datetime DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `updated` datetime DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `flickerPlt` bigint(20) DEFAULT NULL,
  `flickerPst` bigint(20) DEFAULT NULL,
  `harmonicVoltage` bigint(20) DEFAULT NULL,
  `longInterruptions` bigint(20) DEFAULT NULL,
  `mainsVoltage` bigint(20) DEFAULT NULL,
  `measurementProtocol` smallint(6) DEFAULT NULL,
  `powerFrequency` bigint(20) DEFAULT NULL,
  `rapidVoltageChanges` bigint(20) DEFAULT NULL,
  `shortInterruptions` bigint(20) DEFAULT NULL,
  `duration` bigint(20) DEFAULT NULL,
  `start` bigint(20) DEFAULT NULL,
  `supplyVoltageDips` bigint(20) DEFAULT NULL,
  `supplyVoltageImbalance` bigint(20) DEFAULT NULL,
  `supplyVoltageVariations` bigint(20) DEFAULT NULL,
  `tempOvervoltage` bigint(20) DEFAULT NULL,
  `usage_point_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_7f627b5cde37486081d0afbf1da` (`usage_point_id`),
  CONSTRAINT `FK_7f627b5cde37486081d0afbf1da` FOREIGN KEY (`usage_point_id`) REFERENCES `usage_points` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `third_parties`
--

DROP TABLE IF EXISTS `third_parties`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `third_parties` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `client_id` varchar(64) NOT NULL,
  `name` varchar(64) NOT NULL,
  `url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_3a474a26f54d417aa10771f8789` (`client_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-10-18 13:20:38
