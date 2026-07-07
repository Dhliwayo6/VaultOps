-- Create denylisted_tokens table
CREATE TABLE `denylisted_tokens` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `token_hash` VARCHAR(255) NOT NULL UNIQUE,
    `expiry_time` DATETIME(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create user_refresh_tokens table
CREATE TABLE `user_refresh_tokens` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `token_hash` VARCHAR(255) NOT NULL UNIQUE,
    `expiry_time` DATETIME(6) NOT NULL,
    `revoked` BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT `fk_refresh_tokens_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create password_reset_tokens table
CREATE TABLE `password_reset_tokens` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `token_hash` VARCHAR(255) NOT NULL UNIQUE,
    `expiry_time` DATETIME(6) NOT NULL,
    `used` BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT `fk_reset_tokens_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
