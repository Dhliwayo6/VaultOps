-- Initial Schema baseline for VaultOps

-- Table: assets
CREATE TABLE `assets` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(255) NOT NULL,
    `type` VARCHAR(255) NOT NULL,
    `location` VARCHAR(255) NOT NULL,
    `assignment` VARCHAR(255) NOT NULL,
    `serial_number` VARCHAR(255) UNIQUE,
    `purchase_price` DECIMAL(38, 2),
    `purchase_date` DATE,
    `condition_status` VARCHAR(255) NOT NULL,
    `usageStatus` VARCHAR(255) NOT NULL,
    `assignedTo` VARCHAR(255),
    `created_at` DATETIME(6),
    `latest_updated_date` DATETIME(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Indexes for assets table as recommended in audit:
-- idx_assets_condition_status: support asset statistics and filtering by condition status (EXCELLENT, GOOD, DAMAGED, etc.)
CREATE INDEX `idx_assets_condition_status` ON `assets` (`condition_status`);

-- idx_assets_usage_status: support filtering by usage status (IN_USE, STORAGE, SERVICE) and stats calculation
CREATE INDEX `idx_assets_usage_status` ON `assets` (`usageStatus`);

-- idx_assets_assigned_to: support filtering assets assigned to specific users/departments
CREATE INDEX `idx_assets_assigned_to` ON `assets` (`assignedTo`);

-- idx_assets_created_at: support the ordering/sorting of top 4 most recent assets
CREATE INDEX `idx_assets_created_at` ON `assets` (`created_at`);


-- Table: maintenance
CREATE TABLE `maintenance` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `asset_id` BIGINT NOT NULL,
    `date` DATE,
    `performed_by` VARCHAR(255) NOT NULL,
    `description` VARCHAR(255),
    `cost` DECIMAL(38, 2),
    CONSTRAINT `fk_maintenance_asset_id` FOREIGN KEY (`asset_id`) REFERENCES `assets` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Index for maintenance table:
-- idx_maintenance_asset_id: support fast lookup and joining of maintenance records per asset
CREATE INDEX `idx_maintenance_asset_id` ON `maintenance` (`asset_id`);


-- Table: migration
CREATE TABLE `migration` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `asset_id` BIGINT,
    `from_location` VARCHAR(255),
    `to_location` VARCHAR(255),
    `moved_by` VARCHAR(255),
    `description` VARCHAR(255),
    CONSTRAINT `fk_migration_asset_id` FOREIGN KEY (`asset_id`) REFERENCES `assets` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Index for migration table:
-- idx_migration_asset_id: support lookup and joining of migration history records per asset
CREATE INDEX `idx_migration_asset_id` ON `migration` (`asset_id`);


-- Table: import_logs
CREATE TABLE `import_logs` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `userId` VARCHAR(255),
    `fileName` VARCHAR(255),
    `fileSize` BIGINT,
    `status` VARCHAR(255),
    `totalRecords` INT,
    `successCount` INT,
    `errorCount` INT,
    `errorMessage` VARCHAR(255),
    `startedAt` DATETIME(6),
    `completedAt` DATETIME(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- Table: import_errors
CREATE TABLE `import_errors` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `import_log_id` BIGINT,
    `rowNum` INT,
    `fieldName` VARCHAR(255),
    `errorMessage` VARCHAR(255),
    `invalidValue` VARCHAR(255),
    CONSTRAINT `fk_import_errors_import_log_id` FOREIGN KEY (`import_log_id`) REFERENCES `import_logs` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Index for import_errors table:
-- idx_import_errors_import_log_id: support retrieving specific validation/import errors for an import batch
CREATE INDEX `idx_import_errors_import_log_id` ON `import_errors` (`import_log_id`);
