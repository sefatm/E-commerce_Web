CREATE DATABASE  IF NOT EXISTS `rural_mart` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `rural_mart`;
-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: localhost    Database: rural_mart
-- ------------------------------------------------------
-- Server version	8.0.45

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
-- Table structure for table `add_product`
--

DROP TABLE IF EXISTS `add_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `add_product` (
  `id` int NOT NULL AUTO_INCREMENT,
  `category` varchar(255) DEFAULT NULL,
  `description` text,
  `image` varchar(500) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `price` double DEFAULT NULL,
  `created_at` date DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `category_id` bigint DEFAULT NULL,
  `seller_id` bigint DEFAULT NULL,
  `is_featured` bit(1) DEFAULT NULL,
  `is_on_sale` bit(1) DEFAULT NULL,
  `sale_price` double DEFAULT NULL,
  `sku` varchar(255) DEFAULT NULL,
  `stock` int DEFAULT NULL,
  `approval_status` varchar(255) DEFAULT NULL,
  `artisan_story` text,
  `craft_process` text,
  `estimated_production_days` int DEFAULT NULL,
  `origin_area` varchar(500) DEFAULT NULL,
  `pre_order_available` bit(1) DEFAULT NULL,
  `rejection_reason` text,
  `brand_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrkl2pg4xt4scsavy2by929a53` (`category_id`),
  KEY `FKd242igk3ummt1cyvi3wqobl3o` (`seller_id`),
  KEY `FKq1vtj6enpdrbi8n5rjtxu9wng` (`brand_id`),
  CONSTRAINT `FKd242igk3ummt1cyvi3wqobl3o` FOREIGN KEY (`seller_id`) REFERENCES `seller` (`id`),
  CONSTRAINT `FKq1vtj6enpdrbi8n5rjtxu9wng` FOREIGN KEY (`brand_id`) REFERENCES `brand` (`id`),
  CONSTRAINT `FKrkl2pg4xt4scsavy2by929a53` FOREIGN KEY (`category_id`) REFERENCES `product_category` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=84 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `add_product`
--

LOCK TABLES `add_product` WRITE;
/*!40000 ALTER TABLE `add_product` DISABLE KEYS */;
/*!40000 ALTER TABLE `add_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `brand`
--

DROP TABLE IF EXISTS `brand`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `brand` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` date DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `logo` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `product_count` int DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `brand`
--

LOCK TABLES `brand` WRITE;
/*!40000 ALTER TABLE `brand` DISABLE KEYS */;
/*!40000 ALTER TABLE `brand` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `commission`
--

DROP TABLE IF EXISTS `commission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `commission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `commission_amount` double DEFAULT NULL,
  `commission_rate` double DEFAULT NULL,
  `created_at` date DEFAULT NULL,
  `gross_amount` double DEFAULT NULL,
  `paid_at` date DEFAULT NULL,
  `seller_amount` double DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `order_id` bigint DEFAULT NULL,
  `seller_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6uxs4k33s0fiaro14vj8gw5ah` (`order_id`),
  KEY `FK45sefx6g9rokw6tnjnt997b6l` (`seller_id`),
  CONSTRAINT `FK45sefx6g9rokw6tnjnt997b6l` FOREIGN KEY (`seller_id`) REFERENCES `seller` (`id`),
  CONSTRAINT `FK6uxs4k33s0fiaro14vj8gw5ah` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `commission`
--

LOCK TABLES `commission` WRITE;
/*!40000 ALTER TABLE `commission` DISABLE KEYS */;
/*!40000 ALTER TABLE `commission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `coupon`
--

DROP TABLE IF EXISTS `coupon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `coupon` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `created_at` date DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `discount_type` varchar(255) NOT NULL,
  `discount_value` double NOT NULL,
  `end_date` date DEFAULT NULL,
  `max_discount_amount` double DEFAULT NULL,
  `min_order_amount` double DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `usage_limit` int DEFAULT NULL,
  `used_count` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKbg4p9ontpj7adq7yr71h93sdn` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `coupon`
--

