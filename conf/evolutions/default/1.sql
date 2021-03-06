# --- !Ups
CREATE TABLE brand_images (
  brand_id int(11) NOT NULL DEFAULT '0',
  image_id int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (brand_id,image_id),
  KEY FK73D47E30B4A81355 (brand_id),
  KEY FK73D47E301EC8A9FD (image_id)
);

CREATE TABLE brands (
  id int(11) NOT NULL AUTO_INCREMENT,
  title varchar(127) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE categories (
  category_id int(11) NOT NULL AUTO_INCREMENT,
  title varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  left_value int(11) DEFAULT NULL,
  right_value int(11) DEFAULT NULL,
  parent_category_id int(11) DEFAULT NULL,
  PRIMARY KEY (category_id)
);

CREATE TABLE category_images (
  category_id int(11) NOT NULL DEFAULT '0',
  image_id int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (category_id,image_id),
  KEY imageId (image_id)
);

CREATE TABLE cities (
  id int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(45) CHARACTER SET utf8 NOT NULL,
  domain varchar(45) NOT NULL,
  delivery text CHARACTER SET utf8,
  payment text CHARACTER SET utf8,
  phone varchar(45) CHARACTER SET utf8 DEFAULT NULL,
  prefix varchar(45) CHARACTER SET utf8 DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY domain_UNIQUE (domain)
);

CREATE TABLE dealers (
  id int(11) NOT NULL AUTO_INCREMENT,
  title varchar(60) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE images (
  image_id int(11) NOT NULL AUTO_INCREMENT,
  file_path varchar(256) CHARACTER SET utf8 DEFAULT NULL,
  md5 varchar(100) CHARACTER SET utf8 DEFAULT NULL,
  PRIMARY KEY (image_id),
  UNIQUE KEY filePath_UNIQUE (file_path(255))
);

CREATE TABLE order_delivery_addresses (
  order_id int(11) NOT NULL,
  city varchar(50) CHARACTER SET utf8 NOT NULL,
  street varchar(50) CHARACTER SET utf8 NOT NULL,
  building varchar(50) CHARACTER SET utf8 NOT NULL,
  housing varchar(50) CHARACTER SET utf8 DEFAULT NULL,
  apartment varchar(45) CHARACTER SET utf8 DEFAULT NULL
);

CREATE TABLE order_delivery_shops (
  order_id int(11) NOT NULL,
  shop_id int(11) NOT NULL
);

CREATE TABLE order_items (
  order_id int(11) DEFAULT NULL,
  product_id int(11) DEFAULT NULL,
  quantity int(11) NOT NULL,
  unit_price double DEFAULT NULL
);

CREATE TABLE orders (
  id int(11) NOT NULL AUTO_INCREMENT,
  user_id int(11) DEFAULT NULL,
  city_id int(11) DEFAULT NULL,
  place_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` varchar(45) CHARACTER SET utf8 DEFAULT 'unconfirmed',
  email varchar(85) CHARACTER SET utf8 DEFAULT NULL,
  phone varchar(45) CHARACTER SET utf8 DEFAULT NULL,
  `name` varchar(50) CHARACTER SET utf8 DEFAULT NULL,
  middle_name varchar(50) CHARACTER SET utf8 DEFAULT NULL,
  last_name varchar(50) CHARACTER SET utf8 DEFAULT NULL,
  `comment` text CHARACTER SET utf8,
  delivery_type varchar(45) CHARACTER SET utf8 DEFAULT NULL,
  address varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE page_sections (
  id int(11) NOT NULL AUTO_INCREMENT,
  page_id int(11) NOT NULL,
  `name` varchar(45) CHARACTER SET utf8 DEFAULT NULL,
  content text CHARACTER SET utf8,
  PRIMARY KEY (id),
  UNIQUE KEY id_UNIQUE (id)
);

CREATE TABLE pages (
  id int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) CHARACTER SET utf8 NOT NULL,
  alias varchar(45) NOT NULL,
  title varchar(45) CHARACTER SET utf8 DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY id_UNIQUE (id)
);

CREATE TABLE product_images (
  image_id int(11) NOT NULL DEFAULT '0',
  product_id int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (image_id,product_id),
  KEY FKEF9817C87AD125 (product_id),
  KEY FKEF9817C81EC8A9FD (image_id)
);

CREATE TABLE products (
  id int(11) NOT NULL AUTO_INCREMENT,
  title varchar(256) CHARACTER SET utf8 NOT NULL,
  description text CHARACTER SET utf8,
  price decimal(10,2) DEFAULT NULL,
  added_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  brand_id int(11) DEFAULT NULL,
  short_description text CHARACTER SET utf8,
  default_image_id int(11) DEFAULT NULL,
  visit_counter int(11) DEFAULT NULL,
  created_by_user int(11) DEFAULT NULL,
  archived bit(1) DEFAULT b'0',
  is_available bit(1) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY FKC42BD164B4A81355 (brand_id)
);

CREATE TABLE products_categories (
  product_id int(11) NOT NULL DEFAULT '0',
  category_id int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (product_id,category_id),
  KEY FK1A81C3D73280D32B (category_id),
  KEY FK1A81C3D77AD125 (product_id)
);

CREATE TABLE questions (
  id int(11) NOT NULL AUTO_INCREMENT,
  product_id int(11) DEFAULT NULL,
  email varchar(100) CHARACTER SET utf8 NOT NULL,
  `type` varchar(45) CHARACTER SET utf8 NOT NULL DEFAULT 'availability',
  `status` varchar(45) CHARACTER SET utf8 NOT NULL DEFAULT 'unanswered',
  PRIMARY KEY (id)
);

CREATE TABLE roles (
  role_id int(11) NOT NULL DEFAULT '0',
  title varchar(45) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (role_id),
  UNIQUE KEY title_UNIQUE (title)
);

CREATE TABLE shopping_items (
  user_id int(11) DEFAULT NULL,
  product_id int(11) DEFAULT NULL,
  quantity int(11) NOT NULL DEFAULT '0',
  unit_price double DEFAULT NULL
);

CREATE TABLE shops (
  id int(11) NOT NULL AUTO_INCREMENT,
  title varchar(45) CHARACTER SET utf8 NOT NULL,
  city varchar(45) CHARACTER SET utf8 NULL,
  city_id int(11) DEFAULT NULL,
  address varchar(45) CHARACTER SET utf8 NOT NULL,
  phones varchar(500) DEFAULT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE stock_items (
  id int(11) NOT NULL AUTO_INCREMENT,
  product_id int(11) NOT NULL,
  dealer_id int(11) NOT NULL,
  dealer_price double NOT NULL DEFAULT '0',
  quantity int(11) NOT NULL DEFAULT '1',
  date_added timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  shop_id int(11) DEFAULT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE user_profiles (
  email varchar(85) NOT NULL,
  `password` varchar(45) NOT NULL,
  user_id int(11) DEFAULT NULL,
  user_name varchar(50) CHARACTER SET utf8 DEFAULT NULL,
  user_last_name varchar(50) CHARACTER SET utf8 DEFAULT NULL,
  user_middle_name varchar(50) CHARACTER SET utf8 DEFAULT NULL,
  phone_number varchar(30) CHARACTER SET utf8 DEFAULT NULL,
  address varchar(200) CHARACTER SET utf8 DEFAULT NULL,
  city varchar(45) CHARACTER SET utf8 DEFAULT NULL,
  street varchar(45) CHARACTER SET utf8 DEFAULT NULL,
  building varchar(45) CHARACTER SET utf8 DEFAULT NULL,
  housing varchar(45) CHARACTER SET utf8 DEFAULT NULL,
  apartment varchar(45) CHARACTER SET utf8 DEFAULT NULL,
  PRIMARY KEY (email),
  UNIQUE KEY userId_UNIQUE (user_id),
  KEY userId (user_id),
  KEY userId_fk (user_id)
);

CREATE TABLE users (
  id int(11) NOT NULL AUTO_INCREMENT,
  register_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  session_id varchar(45) CHARACTER SET utf8 DEFAULT NULL,
  PRIMARY KEY (id),
  KEY userId (id)
);

CREATE TABLE users_roles (
  user_id int(11) NOT NULL DEFAULT '0',
  role_id int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (user_id,role_id)
);

CREATE TABLE yandex_shop_info (
  shop_id int(11) NOT NULL,
  title varchar(128) CHARACTER SET utf8 NOT NULL,
  company varchar(128) CHARACTER SET utf8 NOT NULL,
  url varchar(256) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (shop_id),
  UNIQUE KEY shop_id_UNIQUE (shop_id)
);

# --- !Downs
DROP TABLE IF EXISTS brand_images;
DROP TABLE IF EXISTS brands;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS category_images;
DROP TABLE IF EXISTS cities;
DROP TABLE IF EXISTS dealers;
DROP TABLE IF EXISTS images;
DROP TABLE IF EXISTS order_delivery_addresses;
DROP TABLE IF EXISTS order_delivery_shops;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS page_sections;
DROP TABLE IF EXISTS pages;
DROP TABLE IF EXISTS play_evolutions;
DROP TABLE IF EXISTS product_images;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS products_categories;
DROP TABLE IF EXISTS questions;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS shopping_items;
DROP TABLE IF EXISTS shops;
DROP TABLE IF EXISTS stock_items;
DROP TABLE IF EXISTS user_profiles;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS users_roles;
DROP TABLE IF EXISTS yandex_shop_info;
