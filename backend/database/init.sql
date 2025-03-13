CREATE DATABASE  IF NOT EXISTS `robot_info` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `robot_info`;
-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: robot_info
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `callback_rec`
--

DROP TABLE IF EXISTS `callback_rec`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `callback_rec` (
  `cb_id` int NOT NULL AUTO_INCREMENT,
  `ins_id` int DEFAULT NULL,
  `robot_id` int DEFAULT NULL,
  `callback` varchar(1024) DEFAULT NULL,
  `ctime` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`cb_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10286 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `callback_rec`
--

LOCK TABLES `callback_rec` WRITE;
/*!40000 ALTER TABLE `callback_rec` DISABLE KEYS */;
INSERT INTO `callback_rec` VALUES (4752,NULL,6,'Obstacle avoidance timeout, the destination can not be reached, the timeout time is set through the parameters','2024-10-31 14:40:06'),(4753,NULL,6,'Start to navigation to point 2','2024-10-31 14:40:13'),(4754,NULL,6,'0:Run navigation api: startNavigation','2024-10-31 14:40:13'),(4755,NULL,6,'Navigation success','2024-10-31 14:40:31'),(4756,NULL,16,'-1:Run map api: getPlaceList','2024-11-09 12:52:03'),(4757,NULL,16,'-1:Run map api: getPlaceList','2024-11-10 03:03:30'),(4758,NULL,6,'Start to navigation to point2','2024-11-09 14:46:34'),(4759,NULL,6,'0:Run navigation api: startNavigation','2024-11-09 14:46:34'),(4760,NULL,6,'Navigation destination does not exist','2024-11-09 14:46:34'),(4761,NULL,6,'Start to navigation to point 2','2024-11-09 14:46:35'),(4762,NULL,6,'0:Run navigation api: startNavigation','2024-11-09 14:46:35'),(4763,NULL,6,'The current route is blocked by obstacles','2024-11-09 14:46:41'),(4764,NULL,6,'Obstacles removed','2024-11-09 14:47:03'),(4765,NULL,6,'The current route is blocked by obstacles','2024-11-09 14:47:05'),(4766,NULL,6,'Obstacles removed','2024-11-09 14:47:08'),(4767,NULL,6,'Navigation success','2024-11-09 14:47:13'),(4768,NULL,6,'Start to navigation to point 1','2024-11-09 14:47:13'),(4769,NULL,6,'0:Run navigation api: startNavigation','2024-11-09 14:47:13'),(4770,NULL,6,'Navigation success','2024-11-09 14:47:23'),(4771,NULL,6,'Start to navigation to 1','2024-11-09 14:47:23'),(4772,NULL,6,'0:Run navigation api: startNavigation','2024-11-09 14:47:24'),(4773,NULL,6,'Navigation destination does not exist','2024-11-09 14:47:24'),(4774,NULL,6,'Start to navigation to 2','2024-11-09 14:48:13'),(4775,NULL,6,'0:Run navigation api: startNavigation','2024-11-09 14:48:13'),(4776,NULL,6,'Navigation destination does not exist','2024-11-09 14:48:13'),(4777,NULL,6,'Start to navigation to point 2','2024-11-09 14:48:23'),(4778,NULL,6,'0:Run navigation api: startNavigation','2024-11-09 14:48:23'),(4779,NULL,6,'Navigation success','2024-11-09 14:48:30'),(4780,NULL,16,'-1:Run navigation api: startNavigation','2024-11-10 05:13:17'),(4781,NULL,16,'-1:Run navigation api: startNavigation','2024-11-10 05:50:28'),(4782,NULL,16,'-1:Run navigation api: startNavigation','2024-11-10 05:50:28'),(4783,NULL,16,'-1:Run navigation api: startNavigation','2024-11-10 05:50:28'),(4784,NULL,16,'-1:Run navigation api: startNavigation','2024-11-10 05:50:28'),(4785,NULL,16,'-1:Run navigation api: startNavigation','2024-11-10 05:50:28'),(4786,NULL,16,'-1:Run navigation api: startNavigation','2024-11-10 05:50:28'),(4787,NULL,16,'-1:Run navigation api: startNavigation','2024-11-10 05:50:28'),(4788,NULL,16,'-1:Run navigation api: startNavigation','2024-11-10 13:56:30'),(4789,NULL,6,'Start to navigation to 0','2024-11-15 17:40:25'),(4790,NULL,6,'0:Run navigation api: startNavigation','2024-11-15 17:40:25'),(4791,NULL,6,'Navigation destination does not exist','2024-11-15 17:40:25'),(4792,NULL,6,'Start to navigation to 0','2024-11-15 17:40:26'),(4793,NULL,6,'0:Run navigation api: startNavigation','2024-11-15 17:40:26'),(4794,NULL,6,'Navigation destination does not exist','2024-11-15 17:40:27'),(4795,NULL,6,'Start to navigation to 0','2024-11-15 17:40:28'),(4796,NULL,6,'0:Run navigation api: startNavigation','2024-11-15 17:40:28'),(4797,NULL,6,'Navigation destination does not exist','2024-11-15 17:40:28'),(4798,NULL,6,'-1:Cannot classify api type: updateMap','2024-11-15 17:40:29'),(4799,NULL,6,'Start to navigation to point 1','2024-11-15 17:40:30'),(4800,NULL,6,'0:Run navigation api: startNavigation','2024-11-15 17:40:30'),(4801,NULL,6,'Obstacle avoidance timeout, the destination can not be reached, the timeout time is set through the parameters','2024-11-15 17:41:00'),(4802,NULL,6,'Start to navigation to point 1','2024-11-15 17:41:01'),(4803,NULL,6,'0:Run navigation api: startNavigation','2024-11-15 17:41:01'),(4804,NULL,6,'Obstacle avoidance timeout, the destination can not be reached, the timeout time is set through the parameters','2024-11-15 17:41:31'),(4805,NULL,6,'Start to navigation to point 2','2024-11-15 17:41:32'),(4806,NULL,6,'0:Run navigation api: startNavigation','2024-11-15 17:41:32'),(4807,NULL,6,'Obstacle avoidance timeout, the destination can not be reached, the timeout time is set through the parameters','2024-11-15 17:42:02'),(4808,NULL,6,'Start to navigation to 1','2024-11-15 17:42:03'),(4809,NULL,6,'0:Run navigation api: startNavigation','2024-11-15 17:42:03'),(4810,NULL,6,'Navigation destination does not exist','2024-11-15 17:42:03'),(4811,NULL,6,'Start to navigation to 1','2024-11-15 17:42:04'),(4812,NULL,6,'0:Run navigation api: startNavigation','2024-11-15 17:42:04'),(4813,NULL,6,'Navigation destination does not exist','2024-11-15 17:42:04'),(4814,NULL,6,'Start to navigation to 2','2024-11-15 17:42:06'),(4815,NULL,6,'0:Run navigation api: startNavigation','2024-11-15 17:42:06'),(4816,NULL,6,'Navigation destination does not exist','2024-11-15 17:42:06'),(4817,NULL,6,'Start to navigation to 3','2024-11-15 17:42:07'),(4818,NULL,6,'0:Run navigation api: startNavigation','2024-11-15 17:42:07'),(4819,NULL,6,'Navigation destination does not exist','2024-11-15 17:42:07'),(4820,NULL,6,'Start to navigation to 4','2024-11-15 17:42:08'),(4821,NULL,6,'0:Run navigation api: startNavigation','2024-11-15 17:42:08'),(4822,NULL,6,'Navigation destination does not exist','2024-11-15 17:42:08');
/*!40000 ALTER TABLE `callback_rec` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `email_confirmations`
--

DROP TABLE IF EXISTS `email_confirmations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `email_confirmations` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `token` varchar(255) NOT NULL,
  `expires_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `email_confirmations_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `email_confirmations`
--

LOCK TABLES `email_confirmations` WRITE;
/*!40000 ALTER TABLE `email_confirmations` DISABLE KEYS */;
/*!40000 ALTER TABLE `email_confirmations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ins_send`
--

DROP TABLE IF EXISTS `ins_send`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ins_send` (
  `ins_id` int NOT NULL AUTO_INCREMENT,
  `robot_id` int DEFAULT NULL,
  `instruction` varchar(256) DEFAULT NULL,
  `status` varchar(128) DEFAULT NULL,
  `ctime` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ins_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3759 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ins_send`
--

LOCK TABLES `ins_send` WRITE;
/*!40000 ALTER TABLE `ins_send` DISABLE KEYS */;
INSERT INTO `ins_send` VALUES (1800,6,'navigation:startNavigation:Charging Pole','complete','2024-10-31 14:40:06'),(1801,6,'navigation:startNavigation:point 2','complete','2024-10-31 14:40:31'),(1802,11,'navigation:startNavigation:point 1','order','2024-10-31 20:48:52'),(1803,11,'navigation:startNavigation:point 2','order','2024-10-31 20:49:14'),(1804,11,'navigation:startNavigation:point 3','order','2024-10-31 20:49:22'),(1805,4,'map:getPlaceList','order','2024-11-04 08:24:20'),(1806,11,'navigation:startNavigation:point1','order','2024-11-09 20:37:01'),(1807,6,'navigation:startNavigation:point2','complete','2024-11-09 14:46:34'),(1808,6,'navigation:startNavigation:point 2','complete','2024-11-09 14:47:13'),(1809,6,'navigation:startNavigation:point 1','complete','2024-11-09 14:47:23'),(1810,6,'navigation:startNavigation:1','complete','2024-11-09 14:47:24'),(1811,6,'navigation:startNavigation:2','complete','2024-11-09 14:48:13'),(1812,6,'navigation:startNavigation:point 2','complete','2024-11-09 14:48:30'),(1813,16,'navigation:startNavigation:point 1','accept','2024-11-10 05:13:17'),(1814,16,'navigation:startNavigation:point 2','accept','2024-11-10 05:50:28'),(1815,16,'navigation:startNavigation:point 1','accept','2024-11-10 13:56:30'),(1816,16,'navigation:startNavigation:point 2','accept','2024-11-10 15:11:11'),(1817,6,'navigation:startNavigation:0','complete','2024-11-15 17:40:26'),(1819,6,'navigation:startNavigation:0','complete','2024-11-15 17:40:27'),(1821,6,'navigation:startNavigation:0','complete','2024-11-15 17:40:28'),(1824,6,'updateMap:6','complete','2024-11-15 17:40:29'),(1826,6,'navigation:startNavigation:point 1','complete','2024-11-15 17:41:00'),(1830,6,'navigation:startNavigation:point 1','complete','2024-11-15 17:41:31'),(1831,6,'navigation:startNavigation:point 2','complete','2024-11-15 17:42:02'),(1832,6,'navigation:startNavigation:1','complete','2024-11-15 17:42:03'),(1834,6,'navigation:startNavigation:1','complete','2024-11-15 17:42:05'),(1835,6,'navigation:startNavigation:2','complete','2024-11-15 17:42:06'),(1836,6,'navigation:startNavigation:3','complete','2024-11-15 17:42:07'),(1837,6,'navigation:startNavigation:4','complete','2024-11-15 17:42:08');
/*!40000 ALTER TABLE `ins_send` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `location`
--

DROP TABLE IF EXISTS `location`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `location` (
  `loc_id` int NOT NULL AUTO_INCREMENT,
  `robot_id` int DEFAULT NULL,
  `x` double DEFAULT NULL,
  `y` double DEFAULT NULL,
  `z` int DEFAULT NULL,
  `theta` double DEFAULT NULL,
  `name` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`loc_id`)
) ENGINE=InnoDB AUTO_INCREMENT=489833 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `location`
--

LOCK TABLES `location` WRITE;
/*!40000 ALTER TABLE `location` DISABLE KEYS */;
INSERT INTO `location` VALUES (489779,11,0.52,2.6525000000000003,-99,0.82,'1'),(489780,11,-0.028294880667081166,-0.39899799464162183,-99,86,'Charging Station'),(489781,11,-0.18832019107948447,-0.09982944121660209,-99,92,'Charging Station'),(489782,11,-0.028294880667081166,-0.39899799464162183,2,85.94,'Charging pile'),(489783,11,0.03537797409126446,0.4980654837012203,2,265.94,'Charging pile Docking Point'),(489784,11,5.160000000000001,6.392500000000001,-99,1.71,'door 1'),(489785,11,-6.672500000000001,81.1925,-99,2.94,'door 3'),(489786,11,1.5000000000000002,2.5,2,0,'nav_test_point'),(489787,11,-0.42,4.42,2,3.44,'point 1'),(489788,11,-0.6400000000000001,2.465,2,265.85,'point 2'),(489789,11,-0.5425000000000001,6.02,2,181.63,'point 3'),(489790,11,1.5000000000000002,2.5,2,0,'point 6'),(489791,11,-18.0475,4.2275,-99,-1.44,'room 2'),(489792,11,-2.155,1.1075,-99,-0.04,'stan'),(489793,11,0.23000000000000004,5.232500000000001,2,-1.48,'test'),(489794,11,1.5000000000000002,2.5,2,0,'test_navigation_point'),(489795,11,100,200,2,90,'test_poi_point'),(489796,11,-0.15,-0.15,2,1,'test_poi_point'),(489797,11,-0.15,-0.15,2,1,'test_poi_point2'),(489798,11,-0.15,-0.15,2,1,'test_poi_point2'),(489799,11,-0.15,-0.15,2,1,'test_poi_point3'),(489800,11,0,0,2,1,'test_poi_point4'),(489801,11,0,0,2,1,'test_poi_point4'),(489802,11,0,0,2,1,'test_poi_point5'),(489803,11,0,0,2,1,'test_poi_point6'),(489804,11,0,0,2,0,'test_point_1'),(489805,11,1.5000000000000002,2.5,2,0,'test_point_2'),(489806,11,1.5000000000000002,2.5,2,0,'test_point_3'),(489813,11,1,2,NULL,2,'test_point_dec'),(489814,6,3.1701099472239997,-0.017863878847999448,NULL,2,'test_point_dec'),(489815,6,1.5,2,0,0,'db_test_point'),(489816,8,0.14980546887200008,-0.7164123449039999,NULL,0,'db_test_point'),(489817,11,13416.473172722663,21165.73019671905,NULL,0,'db_test_point'),(489818,6,1,1,NULL,0,'db_batch_1'),(489819,8,1.205443128872,-1.0261681649039998,NULL,0,'db_batch_1'),(489820,11,9207.679365972663,10778.757776719054,NULL,0,'db_batch_1'),(489821,6,2,2,NULL,0,'db_batch_2'),(489822,8,0.06258026487199997,-0.23220611690399995,NULL,0,'db_batch_2'),(489823,11,18413.02090207267,21559.607158019055,NULL,0,'db_batch_2'),(489824,6,1,1,1,1,'frontendTest_1'),(489825,8,1.205443128872,-1.0261681649039998,NULL,1,'frontendTest_1'),(489826,11,9207.679365972663,10778.757776719054,NULL,1,'frontendTest_1'),(489827,6,1,1,1,1,'frontendTest_2'),(489828,8,1.205443128872,-1.0261681649039998,NULL,1,'frontendTest_2'),(489829,11,9207.679365972663,10778.757776719054,NULL,1,'frontendTest_2'),(489830,6,3.744840383529663,-2.6590445041656494,1,1,'frontendTest_3'),(489831,8,1.205443128872,-1.0261681649039998,NULL,1,'frontendTest_3'),(489832,11,9207.679365972663,10778.757776719054,NULL,1,'frontendTest_3');
/*!40000 ALTER TABLE `location` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `map`
--

DROP TABLE IF EXISTS `map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `map` (
  `map_id` int NOT NULL AUTO_INCREMENT,
  `robot_id` int DEFAULT NULL,
  `map_name` varchar(256) DEFAULT NULL,
  `file_path` varchar(256) DEFAULT NULL,
  `lift_x` int DEFAULT NULL,
  `lift_y` int DEFAULT NULL,
  `lift_z` int DEFAULT NULL,
  `scale` int DEFAULT NULL,
  PRIMARY KEY (`map_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `map`
--

LOCK TABLES `map` WRITE;
/*!40000 ALTER TABLE `map` DISABLE KEYS */;
INSERT INTO `map` VALUES (1,3,'hotal_layer_1.txt','/home/renRobotRoot/Database/API/app/static/map/hotal_layer_1.txt',618,528,1,35),(2,3,'hotal_layer_2.txt','/home/renRobotRoot/Database/API/app/static/map/hotal_layer_2.txt',498,438,2,35),(3,4,'hotal_layer_1.txt','/home/renRobotRoot/Database/API/app/static/map/hotal_layer_1.txt',618,528,1,35),(4,4,'hotal_layer_2.txt','/home/renRobotRoot/Database/API/app/static/map/hotal_layer_2.txt',498,438,2,35),(5,5,'hotal_layer_1.txt','/home/renRobotRoot/Database/API/app/static/map/hotal_layer_1.txt',618,528,1,35),(6,5,'hotal_layer_2.txt','/home/renRobotRoot/Database/API/app/static/map/hotal_layer_2.txt',498,438,2,35);
/*!40000 ALTER TABLE `map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `map_file`
--

DROP TABLE IF EXISTS `map_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `map_file` (
  `map_id` int NOT NULL AUTO_INCREMENT,
  `map_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `file` blob NOT NULL,
  PRIMARY KEY (`map_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `map_file`
--

LOCK TABLES `map_file` WRITE;
/*!40000 ALTER TABLE `map_file` DISABLE KEYS */;
/*!40000 ALTER TABLE `map_file` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `map_transform`
--

DROP TABLE IF EXISTS `map_transform`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `map_transform` (
  `trans_id` int NOT NULL AUTO_INCREMENT,
  `main_id` int NOT NULL,
  `second_id` int NOT NULL,
  `matrix_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `scale` double NOT NULL,
  `centroid_1` varchar(120) NOT NULL,
  `centroid_2` varchar(120) NOT NULL,
  PRIMARY KEY (`trans_id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `map_transform`
--

LOCK TABLES `map_transform` WRITE;
/*!40000 ALTER TABLE `map_transform` DISABLE KEYS */;
INSERT INTO `map_transform` VALUES (21,6,8,'[[ 0.990228,-0.139461],\n [ 0.139461, 0.990228]]',1.0126006875249984,'[1.465295,2.165993]','[1.098015,0.413688]'),(23,6,11,'[[ 0.996907,-0.078586],\n [ 0.078586, 0.996907]]',10024.100104722715,'[1.465295,2.165993]','[-5.343333e-05, 4.301667e-04]'),(24,6,11,'[[ 0.996907,-0.078586],\n [ 0.078586, 0.996907]]',1.0024100104722715,'[1.465295,2.165993]','[-0.534333, 4.301667]'),(25,6,8,'[[-0.17728745857176795, -0.9841591116446384], [0.9841591116446384, -0.17728745857176786]]',0.9840158725272516,'[2.503529131412506, -2.307669758796692]','[-0.5147284716367722, 0.06868848204612732]');
/*!40000 ALTER TABLE `map_transform` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `operation_site`
--

DROP TABLE IF EXISTS `operation_site`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `operation_site` (
  `site_id` int NOT NULL,
  `site_name` text NOT NULL,
  `num_robots` int NOT NULL,
  `robot_list` json NOT NULL,
  PRIMARY KEY (`site_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `operation_site`
--

LOCK TABLES `operation_site` WRITE;
/*!40000 ALTER TABLE `operation_site` DISABLE KEYS */;
INSERT INTO `operation_site` VALUES (1,'London',3,'[6, 11, 13]'),(2,'Toronto',3,'[1, 2, 3]'),(3,'Vancouver',5,'[4, 5, 7, 9, 10]');
/*!40000 ALTER TABLE `operation_site` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `password_reset_tokens`
--

DROP TABLE IF EXISTS `password_reset_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `password_reset_tokens` (
  `id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `reset_code` varchar(6) NOT NULL,
  `expires_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `password_reset_tokens`
--

LOCK TABLES `password_reset_tokens` WRITE;
/*!40000 ALTER TABLE `password_reset_tokens` DISABLE KEYS */;
/*!40000 ALTER TABLE `password_reset_tokens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `path`
--

DROP TABLE IF EXISTS `path`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `path` (
  `path_id` int NOT NULL AUTO_INCREMENT,
  `map_id` int DEFAULT NULL,
  `route` text,
  `algorithm` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`path_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `path`
--

LOCK TABLES `path` WRITE;
/*!40000 ALTER TABLE `path` DISABLE KEYS */;
INSERT INTO `path` VALUES (1,1,'[[200.0, 1.0]]','A*'),(2,1,'[[100.0, 0.0]]','A*'),(3,1,'[[120.0, 0.0], [8.485281374238571, 0.25], [128.0, 0.0], [1.4142135623730951, 0.25], [2.0, 0.0], [1.4142135623730951, 0.25]]','A*'),(4,2,'[[58.0, 0.5], [1.4142135623730951, 0.25], [2.0, 0.5], [1.4142135623730951, 0.25]]','A*'),(5,1,'[[36.0, 1.0], [1.4142135623730951, -0.75], [1.0, 1.0], [1.4142135623730951, -0.75], [1.0, 1.0], [1.4142135623730951, -0.75], [2.0, 1.0], [1.4142135623730951, -0.75], [1.0, 1.0], [1.4142135623730951, -0.75], [1.0, 1.0], [1.4142135623730951, -0.75], [1.0, 1.0], [1.4142135623730951, -0.75], [1.0, 1.0], [1.4142135623730951, -0.75], [2.0, 1.0], [1.4142135623730951, -0.75], [1.0, 1.0], [1.4142135623730951, -0.75], [1.0, 1.0], [1.4142135623730951, -0.75], [1.0, 1.0], [1.4142135623730951, -0.75], [1.0, 1.0], [1.4142135623730951, -0.75], [2.0, 1.0], [1.4142135623730951, -0.75], [1.0, 1.0], [1.4142135623730951, -0.75], [1.0, 1.0], [1.4142135623730951, -0.75], [1.0, 1.0], [1.4142135623730951, -0.75], [1.0, 1.0], [1.4142135623730951, -0.75], [2.0, 1.0], [1.4142135623730951, -0.75], [1.0, 1.0], [1.4142135623730951, -0.75], [1.0, 1.0], [1.4142135623730951, -0.75], [85.0, 1.0], [7.0, -0.5], [1.4142135623730951, -0.75], [16.0, -0.5], [1.4142135623730951, -0.75], [1.0, -0.5], [1.4142135623730951, -0.75], [1.0, -0.5], [1.4142135623730951, -0.75], [1.0, -0.5], [1.4142135623730951, -0.75], [2.0, -0.5], [1.4142135623730951, -0.75], [1.0, -0.5], [1.4142135623730951, -0.75], [1.0, -0.5], [1.4142135623730951, -0.75], [1.0, -0.5], [1.4142135623730951, -0.75], [1.0, -0.5], [1.4142135623730951, -0.75], [1.0, -0.5]]','A*'),(6,1,'[[56.0, 1.0]]','A*'),(7,1,'[[3.0, 1.0], [4.242640687119286, -0.75], [2.0, 1.0], [91.92388155425108, -0.75], [1.0, -0.5], [26.870057685088817, -0.75], [38.0, -0.5], [21.213203435596434, -0.75], [2.0, 1.0], [28.0, 0.5], [1.4142135623730951, 0.75], [36.0, 0.5], [1.4142135623730951, 0.75], [90.0, 0.5], [1.4142135623730951, 0.75], [1.0, 0.5], [1.4142135623730951, 0.75], [1.0, 0.5], [1.4142135623730951, 0.75], [1.0, 0.5], [1.4142135623730951, 0.75], [1.0, 0.5], [1.4142135623730951, 0.75], [2.0, 0.5], [1.4142135623730951, 0.75], [1.0, 0.5], [1.4142135623730951, 0.75], [1.0, 0.5], [1.4142135623730951, 0.75], [1.0, 0.5], [1.4142135623730951, 0.75], [1.0, 0.5], [1.4142135623730951, 0.75], [2.0, 0.5], [1.4142135623730951, 0.75], [1.0, 0.5], [1.4142135623730951, 0.75], [1.0, 0.5], [1.4142135623730951, 0.75], [1.0, 0.5], [1.4142135623730951, 0.75], [1.0, 0.5], [1.4142135623730951, 0.75], [1.0, 0.5]]','A*'),(8,1,'[[1.4142135623730951, -0.75], [1.0, 1.0], [2.8284271247461903, -0.75], [26.0, 1.0], [32.5269119345812, -0.75], [1.0, 1.0], [42.426406871192846, -0.75], [26.0, 1.0], [7.0, -0.5], [1.4142135623730951, -0.75], [47.0, -0.5]]','A*'),(9,1,'[[3.0, -0.5], [4.242640687119286, -0.75], [1.0, -0.5], [55.154328932550676, -0.75], [2.0, -0.5], [41.012193308819754, -0.75], [2.0, 1.0], [5.0, -0.5], [15.55634918610405, -0.75], [213.0, -0.5], [1.4142135623730951, -0.75], [1.0, -0.5], [1.4142135623730951, -0.75], [1.0, -0.5], [1.4142135623730951, -0.75], [2.0, -0.5], [1.4142135623730951, -0.75], [1.0, -0.5], [1.4142135623730951, -0.75], [1.0, -0.5], [1.4142135623730951, -0.75], [1.0, -0.5], [1.4142135623730951, -0.75], [1.0, -0.5], [1.4142135623730951, -0.75], [2.0, -0.5], [1.4142135623730951, -0.75], [1.0, -0.5], [1.4142135623730951, -0.75], [1.0, -0.5], [1.4142135623730951, -0.75], [1.0, -0.5], [1.4142135623730951, -0.75], [1.0, -0.5], [1.4142135623730951, -0.75], [2.0, -0.5], [1.4142135623730951, -0.75], [1.0, -0.5], [1.4142135623730951, -0.75], [1.0, -0.5], [1.4142135623730951, -0.75], [1.0, -0.5], [1.4142135623730951, -0.75], [1.0, -0.5], [1.4142135623730951, -0.75], [1.0, -0.5]]','A*'),(10,1,'[[1.4142135623730951, -0.25], [31.0, -0.5], [21.213203435596434, -0.25], [112.0, -0.5], [1.4142135623730951, -0.25], [28.0, -0.5], [2.0, 0.0], [93.33809511662417, 0.25], [1.0, 0.5], [1.4142135623730951, 0.25], [1.0, 0.5], [1.4142135623730951, 0.25], [1.0, 0.5], [1.4142135623730951, 0.25], [1.0, 0.5], [1.4142135623730951, 0.25], [2.0, 0.5], [1.4142135623730951, 0.25]]','A*'),(11,1,'[[167.0, 1.0]]','A*'),(12,1,'[[26.870057685088817, -0.75], [4.0, 1.0], [2.8284271247461903, -0.75], [1.0, 1.0], [7.0, -0.5], [1.4142135623730951, -0.75], [47.0, -0.5]]','A*'),(13,1,'[[1.4142135623730951, -0.75], [1.0, -0.5], [2.8284271247461903, -0.75], [26.0, -0.5], [32.5269119345812, -0.75], [1.0, -0.5], [11.313708498984763, -0.75], [1.0, -0.5], [1.4142135623730951, -0.75], [1.0, -0.5], [1.4142135623730951, -0.75], [2.0, -0.5], [1.4142135623730951, -0.75], [1.0, -0.5], [1.4142135623730951, -0.75], [1.0, -0.5], [1.4142135623730951, -0.75], [1.0, -0.5], [1.4142135623730951, -0.75], [1.0, -0.5], [1.4142135623730951, -0.75], [2.0, -0.5], [1.4142135623730951, -0.75], [1.0, -0.5], [1.4142135623730951, -0.75], [1.0, -0.5], [1.4142135623730951, -0.75], [1.0, -0.5], [1.4142135623730951, -0.75], [1.0, -0.5], [1.4142135623730951, -0.75], [2.0, -0.5], [1.4142135623730951, -0.75], [1.0, -0.5], [1.4142135623730951, -0.75], [1.0, -0.5], [1.4142135623730951, -0.75], [1.0, -0.5], [1.4142135623730951, -0.75], [1.0, -0.5], [1.4142135623730951, -0.75], [2.0, -0.5], [5.656854249492381, -0.75], [4.0, -0.5], [1.4142135623730951, -0.75], [1.0, -0.5]]','A*');
/*!40000 ALTER TABLE `path` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `robot`
--

DROP TABLE IF EXISTS `robot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `robot` (
  `robot_id` int NOT NULL AUTO_INCREMENT,
  `type` varchar(64) NOT NULL,
  `ip_add` varchar(64) NOT NULL,
  `port` smallint NOT NULL,
  `serial_num` text NOT NULL,
  `ctime` datetime DEFAULT CURRENT_TIMESTAMP,
  `status` tinyint(1) DEFAULT NULL,
  `speed` mediumint NOT NULL DEFAULT '0',
  `battery` mediumint NOT NULL DEFAULT '100',
  `mileage` int unsigned NOT NULL DEFAULT '100',
  `site_id` int NOT NULL,
  `is_charging` tinyint DEFAULT '0',
  PRIMARY KEY (`robot_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `robot`
--

LOCK TABLES `robot` WRITE;
/*!40000 ALTER TABLE `robot` DISABLE KEYS */;
INSERT INTO `robot` VALUES (1,'Mini','10.0.2.1',30000,'','2023-03-16 11:16:43',0,60,100,100,0,0),(2,'Keenon','10.0.2.2',30000,'','2023-03-17 11:28:36',0,40,100,100,0,1),(3,'Ros simulate robot 3 ','10.0.2.15',8000,'','2023-03-17 21:28:38',0,30,100,70,0,0),(4,'Ros simulate robot 4','10.0.2.15',8001,'','2023-03-17 21:29:15',0,30,100,50,0,0),(5,'Ros simulate robot 5 ','10.0.2.15',8002,'','2023-03-17 21:29:17',0,30,100,30,0,0),(6,'Mini 6 Western','172.30.116.30',30000,'','2024-09-06 18:39:01',0,50,5,10,1,0),(7,'Keenon 7 Western','10.0.2.3',30000,'','2023-03-16 12:19:39',0,50,80,10,0,0),(8,'Mini 8 Van','10.0.2.4',30000,'','2024-11-16 15:39:48',0,60,97,20,0,0),(9,'Keenon 9 Van','10.0.2.5',30000,'','2023-03-16 18:21:25',0,57,89,30,0,0),(10,'Mini2 10 Western','172.30.33.145',30000,'','2024-02-03 16:24:46',0,35,100,10,0,0),(11,'Autoxing','8892301101768sw',3000,'','2024-03-27 03:06:25',1,50,100,100,1,0),(12,'Mini 12 Western','192.168.1.77',30000,'','2024-05-21 21:12:57',0,50,100,100,1,0),(13,'Mini 10','10.0.2.6',3000,'MC1TCNC0100220068717','2024-05-24 16:01:05',0,0,100,100,0,0),(14,'Keenon 14','10.0.2.14',3000,'','2024-05-29 18:01:25',0,0,100,100,0,0),(15,'Mini 15','10.0.2.7',3000,'','2024-05-29 14:57:39',0,0,100,100,0,0),(16,'Keenon 16','172.30.40.248',3000,'','2024-05-29 18:01:01',0,0,100,100,0,0);
/*!40000 ALTER TABLE `robot` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `robot_distance`
--

DROP TABLE IF EXISTS `robot_distance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `robot_distance` (
  `dist_id` int NOT NULL AUTO_INCREMENT,
  `main_id` int NOT NULL,
  `second_id` int NOT NULL,
  `id_pair` varchar(64) NOT NULL,
  `distance` float NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`dist_id`)
) ENGINE=InnoDB AUTO_INCREMENT=263220 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `robot_distance`
--

LOCK TABLES `robot_distance` WRITE;
/*!40000 ALTER TABLE `robot_distance` DISABLE KEYS */;
INSERT INTO `robot_distance` VALUES (227827,6,11,'(6, 11)',0.729822,'2024-12-03 00:49:17'),(227828,6,11,'(6, 11)',0.729822,'2024-12-03 00:49:18'),(227829,6,11,'(6, 11)',0.729822,'2024-12-03 00:49:19'),(227830,6,11,'(6, 11)',0.729822,'2024-12-03 00:49:20'),(227831,6,11,'(6, 11)',0.729822,'2024-12-03 00:49:21'),(227832,6,11,'(6, 11)',0.729822,'2024-12-03 00:49:22'),(227833,6,11,'(6, 11)',0.729822,'2024-12-03 00:49:24'),(227834,6,11,'(6, 11)',0.729822,'2024-12-03 00:49:25'),(227835,6,11,'(6, 11)',0.729822,'2024-12-03 00:49:26'),(227836,6,11,'(6, 11)',0.729822,'2024-12-03 00:49:27'),(263210,6,8,'(6, 8)',2.14552,'2024-12-17 01:57:32'),(263211,6,8,'(6, 8)',2.14552,'2024-12-17 01:57:33'),(263212,6,8,'(6, 8)',2.14552,'2024-12-17 01:57:34'),(263213,6,8,'(6, 8)',2.14552,'2024-12-17 01:57:35'),(263214,6,8,'(6, 8)',2.14552,'2024-12-17 01:57:36'),(263215,6,8,'(6, 8)',2.14552,'2024-12-17 01:57:37'),(263216,6,8,'(6, 8)',2.14552,'2024-12-17 01:57:38'),(263217,6,8,'(6, 8)',2.14552,'2024-12-17 01:57:39'),(263218,6,8,'(6, 8)',2.14552,'2024-12-17 01:57:40'),(263219,6,8,'(6, 8)',2.14552,'2024-12-17 01:57:41');
/*!40000 ALTER TABLE `robot_distance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `robot_location`
--

DROP TABLE IF EXISTS `robot_location`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `robot_location` (
  `r_loc_id` int unsigned NOT NULL AUTO_INCREMENT,
  `robot_id` int DEFAULT NULL,
  `x` double DEFAULT NULL,
  `y` double DEFAULT NULL,
  `z` double DEFAULT '1',
  `theta` double DEFAULT NULL,
  `ctime` datetime DEFAULT CURRENT_TIMESTAMP,
  `map_x` double DEFAULT NULL,
  `map_y` double DEFAULT NULL,
  `map_id` int DEFAULT NULL,
  PRIMARY KEY (`r_loc_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3415745 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `robot_location`
--

LOCK TABLES `robot_location` WRITE;
/*!40000 ALTER TABLE `robot_location` DISABLE KEYS */;
INSERT INTO `robot_location` VALUES (3317513,6,3.744840383529663,-2.6590445041656494,NULL,2.1766414642333984,'2024-12-17 01:57:31',NULL,NULL,NULL),(3317517,6,3.744840383529663,-2.6590445041656494,NULL,2.177248001098633,'2024-12-17 01:57:32',NULL,NULL,NULL),(3317521,6,3.744840383529663,-2.6590445041656494,NULL,2.1766133308410645,'2024-12-17 01:57:33',NULL,NULL,NULL),(3317524,6,3.744840383529663,-2.6590445041656494,NULL,2.1768124103546143,'2024-12-17 01:57:34',NULL,NULL,NULL),(3317528,6,3.744840383529663,-2.6590445041656494,NULL,2.1766068935394287,'2024-12-17 01:57:35',NULL,NULL,NULL),(3317532,6,3.744840383529663,-2.6590445041656494,NULL,2.1765387058258057,'2024-12-17 01:57:36',NULL,NULL,NULL),(3317535,6,3.744840383529663,-2.6590445041656494,NULL,2.176945447921753,'2024-12-17 01:57:37',NULL,NULL,NULL),(3317539,6,3.744840383529663,-2.6590445041656494,NULL,2.1770992279052734,'2024-12-17 01:57:38',NULL,NULL,NULL),(3317542,6,3.744840383529663,-2.6590445041656494,NULL,2.176849842071533,'2024-12-17 01:57:39',NULL,NULL,NULL),(3317546,6,3.744840383529663,-2.6590445041656494,NULL,2.1764845848083496,'2024-12-17 01:57:40',NULL,NULL,NULL),(3317550,6,3.744840383529663,-2.6590445041656494,NULL,2.177021026611328,'2024-12-17 01:57:42',174.89680767059326,253.180890083313,NULL),(3325437,8,-0.347635418176651,0.9406717419624329,NULL,0.07677920162677765,'2024-12-17 03:22:58',NULL,NULL,NULL),(3325438,8,-0.347635418176651,0.9406717419624329,NULL,0.07680164277553558,'2024-12-17 03:22:59',NULL,NULL,NULL),(3325439,8,-0.347635418176651,0.9406717419624329,NULL,0.07680726796388626,'2024-12-17 03:23:00',NULL,NULL,NULL),(3325440,8,-0.347635418176651,0.9406717419624329,NULL,0.07681190967559814,'2024-12-17 03:23:01',NULL,NULL,NULL),(3325441,8,-0.347635418176651,0.9406717419624329,NULL,0.0768025666475296,'2024-12-17 03:23:02',NULL,NULL,NULL),(3325442,8,-0.347635418176651,0.9406717419624329,NULL,0.07678630203008652,'2024-12-17 03:23:03',NULL,NULL,NULL),(3325443,8,-0.347635418176651,0.9406717419624329,NULL,0.07692316174507141,'2024-12-17 03:23:04',NULL,NULL,NULL),(3325444,8,-0.34827670454978943,0.9406261444091797,NULL,0.06137339025735855,'2024-12-17 03:23:05',NULL,NULL,NULL),(3325445,8,-0.5150233507156372,0.9721590876579285,NULL,-0.21860559284687042,'2024-12-17 03:23:06',NULL,NULL,NULL),(3325446,8,-0.8956071734428406,1.0509769916534424,NULL,-0.35646793246269226,'2024-12-17 03:23:07',NULL,NULL,NULL),(3325447,8,-0.9914401173591614,1.094071626663208,NULL,-0.4577879309654236,'2024-12-17 03:23:08',NULL,NULL,NULL),(3415735,11,-0.1,-0.1,NULL,1.5,'2025-01-14 23:18:59',NULL,NULL,NULL),(3415736,11,-0.1,-0.1,NULL,1.5,'2025-01-14 23:19:00',NULL,NULL,NULL),(3415737,11,-0.1,-0.1,NULL,1.5,'2025-01-14 23:19:01',NULL,NULL,NULL),(3415738,11,-0.1,-0.1,NULL,1.5,'2025-01-14 23:19:02',NULL,NULL,NULL),(3415739,11,-0.1,-0.1,NULL,1.5,'2025-01-14 23:19:04',NULL,NULL,NULL),(3415740,11,-0.1,-0.1,NULL,1.5,'2025-01-14 23:19:05',NULL,NULL,NULL),(3415741,11,-0.1,-0.1,NULL,1.5,'2025-01-14 23:19:06',NULL,NULL,NULL),(3415742,11,-0.1,-0.1,NULL,1.5,'2025-01-14 23:19:07',NULL,NULL,NULL),(3415743,11,-0.1,-0.1,NULL,1.5,'2025-01-14 23:19:08',NULL,NULL,NULL),(3415744,11,-0.1,-0.1,NULL,1.5,'2025-01-14 23:19:10',NULL,NULL,NULL);
/*!40000 ALTER TABLE `robot_location` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `robot_map`
--

DROP TABLE IF EXISTS `robot_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `robot_map` (
  `map_id` int NOT NULL AUTO_INCREMENT,
  `robot_id` int DEFAULT NULL,
  `x` double DEFAULT NULL,
  `y` double DEFAULT NULL,
  `res` double DEFAULT NULL,
  `height` int DEFAULT NULL,
  `width` int DEFAULT NULL,
  `current` tinyint DEFAULT '0',
  `map_name` varchar(256) NOT NULL DEFAULT '',
  PRIMARY KEY (`map_id`),
  KEY `idx_robot_map_robot_id_map_id` (`robot_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `robot_map`
--

LOCK TABLES `robot_map` WRITE;
/*!40000 ALTER TABLE `robot_map` DISABLE KEYS */;
INSERT INTO `robot_map` VALUES (1,6,-4.3,-4.3,0.05,280,300,0,''),(2,6,-8,-5,0.05,320,260,0,''),(5,6,-5,-7,0.05,340,260,1,'');
/*!40000 ALTER TABLE `robot_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `task`
--

DROP TABLE IF EXISTS `task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `task` (
  `task_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(128) DEFAULT NULL,
  `robot_id` int DEFAULT NULL,
  `state` tinyint DEFAULT NULL,
  `cost` int DEFAULT NULL,
  `start` varchar(128) DEFAULT NULL,
  `end` varchar(128) DEFAULT NULL,
  `timeStamp` datetime DEFAULT NULL,
  PRIMARY KEY (`task_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `task`
--

LOCK TABLES `task` WRITE;
/*!40000 ALTER TABLE `task` DISABLE KEYS */;
INSERT INTO `task` VALUES (1,'task1',1,1,NULL,'2023-03-16 08:45:21',NULL,NULL),(2,'task2',11,2,NULL,'2025-02-20 13:35:07','2025-01-01 09:27:49',NULL),(3,'task3',1,-1,NULL,NULL,NULL,NULL),(4,'task4',2,0,NULL,NULL,NULL,NULL),(5,'task5',3,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `task` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(20) NOT NULL,
  `hashed_password` varchar(255) NOT NULL,
  `email` varchar(100) NOT NULL,
  `confirmed` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','$2b$10$49Avw/OC0.oPCHUr4mGPKex2c3OdS57gN8Npu0vViKzOzzpD.1HDC','admin@gmail.com',1),(2,'testuser','$2b$10$vofg/FFLYxvHS5QG1QXalOaoAcmLOUqxNAKdP7jOwWGmhbSHNNrf6','robonav41@gmail.com',1);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-03-12 20:47:31
