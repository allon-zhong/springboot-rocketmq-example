CREATE TABLE `msgrecord` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `column1` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `msgid` char(32) COLLATE utf8_bin DEFAULT NULL,
  `rt` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `column1_UNIQUE` (`column1`),
  KEY `msgid` (`id`,`msgid`)
) ENGINE=InnoDB AUTO_INCREMENT=142168 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `ordertest` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `column1` varchar(45) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `column2` varchar(45) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `column3` varchar(45) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `column4` varchar(45) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `column5` varchar(45) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `msgid` char(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `rt` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `column1_unique` (`column1`),
  KEY `msgid` (`msgid`,`id`)
) ENGINE=InnoDB AUTO_INCREMENT=111001 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
