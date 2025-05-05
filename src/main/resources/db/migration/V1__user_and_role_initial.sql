-- V1__user_security_schema_and_data.sql

-- Create schema
CREATE SCHEMA IF NOT EXISTS "user_acc";

-- Create an enum type for security roles
CREATE TYPE "user_acc".app_security_role AS ENUM (
    'APP_USER',
    'APP_ADMIN',
    'APP_SUPER_ADMIN'
);

-- Create security_role table
CREATE TABLE "user_acc".security_role (
                                      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                      name "user_acc".app_security_role NOT NULL UNIQUE,
                                      description VARCHAR(255) NOT NULL
);

-- Create user_account table
CREATE TABLE "user_acc".user_account (
                                     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                     username VARCHAR(150) NOT NULL UNIQUE,
                                     password VARCHAR(255) NOT NULL,
                                     email VARCHAR(255) NOT NULL UNIQUE,
                                     first_name VARCHAR(80) NOT NULL,
                                     last_name VARCHAR(80),
                                     expired BOOLEAN DEFAULT FALSE NOT NULL,
                                     expiry_date TIMESTAMP,
                                     created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     is_locked BOOLEAN DEFAULT FALSE NOT NULL,
                                     enabled BOOLEAN DEFAULT TRUE NOT NULL,
                                     is_credentials_expired BOOLEAN DEFAULT FALSE NOT NULL,
                                     last_login_date TIMESTAMP
);

-- Create join table for user_account and security_role
CREATE TABLE "user_acc".user_security_roles (
                                            user_id UUID NOT NULL,
                                            role_id UUID NOT NULL,
                                            PRIMARY KEY (user_id, role_id),
                                            FOREIGN KEY (user_id) REFERENCES "user_acc".user_account(id) ON DELETE CASCADE,
                                            FOREIGN KEY (role_id) REFERENCES "user_acc".security_role(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_user_account_username ON "user_acc".user_account(username);
CREATE INDEX idx_user_account_email ON "user_acc".user_account(email);
CREATE INDEX idx_security_role_name ON "user_acc".security_role(name);
CREATE INDEX idx_user_security_roles_user_id ON "user_acc".user_security_roles(user_id);
CREATE INDEX idx_user_security_roles_role_id ON "user_acc".user_security_roles(role_id);

-- Add constraints for data validation
ALTER TABLE "user_acc".user_account
    ADD CONSTRAINT chk_email_format
        CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

ALTER TABLE "user_acc".user_account
    ADD CONSTRAINT chk_username_format
        CHECK (length(username) >= 3);

-- Create procedure for last_login_date updates
CREATE FUNCTION "user_acc".update_last_login(user_id UUID)
    RETURNS void AS $$
BEGIN
    -- Update last_login_date only if the user exists
    UPDATE "user_acc".user_account
    SET last_login_date = CURRENT_TIMESTAMP
    WHERE id = user_id;
END;
$$ LANGUAGE plpgsql;

-- Insert initial security roles
INSERT INTO "user_acc".security_role (id, name, description) VALUES
                                                             (gen_random_uuid(), 'APP_USER', 'Regular user with standard privileges'),
                                                             (gen_random_uuid(), 'APP_ADMIN', 'Administrator with full system access'),
                                                             (gen_random_uuid(), 'APP_SUPER_ADMIN', 'Super administrator with complete system control');

-- Comments for documentation
COMMENT ON SCHEMA "user_acc" IS 'Schema for user management and security';
COMMENT ON TABLE "user_acc".user_account IS 'Stores user account information';
COMMENT ON TABLE "user_acc".security_role IS 'Stores available security roles';
COMMENT ON TABLE "user_acc".user_security_roles IS 'Junction table for user-role associations';
COMMENT ON COLUMN "user_acc".user_account.email IS 'User''s email address (must be unique)';
COMMENT ON COLUMN "user_acc".user_account.username IS 'User''s login username (must be unique)';
COMMENT ON COLUMN "user_acc".user_account.created_date IS 'Timestamp when the user account was created';
COMMENT ON COLUMN "user_acc".user_account.last_login_date IS 'Timestamp of the user''s last login';
