CREATE TABLE `gdt_one_link` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `utm_source` varchar(64) NOT NULL,
  `utm_medium` varchar(64) NOT NULL,
  `utm_campaign` varchar(64) NOT NULL,
  `utm_content` varchar(64) NOT NULL,
  `landing_page_id` int(11) NOT NULL COMMENT 'landing_page id',
  `landing_page_path` varchar(512)  COMMENT 'landing_page path',
  `name` varchar(256) NOT NULL,
  `link` varchar(1024) NOT NULL COMMENT 'deeplink content',
  `param` varchar(512),
  `expired_time` timestamp NULL COMMENT 'expire time',
  `status` varchar(32) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE or OFF',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '',
  `creator` varchar(64) NOT NULL COMMENT '',
  `editor` varchar(64) NOT NULL COMMENT '',
  PRIMARY KEY (`id`)
) ;

CREATE TABLE `deep_link` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `utm_source` varchar(64) NOT NULL,
  `utm_medium` varchar(64) NOT NULL,
  `utm_campaign` varchar(64) NOT NULL,
  `utm_content` varchar(64) NOT NULL,
  `landing_page_id` int(11) NOT NULL COMMENT 'landing_page id',
  `landing_page_path` varchar(512)  COMMENT 'landing_page path',
  `name` varchar(256) NOT NULL,
  `link` varchar(1024) NOT NULL COMMENT 'deeplink content',
  `platform` enum('GH','SAA') NOT NULL COMMENT '',
  `expired_time` timestamp NULL COMMENT 'expire time',
  `status` varchar(32) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE or OFF',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '',
  `creator` varchar(64) NOT NULL COMMENT '',
  `editor` varchar(64) NOT NULL COMMENT '',
  PRIMARY KEY (`id`)
) ;


CREATE TABLE `landing_page` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `module` varchar(64) NOT NULL COMMENT '',
  `scheme_name` varchar(64) NOT NULL COMMENT '',
  `path_template` varchar(256) NOT NULL COMMENT '',
  `name` varchar(64) NOT NULL COMMENT '',
  `platform` enum('GH','SAA') NOT NULL COMMENT '',
  `is_native` TINYINT NOT NULL COMMENT '',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '',
  `creator` varchar(64) NOT NULL COMMENT '',
  `editor` varchar(64) NOT NULL COMMENT '',
  PRIMARY KEY (`id`)
) ;


CREATE TABLE `short_deep_link` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `short_link_id` int(11) NOT NULL,
  `deep_link_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ;
CREATE TABLE `short_link_relation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `short_link_id` int(11) NOT NULL,
  `original_id` int(11) ,
  `original_link` varchar(1024),
  `link_type` varchar(32),
  PRIMARY KEY (`id`)
) ;


CREATE TABLE `short_link` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Id',
  `code` varchar(10) NOT NULL COMMENT '',
  `expired_time` timestamp NULL COMMENT '',
  `name` varchar(45) NOT NULL COMMENT '',
  `status` varchar(32) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE or OFF',
  `link_type` varchar(32),
  `link` varchar(128),
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '',
  `creator` varchar(64) NOT NULL COMMENT '',
  `editor` varchar(64) NOT NULL COMMENT '',
  PRIMARY KEY (`id`)
) ;


