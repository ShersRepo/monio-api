-- V1__create_user_schema_and_data.sql

-- Create schema
CREATE SCHEMA IF NOT EXISTS "user";

-- Create an enum type for security roles
CREATE TYPE "user".app_security_role AS ENUM (
    'APP_USER',
    'APP_ADMIN',
    'APP_SUPER_ADMIN'
);

-- Create a security_role table
CREATE TABLE "user".security_role (
                                      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                      name "user".app_security_role NOT NULL UNIQUE,
                                      description VARCHAR(255) NOT NULL
);

-- Create the user_account table
CREATE TABLE "user".user_account (
                                     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                     username VARCHAR(150) NOT NULL UNIQUE,
                                     password VARCHAR(255) NOT NULL,
                                     email VARCHAR(255) NOT NULL UNIQUE,
                                     first_name VARCHAR(80) NOT NULL,
                                     last_name VARCHAR(80),
                                     expired BOOLEAN DEFAULT FALSE NOT NULL,
                                     expiry_date TIMESTAMP,
                                     created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                     is_locked BOOLEAN DEFAULT FALSE NOT NULL,
                                     enabled BOOLEAN DEFAULT TRUE NOT NULL,
                                     is_credentials_expired BOOLEAN DEFAULT FALSE NOT NULL,
                                     last_login_date TIMESTAMP
);

-- Create join table for user_account and security_role
CREATE TABLE "user".user_security_roles (
                                            user_id UUID NOT NULL,
                                            role_id UUID NOT NULL,
                                            PRIMARY KEY (user_id, role_id),
                                            FOREIGN KEY (user_id) REFERENCES "user".user_account(id) ON DELETE CASCADE,
                                            FOREIGN KEY (role_id) REFERENCES "user".security_role(id) ON DELETE CASCADE
);

-- Create indexes
CREATE INDEX idx_user_account_username ON "user".user_account(username);
CREATE INDEX idx_user_account_email ON "user".user_account(email);
CREATE INDEX idx_security_role_name ON "user".security_role(name);

-- Add check constraints
-- Must be Email
ALTER TABLE "user".user_account
    ADD CONSTRAINT chk_email_format
        CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

-- Must be more than 3 chars
ALTER TABLE "user".user_account
    ADD CONSTRAINT chk_username_format
        CHECK (length(username) >= 3);

-- Create procedure for last_login_date updates
CREATE FUNCTION "user".update_last_login(user_id UUID)
    RETURNS void AS $$
BEGIN
    -- Update last_login_date only if the user exists
    UPDATE "user".user_account
    SET last_login_date = CURRENT_TIMESTAMP
    WHERE id = user_id;
END;
$$ LANGUAGE plpgsql;

-- Insert security roles
INSERT INTO "user".security_role (id, name, description) VALUES
(gen_random_uuid(), 'APP_USER', 'Regular application user with standard privileges'),
(gen_random_uuid(), 'APP_ADMIN', 'Administrator with administrative application access'),
(gen_random_uuid(), 'APP_SUPER_ADMIN', 'The Boss :)');

