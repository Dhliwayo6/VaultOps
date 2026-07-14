CREATE TABLE locations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255),
    address VARCHAR(255),
    max_capacity INT NOT NULL
);

-- Insert default location
INSERT INTO locations (id, name, description, address, max_capacity)
VALUES (1, 'Unassigned', 'Default location for existing assets', 'Primary Site Address', 1000);

-- Add location_id column to assets table
ALTER TABLE assets ADD COLUMN location_id BIGINT NOT NULL DEFAULT 1;

-- Add foreign key constraint
ALTER TABLE assets ADD CONSTRAINT fk_assets_location_id FOREIGN KEY (location_id) REFERENCES locations (id);

-- Create index on foreign key column
CREATE INDEX idx_assets_location_id ON assets (location_id);

-- Drop deprecated location string column
ALTER TABLE assets DROP COLUMN location;
