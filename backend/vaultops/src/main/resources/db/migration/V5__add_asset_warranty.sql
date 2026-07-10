-- Add warranty expiration support to assets
ALTER TABLE `assets` ADD COLUMN `warranty_expiry_date` DATE;
