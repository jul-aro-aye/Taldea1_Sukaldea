-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: localhost    Database: erronka2
-- ------------------------------------------------------
-- Server version	8.0.39

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
-- Table structure for table `erabiltzaileak`
--

DROP TABLE IF EXISTS `erabiltzaileak`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erabiltzaileak` (
  `id` int NOT NULL AUTO_INCREMENT,
  `erabiltzailea` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `pasahitza` varchar(255) NOT NULL,
  `rola_id` int NOT NULL,
  `ezabatua` tinyint DEFAULT '0',
  `txat` tinyint DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  KEY `rola_id` (`rola_id`),
  KEY `rola_id_2` (`rola_id`),
  CONSTRAINT `erabiltzaileak_ibfk_1` FOREIGN KEY (`rola_id`) REFERENCES `rolak` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_B553DCB6` FOREIGN KEY (`rola_id`) REFERENCES `rolak` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `erabiltzaileak`
--

LOCK TABLES `erabiltzaileak` WRITE;
/*!40000 ALTER TABLE `erabiltzaileak` DISABLE KEYS */;
/*!40000 ALTER TABLE `erabiltzaileak` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `erreserbak`
--

DROP TABLE IF EXISTS `erreserbak`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `erreserbak` (
  `id` int NOT NULL AUTO_INCREMENT,
  `mahaia_id` int NOT NULL,
  `bezeroa_izena` varchar(120) NOT NULL,
  `telefonoa` varchar(20) DEFAULT NULL,
  `erreserba_data` date NOT NULL,
  `data` date NOT NULL,
  `txanda` enum('bazkaria','afaria') NOT NULL,
  `pertsona_kopurua` tinyint DEFAULT '1',
  `egoera` enum('amaitua','eginda','bertan_behera') DEFAULT 'eginda',
  PRIMARY KEY (`id`),
  KEY `fk_erreserbak_mahaiak1_idx` (`mahaia_id`),
  CONSTRAINT `fk_erreserbak_mahaiak1` FOREIGN KEY (`mahaia_id`) REFERENCES `mahaiak` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `erreserbak`
--

LOCK TABLES `erreserbak` WRITE;
/*!40000 ALTER TABLE `erreserbak` DISABLE KEYS */;
/*!40000 ALTER TABLE `erreserbak` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eskaera_mahaiak`
--

DROP TABLE IF EXISTS `eskaera_mahaiak`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `eskaera_mahaiak` (
  `id` int NOT NULL AUTO_INCREMENT,
  `eskaera_id` int NOT NULL,
  `mahaia_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_eskaera_mahaia` (`eskaera_id`,`mahaia_id`),
  KEY `fk_eskaera_mahaiak_mahaia` (`mahaia_id`),
  CONSTRAINT `fk_eskaera_mahaiak_eskaera` FOREIGN KEY (`eskaera_id`) REFERENCES `eskaerak` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_eskaera_mahaiak_mahaia` FOREIGN KEY (`mahaia_id`) REFERENCES `mahaiak` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eskaera_mahaiak`
--

LOCK TABLES `eskaera_mahaiak` WRITE;
/*!40000 ALTER TABLE `eskaera_mahaiak` DISABLE KEYS */;
/*!40000 ALTER TABLE `eskaera_mahaiak` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eskaera_produktuak`
--

DROP TABLE IF EXISTS `eskaera_produktuak`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `eskaera_produktuak` (
  `id` int NOT NULL AUTO_INCREMENT,
  `eskaera_id` int NOT NULL,
  `produktua_id` int NOT NULL,
  `kantitatea` int NOT NULL DEFAULT '1',
  `egoera` enum('bidalita','egiten','eginda') DEFAULT 'bidalita',
  `prezio_unitarioa` decimal(10,2) NOT NULL,
  `guztira` decimal(10,2) GENERATED ALWAYS AS ((`kantitatea` * `prezio_unitarioa`)) STORED,
  PRIMARY KEY (`id`),
  KEY `eskaera_id` (`eskaera_id`),
  KEY `produktua_id` (`produktua_id`),
  CONSTRAINT `eskaera_produktuak_ibfk_1` FOREIGN KEY (`eskaera_id`) REFERENCES `eskaerak` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `eskaera_produktuak_ibfk_2` FOREIGN KEY (`produktua_id`) REFERENCES `produktuak` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eskaera_produktuak`
--

LOCK TABLES `eskaera_produktuak` WRITE;
/*!40000 ALTER TABLE `eskaera_produktuak` DISABLE KEYS */;
/*!40000 ALTER TABLE `eskaera_produktuak` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eskaerak`
--

DROP TABLE IF EXISTS `eskaerak`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `eskaerak` (
  `id` int NOT NULL AUTO_INCREMENT,
  `mahaia_id` int DEFAULT NULL,
  `erabiltzaile_id` int NOT NULL,
  `komensalak` tinyint DEFAULT NULL,
  `egoera` enum('irekita','itxita','ordainketa_pendiente') DEFAULT 'irekita',
  `sukaldea_egoera` enum('bidalita','egiten','eginda') DEFAULT 'bidalita',
  `sortze_data` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `itxiera_data` datetime DEFAULT NULL,
  `erreserba_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `erreserba_id` (`erreserba_id`),
  KEY `mahaia_id` (`mahaia_id`),
  KEY `erabiltzaile_id` (`erabiltzaile_id`),
  CONSTRAINT `eskaerak_ibfk_1` FOREIGN KEY (`mahaia_id`) REFERENCES `mahaiak` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `eskaerak_ibfk_2` FOREIGN KEY (`erabiltzaile_id`) REFERENCES `erabiltzaileak` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `eskaerak_ibfk_3` FOREIGN KEY (`erreserba_id`) REFERENCES `erreserbak` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eskaerak`
--

LOCK TABLES `eskaerak` WRITE;
/*!40000 ALTER TABLE `eskaerak` DISABLE KEYS */;
/*!40000 ALTER TABLE `eskaerak` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fakturak`
--

DROP TABLE IF EXISTS `fakturak`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fakturak` (
  `id` int NOT NULL AUTO_INCREMENT,
  `eskaera_id` int NOT NULL,
  `pdf_izena` varchar(255) NOT NULL,
  `data` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `guztira` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `eskaera_id` (`eskaera_id`),
  CONSTRAINT `fakturak_ibfk_1` FOREIGN KEY (`eskaera_id`) REFERENCES `eskaerak` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fakturak`
--

LOCK TABLES `fakturak` WRITE;
/*!40000 ALTER TABLE `fakturak` DISABLE KEYS */;
/*!40000 ALTER TABLE `fakturak` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kategoriak`
--

DROP TABLE IF EXISTS `kategoriak`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kategoriak` (
  `id` int NOT NULL AUTO_INCREMENT,
  `izena` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `izena` (`izena`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kategoriak`
--

LOCK TABLES `kategoriak` WRITE;
/*!40000 ALTER TABLE `kategoriak` DISABLE KEYS */;
/*!40000 ALTER TABLE `kategoriak` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mahaiak`
--

DROP TABLE IF EXISTS `mahaiak`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mahaiak` (
  `id` int NOT NULL AUTO_INCREMENT,
  `zenbakia` int NOT NULL,
  `egoera` enum('libre','okupatuta','ordainketa_pendiente') DEFAULT 'libre',
  PRIMARY KEY (`id`),
  UNIQUE KEY `zenbakia` (`zenbakia`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mahaiak`
--

LOCK TABLES `mahaiak` WRITE;
/*!40000 ALTER TABLE `mahaiak` DISABLE KEYS */;
/*!40000 ALTER TABLE `mahaiak` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `osagaiak`
--

DROP TABLE IF EXISTS `osagaiak`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `osagaiak` (
  `id` int NOT NULL AUTO_INCREMENT,
  `izena` varchar(150) NOT NULL,
  `unitatea` varchar(20) DEFAULT NULL,
  `stock_aktuala` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `izena` (`izena`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `osagaiak`
--

LOCK TABLES `osagaiak` WRITE;
/*!40000 ALTER TABLE `osagaiak` DISABLE KEYS */;
/*!40000 ALTER TABLE `osagaiak` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `produktu_osagaiak`
--

DROP TABLE IF EXISTS `produktu_osagaiak`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `produktu_osagaiak` (
  `produktua_id` int NOT NULL,
  `osagaia_id` int NOT NULL,
  `kantitatea` decimal(10,2) NOT NULL,
  `unitatea` varchar(20) NOT NULL,
  PRIMARY KEY (`produktua_id`,`osagaia_id`),
  KEY `produktu_osagaiak_ibfk_2` (`osagaia_id`),
  CONSTRAINT `produktu_osagaiak_ibfk_1` FOREIGN KEY (`produktua_id`) REFERENCES `produktuak` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `produktu_osagaiak_ibfk_2` FOREIGN KEY (`osagaia_id`) REFERENCES `osagaiak` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `produktu_osagaiak`
--

LOCK TABLES `produktu_osagaiak` WRITE;
/*!40000 ALTER TABLE `produktu_osagaiak` DISABLE KEYS */;
/*!40000 ALTER TABLE `produktu_osagaiak` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `produktuak`
--

DROP TABLE IF EXISTS `produktuak`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `produktuak` (
  `id` int NOT NULL AUTO_INCREMENT,
  `izena` varchar(200) NOT NULL,
  `kategoria_id` int NOT NULL,
  `prezioa` decimal(10,2) NOT NULL,
  `stock_aktuala` int DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `kategoria_id` (`kategoria_id`),
  CONSTRAINT `produktuak_ibfk_1` FOREIGN KEY (`kategoria_id`) REFERENCES `kategoriak` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=99 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `produktuak`
--

LOCK TABLES `produktuak` WRITE;
/*!40000 ALTER TABLE `produktuak` DISABLE KEYS */;
/*!40000 ALTER TABLE `produktuak` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rolak`
--

DROP TABLE IF EXISTS `rolak`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rolak` (
  `id` int NOT NULL AUTO_INCREMENT,
  `izena` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rolak`
--

LOCK TABLES `rolak` WRITE;
/*!40000 ALTER TABLE `rolak` DISABLE KEYS */;
/*!40000 ALTER TABLE `rolak` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-25 13:48:07
