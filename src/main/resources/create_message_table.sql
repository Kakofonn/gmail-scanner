'CREATE TABLE `message` (
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`message_id` varchar(45) DEFAULT NULL,
	`sender_email` mediumtext,
	`snippet` mediumtext,
	PRIMARY KEY (`id`),
	UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=606 DEFAULT CHARSET=utf8'
