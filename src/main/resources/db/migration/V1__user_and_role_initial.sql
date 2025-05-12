-- V1__initial_schema.sql

BEGIN;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE SCHEMA ledger;
CREATE SCHEMA user_acc;
CREATE SCHEMA audit;

COMMENT ON SCHEMA ledger IS 'Contains all financial and ledger related entities';
COMMENT ON SCHEMA user_acc IS 'Contains user management and security related entities';
COMMENT ON SCHEMA audit IS 'Audit logs for transactions and activities';

-- Update user_account table structure
CREATE TABLE user_acc.user_account (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(150) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    first_name VARCHAR(80) NOT NULL,
    last_name VARCHAR(80),
    status VARCHAR(75) NOT NULL DEFAULT 'DISABLED',
    due_to_expire_date TIMESTAMP,
    verification_pending_start_date TIMESTAMP NOT NULL,
    verification_pending_completion_date TIMESTAMP,
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_credentials_expired BOOLEAN DEFAULT FALSE,
    last_login_date TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT chk_status_enum_values CHECK (
        status IN (
                   'ACTIVE',
                   'LOCKED',
                   'DISABLED',
                   'CREDENTIAL_EXPIRED',
                   'VERIFICATION_PENDING',
                   'CLOSED',
                   'EXPIRED'
            )
        ),
    CONSTRAINT unq_user_acc_username UNIQUE (username),
    CONSTRAINT unq_user_acc_email UNIQUE (email),
    CONSTRAINT chk_user_acc_password_min_length CHECK (length(password) >= 8)
);
COMMENT ON TABLE user_acc.user_account IS 'Stores user account information and credentials';
COMMENT ON COLUMN user_acc.user_account.password IS 'Encrypted password hash';
COMMENT ON COLUMN user_acc.user_account.status IS 'Account status defaults to disabled state';


CREATE TABLE user_acc.security_role (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(70) NOT NULL,
    description VARCHAR(255) NOT NULL,
    CONSTRAINT unique_user_acc_security_role_name UNIQUE (name)
);
COMMENT ON TABLE user_acc.user_account IS 'Stores security context roles for application';


CREATE TABLE user_acc.user_security_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    assigned_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user
        foreign key (user_id) REFERENCES user_acc.user_account(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role
        foreign key (user_id) REFERENCES user_acc.security_role(id) ON DELETE CASCADE
);
COMMENT ON TABLE user_acc.user_account IS 'Stores user account security context roles for application';


CREATE TABLE ledger.ledger (
                               id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                               name VARCHAR(255) NOT NULL,
                               comment VARCHAR(500),
                               notes TEXT[],
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP,
                               created_by UUID NOT NULL,
                               default_currency VARCHAR(3),
                               CONSTRAINT uk_ledger_name UNIQUE (name),
                               CONSTRAINT chk_currency_enum_values CHECK ( default_currency IN ( 'GBP' ) )
);
COMMENT ON TABLE ledger.ledger IS 'Main ledger container for fiscal items';


CREATE TABLE ledger.fiscal_item (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    description VARCHAR(400),
    amount DECIMAL(19,2) NOT NULL,
    currency VARCHAR(3),
    status VARCHAR(75) NOT NULL DEFAULT 'DRAFT',
    ledger_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by UUID NOT NULL,
    is_expenditure BOOLEAN NOT NULL,
    CONSTRAINT fk_fiscal_item_ledger FOREIGN KEY ( ledger_id )
        REFERENCES ledger.ledger( id ) ON DELETE CASCADE,
    CONSTRAINT fk_ledge_fiscal_created_by FOREIGN KEY ( created_by )
        REFERENCES user_acc.user_account (id) ON DELETE CASCADE,
    CONSTRAINT chk_ledger_fiscal_amount_positive CHECK ( amount > 0 ),
    CONSTRAINT chk_ledger_fiscal_status
        CHECK ( status IN (
                           'ACTIVE',
                           'DRAFT',
                           'REMOVED'
            )
        ),
    CONSTRAINT chk_ledger_fiscal_currency_length CHECK ( length(currency) > 0 )
);
COMMENT ON TABLE ledger.fiscal_item IS 'Stores individual financial transactions';
COMMENT ON COLUMN ledger.fiscal_item.amount IS 'Positive decimal amount with up to 2 decimal places';

CREATE TYPE ledger.recurrence_frequency AS ENUM (
    'DAILY',
    'WEEKLY',
    'MONTHLY',
    'YEARLY'
    );
