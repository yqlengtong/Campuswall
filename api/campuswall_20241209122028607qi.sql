-- MySQL dump 10.13  Distrib 5.7.44, for Linux (x86_64)
--
-- Host: localhost    Database: campuswall
-- ------------------------------------------------------
-- Server version	5.7.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `categories` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (2,'交友'),(3,'吐槽'),(5,'失物招领'),(6,'学术交流'),(4,'日常分享'),(1,'表白');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comments`
--

DROP TABLE IF EXISTS `comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `comments` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `post_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `content` text NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `post_id` (`post_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`),
  CONSTRAINT `comments_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comments`
--

LOCK TABLES `comments` WRITE;
/*!40000 ALTER TABLE `comments` DISABLE KEYS */;
INSERT INTO `comments` VALUES (6,5,3,'太棒了','2024-12-07 08:21:03'),(8,4,5,'可爱呢','2024-12-08 12:17:00');
/*!40000 ALTER TABLE `comments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `likes`
--

DROP TABLE IF EXISTS `likes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `likes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `post_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `post_id` (`post_id`,`user_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `likes_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`),
  CONSTRAINT `likes_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `likes`
--

LOCK TABLES `likes` WRITE;
/*!40000 ALTER TABLE `likes` DISABLE KEYS */;
INSERT INTO `likes` VALUES (1,5,3,'2024-12-07 08:21:06');
/*!40000 ALTER TABLE `likes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `posts`
--

DROP TABLE IF EXISTS `posts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `posts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `category_id` int(11) NOT NULL,
  `content` text NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `image_links` text,
  `is_anonymous` tinyint(1) DEFAULT '0',
  `is_approved` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `category_id` (`category_id`),
  CONSTRAINT `posts_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `posts_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `posts`
--

LOCK TABLES `posts` WRITE;
/*!40000 ALTER TABLE `posts` DISABLE KEYS */;
INSERT INTO `posts` VALUES (1,1,1,'找不到人表白，祖国我爱你！','2024-12-06 17:42:26','[\"https:\\/\\/img.jmm0.cn\\/campuswall\\/6cd7cdba-0a5f-403e-9950-29fe41717474.jpg\"]',0,1),(2,1,4,'命运并非穷凶极恶之物，而是你我终将到达之处。','2024-12-08 18:46:45','[\"https:\\/\\/img.jmm0.cn\\/campuswall\\/582a4f96-be68-4f44-9ccf-ec248a578f1a.heif\",\"https:\\/\\/img.jmm0.cn\\/campuswall\\/354c6367-3e76-40c2-b346-c16318c0792a.heif\"]',0,1),(3,1,5,'女朋友丢了麻烦大家帮我找一下 ಥ_ಥ','2024-12-06 17:52:47','[\"https:\\/\\/img.jmm0.cn\\/campuswall\\/e0e5d033-03e5-477b-b1aa-fcd26e7df326.jpg\"]',1,1),(4,1,4,'1 2 3 汪汪汪','2024-12-08 17:54:36','[\"https:\\/\\/img.jmm0.cn\\/campuswall\\/9b9e28c0-89f9-4464-b641-38540c9283d4.jpg\",\"https:\\/\\/img.jmm0.cn\\/campuswall\\/188f3cf6-5ec6-4856-9a5a-f846c5493663.jpg\",\"https:\\/\\/img.jmm0.cn\\/campuswall\\/6d423e2e-e672-4708-b2b2-55d4ea3e1feb.jpg\",\"https:\\/\\/img.jmm0.cn\\/campuswall\\/f041c296-0376-4f4a-8e71-636e3eb282f1.jpg\",\"https:\\/\\/img.jmm0.cn\\/campuswall\\/e072981c-9ea5-4fc3-bdb4-994c96503a04.jpg\",\"https:\\/\\/img.jmm0.cn\\/campuswall\\/380aa947-bb10-4194-8d2c-1d4145728cfa.jpg\"]',0,1),(5,1,6,'一个人怎么可以聪明成这样','2024-12-07 06:09:27','[\"https:\\/\\/img.jmm0.cn\\/campuswall\\/dc840df5-229e-4080-acee-470995464332.png\"]',0,1),(9,5,1,'哈喽哈喽','2024-12-08 12:08:06','[\"https:\\/\\/img.jmm0.cn\\/campuswall\\/6ace6c03-9731-4e09-83b1-5d789fc67eed.jpg\"]',0,1),(10,3,3,'食堂打的菜太少了','2024-12-06 16:20:34','[]',1,1);
/*!40000 ALTER TABLE `posts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `nickname` varchar(255) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT 'https://img.jmm0.cn/avatars/newuser.png',
  `qq` varchar(255) DEFAULT '未设置',
  `wechat` varchar(255) DEFAULT '未设置',
  `role` varchar(20) DEFAULT 'user',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'lengtong','$2y$10$3S.vpmcG/yYjo3xHcxldkOlRIhDCrRqSn65QiTTreQ1tftXx7IYVm','希微艾丝','https://img.jmm0.cn/avatars/d317f0d3-bf30-4220-ab96-f329cde38d03.jpg','2770568979','nullundefinedn','admin'),(2,'123','$2y$10$rxUyfYY08fyiUfNyRcbov.uDJCm62cU0l7xzfV2Ym7S7ZH/qewdjO','新用户456','https://img.jmm0.cn/avatars/newuser.png',NULL,NULL,'user'),(3,'11','$2y$10$tUJ4cHZPp57fQgCxjSoBJuAzY6FoLGlzcoB52TnLqnSaXSlBRup5i','新用户11','https://img.jmm0.cn/avatars/newuser.png',NULL,NULL,'user'),(5,'zx','$2y$10$JdpROWFQq9o33Prlwdu.FeGjhVxXA5BLJgVviipLnna2hNQWmdScK','瓦达西瓦','https://img.jmm0.cn/avatars/newuser.png','','','user');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_unicode_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`campuswall`@`%`*/ /*!50003 TRIGGER before_insert_users
BEFORE INSERT ON users
FOR EACH ROW
BEGIN
    IF NEW.nickname IS NULL OR NEW.nickname = '' THEN
        SET NEW.nickname = CONCAT('新用户', FLOOR(100 + (RAND() * 900)));
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-12-09  4:20:28
