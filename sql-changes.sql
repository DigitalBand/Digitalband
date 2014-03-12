ALTER TABLE dbs.brand_images CHANGE brandId brand_id INT;
ALTER TABLE dbs.brand_images CHANGE imageId image_d INT;
ALTER TABLE dbs.categories CHANGE CategoryId category_id INT;
ALTER TABLE dbs.categories CHANGE Title title VARCHAR(256);
ALTER TABLE dbs.categories CHANGE LeftValue left_value INT;
ALTER TABLE dbs.categories CHANGE RightValue right_value INT;