LOCK TABLES `coupon` WRITE;
/*!40000 ALTER TABLE `coupon` DISABLE KEYS */;
INSERT INTO `coupon` VALUES (1,'SAVE.20',NULL,'nmkhjxhjc','PERCENTAGE',20,'2026-04-22',200,1000,'2026-04-22','ACTIVE',1,0),(2,'VICKY','2026-05-04','abfjggh,,h','PERCENTAGE',10,'2026-05-05',250,1000,'2016-01-02','ACTIVE',1,0);
/*!40000 ALTER TABLE `coupon` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(500) DEFAULT NULL,
  `created_at` date DEFAULT NULL,
  `district` varchar(255) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phone` varchar(255) NOT NULL,
  `postal_code` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `upazila` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKdwk6cx0afu8bs9o4t536v1j5v` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `delivery_method`
--

DROP TABLE IF EXISTS `delivery_method`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `delivery_method` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `carrier` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `charge` double DEFAULT '0',
  `free_shipping_above` double DEFAULT NULL,
  `estimated_days` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `created_at` date DEFAULT NULL,
  `zone_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_delivery_zone` (`zone_id`),
  CONSTRAINT `FK_delivery_zone` FOREIGN KEY (`zone_id`) REFERENCES `shipping_zone` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `delivery_method`
--

LOCK TABLES `delivery_method` WRITE;
/*!40000 ALTER TABLE `delivery_method` DISABLE KEYS */;
INSERT INTO `delivery_method` VALUES (1,'Same Day Delivery','In-house','একই দিনে ডেলিভারি (সকাল ১১টার মধ্যে অর্ডার দিলে)',80,1500,'আজকের মধ্যে','flat_rate','active','2026-04-22',1),(2,'Next Day Delivery','Pathao','পরের দিন সকালে ডেলিভারি',60,1000,'১ দিন','flat_rate','active','2026-04-22',1),(3,'Express (2-3 days)','Sundarban','দ্রুত ডেলিভারি সেবা',50,800,'২-৩ কার্যদিবস','flat_rate','active','2026-04-22',1),(4,'Pickup Point','In-house','আমাদের অফিস থেকে সংগ্রহ করুন',0,NULL,'প্রস্তুত হলে জানানো হবে','pickup','active','2026-04-22',1),(5,'Standard Delivery','Sundarban','চট্টগ্রামে সাধারণ ডেলিভারি',100,2000,'3-5 কার্যদিবস','flat_rate','active','2026-04-22',2),(6,'Express Delivery','Redx','দ্রুত ডেলিভারি সেবা',150,3000,'1-2 কার্যদিবস','flat_rate','active','2026-04-22',2),(7,'Standard Nationwide','Sundarban','সারাদেশে সাধারণ ডেলিভারি',120,2500,'5-7 কার্যদিবস','flat_rate','active','2026-04-22',4);
/*!40000 ALTER TABLE `delivery_method` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `offer`
--

DROP TABLE IF EXISTS `offer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `offer` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `banner_image` varchar(255) DEFAULT NULL,
  `created_at` date DEFAULT NULL,
  `description` varchar(2000) DEFAULT NULL,
  `discount_percentage` double DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `offer_type` varchar(255) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `category_id` bigint DEFAULT NULL,
  `product_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK2vlou67ewq23a2raolk7s839j` (`category_id`),
  KEY `FKnt03np5lsqog8ai2m3v6bcjbk` (`product_id`),
  CONSTRAINT `FK2vlou67ewq23a2raolk7s839j` FOREIGN KEY (`category_id`) REFERENCES `product_category` (`id`),
  CONSTRAINT `FKnt03np5lsqog8ai2m3v6bcjbk` FOREIGN KEY (`product_id`) REFERENCES `add_product` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `offer`
--

LOCK TABLES `offer` WRITE;
/*!40000 ALTER TABLE `offer` DISABLE KEYS */;
INSERT INTO `offer` VALUES (1,NULL,'2026-04-22','jdsjndskn',30,'2026-04-22','FLASH_SALE','2026-04-22','ACTIVE','Fashion',NULL,NULL);
/*!40000 ALTER TABLE `offer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_item`
--

DROP TABLE IF EXISTS `order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint DEFAULT NULL,
  `product_image` varchar(255) DEFAULT NULL,
  `product_name` varchar(255) DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `total_price` double DEFAULT NULL,
  `unit_price` double DEFAULT NULL,
  `order_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKt4dc2r9nbvbujrljv3e23iibt` (`order_id`),
  CONSTRAINT `FKt4dc2r9nbvbujrljv3e23iibt` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=102 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_item`
--

LOCK TABLES `order_item` WRITE;
/*!40000 ALTER TABLE `order_item` DISABLE KEYS */;
INSERT INTO `order_item` VALUES (1,4,NULL,'Table',1,300,300,NULL),(3,16,'1777069061721_Saree.webp','Hand Make Jamdani',1,2500,2500,NULL),(6,18,NULL,'saree',1,1999,1999,NULL),(14,21,NULL,'churi',1,300,300,NULL),(16,32,'1777581351038_11_dc2c03dd-2d02-4b40-aee3-a64da3accad2.webp','Frock',1,1200,1200,NULL),(17,32,'1777581351038_11_dc2c03dd-2d02-4b40-aee3-a64da3accad2.webp','Frock',1,1100,1100,NULL),(32,40,'1777826568287_sari.webp','Handmade Cotton Saree',1,2500,2500,NULL),(36,36,'1777825772665_images.jpg','Handmade Bamboo Basket',2,300,150,NULL),(37,43,'1777891281921_Terracotta Necklace.jpg','Terracotta Necklace',2,498,249,NULL),(40,51,'1777895098740_churi.jpg','churi',1,250,250,NULL),(41,39,'1777826341587_istockphoto-1277495985-612x612.webp','Clay Flower Vase',1,250,250,NULL),(42,43,'1777891281921_Terracotta Necklace.jpg','Terracotta Necklace',1,249,249,NULL),(43,51,'1777895098740_churi.jpg','churi',1,250,250,NULL),(44,39,'1777826341587_istockphoto-1277495985-612x612.webp','Clay Flower Vase',1,250,250,NULL),(45,43,'1777891281921_Terracotta Necklace.jpg','Terracotta Necklace',1,249,249,NULL),(53,44,'1777891507348_Bamboo Chair.png','Bamboo Chair',1,799,799,NULL),(54,53,'1777968399117_Organic Honey.jpg','Organic Honey',1,350,350,NULL),(57,54,'1778529875100_03_jhuri_single_1.webp','jhuri-single-hanging-basket',1,900,900,NULL),(65,66,'1779164752128_sari.webp','Handmade Saree',1,2500,2500,NULL),(71,71,NULL,'ROAM_ORDER_INVALID_FLOW_TEST_20260520',1,100,100,NULL);
/*!40000 ALTER TABLE `order_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_return`
--

DROP TABLE IF EXISTS `order_return`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_return` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `admin_note` varchar(500) DEFAULT NULL,
  `product_name` varchar(255) DEFAULT NULL,
  `reason` varchar(1000) DEFAULT NULL,
  `request_date` date DEFAULT NULL,
  `resolved_date` date DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `order_id` bigint DEFAULT NULL,
  `refund_amount` double DEFAULT NULL,
  `refund_date` date DEFAULT NULL,
  `refund_status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKptadldyc0sr4n8w3hrqnsshqt` (`order_id`),
  CONSTRAINT `FKptadldyc0sr4n8w3hrqnsshqt` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_return`
--

LOCK TABLES `order_return` WRITE;
/*!40000 ALTER TABLE `order_return` DISABLE KEYS */;
/*!40000 ALTER TABLE `order_return` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `coupon_code` varchar(255) DEFAULT NULL,
  `customer_email` varchar(255) DEFAULT NULL,
  `customer_name` varchar(255) NOT NULL,
  `customer_phone` varchar(255) NOT NULL,
  `delivered_date` date DEFAULT NULL,
  `discount_amount` double DEFAULT NULL,
  `order_code` varchar(255) DEFAULT NULL,
  `order_date` date DEFAULT NULL,
  `order_note` varchar(1000) DEFAULT NULL,
  `payment_method` varchar(255) DEFAULT NULL,
  `payment_status` varchar(255) DEFAULT NULL,
  `shipping_address` varchar(500) NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `subtotal` double DEFAULT NULL,
  `total_amount` double DEFAULT NULL,
  `total_price` decimal(19,2) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKdhk2umg8ijjkg4njg6891trit` (`order_code`),
  KEY `FK32ql8ubntj5uh44ph9659tiih` (`user_id`),
  CONSTRAINT `FK32ql8ubntj5uh44ph9659tiih` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=79 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `password_reset_otp`
--

DROP TABLE IF EXISTS `password_reset_otp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `password_reset_otp` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `otp` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `expires_at` datetime NOT NULL,
  `used` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_otp_email` (`email`),
  KEY `idx_otp_email_code` (`email`,`otp`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `password_reset_otp`
--

LOCK TABLES `password_reset_otp` WRITE;
/*!40000 ALTER TABLE `password_reset_otp` DISABLE KEYS */;
INSERT INTO `password_reset_otp` VALUES (1,'anikalutfunnahar@gmail.com','294106','2026-06-15 12:32:35',1,'2026-06-15 12:22:35'),(2,'anikalutfunnahar@gmail.com','352793','2026-06-15 12:54:25',1,'2026-06-15 12:44:25');
/*!40000 ALTER TABLE `password_reset_otp` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_attribute`
--

DROP TABLE IF EXISTS `product_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_attribute` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `values` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Comma-separated allowed values',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_attribute_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_attribute`
--

LOCK TABLES `product_attribute` WRITE;
/*!40000 ALTER TABLE `product_attribute` DISABLE KEYS */;
INSERT INTO `product_attribute` VALUES (1,'Color','Red,Blue,Green,Black,White,Yellow,Pink,Purple,Orange,Brown'),(2,'Size','XS,S,M,L,XL,XXL'),(3,'Material','Cotton,Silk,Jute,Brass,Bamboo,Clay,Wood,Leather');
/*!40000 ALTER TABLE `product_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_category`
--

DROP TABLE IF EXISTS `product_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_category`
--

LOCK TABLES `product_category` WRITE;
/*!40000 ALTER TABLE `product_category` DISABLE KEYS */;
/*!40000 ALTER TABLE `product_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_variant`
--

DROP TABLE IF EXISTS `product_variant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_variant` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `attribute_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `attribute_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `price_adjustment` double NOT NULL DEFAULT '0',
  `stock` int NOT NULL DEFAULT '0',
  `sku` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_variant_product` (`product_id`),
  CONSTRAINT `fk_variant_product` FOREIGN KEY (`product_id`) REFERENCES `add_product` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_variant`
--

LOCK TABLES `product_variant` WRITE;
/*!40000 ALTER TABLE `product_variant` DISABLE KEYS */;
/*!40000 ALTER TABLE `product_variant` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `refund`
--

DROP TABLE IF EXISTS `refund`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `refund` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `admin_note` varchar(500) DEFAULT NULL,
  `processed_date` date DEFAULT NULL,
  `refund_amount` double DEFAULT NULL,
  `refund_method` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `order_id` bigint DEFAULT NULL,
  `return_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK80vls36avhp4yl7h8apkqm0ek` (`order_id`),
  KEY `FKskhywal0h3q9wr1mymkk5c1oi` (`return_id`),
  CONSTRAINT `FK80vls36avhp4yl7h8apkqm0ek` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `FKskhywal0h3q9wr1mymkk5c1oi` FOREIGN KEY (`return_id`) REFERENCES `order_return` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refund`
--

LOCK TABLES `refund` WRITE;
/*!40000 ALTER TABLE `refund` DISABLE KEYS */;
/*!40000 ALTER TABLE `refund` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `review`
--

DROP TABLE IF EXISTS `review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `customer_id` bigint NOT NULL,
  `rating` tinyint NOT NULL,
  `comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'APPROVED',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_review_customer_product` (`customer_id`,`product_id`),
  KEY `idx_review_product` (`product_id`),
  KEY `idx_review_rating` (`rating`),
  KEY `idx_review_status` (`status`),
  CONSTRAINT `fk_review_customer` FOREIGN KEY (`customer_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_review_product` FOREIGN KEY (`product_id`) REFERENCES `add_product` (`id`) ON DELETE CASCADE,
  CONSTRAINT `review_chk_1` CHECK ((`rating` between 1 and 5))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review`
--

LOCK TABLES `review` WRITE;
/*!40000 ALTER TABLE `review` DISABLE KEYS */;
/*!40000 ALTER TABLE `review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `seller`
--

DROP TABLE IF EXISTS `seller`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `seller` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `address` varchar(500) DEFAULT NULL,
  `nid_no` varchar(50) DEFAULT NULL,
  `status` varchar(30) DEFAULT NULL,
  `created_at` date DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `artisan_story` text,
  `commission_rate` double DEFAULT NULL,
  `craft_process` text,
  `district` varchar(120) DEFAULT NULL,
  `email` varchar(150) DEFAULT NULL,
  `phone` varchar(30) DEFAULT NULL,
  `rating` double DEFAULT NULL,
  `rejection_reason` text,
  `review_count` int DEFAULT NULL,
  `shop_name` varchar(255) DEFAULT NULL,
  `verified` bit(1) DEFAULT NULL,
  `business_type` varchar(80) DEFAULT NULL,
  `nid_back_image` varchar(255) DEFAULT NULL,
  `nid_front_image` varchar(255) DEFAULT NULL,
  `payment_method` varchar(50) DEFAULT NULL,
  `payment_number` varchar(80) DEFAULT NULL,
  `product_category` varchar(120) DEFAULT NULL,
  `profile_photo` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_seller_user` (`user_id`),
  CONSTRAINT `FK_seller_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `seller`
--

LOCK TABLES `seller` WRITE;
/*!40000 ALTER TABLE `seller` DISABLE KEYS */;
INSERT INTO `seller` VALUES (2,'anni','mymensingh','6465299540','APPROVED','2026-05-12',2,'How a simple women becomes a entrepreneur.',10,'skill used in deceiving others.','Mymensingh','seller@test.com','01758090080',0,NULL,0,'Beautyshop',_binary '',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(3,'Anika','Framgate','1020304050','APPROVED','2026-05-12',2,'jksasakjs idsjidwui oidkdoid',10,'nmxzjisa jwejweo deore ','Dhaka','anika@gmail.com','01758090080',0,NULL,0,'Anika\'s House',_binary '',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(4,'keya Akhter','Gafargaon,Mymensingh','5856298588','APPROVED','2026-05-18',NULL,'## Artisan Story — ROAM Featured Artisan\n\n### “মাটির গন্ধ থেকে বিশ্ববাজারে”\n\nনওগাঁর ছোট্ট এক গ্রামের মেয়ে রহিমা খাতুন। ছোটবেলা থেকেই মায়ের পাশে বসে হাতে নকশিকাঁথা সেলাই করতেন। সংসারের অভাব ছিল, কিন্তু হাতের শিল্পের প্রতি ভালোবাসা কখনো কমেনি। গ্রামের মানুষদের কাছে এটা ছিল শুধু “শখের কাজ”, কিন্তু রহিমার কাছে ছিল নিজের পরিচয় তৈরি করার স্বপ্ন।\n\nবিয়ের পর জীবনের দায়িত্ব আরও বেড়ে যায়। স্বামীর অল্প আয়ে সংসার চালানো কঠিন হয়ে পড়ে। তখন তিনি পুরোনো কাপড়, সুতা আর গ্রামের নারীদের নিয়ে আবার কাজ শুরু করেন। প্রথমদিকে স্থানীয় হাটে খুব কম দামে কাঁথা বিক্রি হতো। অনেক সময় দিন শেষে কোনো বিক্রিই হতো না।\n\nকিন্তু রহিমা হাল ছাড়েননি।\n\nধীরে ধীরে তিনি নতুন ডিজাইন শেখেন, গ্রামের আরও নারীদের প্রশিক্ষণ দেন এবং হাতে তৈরি প্রতিটি পণ্যে বাংলাদেশের গ্রামীণ সংস্কৃতির গল্প ফুটিয়ে তুলতে শুরু করেন। আজ তার তৈরি নকশিকাঁথা, জুট ব্যাগ ও হ্যান্ডক্রাফট পণ্য শুধু দেশের বিভিন্ন শহরেই নয়, অনলাইন প্ল্যাটফর্মের মাধ্যমে বিদেশেও পৌঁছাচ্ছে।\n\nROAM-এর মাধ্যমে রহিমা এখন সরাসরি ক্রেতাদের কাছে নিজের পণ্য বিক্রি করতে পারেন। মধ্যস্বত্বভোগী ছাড়াই তিনি ন্যায্য মূল্য পান, আর তার সঙ্গে কাজ করা ২৫ জন গ্রামের নারীও এখন স্বাবলম্বী।\n\nরহিমা বলেন:\n\n> “আমাদের হাতে তৈরি প্রতিটি জিনিসে শুধু সুতা না, আমাদের জীবনের গল্পও সেলাই করা থাকে।”\n\nএই গল্প শুধু একজন শিল্পীর নয় — এটি বাংলাদেশের হাজারো গ্রামীণ কারুশিল্পীর সংগ্রাম, স্বপ্ন আর আত্মমর্যাদার গল্প।\n',10,'গ্রামীণ সংস্কৃতি, লোকজ নকশা ও আধুনিক ডিজাইনের সমন্বয়ে পণ্য তৈরি করা হয়।\nপ্রতিটি পণ্যে artisan-এর নিজস্ব সৃজনশীলতা ফুটে ওঠে।','Mymensingh','anikalutfunnahar@gmail.com','01758090080',0,NULL,0,'keya Beauty Shop',_binary '',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(5,'Kamal Hossain','Dhaka','6235591978','APPROVED','2026-05-19',NULL,'## Artisan Story — ROAM Featured Artisan\n\n### “মাটির গন্ধ থেকে বিশ্ববাজারে”\n\nনওগাঁর ছোট্ট এক গ্রামের মেয়ে রহিমা খাতুন। ছোটবেলা থেকেই মায়ের পাশে বসে হাতে নকশিকাঁথা সেলাই করতেন। সংসারের অভাব ছিল, কিন্তু হাতের শিল্পের প্রতি ভালোবাসা কখনো কমেনি। গ্রামের মানুষদের কাছে এটা ছিল শুধু “শখের কাজ”, কিন্তু রহিমার কাছে ছিল নিজের পরিচয় তৈরি করার স্বপ্ন।\n\nবিয়ের পর জীবনের দায়িত্ব আরও বেড়ে যায়। স্বামীর অল্প আয়ে সংসার চালানো কঠিন হয়ে পড়ে। তখন তিনি পুরোনো কাপড়, সুতা আর গ্রামের নারীদের নিয়ে আবার কাজ শুরু করেন। প্রথমদিকে স্থানীয় হাটে খুব কম দামে কাঁথা বিক্রি হতো। অনেক সময় দিন শেষে কোনো বিক্রিই হতো না।\n\nকিন্তু রহিমা হাল ছাড়েননি।\n\nধীরে ধীরে তিনি নতুন ডিজাইন শেখেন, গ্রামের আরও নারীদের প্রশিক্ষণ দেন এবং হাতে তৈরি প্রতিটি পণ্যে বাংলাদেশের গ্রামীণ সংস্কৃতির গল্প ফুটিয়ে তুলতে শুরু করেন। আজ তার তৈরি নকশিকাঁথা, জুট ব্যাগ ও হ্যান্ডক্রাফট পণ্য শুধু দেশের বিভিন্ন শহরেই নয়, অনলাইন প্ল্যাটফর্মের মাধ্যমে বিদেশেও পৌঁছাচ্ছে।\n\nROAM-এর মাধ্যমে রহিমা এখন সরাসরি ক্রেতাদের কাছে নিজের পণ্য বিক্রি করতে পারেন। মধ্যস্বত্বভোগী ছাড়াই তিনি ন্যায্য মূল্য পান, আর তার সঙ্গে কাজ করা ২৫ জন গ্রামের নারীও এখন স্বাবলম্বী।\n\nরহিমা বলেন:\n\n> “আমাদের হাতে তৈরি প্রতিটি জিনিসে শুধু সুতা না, আমাদের জীবনের গল্পও সেলাই করা থাকে।”\n\nএই গল্প শুধু একজন শিল্পীর নয় — এটি বাংলাদেশের হাজারো গ্রামীণ কারুশিল্পীর সংগ্রাম, স্বপ্ন আর আত্মমর্যাদার গল্প।\n',10,'গ্রামীণ সংস্কৃতি, লোকজ নকশা ও আধুনিক ডিজাইনের সমন্বয়ে পণ্য তৈরি করা হয়।\nপ্রতিটি পণ্যে artisan-এর নিজস্ব সৃজনশীলতা ফুটে ওঠে।','Dhaka','anikalutfunnahar@gmail.com','01758090080',0,NULL,0,'Dream House',_binary '',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(6,'Mahbub Alam','Jamalpur','6354557829','APPROVED','2026-05-19',NULL,'## Artisan Story — ROAM Featured Artisan\n\n### “মাটির গন্ধ থেকে বিশ্ববাজারে”\n\nনওগাঁর ছোট্ট এক গ্রামের মেয়ে রহিমা খাতুন। ছোটবেলা থেকেই মায়ের পাশে বসে হাতে নকশিকাঁথা সেলাই করতেন। সংসারের অভাব ছিল, কিন্তু হাতের শিল্পের প্রতি ভালোবাসা কখনো কমেনি। গ্রামের মানুষদের কাছে এটা ছিল শুধু “শখের কাজ”, কিন্তু রহিমার কাছে ছিল নিজের পরিচয় তৈরি করার স্বপ্ন।\n\nবিয়ের পর জীবনের দায়িত্ব আরও বেড়ে যায়। স্বামীর অল্প আয়ে সংসার চালানো কঠিন হয়ে পড়ে। তখন তিনি পুরোনো কাপড়, সুতা আর গ্রামের নারীদের নিয়ে আবার কাজ শুরু করেন। প্রথমদিকে স্থানীয় হাটে খুব কম দামে কাঁথা বিক্রি হতো। অনেক সময় দিন শেষে কোনো বিক্রিই হতো না।\n\nকিন্তু রহিমা হাল ছাড়েননি।\n\nধীরে ধীরে তিনি নতুন ডিজাইন শেখেন, গ্রামের আরও নারীদের প্রশিক্ষণ দেন এবং হাতে তৈরি প্রতিটি পণ্যে বাংলাদেশের গ্রামীণ সংস্কৃতির গল্প ফুটিয়ে তুলতে শুরু করেন। আজ তার তৈরি নকশিকাঁথা, জুট ব্যাগ ও হ্যান্ডক্রাফট পণ্য শুধু দেশের বিভিন্ন শহরেই নয়, অনলাইন প্ল্যাটফর্মের মাধ্যমে বিদেশেও পৌঁছাচ্ছে।\n\nROAM-এর মাধ্যমে রহিমা এখন সরাসরি ক্রেতাদের কাছে নিজের পণ্য বিক্রি করতে পারেন। মধ্যস্বত্বভোগী ছাড়াই তিনি ন্যায্য মূল্য পান, আর তার সঙ্গে কাজ করা ২৫ জন গ্রামের নারীও এখন স্বাবলম্বী।\n\nরহিমা বলেন:\n\n> “আমাদের হাতে তৈরি প্রতিটি জিনিসে শুধু সুতা না, আমাদের জীবনের গল্পও সেলাই করা থাকে।”\n\nএই গল্প শুধু একজন শিল্পীর নয় — এটি বাংলাদেশের হাজারো গ্রামীণ কারুশিল্পীর সংগ্রাম, স্বপ্ন আর আত্মমর্যাদার গল্প।\n',10,'গ্রামীণ সংস্কৃতি, লোকজ নকশা ও আধুনিক ডিজাইনের সমন্বয়ে পণ্য তৈরি করা হয়।\nপ্রতিটি পণ্যে artisan-এর নিজস্ব সৃজনশীলতা ফুটে ওঠে।','Mymensingh','anikalutfunnahar@gmail.com','01758090080',0,NULL,0,'Alam Online Shop',_binary '',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(7,'Asha moni','Dhaka','3456573877','REJECTED','2026-05-19',NULL,'## Artisan Story — ROAM Featured Artisan\n\n### “মাটির গন্ধ থেকে বিশ্ববাজারে”\n\nনওগাঁর ছোট্ট এক গ্রামের মেয়ে রহিমা খাতুন। ছোটবেলা থেকেই মায়ের পাশে বসে হাতে নকশিকাঁথা সেলাই করতেন। সংসারের অভাব ছিল, কিন্তু হাতের শিল্পের প্রতি ভালোবাসা কখনো কমেনি। গ্রামের মানুষদের কাছে এটা ছিল শুধু “শখের কাজ”, কিন্তু রহিমার কাছে ছিল নিজের পরিচয় তৈরি করার স্বপ্ন।\n\nবিয়ের পর জীবনের দায়িত্ব আরও বেড়ে যায়। স্বামীর অল্প আয়ে সংসার চালানো কঠিন হয়ে পড়ে। তখন তিনি পুরোনো কাপড়, সুতা আর গ্রামের নারীদের নিয়ে আবার কাজ শুরু করেন। প্রথমদিকে স্থানীয় হাটে খুব কম দামে কাঁথা বিক্রি হতো। অনেক সময় দিন শেষে কোনো বিক্রিই হতো না।\n\nকিন্তু রহিমা হাল ছাড়েননি।\n\nধীরে ধীরে তিনি নতুন ডিজাইন শেখেন, গ্রামের আরও নারীদের প্রশিক্ষণ দেন এবং হাতে তৈরি প্রতিটি পণ্যে বাংলাদেশের গ্রামীণ সংস্কৃতির গল্প ফুটিয়ে তুলতে শুরু করেন। আজ তার তৈরি নকশিকাঁথা, জুট ব্যাগ ও হ্যান্ডক্রাফট পণ্য শুধু দেশের বিভিন্ন শহরেই নয়, অনলাইন প্ল্যাটফর্মের মাধ্যমে বিদেশেও পৌঁছাচ্ছে।\n\nROAM-এর মাধ্যমে রহিমা এখন সরাসরি ক্রেতাদের কাছে নিজের পণ্য বিক্রি করতে পারেন। মধ্যস্বত্বভোগী ছাড়াই তিনি ন্যায্য মূল্য পান, আর তার সঙ্গে কাজ করা ২৫ জন গ্রামের নারীও এখন স্বাবলম্বী।\n\nরহিমা বলেন:\n\n> “আমাদের হাতে তৈরি প্রতিটি জিনিসে শুধু সুতা না, আমাদের জীবনের গল্পও সেলাই করা থাকে।”\n\nএই গল্প শুধু একজন শিল্পীর নয় — এটি বাংলাদেশের হাজারো গ্রামীণ কারুশিল্পীর সংগ্রাম, স্বপ্ন আর আত্মমর্যাদার গল্প।\n',10,'## Artisan Story — ROAM Featured Artisan\n\n### “মাটির গন্ধ থেকে বিশ্ববাজারে”\n\nনওগাঁর ছোট্ট এক গ্রামের মেয়ে রহিমা খাতুন। ছোটবেলা থেকেই মায়ের পাশে বসে হাতে নকশিকাঁথা সেলাই করতেন। সংসারের অভাব ছিল, কিন্তু হাতের শিল্পের প্রতি ভালোবাসা কখনো কমেনি। গ্রামের মানুষদের কাছে এটা ছিল শুধু “শখের কাজ”, কিন্তু রহিমার কাছে ছিল নিজের পরিচয় তৈরি করার স্বপ্ন।\n\nবিয়ের পর জীবনের দায়িত্ব আরও বেড়ে যায়। স্বামীর অল্প আয়ে সংসার চালানো কঠিন হয়ে পড়ে। তখন তিনি পুরোনো কাপড়, সুতা আর গ্রামের নারীদের নিয়ে আবার কাজ শুরু করেন। প্রথমদিকে স্থানীয় হাটে খুব কম দামে কাঁথা বিক্রি হতো। অনেক সময় দিন শেষে কোনো বিক্রিই হতো না।\n\nকিন্তু রহিমা হাল ছাড়েননি।\n\nধীরে ধীরে তিনি নতুন ডিজাইন শেখেন, গ্রামের আরও নারীদের প্রশিক্ষণ দেন এবং হাতে তৈরি প্রতিটি পণ্যে বাংলাদেশের গ্রামীণ সংস্কৃতির গল্প ফুটিয়ে তুলতে শুরু করেন। আজ তার তৈরি নকশিকাঁথা, জুট ব্যাগ ও হ্যান্ডক্রাফট পণ্য শুধু দেশের বিভিন্ন শহরেই নয়, অনলাইন প্ল্যাটফর্মের মাধ্যমে বিদেশেও পৌঁছাচ্ছে।\n\nROAM-এর মাধ্যমে রহিমা এখন সরাসরি ক্রেতাদের কাছে নিজের পণ্য বিক্রি করতে পারেন। মধ্যস্বত্বভোগী ছাড়াই তিনি ন্যায্য মূল্য পান, আর তার সঙ্গে কাজ করা ২৫ জন গ্রামের নারীও এখন স্বাবলম্বী।\n\nরহিমা বলেন:\n\n> “আমাদের হাতে তৈরি প্রতিটি জিনিসে শুধু সুতা না, আমাদের জীবনের গল্পও সেলাই করা থাকে।”\n\nএই গল্প শুধু একজন শিল্পীর নয় — এটি বাংলাদেশের হাজারো গ্রামীণ কারুশিল্পীর সংগ্রাম, স্বপ্ন আর আত্মমর্যাদার গল্প।\n','Dhaka','anikalutfunnahar@gmail.com','01758090080',0,'invalid information',0,'Asha Shop',_binary '\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(8,'Anisha','Chittagong','2436587699','APPROVED','2026-05-19',NULL,'গ্রামীণ সংস্কৃতি, লোকজ নকশা ও আধুনিক ডিজাইনের সমন্বয়ে পণ্য তৈরি করা হয়।\nপ্রতিটি পণ্যে artisan-এর নিজস্ব সৃজনশীলতা ফুটে ওঠে।',10,'গ্রামীণ সংস্কৃতি, লোকজ নকশা ও আধুনিক ডিজাইনের সমন্বয়ে পণ্য তৈরি করা হয়।\nপ্রতিটি পণ্যে artisan-এর নিজস্ব সৃজনশীলতা ফুটে ওঠে।','Chittagong','anikalutfunnahar@gmail.com','01758090080',0,NULL,0,'Anisha Shop House',_binary '',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(9,'Habib Al Fahad','Gazipur','4729904847','REJECTED','2026-05-19',NULL,'## Artisan Story — ROAM Featured Artisan\n\n### “মাটির গন্ধ থেকে বিশ্ববাজারে”\n\nনওগাঁর ছোট্ট এক গ্রামের মেয়ে রহিমা খাতুন। ছোটবেলা থেকেই মায়ের পাশে বসে হাতে নকশিকাঁথা সেলাই করতেন। সংসারের অভাব ছিল, কিন্তু হাতের শিল্পের প্রতি ভালোবাসা কখনো কমেনি। গ্রামের মানুষদের কাছে এটা ছিল শুধু “শখের কাজ”, কিন্তু রহিমার কাছে ছিল নিজের পরিচয় তৈরি করার স্বপ্ন।\n\nবিয়ের পর জীবনের দায়িত্ব আরও বেড়ে যায়। স্বামীর অল্প আয়ে সংসার চালানো কঠিন হয়ে পড়ে। তখন তিনি পুরোনো কাপড়, সুতা আর গ্রামের নারীদের নিয়ে আবার কাজ শুরু করেন। প্রথমদিকে স্থানীয় হাটে খুব কম দামে কাঁথা বিক্রি হতো। অনেক সময় দিন শেষে কোনো বিক্রিই হতো না।\n\nকিন্তু রহিমা হাল ছাড়েননি।\n\nধীরে ধীরে তিনি নতুন ডিজাইন শেখেন, গ্রামের আরও নারীদের প্রশিক্ষণ দেন এবং হাতে তৈরি প্রতিটি পণ্যে বাংলাদেশের গ্রামীণ সংস্কৃতির গল্প ফুটিয়ে তুলতে শুরু করেন। আজ তার তৈরি নকশিকাঁথা, জুট ব্যাগ ও হ্যান্ডক্রাফট পণ্য শুধু দেশের বিভিন্ন শহরেই নয়, অনলাইন প্ল্যাটফর্মের মাধ্যমে বিদেশেও পৌঁছাচ্ছে।\n\nROAM-এর মাধ্যমে রহিমা এখন সরাসরি ক্রেতাদের কাছে নিজের পণ্য বিক্রি করতে পারেন। মধ্যস্বত্বভোগী ছাড়াই তিনি ন্যায্য মূল্য পান, আর তার সঙ্গে কাজ করা ২৫ জন গ্রামের নারীও এখন স্বাবলম্বী।\n\nরহিমা বলেন:\n\n> “আমাদের হাতে তৈরি প্রতিটি জিনিসে শুধু সুতা না, আমাদের জীবনের গল্পও সেলাই করা থাকে।”\n\nএই গল্প শুধু একজন শিল্পীর নয় — এটি বাংলাদেশের হাজারো গ্রামীণ কারুশিল্পীর সংগ্রাম, স্বপ্ন আর আত্মমর্যাদার গল্প।\n',10,'অভিজ্ঞ কারুশিল্পীরা হাতে প্রতিটি উপকরণ প্রস্তুত করেন।\nএখানে machine production নয়, traditional handmade technique ব্যবহার করা হয়।','Gazipur','anikalutfunnahar@gmail.com','01758090080',0,'Invalid information',0,'Unique Fashion',_binary '\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(10,'Mousumi  Akter','Jhenaidah','5467526773','APPROVED','2026-05-19',NULL,'অভিজ্ঞ কারুশিল্পীরা হাতে প্রতিটি উপকরণ প্রস্তুত করেন।\nএখানে machine production নয়, traditional handmade technique ব্যবহার করা হয়।',10,'অভিজ্ঞ কারুশিল্পীরা হাতে প্রতিটি উপকরণ প্রস্তুত করেন।\nএখানে machine production নয়, traditional handmade technique ব্যবহার করা হয়।','Jhenaidah','anikalutfunnahar@gmail.com','01758090080',0,NULL,0,'Mousumi Online',_binary '',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(11,'Orna Rahman','Gazipur','3647726478','APPROVED','2026-05-21',NULL,'## Artisan Story — ROAM Featured Artisan\n\n### “মাটির গন্ধ থেকে বিশ্ববাজারে”\n\nনওগাঁর ছোট্ট এক গ্রামের মেয়ে রহিমা খাতুন। ছোটবেলা থেকেই মায়ের পাশে বসে হাতে নকশিকাঁথা সেলাই করতেন। সংসারের অভাব ছিল, কিন্তু হাতের শিল্পের প্রতি ভালোবাসা কখনো কমেনি। গ্রামের মানুষদের কাছে এটা ছিল শুধু “শখের কাজ”, কিন্তু রহিমার কাছে ছিল নিজের পরিচয় তৈরি করার স্বপ্ন।\n\nবিয়ের পর জীবনের দায়িত্ব আরও বেড়ে যায়। স্বামীর অল্প আয়ে সংসার চালানো কঠিন হয়ে পড়ে। তখন তিনি পুরোনো কাপড়, সুতা আর গ্রামের নারীদের নিয়ে আবার কাজ শুরু করেন। প্রথমদিকে স্থানীয় হাটে খুব কম দামে কাঁথা বিক্রি হতো। অনেক সময় দিন শেষে কোনো বিক্রিই হতো না।\n\nকিন্তু রহিমা হাল ছাড়েননি।\n\nধীরে ধীরে তিনি নতুন ডিজাইন শেখেন, গ্রামের আরও নারীদের প্রশিক্ষণ দেন এবং হাতে তৈরি প্রতিটি পণ্যে বাংলাদেশের গ্রামীণ সংস্কৃতির গল্প ফুটিয়ে তুলতে শুরু করেন। আজ তার তৈরি নকশিকাঁথা, জুট ব্যাগ ও হ্যান্ডক্রাফট পণ্য শুধু দেশের বিভিন্ন শহরেই নয়, অনলাইন প্ল্যাটফর্মের মাধ্যমে বিদেশেও পৌঁছাচ্ছে।\n\nROAM-এর মাধ্যমে রহিমা এখন সরাসরি ক্রেতাদের কাছে নিজের পণ্য বিক্রি করতে পারেন। মধ্যস্বত্বভোগী ছাড়াই তিনি ন্যায্য মূল্য পান, আর তার সঙ্গে কাজ করা ২৫ জন গ্রামের নারীও এখন স্বাবলম্বী।\n\nরহিমা বলেন:\n\n> “আমাদের হাতে তৈরি প্রতিটি জিনিসে শুধু সুতা না, আমাদের জীবনের গল্পও সেলাই করা থাকে।”\n\nএই গল্প শুধু একজন শিল্পীর নয় — এটি বাংলাদেশের হাজারো গ্রামীণ কারুশিল্পীর সংগ্রাম, স্বপ্ন আর আত্মমর্যাদার গল্প।\n',10,'অভিজ্ঞ কারুশিল্পীরা হাতে প্রতিটি উপকরণ প্রস্তুত করেন।\nএখানে machine production নয়, traditional handmade technique ব্যবহার করা হয়।','Gazipur','anikalutfunnahar@gmail.com','01758090080',0,NULL,0,'The Earth Beauty',_binary '',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(12,' Jui Nahar ','Mymensingh','1299879933','APPROVED','2026-05-24',NULL,'## Artisan Story — ROAM Featured Artisan\n\n### “মাটির গন্ধ থেকে বিশ্ববাজারে”\n\nনওগাঁর ছোট্ট এক গ্রামের মেয়ে রহিমা খাতুন। ছোটবেলা থেকেই মায়ের পাশে বসে হাতে নকশিকাঁথা সেলাই করতেন। সংসারের অভাব ছিল, কিন্তু হাতের শিল্পের প্রতি ভালোবাসা কখনো কমেনি। গ্রামের মানুষদের কাছে এটা ছিল শুধু “শখের কাজ”, কিন্তু রহিমার কাছে ছিল নিজের পরিচয় তৈরি করার স্বপ্ন।\n\nবিয়ের পর জীবনের দায়িত্ব আরও বেড়ে যায়। স্বামীর অল্প আয়ে সংসার চালানো কঠিন হয়ে পড়ে। তখন তিনি পুরোনো কাপড়, সুতা আর গ্রামের নারীদের নিয়ে আবার কাজ শুরু করেন। প্রথমদিকে স্থানীয় হাটে খুব কম দামে কাঁথা বিক্রি হতো। অনেক সময় দিন শেষে কোনো বিক্রিই হতো না।\n\nকিন্তু রহিমা হাল ছাড়েননি।\n\nধীরে ধীরে তিনি নতুন ডিজাইন শেখেন, গ্রামের আরও নারীদের প্রশিক্ষণ দেন এবং হাতে তৈরি প্রতিটি পণ্যে বাংলাদেশের গ্রামীণ সংস্কৃতির গল্প ফুটিয়ে তুলতে শুরু করেন। আজ তার তৈরি নকশিকাঁথা, জুট ব্যাগ ও হ্যান্ডক্রাফট পণ্য শুধু দেশের বিভিন্ন শহরেই নয়, অনলাইন প্ল্যাটফর্মের মাধ্যমে বিদেশেও পৌঁছাচ্ছে।\n\nROAM-এর মাধ্যমে রহিমা এখন সরাসরি ক্রেতাদের কাছে নিজের পণ্য বিক্রি করতে পারেন। মধ্যস্বত্বভোগী ছাড়াই তিনি ন্যায্য মূল্য পান, আর তার সঙ্গে কাজ করা ২৫ জন গ্রামের নারীও এখন স্বাবলম্বী।\n\nরহিমা বলেন:\n\n> “আমাদের হাতে তৈরি প্রতিটি জিনিসে শুধু সুতা না, আমাদের জীবনের গল্পও সেলাই করা থাকে।”\n\nএই গল্প শুধু একজন শিল্পীর নয় — এটি বাংলাদেশের হাজারো গ্রামীণ কারুশিল্পীর সংগ্রাম, স্বপ্ন আর আত্মমর্যাদার গল্প।\n',10,'গ্রামীণ সংস্কৃতি, লোকজ নকশা ও আধুনিক ডিজাইনের সমন্বয়ে পণ্য তৈরি করা হয়।\nপ্রতিটি পণ্যে artisan-এর নিজস্ব সৃজনশীলতা ফুটে ওঠে।','Mymensingh','lutfunnahar@gmail.com','01722355578',0,NULL,0,'Unique Fashion',_binary '',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(13,'Noyan Ahamed','asdgsgsda','2155154855','APPROVED','2026-06-05',NULL,'sadgsgasgasdghjfdjdjfdgjfgjgfjfgjfdgj',10,'asdgasgsagasgsadfgdgjdfjfdjfdgjfdgjfgjfgjfgj','Dhaka','noyanahamed@gmail.com','01526398754',0,NULL,0,'NoyonTara',_binary '',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(14,'Kamal Hossain','Mymensingh','23659677625','APPROVED','2026-06-07',NULL,'hhfhrhgthjnidhyesvgf drmh bym',10,'hhfhrhgthjnidhyesvgf drmh bym','Mymensingh','kamal@gmail.com','01722355578',0,NULL,0,'BestBy',_binary '',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(15,'Anni Islam','Dhaka','7354698087','APPROVED','2026-06-07',NULL,'vnsdjbgdhjbifhjhhyu,jk.lk/l;nvm',10,'vnsdjbgdhjbifhjhhyu,jk.lk/l;nvm','Dhaka','anni@gmail.com','01612356799',0,NULL,0,'Unique House',_binary '',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(16,'Kamal Hossian','Dhaka','6345654765','APPROVED','2026-06-07',NULL,'fnhrghtuhgtrhurthhtyjnh,kj,',10,'fnhrghtuhgtrhurthhtyjnh,kj,','Dhaka','kamal@gmail.com','01758090090',0,NULL,0,'Matir Mayna',_binary '',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(17,'Jamal  Ahamed','Dhaka','2758689579','APPROVED','2026-06-07',NULL,'### “মাটির গন্ধ থেকে বিশ্ববাজারে”\n\nনওগাঁর ছোট্ট এক গ্রামের মেয়ে রহিমা খাতুন। ছোটবেলা থেকেই মায়ের পাশে বসে হাতে নকশিকাঁথা সেলাই করতেন। সংসারের অভাব ছিল, কিন্তু হাতের শিল্পের প্রতি ভালোবাসা কখনো কমেনি। গ্রামের মানুষদের কাছে এটা ছিল শুধু “শখের কাজ”, কিন্তু রহিমার কাছে ছিল নিজের পরিচয় তৈরি করার স্বপ্ন।\n\nবিয়ের পর জীবনের দায়িত্ব আরও বেড়ে যায়। স্বামীর অল্প আয়ে সংসার চালানো কঠিন হয়ে পড়ে। তখন তিনি পুরোনো কাপড়, সুতা আর গ্রামের নারীদের নিয়ে আবার কাজ শুরু করেন। প্রথমদিকে স্থানীয় হাটে খুব কম দামে কাঁথা বিক্রি হতো। অনেক সময় দিন শেষে কোনো বিক্রিই হতো না।\n\nকিন্তু রহিমা হাল ছাড়েননি।\n\nধীরে ধীরে তিনি নতুন ডিজাইন শেখেন, গ্রামের আরও নারীদের প্রশিক্ষণ দেন এবং হাতে তৈরি প্রতিটি পণ্যে বাংলাদেশের গ্রামীণ সংস্কৃতির গল্প ফুটিয়ে তুলতে শুরু করেন। আজ তার তৈরি নকশিকাঁথা, জুট ব্যাগ ও হ্যান্ডক্রাফট পণ্য শুধু দেশের বিভিন্ন শহরেই নয়, অনলাইন প্ল্যাটফর্মের মাধ্যমে বিদেশেও পৌঁছাচ্ছে।\n\nROAM-এর মাধ্যমে রহিমা এখন সরাসরি ক্রেতাদের কাছে নিজের পণ্য বিক্রি করতে পারেন। মধ্যস্বত্বভোগী ছাড়াই তিনি ন্যায্য মূল্য পান, আর তার সঙ্গে কাজ করা ২৫ জন গ্রামের নারীও এখন স্বাবলম্বী।\n\nরহিমা বলেন:\n\n> “আমাদের হাতে তৈরি প্রতিটি জিনিসে শুধু সুতা না, আমাদের জীবনের গল্পও সেলাই করা থাকে।”',10,'অভিজ্ঞ কারুশিল্পীরা হাতে প্রতিটি উপকরণ প্রস্তুত করেন।\nএখানে machine production নয়, traditional handmade technique ব্যবহার করা হয়।','Dhaka','jamal@gmail.com','01525252525',0,NULL,0,'Desi Creation',_binary '',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(18,'Jamal Islam','Dhaka','5748538968','APPROVED','2026-06-07',NULL,'### “মাটির গন্ধ থেকে বিশ্ববাজারে”\n\nনওগাঁর ছোট্ট এক গ্রামের মেয়ে রহিমা খাতুন। ছোটবেলা থেকেই মায়ের পাশে বসে হাতে নকশিকাঁথা সেলাই করতেন। সংসারের অভাব ছিল, কিন্তু হাতের শিল্পের প্রতি ভালোবাসা কখনো কমেনি। গ্রামের মানুষদের কাছে এটা ছিল শুধু “শখের কাজ”, কিন্তু রহিমার কাছে ছিল নিজের পরিচয় তৈরি করার স্বপ্ন।\n\nবিয়ের পর জীবনের দায়িত্ব আরও বেড়ে যায়। স্বামীর অল্প আয়ে সংসার চালানো কঠিন হয়ে পড়ে। তখন তিনি পুরোনো কাপড়, সুতা আর গ্রামের নারীদের নিয়ে আবার কাজ শুরু করেন। প্রথমদিকে স্থানীয় হাটে খুব কম দামে কাঁথা বিক্রি হতো। অনেক সময় দিন শেষে কোনো বিক্রিই হতো না।\n\nকিন্তু রহিমা হাল ছাড়েননি।\n\nধীরে ধীরে তিনি নতুন ডিজাইন শেখেন, গ্রামের আরও নারীদের প্রশিক্ষণ দেন এবং হাতে তৈরি প্রতিটি পণ্যে বাংলাদেশের গ্রামীণ সংস্কৃতির গল্প ফুটিয়ে তুলতে শুরু করেন। আজ তার তৈরি নকশিকাঁথা, জুট ব্যাগ ও হ্যান্ডক্রাফট পণ্য শুধু দেশের বিভিন্ন শহরেই নয়, অনলাইন প্ল্যাটফর্মের মাধ্যমে বিদেশেও পৌঁছাচ্ছে।\n\nROAM-এর মাধ্যমে রহিমা এখন সরাসরি ক্রেতাদের কাছে নিজের পণ্য বিক্রি করতে পারেন। মধ্যস্বত্বভোগী ছাড়াই তিনি ন্যায্য মূল্য পান, আর তার সঙ্গে কাজ করা ২৫ জন গ্রামের নারীও এখন স্বাবলম্বী।\n\nরহিমা বলেন:\n\n> “আমাদের হাতে তৈরি প্রতিটি জিনিসে শুধু সুতা না, আমাদের জীবনের গল্পও সেলাই করা থাকে।”',10,'### “মাটির গন্ধ থেকে বিশ্ববাজারে”\n\nনওগাঁর ছোট্ট এক গ্রামের মেয়ে রহিমা খাতুন। ছোটবেলা থেকেই মায়ের পাশে বসে হাতে নকশিকাঁথা সেলাই করতেন। সংসারের অভাব ছিল, কিন্তু হাতের শিল্পের প্রতি ভালোবাসা কখনো কমেনি। গ্রামের মানুষদের কাছে এটা ছিল শুধু “শখের কাজ”, কিন্তু রহিমার কাছে ছিল নিজের পরিচয় তৈরি করার স্বপ্ন।\n\nবিয়ের পর জীবনের দায়িত্ব আরও বেড়ে যায়। স্বামীর অল্প আয়ে সংসার চালানো কঠিন হয়ে পড়ে। তখন তিনি পুরোনো কাপড়, সুতা আর গ্রামের নারীদের নিয়ে আবার কাজ শুরু করেন। প্রথমদিকে স্থানীয় হাটে খুব কম দামে কাঁথা বিক্রি হতো। অনেক সময় দিন শেষে কোনো বিক্রিই হতো না।\n\nকিন্তু রহিমা হাল ছাড়েননি।\n\nধীরে ধীরে তিনি নতুন ডিজাইন শেখেন, গ্রামের আরও নারীদের প্রশিক্ষণ দেন এবং হাতে তৈরি প্রতিটি পণ্যে বাংলাদেশের গ্রামীণ সংস্কৃতির গল্প ফুটিয়ে তুলতে শুরু করেন। আজ তার তৈরি নকশিকাঁথা, জুট ব্যাগ ও হ্যান্ডক্রাফট পণ্য শুধু দেশের বিভিন্ন শহরেই নয়, অনলাইন প্ল্যাটফর্মের মাধ্যমে বিদেশেও পৌঁছাচ্ছে।\n\nROAM-এর মাধ্যমে রহিমা এখন সরাসরি ক্রেতাদের কাছে নিজের পণ্য বিক্রি করতে পারেন। মধ্যস্বত্বভোগী ছাড়াই তিনি ন্যায্য মূল্য পান, আর তার সঙ্গে কাজ করা ২৫ জন গ্রামের নারীও এখন স্বাবলম্বী।\n\nরহিমা বলেন:\n\n> “আমাদের হাতে তৈরি প্রতিটি জিনিসে শুধু সুতা না, আমাদের জীবনের গল্পও সেলাই করা থাকে।”','Dhaka','jamal@gmail.com','01525252525',0,NULL,0,'MyShop',_binary '',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(19,'Salam Ali','Dhaka','4253465768','APPROVED','2026-06-07',NULL,'### “মাটির গন্ধ থেকে বিশ্ববাজারে”\n\nনওগাঁর ছোট্ট এক গ্রামের মেয়ে রহিমা খাতুন। ছোটবেলা থেকেই মায়ের পাশে বসে হাতে নকশিকাঁথা সেলাই করতেন। সংসারের অভাব ছিল, কিন্তু হাতের শিল্পের প্রতি ভালোবাসা কখনো কমেনি। গ্রামের মানুষদের কাছে এটা ছিল শুধু “শখের কাজ”, কিন্তু রহিমার কাছে ছিল নিজের পরিচয় তৈরি করার স্বপ্ন।\n\nবিয়ের পর জীবনের দায়িত্ব আরও বেড়ে যায়। স্বামীর অল্প আয়ে সংসার চালানো কঠিন হয়ে পড়ে। তখন তিনি পুরোনো কাপড়, সুতা আর গ্রামের নারীদের নিয়ে আবার কাজ শুরু করেন। প্রথমদিকে স্থানীয় হাটে খুব কম দামে কাঁথা বিক্রি হতো। অনেক সময় দিন শেষে কোনো বিক্রিই হতো না।\n\nকিন্তু রহিমা হাল ছাড়েননি।\n\nধীরে ধীরে তিনি নতুন ডিজাইন শেখেন, গ্রামের আরও নারীদের প্রশিক্ষণ দেন এবং হাতে তৈরি প্রতিটি পণ্যে বাংলাদেশের গ্রামীণ সংস্কৃতির গল্প ফুটিয়ে তুলতে শুরু করেন। আজ তার তৈরি নকশিকাঁথা, জুট ব্যাগ ও হ্যান্ডক্রাফট পণ্য শুধু দেশের বিভিন্ন শহরেই নয়, অনলাইন প্ল্যাটফর্মের মাধ্যমে বিদেশেও পৌঁছাচ্ছে।\n\nROAM-এর মাধ্যমে রহিমা এখন সরাসরি ক্রেতাদের কাছে নিজের পণ্য বিক্রি করতে পারেন। মধ্যস্বত্বভোগী ছাড়াই তিনি ন্যায্য মূল্য পান, আর তার সঙ্গে কাজ করা ২৫ জন গ্রামের নারীও এখন স্বাবলম্বী।\n\nরহিমা বলেন:\n\n> “আমাদের হাতে তৈরি প্রতিটি জিনিসে শুধু সুতা না, আমাদের জীবনের গল্পও সেলাই করা থাকে।”',10,'### “মাটির গন্ধ থেকে বিশ্ববাজারে”\n\nনওগাঁর ছোট্ট এক গ্রামের মেয়ে রহিমা খাতুন। ছোটবেলা থেকেই মায়ের পাশে বসে হাতে নকশিকাঁথা সেলাই করতেন। সংসারের অভাব ছিল, কিন্তু হাতের শিল্পের প্রতি ভালোবাসা কখনো কমেনি। গ্রামের মানুষদের কাছে এটা ছিল শুধু “শখের কাজ”, কিন্তু রহিমার কাছে ছিল নিজের পরিচয় তৈরি করার স্বপ্ন।\n\nবিয়ের পর জীবনের দায়িত্ব আরও বেড়ে যায়। স্বামীর অল্প আয়ে সংসার চালানো কঠিন হয়ে পড়ে। তখন তিনি পুরোনো কাপড়, সুতা আর গ্রামের নারীদের নিয়ে আবার কাজ শুরু করেন। প্রথমদিকে স্থানীয় হাটে খুব কম দামে কাঁথা বিক্রি হতো। অনেক সময় দিন শেষে কোনো বিক্রিই হতো না।\n\nকিন্তু রহিমা হাল ছাড়েননি।\n\nধীরে ধীরে তিনি নতুন ডিজাইন শেখেন, গ্রামের আরও নারীদের প্রশিক্ষণ দেন এবং হাতে তৈরি প্রতিটি পণ্যে বাংলাদেশের গ্রামীণ সংস্কৃতির গল্প ফুটিয়ে তুলতে শুরু করেন। আজ তার তৈরি নকশিকাঁথা, জুট ব্যাগ ও হ্যান্ডক্রাফট পণ্য শুধু দেশের বিভিন্ন শহরেই নয়, অনলাইন প্ল্যাটফর্মের মাধ্যমে বিদেশেও পৌঁছাচ্ছে।\n\nROAM-এর মাধ্যমে রহিমা এখন সরাসরি ক্রেতাদের কাছে নিজের পণ্য বিক্রি করতে পারেন। মধ্যস্বত্বভোগী ছাড়াই তিনি ন্যায্য মূল্য পান, আর তার সঙ্গে কাজ করা ২৫ জন গ্রামের নারীও এখন স্বাবলম্বী।\n\nরহিমা বলেন:\n\n> “আমাদের হাতে তৈরি প্রতিটি জিনিসে শুধু সুতা না, আমাদের জীবনের গল্পও সেলাই করা থাকে।”','Dhaka','salam@gmail.com','01367486582',0,NULL,0,'BestBD',_binary '',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(20,'Jamal Hossain','Dhaka','1324354756','APPROVED','2026-06-07',NULL,'### “মাটির গন্ধ থেকে বিশ্ববাজারে”\n\nনওগাঁর ছোট্ট এক গ্রামের মেয়ে রহিমা খাতুন। ছোটবেলা থেকেই মায়ের পাশে বসে হাতে নকশিকাঁথা সেলাই করতেন। সংসারের অভাব ছিল, কিন্তু হাতের শিল্পের প্রতি ভালোবাসা কখনো কমেনি। গ্রামের মানুষদের কাছে এটা ছিল শুধু “শখের কাজ”, কিন্তু রহিমার কাছে ছিল নিজের পরিচয় তৈরি করার স্বপ্ন।\n\nবিয়ের পর জীবনের দায়িত্ব আরও বেড়ে যায়। স্বামীর অল্প আয়ে সংসার চালানো কঠিন হয়ে পড়ে। তখন তিনি পুরোনো কাপড়, সুতা আর গ্রামের নারীদের নিয়ে আবার কাজ শুরু করেন। প্রথমদিকে স্থানীয় হাটে খুব কম দামে কাঁথা বিক্রি হতো। অনেক সময় দিন শেষে কোনো বিক্রিই হতো না।\n\nকিন্তু রহিমা হাল ছাড়েননি।\n\nধীরে ধীরে তিনি নতুন ডিজাইন শেখেন, গ্রামের আরও নারীদের প্রশিক্ষণ দেন এবং হাতে তৈরি প্রতিটি পণ্যে বাংলাদেশের গ্রামীণ সংস্কৃতির গল্প ফুটিয়ে তুলতে শুরু করেন। আজ তার তৈরি নকশিকাঁথা, জুট ব্যাগ ও হ্যান্ডক্রাফট পণ্য শুধু দেশের বিভিন্ন শহরেই নয়, অনলাইন প্ল্যাটফর্মের মাধ্যমে বিদেশেও পৌঁছাচ্ছে।\n\nROAM-এর মাধ্যমে রহিমা এখন সরাসরি ক্রেতাদের কাছে নিজের পণ্য বিক্রি করতে পারেন। মধ্যস্বত্বভোগী ছাড়াই তিনি ন্যায্য মূল্য পান, আর তার সঙ্গে কাজ করা ২৫ জন গ্রামের নারীও এখন স্বাবলম্বী।\n\nরহিমা বলেন:\n\n> “আমাদের হাতে তৈরি প্রতিটি জিনিসে শুধু সুতা না, আমাদের জীবনের গল্পও সেলাই করা থাকে।”',10,'### “মাটির গন্ধ থেকে বিশ্ববাজারে”\n\nনওগাঁর ছোট্ট এক গ্রামের মেয়ে রহিমা খাতুন। ছোটবেলা থেকেই মায়ের পাশে বসে হাতে নকশিকাঁথা সেলাই করতেন। সংসারের অভাব ছিল, কিন্তু হাতের শিল্পের প্রতি ভালোবাসা কখনো কমেনি। গ্রামের মানুষদের কাছে এটা ছিল শুধু “শখের কাজ”, কিন্তু রহিমার কাছে ছিল নিজের পরিচয় তৈরি করার স্বপ্ন।\n\nবিয়ের পর জীবনের দায়িত্ব আরও বেড়ে যায়। স্বামীর অল্প আয়ে সংসার চালানো কঠিন হয়ে পড়ে। তখন তিনি পুরোনো কাপড়, সুতা আর গ্রামের নারীদের নিয়ে আবার কাজ শুরু করেন। প্রথমদিকে স্থানীয় হাটে খুব কম দামে কাঁথা বিক্রি হতো। অনেক সময় দিন শেষে কোনো বিক্রিই হতো না।\n\nকিন্তু রহিমা হাল ছাড়েননি।\n\nধীরে ধীরে তিনি নতুন ডিজাইন শেখেন, গ্রামের আরও নারীদের প্রশিক্ষণ দেন এবং হাতে তৈরি প্রতিটি পণ্যে বাংলাদেশের গ্রামীণ সংস্কৃতির গল্প ফুটিয়ে তুলতে শুরু করেন। আজ তার তৈরি নকশিকাঁথা, জুট ব্যাগ ও হ্যান্ডক্রাফট পণ্য শুধু দেশের বিভিন্ন শহরেই নয়, অনলাইন প্ল্যাটফর্মের মাধ্যমে বিদেশেও পৌঁছাচ্ছে।\n\nROAM-এর মাধ্যমে রহিমা এখন সরাসরি ক্রেতাদের কাছে নিজের পণ্য বিক্রি করতে পারেন। মধ্যস্বত্বভোগী ছাড়াই তিনি ন্যায্য মূল্য পান, আর তার সঙ্গে কাজ করা ২৫ জন গ্রামের নারীও এখন স্বাবলম্বী।\n\nরহিমা বলেন:\n\n> “আমাদের হাতে তৈরি প্রতিটি জিনিসে শুধু সুতা না, আমাদের জীবনের গল্পও সেলাই করা থাকে।”','Dhaka','jamal123@gmail.com','01758090060',0,NULL,0,'MyBD',_binary '',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(21,'Akash Khan','Dhaka','1278458768','APPROVED','2026-06-07',NULL,'### “মাটির গন্ধ থেকে বিশ্ববাজারে”\n\nনওগাঁর ছোট্ট এক গ্রামের মেয়ে রহিমা খাতুন। ছোটবেলা থেকেই মায়ের পাশে বসে হাতে নকশিকাঁথা সেলাই করতেন। সংসারের অভাব ছিল, কিন্তু হাতের শিল্পের প্রতি ভালোবাসা কখনো কমেনি। গ্রামের মানুষদের কাছে এটা ছিল শুধু “শখের কাজ”, কিন্তু রহিমার কাছে ছিল নিজের পরিচয় তৈরি করার স্বপ্ন।\n\nবিয়ের পর জীবনের দায়িত্ব আরও বেড়ে যায়। স্বামীর অল্প আয়ে সংসার চালানো কঠিন হয়ে পড়ে। তখন তিনি পুরোনো কাপড়, সুতা আর গ্রামের নারীদের নিয়ে আবার কাজ শুরু করেন। প্রথমদিকে স্থানীয় হাটে খুব কম দামে কাঁথা বিক্রি হতো। অনেক সময় দিন শেষে কোনো বিক্রিই হতো না।\n\nকিন্তু রহিমা হাল ছাড়েননি।\n\nধীরে ধীরে তিনি নতুন ডিজাইন শেখেন, গ্রামের আরও নারীদের প্রশিক্ষণ দেন এবং হাতে তৈরি প্রতিটি পণ্যে বাংলাদেশের গ্রামীণ সংস্কৃতির গল্প ফুটিয়ে তুলতে শুরু করেন। আজ তার তৈরি নকশিকাঁথা, জুট ব্যাগ ও হ্যান্ডক্রাফট পণ্য শুধু দেশের বিভিন্ন শহরেই নয়, অনলাইন প্ল্যাটফর্মের মাধ্যমে বিদেশেও পৌঁছাচ্ছে।\n\nROAM-এর মাধ্যমে রহিমা এখন সরাসরি ক্রেতাদের কাছে নিজের পণ্য বিক্রি করতে পারেন। মধ্যস্বত্বভোগী ছাড়াই তিনি ন্যায্য মূল্য পান, আর তার সঙ্গে কাজ করা ২৫ জন গ্রামের নারীও এখন স্বাবলম্বী।\n\nরহিমা বলেন:\n\n> “আমাদের হাতে তৈরি প্রতিটি জিনিসে শুধু সুতা না, আমাদের জীবনের গল্পও সেলাই করা থাকে।”',10,'### “মাটির গন্ধ থেকে বিশ্ববাজারে”\n\nনওগাঁর ছোট্ট এক গ্রামের মেয়ে রহিমা খাতুন। ছোটবেলা থেকেই মায়ের পাশে বসে হাতে নকশিকাঁথা সেলাই করতেন। সংসারের অভাব ছিল, কিন্তু হাতের শিল্পের প্রতি ভালোবাসা কখনো কমেনি। গ্রামের মানুষদের কাছে এটা ছিল শুধু “শখের কাজ”, কিন্তু রহিমার কাছে ছিল নিজের পরিচয় তৈরি করার স্বপ্ন।\n\nবিয়ের পর জীবনের দায়িত্ব আরও বেড়ে যায়। স্বামীর অল্প আয়ে সংসার চালানো কঠিন হয়ে পড়ে। তখন তিনি পুরোনো কাপড়, সুতা আর গ্রামের নারীদের নিয়ে আবার কাজ শুরু করেন। প্রথমদিকে স্থানীয় হাটে খুব কম দামে কাঁথা বিক্রি হতো। অনেক সময় দিন শেষে কোনো বিক্রিই হতো না।\n\nকিন্তু রহিমা হাল ছাড়েননি।\n\nধীরে ধীরে তিনি নতুন ডিজাইন শেখেন, গ্রামের আরও নারীদের প্রশিক্ষণ দেন এবং হাতে তৈরি প্রতিটি পণ্যে বাংলাদেশের গ্রামীণ সংস্কৃতির গল্প ফুটিয়ে তুলতে শুরু করেন। আজ তার তৈরি নকশিকাঁথা, জুট ব্যাগ ও হ্যান্ডক্রাফট পণ্য শুধু দেশের বিভিন্ন শহরেই নয়, অনলাইন প্ল্যাটফর্মের মাধ্যমে বিদেশেও পৌঁছাচ্ছে।\n\nROAM-এর মাধ্যমে রহিমা এখন সরাসরি ক্রেতাদের কাছে নিজের পণ্য বিক্রি করতে পারেন। মধ্যস্বত্বভোগী ছাড়াই তিনি ন্যায্য মূল্য পান, আর তার সঙ্গে কাজ করা ২৫ জন গ্রামের নারীও এখন স্বাবলম্বী।\n\nরহিমা বলেন:\n\n> “আমাদের হাতে তৈরি প্রতিটি জিনিসে শুধু সুতা না, আমাদের জীবনের গল্পও সেলাই করা থাকে।”','Dhaka','akash@test.com','01758090089',0,NULL,0,'HerChoice',_binary '',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(22,'Taposh Khan','Dhaka','1286485467','APPROVED','2026-06-07',NULL,'### “মাটির গন্ধ থেকে বিশ্ববাজারে”\n\nনওগাঁর ছোট্ট এক গ্রামের মেয়ে রহিমা খাতুন। ছোটবেলা থেকেই মায়ের পাশে বসে হাতে নকশিকাঁথা সেলাই করতেন। সংসারের অভাব ছিল, কিন্তু হাতের শিল্পের প্রতি ভালোবাসা কখনো কমেনি। গ্রামের মানুষদের কাছে এটা ছিল শুধু “শখের কাজ”, কিন্তু রহিমার কাছে ছিল নিজের পরিচয় তৈরি করার স্বপ্ন।\n\nবিয়ের পর জীবনের দায়িত্ব আরও বেড়ে যায়। স্বামীর অল্প আয়ে সংসার চালানো কঠিন হয়ে পড়ে। তখন তিনি পুরোনো কাপড়, সুতা আর গ্রামের নারীদের নিয়ে আবার কাজ শুরু করেন। প্রথমদিকে স্থানীয় হাটে খুব কম দামে কাঁথা বিক্রি হতো। অনেক সময় দিন শেষে কোনো বিক্রিই হতো না।\n\nকিন্তু রহিমা হাল ছাড়েননি।\n\nধীরে ধীরে তিনি নতুন ডিজাইন শেখেন, গ্রামের আরও নারীদের প্রশিক্ষণ দেন এবং হাতে তৈরি প্রতিটি পণ্যে বাংলাদেশের গ্রামীণ সংস্কৃতির গল্প ফুটিয়ে তুলতে শুরু করেন। আজ তার তৈরি নকশিকাঁথা, জুট ব্যাগ ও হ্যান্ডক্রাফট পণ্য শুধু দেশের বিভিন্ন শহরেই নয়, অনলাইন প্ল্যাটফর্মের মাধ্যমে বিদেশেও পৌঁছাচ্ছে।\n\nROAM-এর মাধ্যমে রহিমা এখন সরাসরি ক্রেতাদের কাছে নিজের পণ্য বিক্রি করতে পারেন। মধ্যস্বত্বভোগী ছাড়াই তিনি ন্যায্য মূল্য পান, আর তার সঙ্গে কাজ করা ২৫ জন গ্রামের নারীও এখন স্বাবলম্বী।\n\nরহিমা বলেন:\n\n> “আমাদের হাতে তৈরি প্রতিটি জিনিসে শুধু সুতা না, আমাদের জীবনের গল্পও সেলাই করা থাকে।”',10,'### “মাটির গন্ধ থেকে বিশ্ববাজারে”\n\nনওগাঁর ছোট্ট এক গ্রামের মেয়ে রহিমা খাতুন। ছোটবেলা থেকেই মায়ের পাশে বসে হাতে নকশিকাঁথা সেলাই করতেন। সংসারের অভাব ছিল, কিন্তু হাতের শিল্পের প্রতি ভালোবাসা কখনো কমেনি। গ্রামের মানুষদের কাছে এটা ছিল শুধু “শখের কাজ”, কিন্তু রহিমার কাছে ছিল নিজের পরিচয় তৈরি করার স্বপ্ন।\n\nবিয়ের পর জীবনের দায়িত্ব আরও বেড়ে যায়। স্বামীর অল্প আয়ে সংসার চালানো কঠিন হয়ে পড়ে। তখন তিনি পুরোনো কাপড়, সুতা আর গ্রামের নারীদের নিয়ে আবার কাজ শুরু করেন। প্রথমদিকে স্থানীয় হাটে খুব কম দামে কাঁথা বিক্রি হতো। অনেক সময় দিন শেষে কোনো বিক্রিই হতো না।\n\nকিন্তু রহিমা হাল ছাড়েননি।\n\nধীরে ধীরে তিনি নতুন ডিজাইন শেখেন, গ্রামের আরও নারীদের প্রশিক্ষণ দেন এবং হাতে তৈরি প্রতিটি পণ্যে বাংলাদেশের গ্রামীণ সংস্কৃতির গল্প ফুটিয়ে তুলতে শুরু করেন। আজ তার তৈরি নকশিকাঁথা, জুট ব্যাগ ও হ্যান্ডক্রাফট পণ্য শুধু দেশের বিভিন্ন শহরেই নয়, অনলাইন প্ল্যাটফর্মের মাধ্যমে বিদেশেও পৌঁছাচ্ছে।\n\nROAM-এর মাধ্যমে রহিমা এখন সরাসরি ক্রেতাদের কাছে নিজের পণ্য বিক্রি করতে পারেন। মধ্যস্বত্বভোগী ছাড়াই তিনি ন্যায্য মূল্য পান, আর তার সঙ্গে কাজ করা ২৫ জন গ্রামের নারীও এখন স্বাবলম্বী।\n\nরহিমা বলেন:\n\n> “আমাদের হাতে তৈরি প্রতিটি জিনিসে শুধু সুতা না, আমাদের জীবনের গল্পও সেলাই করা থাকে।”','Dhaka','taposh@test.com','01758090070',0,NULL,0,'MyShop',_binary '',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(23,'Ekra Khan','DHaka','4679025367','APPROVED','2026-06-07',NULL,'### “মাটির গন্ধ থেকে বিশ্ববাজারে”\n\nনওগাঁর ছোট্ট এক গ্রামের মেয়ে রহিমা খাতুন। ছোটবেলা থেকেই মায়ের পাশে বসে হাতে নকশিকাঁথা সেলাই করতেন। সংসারের অভাব ছিল, কিন্তু হাতের শিল্পের প্রতি ভালোবাসা কখনো কমেনি। গ্রামের মানুষদের কাছে এটা ছিল শুধু “শখের কাজ”, কিন্তু রহিমার কাছে ছিল নিজের পরিচয় তৈরি করার স্বপ্ন।\n\nবিয়ের পর জীবনের দায়িত্ব আরও বেড়ে যায়। স্বামীর অল্প আয়ে সংসার চালানো কঠিন হয়ে পড়ে। তখন তিনি পুরোনো কাপড়, সুতা আর গ্রামের নারীদের নিয়ে আবার কাজ শুরু করেন। প্রথমদিকে স্থানীয় হাটে খুব কম দামে কাঁথা বিক্রি হতো। অনেক সময় দিন শেষে কোনো বিক্রিই হতো না।\n\nকিন্তু রহিমা হাল ছাড়েননি।\n\nধীরে ধীরে তিনি নতুন ডিজাইন শেখেন, গ্রামের আরও নারীদের প্রশিক্ষণ দেন এবং হাতে তৈরি প্রতিটি পণ্যে বাংলাদেশের গ্রামীণ সংস্কৃতির গল্প ফুটিয়ে তুলতে শুরু করেন। আজ তার তৈরি নকশিকাঁথা, জুট ব্যাগ ও হ্যান্ডক্রাফট পণ্য শুধু দেশের বিভিন্ন শহরেই নয়, অনলাইন প্ল্যাটফর্মের মাধ্যমে বিদেশেও পৌঁছাচ্ছে।\n\nROAM-এর মাধ্যমে রহিমা এখন সরাসরি ক্রেতাদের কাছে নিজের পণ্য বিক্রি করতে পারেন। মধ্যস্বত্বভোগী ছাড়াই তিনি ন্যায্য মূল্য পান, আর তার সঙ্গে কাজ করা ২৫ জন গ্রামের নারীও এখন স্বাবলম্বী।\n\nরহিমা বলেন:\n\n> “আমাদের হাতে তৈরি প্রতিটি জিনিসে শুধু সুতা না, আমাদের জীবনের গল্পও সেলাই করা থাকে।”',10,'### “মাটির গন্ধ থেকে বিশ্ববাজারে”\n\nনওগাঁর ছোট্ট এক গ্রামের মেয়ে রহিমা খাতুন। ছোটবেলা থেকেই মায়ের পাশে বসে হাতে নকশিকাঁথা সেলাই করতেন। সংসারের অভাব ছিল, কিন্তু হাতের শিল্পের প্রতি ভালোবাসা কখনো কমেনি। গ্রামের মানুষদের কাছে এটা ছিল শুধু “শখের কাজ”, কিন্তু রহিমার কাছে ছিল নিজের পরিচয় তৈরি করার স্বপ্ন।\n\nবিয়ের পর জীবনের দায়িত্ব আরও বেড়ে যায়। স্বামীর অল্প আয়ে সংসার চালানো কঠিন হয়ে পড়ে। তখন তিনি পুরোনো কাপড়, সুতা আর গ্রামের নারীদের নিয়ে আবার কাজ শুরু করেন। প্রথমদিকে স্থানীয় হাটে খুব কম দামে কাঁথা বিক্রি হতো। অনেক সময় দিন শেষে কোনো বিক্রিই হতো না।\n\nকিন্তু রহিমা হাল ছাড়েননি।\n\nধীরে ধীরে তিনি নতুন ডিজাইন শেখেন, গ্রামের আরও নারীদের প্রশিক্ষণ দেন এবং হাতে তৈরি প্রতিটি পণ্যে বাংলাদেশের গ্রামীণ সংস্কৃতির গল্প ফুটিয়ে তুলতে শুরু করেন। আজ তার তৈরি নকশিকাঁথা, জুট ব্যাগ ও হ্যান্ডক্রাফট পণ্য শুধু দেশের বিভিন্ন শহরেই নয়, অনলাইন প্ল্যাটফর্মের মাধ্যমে বিদেশেও পৌঁছাচ্ছে।\n\nROAM-এর মাধ্যমে রহিমা এখন সরাসরি ক্রেতাদের কাছে নিজের পণ্য বিক্রি করতে পারেন। মধ্যস্বত্বভোগী ছাড়াই তিনি ন্যায্য মূল্য পান, আর তার সঙ্গে কাজ করা ২৫ জন গ্রামের নারীও এখন স্বাবলম্বী।\n\nরহিমা বলেন:\n\n> “আমাদের হাতে তৈরি প্রতিটি জিনিসে শুধু সুতা না, আমাদের জীবনের গল্পও সেলাই করা থাকে।”','Dhaka','ekra@gmail.com','01745739902',0,NULL,0,'My Shop BD',_binary '',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(24,'Ekra Khan','Jamalpur','3575465797','APPROVED','2026-06-07',NULL,'### “মাটির গন্ধ থেকে বিশ্ববাজারে”\n\nনওগাঁর ছোট্ট এক গ্রামের মেয়ে রহিমা খাতুন। ছোটবেলা থেকেই মায়ের পাশে বসে হাতে নকশিকাঁথা সেলাই করতেন। সংসারের অভাব ছিল, কিন্তু হাতের শিল্পের প্রতি ভালোবাসা কখনো কমেনি। গ্রামের মানুষদের কাছে এটা ছিল শুধু “শখের কাজ”, কিন্তু রহিমার কাছে ছিল নিজের পরিচয় তৈরি করার স্বপ্ন।\n\nবিয়ের পর জীবনের দায়িত্ব আরও বেড়ে যায়। স্বামীর অল্প আয়ে সংসার চালানো কঠিন হয়ে পড়ে। তখন তিনি পুরোনো কাপড়, সুতা আর গ্রামের নারীদের নিয়ে আবার কাজ শুরু করেন। প্রথমদিকে স্থানীয় হাটে খুব কম দামে কাঁথা বিক্রি হতো। অনেক সময় দিন শেষে কোনো বিক্রিই হতো না।\n\nকিন্তু রহিমা হাল ছাড়েননি।\n\nধীরে ধীরে তিনি নতুন ডিজাইন শেখেন, গ্রামের আরও নারীদের প্রশিক্ষণ দেন এবং হাতে তৈরি প্রতিটি পণ্যে বাংলাদেশের গ্রামীণ সংস্কৃতির গল্প ফুটিয়ে তুলতে শুরু করেন। আজ তার তৈরি নকশিকাঁথা, জুট ব্যাগ ও হ্যান্ডক্রাফট পণ্য শুধু দেশের বিভিন্ন শহরেই নয়, অনলাইন প্ল্যাটফর্মের মাধ্যমে বিদেশেও পৌঁছাচ্ছে।\n\nROAM-এর মাধ্যমে রহিমা এখন সরাসরি ক্রেতাদের কাছে নিজের পণ্য বিক্রি করতে পারেন। মধ্যস্বত্বভোগী ছাড়াই তিনি ন্যায্য মূল্য পান, আর তার সঙ্গে কাজ করা ২৫ জন গ্রামের নারীও এখন স্বাবলম্বী।\n\nরহিমা বলেন:\n\n> “আমাদের হাতে তৈরি প্রতিটি জিনিসে শুধু সুতা না, আমাদের জীবনের গল্পও সেলাই করা থাকে।”',10,'### “মাটির গন্ধ থেকে বিশ্ববাজারে”\n\nনওগাঁর ছোট্ট এক গ্রামের মেয়ে রহিমা খাতুন। ছোটবেলা থেকেই মায়ের পাশে বসে হাতে নকশিকাঁথা সেলাই করতেন। সংসারের অভাব ছিল, কিন্তু হাতের শিল্পের প্রতি ভালোবাসা কখনো কমেনি। গ্রামের মানুষদের কাছে এটা ছিল শুধু “শখের কাজ”, কিন্তু রহিমার কাছে ছিল নিজের পরিচয় তৈরি করার স্বপ্ন।\n\nবিয়ের পর জীবনের দায়িত্ব আরও বেড়ে যায়। স্বামীর অল্প আয়ে সংসার চালানো কঠিন হয়ে পড়ে। তখন তিনি পুরোনো কাপড়, সুতা আর গ্রামের নারীদের নিয়ে আবার কাজ শুরু করেন। প্রথমদিকে স্থানীয় হাটে খুব কম দামে কাঁথা বিক্রি হতো। অনেক সময় দিন শেষে কোনো বিক্রিই হতো না।\n\nকিন্তু রহিমা হাল ছাড়েননি।\n\nধীরে ধীরে তিনি নতুন ডিজাইন শেখেন, গ্রামের আরও নারীদের প্রশিক্ষণ দেন এবং হাতে তৈরি প্রতিটি পণ্যে বাংলাদেশের গ্রামীণ সংস্কৃতির গল্প ফুটিয়ে তুলতে শুরু করেন। আজ তার তৈরি নকশিকাঁথা, জুট ব্যাগ ও হ্যান্ডক্রাফট পণ্য শুধু দেশের বিভিন্ন শহরেই নয়, অনলাইন প্ল্যাটফর্মের মাধ্যমে বিদেশেও পৌঁছাচ্ছে।\n\nROAM-এর মাধ্যমে রহিমা এখন সরাসরি ক্রেতাদের কাছে নিজের পণ্য বিক্রি করতে পারেন। মধ্যস্বত্বভোগী ছাড়াই তিনি ন্যায্য মূল্য পান, আর তার সঙ্গে কাজ করা ২৫ জন গ্রামের নারীও এখন স্বাবলম্বী।\n\nরহিমা বলেন:\n\n> “আমাদের হাতে তৈরি প্রতিটি জিনিসে শুধু সুতা না, আমাদের জীবনের গল্পও সেলাই করা থাকে।”','Jamalpur','ekra12345@gmail.com','01757057682',0,NULL,0,'Online Shop',_binary '',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(25,'Manik Mia','Faridpur','1543267345','APPROVED','2026-06-08',NULL,'### “মাটির গন্ধ থেকে বিশ্ববাজারে”\n\nনওগাঁর ছোট্ট এক গ্রামের মেয়ে রহিমা খাতুন। ছোটবেলা থেকেই মায়ের পাশে বসে হাতে নকশিকাঁথা সেলাই করতেন। সংসারের অভাব ছিল, কিন্তু হাতের শিল্পের প্রতি ভালোবাসা কখনো কমেনি। গ্রামের মানুষদের কাছে এটা ছিল শুধু “শখের কাজ”, কিন্তু রহিমার কাছে ছিল নিজের পরিচয় তৈরি করার স্বপ্ন।\n\nবিয়ের পর জীবনের দায়িত্ব আরও বেড়ে যায়। স্বামীর অল্প আয়ে সংসার চালানো কঠিন হয়ে পড়ে। তখন তিনি পুরোনো কাপড়, সুতা আর গ্রামের নারীদের নিয়ে আবার কাজ শুরু করেন। প্রথমদিকে স্থানীয় হাটে খুব কম দামে কাঁথা বিক্রি হতো। অনেক সময় দিন শেষে কোনো বিক্রিই হতো না।\n\nকিন্তু রহিমা হাল ছাড়েননি।\n\nধীরে ধীরে তিনি নতুন ডিজাইন শেখেন, গ্রামের আরও নারীদের প্রশিক্ষণ দেন এবং হাতে তৈরি প্রতিটি পণ্যে বাংলাদেশের গ্রামীণ সংস্কৃতির গল্প ফুটিয়ে তুলতে শুরু করেন। আজ তার তৈরি নকশিকাঁথা, জুট ব্যাগ ও হ্যান্ডক্রাফট পণ্য শুধু দেশের বিভিন্ন শহরেই নয়, অনলাইন প্ল্যাটফর্মের মাধ্যমে বিদেশেও পৌঁছাচ্ছে।\n\nROAM-এর মাধ্যমে রহিমা এখন সরাসরি ক্রেতাদের কাছে নিজের পণ্য বিক্রি করতে পারেন। মধ্যস্বত্বভোগী ছাড়াই তিনি ন্যায্য মূল্য পান, আর তার সঙ্গে কাজ করা ২৫ জন গ্রামের নারীও এখন স্বাবলম্বী।\n\nরহিমা বলেন:\n\n> “আমাদের হাতে তৈরি প্রতিটি জিনিসে শুধু সুতা না, আমাদের জীবনের গল্পও সেলাই করা থাকে।”',10,'### “মাটির গন্ধ থেকে বিশ্ববাজারে”\n\nনওগাঁর ছোট্ট এক গ্রামের মেয়ে রহিমা খাতুন। ছোটবেলা থেকেই মায়ের পাশে বসে হাতে নকশিকাঁথা সেলাই করতেন। সংসারের অভাব ছিল, কিন্তু হাতের শিল্পের প্রতি ভালোবাসা কখনো কমেনি। গ্রামের মানুষদের কাছে এটা ছিল শুধু “শখের কাজ”, কিন্তু রহিমার কাছে ছিল নিজের পরিচয় তৈরি করার স্বপ্ন।\n\nবিয়ের পর জীবনের দায়িত্ব আরও বেড়ে যায়। স্বামীর অল্প আয়ে সংসার চালানো কঠিন হয়ে পড়ে। তখন তিনি পুরোনো কাপড়, সুতা আর গ্রামের নারীদের নিয়ে আবার কাজ শুরু করেন। প্রথমদিকে স্থানীয় হাটে খুব কম দামে কাঁথা বিক্রি হতো। অনেক সময় দিন শেষে কোনো বিক্রিই হতো না।\n\nকিন্তু রহিমা হাল ছাড়েননি।\n\nধীরে ধীরে তিনি নতুন ডিজাইন শেখেন, গ্রামের আরও নারীদের প্রশিক্ষণ দেন এবং হাতে তৈরি প্রতিটি পণ্যে বাংলাদেশের গ্রামীণ সংস্কৃতির গল্প ফুটিয়ে তুলতে শুরু করেন। আজ তার তৈরি নকশিকাঁথা, জুট ব্যাগ ও হ্যান্ডক্রাফট পণ্য শুধু দেশের বিভিন্ন শহরেই নয়, অনলাইন প্ল্যাটফর্মের মাধ্যমে বিদেশেও পৌঁছাচ্ছে।\n\nROAM-এর মাধ্যমে রহিমা এখন সরাসরি ক্রেতাদের কাছে নিজের পণ্য বিক্রি করতে পারেন। মধ্যস্বত্বভোগী ছাড়াই তিনি ন্যায্য মূল্য পান, আর তার সঙ্গে কাজ করা ২৫ জন গ্রামের নারীও এখন স্বাবলম্বী।\n\nরহিমা বলেন:\n\n> “আমাদের হাতে তৈরি প্রতিটি জিনিসে শুধু সুতা না, আমাদের জীবনের গল্পও সেলাই করা থাকে।”','Faridpur','manik@gmail.com','01758090050',0,NULL,0,'Trust Online Shop',_binary '',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(26,'Jamal Hossain','Pabna','2335467795','APPROVED','2026-06-08',NULL,'### “মাটির গন্ধ থেকে বিশ্ববাজারে”\n\nনওগাঁর ছোট্ট এক গ্রামের মেয়ে রহিমা খাতুন। ছোটবেলা থেকেই মায়ের পাশে বসে হাতে নকশিকাঁথা সেলাই করতেন। সংসারের অভাব ছিল, কিন্তু হাতের শিল্পের প্রতি ভালোবাসা কখনো কমেনি। গ্রামের মানুষদের কাছে এটা ছিল শুধু “শখের কাজ”, কিন্তু রহিমার কাছে ছিল নিজের পরিচয় তৈরি করার স্বপ্ন।\n\nবিয়ের পর জীবনের দায়িত্ব আরও বেড়ে যায়। স্বামীর অল্প আয়ে সংসার চালানো কঠিন হয়ে পড়ে। তখন তিনি পুরোনো কাপড়, সুতা আর গ্রামের নারীদের নিয়ে আবার কাজ শুরু করেন। প্রথমদিকে স্থানীয় হাটে খুব কম দামে কাঁথা বিক্রি হতো। অনেক সময় দিন শেষে কোনো বিক্রিই হতো না।\n\nকিন্তু রহিমা হাল ছাড়েননি।\n\nধীরে ধীরে তিনি নতুন ডিজাইন শেখেন, গ্রামের আরও নারীদের প্রশিক্ষণ দেন এবং হাতে তৈরি প্রতিটি পণ্যে বাংলাদেশের গ্রামীণ সংস্কৃতির গল্প ফুটিয়ে তুলতে শুরু করেন। আজ তার তৈরি নকশিকাঁথা, জুট ব্যাগ ও হ্যান্ডক্রাফট পণ্য শুধু দেশের বিভিন্ন শহরেই নয়, অনলাইন প্ল্যাটফর্মের মাধ্যমে বিদেশেও পৌঁছাচ্ছে।\n\nROAM-এর মাধ্যমে রহিমা এখন সরাসরি ক্রেতাদের কাছে নিজের পণ্য বিক্রি করতে পারেন। মধ্যস্বত্বভোগী ছাড়াই তিনি ন্যায্য মূল্য পান, আর তার সঙ্গে কাজ করা ২৫ জন গ্রামের নারীও এখন স্বাবলম্বী।\n\nরহিমা বলেন:\n\n> “আমাদের হাতে তৈরি প্রতিটি জিনিসে শুধু সুতা না, আমাদের জীবনের গল্পও সেলাই করা থাকে।”',10,'### “মাটির গন্ধ থেকে বিশ্ববাজারে”\n\nনওগাঁর ছোট্ট এক গ্রামের মেয়ে রহিমা খাতুন। ছোটবেলা থেকেই মায়ের পাশে বসে হাতে নকশিকাঁথা সেলাই করতেন। সংসারের অভাব ছিল, কিন্তু হাতের শিল্পের প্রতি ভালোবাসা কখনো কমেনি। গ্রামের মানুষদের কাছে এটা ছিল শুধু “শখের কাজ”, কিন্তু রহিমার কাছে ছিল নিজের পরিচয় তৈরি করার স্বপ্ন।\n\nবিয়ের পর জীবনের দায়িত্ব আরও বেড়ে যায়। স্বামীর অল্প আয়ে সংসার চালানো কঠিন হয়ে পড়ে। তখন তিনি পুরোনো কাপড়, সুতা আর গ্রামের নারীদের নিয়ে আবার কাজ শুরু করেন। প্রথমদিকে স্থানীয় হাটে খুব কম দামে কাঁথা বিক্রি হতো। অনেক সময় দিন শেষে কোনো বিক্রিই হতো না।\n\nকিন্তু রহিমা হাল ছাড়েননি।\n\nধীরে ধীরে তিনি নতুন ডিজাইন শেখেন, গ্রামের আরও নারীদের প্রশিক্ষণ দেন এবং হাতে তৈরি প্রতিটি পণ্যে বাংলাদেশের গ্রামীণ সংস্কৃতির গল্প ফুটিয়ে তুলতে শুরু করেন। আজ তার তৈরি নকশিকাঁথা, জুট ব্যাগ ও হ্যান্ডক্রাফট পণ্য শুধু দেশের বিভিন্ন শহরেই নয়, অনলাইন প্ল্যাটফর্মের মাধ্যমে বিদেশেও পৌঁছাচ্ছে।\n\nROAM-এর মাধ্যমে রহিমা এখন সরাসরি ক্রেতাদের কাছে নিজের পণ্য বিক্রি করতে পারেন। মধ্যস্বত্বভোগী ছাড়াই তিনি ন্যায্য মূল্য পান, আর তার সঙ্গে কাজ করা ২৫ জন গ্রামের নারীও এখন স্বাবলম্বী।\n\nরহিমা বলেন:\n\n> “আমাদের হাতে তৈরি প্রতিটি জিনিসে শুধু সুতা না, আমাদের জীবনের গল্পও সেলাই করা থাকে।”','Pabna','jamal@gmail.com','01389642954',0,NULL,0,'Rural Creativity',_binary '',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(27,'Mahadi Hasan','Kalkini, Madaripur','2587456325','APPROVED','2026-07-03',2,'Start With a PlanFirst, find a good spot in your yard or on your balcony. It needs lots of sunlight. You can plant seeds right in the ground or in large pots. Good beginner vegetables are radishes, beans, and carrots.For tips on how to build a small garden bed and care for your plants:1mGrowing Vegetable Soup | Read AloudAndrea TackettYouTube · Apr 20, 2020Plant the SeedsPush the seeds gently into the soil. Read the seed packet to know how deep to dig. The formula for seed depth is usually: Depth = 2 × Seed WidthThink of the dirt like a warm, cozy blanket for the seed.Add Sun and WaterPlants need energy just like you. But their energy comes from sunlight. The soil acts like a sponge, holding water for the plant to drink. Water travels up from the roots to the leaves. This process is called photosynthesis.For a fun story about a family working together to grow and cook their own food:1mStory Time: Growing Vegetable SoupMissouri Botanical GardenYouTube · Mar 26, 2022Watch Them GrowBe patient! Tiny green shoots will pop out of the dirt. Make sure to pull out bad weeds. Weeds steal the water and sun from your vegetables. Soon, you will see flowers that turn into ripe vegetables.Pick and EatWhen your vegetables are full-size, they are ready to eat. Pull them out of the ground or pick them off the vine. Wash them carefully with clean water first. Now, you can enjoy the fruits of your hard work.If you want, tell me:Do you want to grow plants in a pot or in the ground?Do you like spicy, sweet, or crunchy vegetables? I can give you the perfect seed list to get your garden started!',10,'Save Packging and Sundarban Curiar.','Madaripur','sefatmahmud1995@gmail.com','01728444584',0,NULL,0,'Pure Agro',_binary '','Farmer / Producer',NULL,'sellers/nid/1783030640713-6b80b703.jpg','bKash','01728444584','Fresh Vegetables','sellers/profile/1783030640688-1850d6e8.jpeg'),(28,'Mahadi Hasan','kalkini, Madaripur','6547858741','APPROVED','2026-07-03',2,'Start With a PlanFirst, find a good spot in your yard or on your balcony. It needs lots of sunlight. You can plant seeds right in the ground or in large pots. Good beginner vegetables are radishes, beans, and carrots.For tips on how to build a small garden bed and care for your plants:1mGrowing Vegetable Soup | Read AloudAndrea TackettYouTube · Apr 20, 2020Plant the SeedsPush the seeds gently into the soil. Read the seed packet to know how deep to dig. The formula for seed depth is usually:Depth = 2 × Seed WidthThink of the dirt like a warm, cozy blanket for the seed.Add Sun and WaterPlants need energy just like you. But their energy comes from sunlight. The soil acts like a sponge, holding water for the plant to drink. Water travels up from the roots to the leaves. This process is called photosynthesis.For a fun story about a family working together to grow and cook their own food:1mStory Time: Growing Vegetable SoupMissouri Botanical GardenYouTube · Mar 26, 2022Watch Them GrowBe patient! Tiny green shoots will pop out of the dirt. Make sure to pull out bad weeds. Weeds steal the water and sun from your vegetables. Soon, you will see flowers that turn into ripe vegetables.Pick and EatWhen your vegetables are full-size, they are ready to eat. Pull them out of the ground or pick them off the vine. Wash them carefully with clean water first. Now, you can enjoy the fruits of your hard work.If you want, tell me:Do you want to grow plants in a pot or in the ground?Do you like spicy, sweet, or crunchy vegetables? I can give you the perfect seed list to get your garden started!',10,'save packging ang sundarban curiar.','Madaripur','sefatmahmud1995@gmail.com','01728444584',0,NULL,0,'Pure Agro',_binary '','Farmer / Producer',NULL,'sellers/nid/1783031027718-fba5d9dc.jpg','bKash','01728444584','Fresh Vegetables','sellers/profile/1783031027712-06397d93.jpeg');
/*!40000 ALTER TABLE `seller` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `seller_withdraw`
--

DROP TABLE IF EXISTS `seller_withdraw`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `seller_withdraw` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `account_name` varchar(255) DEFAULT NULL,
  `account_number` varchar(255) DEFAULT NULL,
  `admin_note` varchar(500) DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `note` varchar(500) DEFAULT NULL,
  `payment_method` varchar(255) DEFAULT NULL,
  `processed_date` date DEFAULT NULL,
  `request_date` date DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `transaction_ref` varchar(255) DEFAULT NULL,
  `seller_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpd08q2edc515ekrocyd6inpe` (`seller_id`),
  CONSTRAINT `FKpd08q2edc515ekrocyd6inpe` FOREIGN KEY (`seller_id`) REFERENCES `seller` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `seller_withdraw`
--

LOCK TABLES `seller_withdraw` WRITE;
/*!40000 ALTER TABLE `seller_withdraw` DISABLE KEYS */;
/*!40000 ALTER TABLE `seller_withdraw` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `settings`
--

DROP TABLE IF EXISTS `settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `settings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `section` varchar(50) NOT NULL,
  `setting_key` varchar(100) NOT NULL,
  `setting_value` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_section_key` (`section`,`setting_key`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `settings`
--

LOCK TABLES `settings` WRITE;
/*!40000 ALTER TABLE `settings` DISABLE KEYS */;
INSERT INTO `settings` VALUES (1,'site','siteName','Rural Mart'),(2,'site','siteTagline','Your one-stop online shopping destination in Bangladesh.'),(3,'site','contactEmail','sefatmahmud995@gmail.com'),(4,'site','contactPhone','8801728444584'),(5,'site','address','Mohanagar, West Rampura, Dhaka'),(6,'site','currency','BDT'),(7,'site','timezone','Asia/Dhaka'),(8,'site','maintenanceMode','true'),(9,'payment','codEnabled','true'),(10,'payment','bkashEnabled','false'),(11,'payment','nagadEnabled','false'),(12,'payment','bankEnabled','false'),(13,'shipping','freeShippingEnabled','true'),(14,'shipping','freeShippingThreshold','1000'),(15,'shipping','defaultShippingFee','60'),(16,'shipping','expressFee','120'),(17,'shipping','dhakaFee','50'),(18,'shipping','outsideDhakaFee','100'),(19,'notification','emailOnNewOrder','true'),(20,'notification','emailOnOrderStatus','true'),(21,'notification','emailOnLowStock','true'),(22,'notification','lowStockThreshold','5'),(23,'notification','smsOnNewOrder','false'),(24,'profile','name','SEFAT MAHMUD'),(25,'profile','email','sefatmahmud995@gmail.com'),(26,'profile','phone','1728444584'),(27,'profile','profileImage','admin/admin-profile-1783023623291-652d9c85.JPG');
/*!40000 ALTER TABLE `settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shipment_tracking`
--

DROP TABLE IF EXISTS `shipment_tracking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shipment_tracking` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `tracking_number` varchar(255) DEFAULT NULL,
  `carrier` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `recipient_name` varchar(255) DEFAULT NULL,
  `recipient_phone` varchar(255) DEFAULT NULL,
  `shipping_address` varchar(500) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `district` varchar(255) DEFAULT NULL,
  `shipping_charge` double DEFAULT NULL,
  `estimated_delivery` date DEFAULT NULL,
  `delivered_at` date DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `method_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_tracking_method` (`method_id`),
  CONSTRAINT `FK_tracking_method` FOREIGN KEY (`method_id`) REFERENCES `delivery_method` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shipment_tracking`
--

LOCK TABLES `shipment_tracking` WRITE;
/*!40000 ALTER TABLE `shipment_tracking` DISABLE KEYS */;
INSERT INTO `shipment_tracking` VALUES (1,1001,'ROAM-20260420-48291','Pathao','delivered','Rahim Hossain','01711-111111','House 12, Road 5, Dhanmondi','Dhaka','Dhaka',60,'2026-04-18',NULL,NULL,'2026-04-22 04:09:48','2026-04-22 04:09:48',2),(2,1002,'ROAM-20260420-38421','Sundarban','in_transit','Nusrat Jahan','01811-222222','CDA R/A, Khulshi','Chittagong','Chittagong',100,'2026-04-22',NULL,NULL,'2026-04-22 04:09:48','2026-04-22 04:09:48',5),(3,1003,'ROAM-20260420-72819','In-house','out_for_delivery','Karim Ahmed','01911-333333','Gulshan Avenue, Gulshan-1','Dhaka','Dhaka',80,'2026-04-20',NULL,NULL,'2026-04-22 04:09:48','2026-04-22 04:09:48',1),(4,1004,'ROAM-20260420-55103','Sundarban','pending','Sumaiya Islam','01611-444444','Zindabazar, Sylhet','Sylhet','Sylhet',120,'2026-04-25',NULL,NULL,'2026-04-22 04:09:48','2026-04-22 04:09:48',7),(5,1005,'ROAM-20260420-91847','Redx','picked_up','Tanvir Rahman','01511-555555','Agrabad, Chittagong','Chittagong','Chittagong',150,'2026-04-21',NULL,NULL,'2026-04-22 04:09:48','2026-04-22 04:09:48',6),(6,1,'ROAM-20260425-76042','In-house','pending','adiba','01758-90080','gafargaon,mymensingh',NULL,'mymensingh',80,'2026-04-25',NULL,NULL,'2026-04-25 16:27:09','2026-04-25 16:27:09',1);
/*!40000 ALTER TABLE `shipment_tracking` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shipping_zone`
--

DROP TABLE IF EXISTS `shipping_zone`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shipping_zone` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `regions` varchar(1000) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `created_at` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shipping_zone`
--

LOCK TABLES `shipping_zone` WRITE;
/*!40000 ALTER TABLE `shipping_zone` DISABLE KEYS */;
INSERT INTO `shipping_zone` VALUES (1,'ঢাকা সিটি','ঢাকা শহরের ভেতরে সব এলাকা','Dhaka, Narayanganj, Gazipur, Manikganj','active','2026-04-22'),(2,'চট্টগ্রাম বিভাগ','চট্টগ্রাম ও পার্বত্য অঞ্চল','Chittagong, Cox Bazar, Comilla, Noakhali, Feni','active','2026-04-22'),(3,'সিলেট বিভাগ','সিলেট ও হাওর এলাকা','Sylhet, Moulvibazar, Habiganj, Sunamganj','active','2026-04-22'),(4,'সারাদেশ (Default)','বাংলাদেশের যেকোনো জেলায়','Nationwide','active','2026-04-22');
/*!40000 ALTER TABLE `shipping_zone` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `user_code` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL COMMENT 'BCrypt hashed — plain text রাখবেন না',
  `status` varchar(255) DEFAULT NULL,
  `profile_image` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (2,'Mohanagar, West Rampura, Dhaka','sefatmahmud995@gmail.com','SEFAT MAHMUD','01728444584','admin','USR-20260501-4634','$2a$10$B3axH2ogqIU81gXDTdWajOQI0Cc51UBYvsXvuK332M3C0jE3iY5sW',NULL,'profiles/profile-1783032792641-7b841026.png');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vendor`
--

DROP TABLE IF EXISTS `vendor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vendor` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `bank_account_name` varchar(255) DEFAULT NULL,
  `bank_account_number` varchar(255) DEFAULT NULL,
  `bank_branch` varchar(255) DEFAULT NULL,
  `bank_name` varchar(255) DEFAULT NULL,
  `commission_rate` double DEFAULT NULL,
  `created_at` date DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `nid_no` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `rating` double DEFAULT NULL,
  `rejection_reason` varchar(255) DEFAULT NULL,
  `review_count` int DEFAULT NULL,
  `shop_description` varchar(1000) DEFAULT NULL,
  `shop_logo` varchar(255) DEFAULT NULL,
  `shop_name` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `total_earnings` double DEFAULT NULL,
  `total_orders` int DEFAULT NULL,
  `total_products` int DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKkjl81avhd6288yio9slt0erx1` (`user_id`),
  CONSTRAINT `FKkjl81avhd6288yio9slt0erx1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vendor`
--

LOCK TABLES `vendor` WRITE;
/*!40000 ALTER TABLE `vendor` DISABLE KEYS */;
INSERT INTO `vendor` VALUES (1,'azimpur, Dhaka',NULL,NULL,NULL,NULL,10,'2026-04-26','anikalutfunnahar@gmail.com','Mahamuda','2587413695','01701886341',0,NULL,0,'',NULL,'Online Mart','active',10000,0,0,NULL);
/*!40000 ALTER TABLE `vendor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vendor_payout`
--

DROP TABLE IF EXISTS `vendor_payout`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vendor_payout` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` double NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `payment_method` varchar(255) DEFAULT NULL,
  `processed_date` date DEFAULT NULL,
  `request_date` date DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `transaction_ref` varchar(255) DEFAULT NULL,
  `vendor_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_payout_vendor` (`vendor_id`),
  CONSTRAINT `FK_payout_vendor` FOREIGN KEY (`vendor_id`) REFERENCES `vendor` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vendor_payout`
--

LOCK TABLES `vendor_payout` WRITE;
/*!40000 ALTER TABLE `vendor_payout` DISABLE KEYS */;
INSERT INTO `vendor_payout` VALUES (1,10000,'ROAM','bank_transfer','2026-04-26','2026-04-26','paid','TXN-258712',1);
/*!40000 ALTER TABLE `vendor_payout` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wishlist`
--

DROP TABLE IF EXISTS `wishlist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wishlist` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` date DEFAULT NULL,
  `product_id` int DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKoa9vib6j4l9wuwlus2yjkgtb2` (`product_id`),
  KEY `FKtrd6335blsefl2gxpb8lr0gr7` (`user_id`),
  CONSTRAINT `FKoa9vib6j4l9wuwlus2yjkgtb2` FOREIGN KEY (`product_id`) REFERENCES `add_product` (`id`),
  CONSTRAINT `FKtrd6335blsefl2gxpb8lr0gr7` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wishlist`
--

LOCK TABLES `wishlist` WRITE;
/*!40000 ALTER TABLE `wishlist` DISABLE KEYS */;
/*!40000 ALTER TABLE `wishlist` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-11 14:54:04