CREATE TABLE ledger.recurrence_rules (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    rule_name VARCHAR(100) NOT NULL,
    interval INTEGER NOT NULL,
    frequency ledger.recurrence_frequency NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT chk_recurrence_dates CHECK (end_date > start_date),
    CONSTRAINT chk_interval_positive CHECK (interval > 0),
    CONSTRAINT uk_rule_name UNIQUE (rule_name)
);
COMMENT ON TABLE ledger.recurrence_rules IS 'Defines recurring transaction patterns';

CREATE INDEX idx_user_username ON user_acc.user_account(username);
CREATE INDEX idx_user_email ON user_acc.user_account(email);
CREATE INDEX idx_user_status ON user_acc.user_account(status);
CREATE INDEX idx_user_verification_dates ON user_acc.user_account(verification_pending_start_date, verification_pending_completion_date);
CREATE INDEX idx_user_created_by ON ledger.ledger(created_by);
CREATE INDEX idx_fiscal_item_currency ON ledger.fiscal_item(currency);
CREATE INDEX idx_fiscal_item_status ON ledger.fiscal_item(status);
CREATE INDEX idx_fiscal_item_ledger ON ledger.fiscal_item(ledger_id);
CREATE INDEX idx_fiscal_item_created_by ON ledger.fiscal_item(created_by);
CREATE INDEX idx_fiscal_item_ ON ledger.fiscal_item(is_expenditure);
CREATE INDEX idx_recurrence_dates ON ledger.recurrence_rules(start_date, end_date);

-- Best Practice: Create audit triggers for updated_at columns
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Apply update trigger to all tables with updated_at column
CREATE TRIGGER update_user_account_timestamp
    BEFORE UPDATE ON user_acc.user_account
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_security_role_timestamp
    BEFORE UPDATE ON user_acc.security_role
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_ledger_timestamp
    BEFORE UPDATE ON ledger.ledger
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_fiscal_item_timestamp
    BEFORE UPDATE ON ledger.fiscal_item
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- CREATE TRIGGER update_fiscal_item_type_timestamp
--     BEFORE UPDATE ON ledger.fiscal_item_type
--     FOR EACH ROW
--     EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_recurrence_rules_timestamp
    BEFORE UPDATE ON ledger.recurrence_rules
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Add comments
COMMENT ON COLUMN user_acc.user_account.status IS 'Current status of the user account';
COMMENT ON COLUMN user_acc.user_account.verification_pending_start_date IS 'Date when verification process started';
COMMENT ON COLUMN user_acc.user_account.verification_pending_completion_date IS 'Date when verification process was completed';
COMMENT ON COLUMN user_acc.user_account.due_to_expire_date IS 'Date when the account is scheduled to expire';
COMMENT ON COLUMN ledger.fiscal_item.currency IS 'ISO 4217 currency code';
COMMENT ON COLUMN ledger.fiscal_item.status IS 'Current status of the fiscal item';
COMMENT ON COLUMN ledger.recurrence_rules.interval IS 'The frequency interval for the recurrence';

-- -- Add audit trigger for status changes
-- CREATE TABLE user_acc.user_status_history (
--     id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
--     user_id UUID NOT NULL,
--     old_status user_acc.account_status,
--     new_status user_acc.account_status NOT NULL,
--     changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     CONSTRAINT fk_status_history_user FOREIGN KEY (user_id)
--         REFERENCES user_acc.user_account(id) ON DELETE CASCADE
-- );

-- CREATE OR REPLACE FUNCTION track_user_status_changes()
-- RETURNS TRIGGER AS $$
-- BEGIN
--     IF (OLD.status IS NULL OR NEW.status != OLD.status) THEN
--         INSERT INTO user_acc.user_status_history (user_id, old_status, new_status)
--         VALUES (NEW.id, OLD.status, NEW.status);
--     END IF;
--     RETURN NEW;
-- END;
-- $$ LANGUAGE plpgsql;

-- CREATE TRIGGER user_status_audit
--     AFTER UPDATE OF status ON user_acc.user_account
--     FOR EACH ROW
--     EXECUTE FUNCTION track_user_status_changes();

-- Create validation functions
CREATE OR REPLACE FUNCTION validate_currency_code(code text)
RETURNS BOOLEAN AS $$
BEGIN
    RETURN code ~ '^[A-Z]{3}$';
END;
$$ LANGUAGE plpgsql;

-- Add domain for currency codes
CREATE DOMAIN currency_code AS text
    CONSTRAINT currency_code_check CHECK (validate_currency_code(VALUE));


COMMIT;