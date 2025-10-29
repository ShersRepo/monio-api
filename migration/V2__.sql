CREATE TABLE user_acc.security_role
(
    id          UUID         NOT NULL,
    name        VARCHAR(70)  NOT NULL,
    description VARCHAR(255) NOT NULL,
    CONSTRAINT pk_security_role PRIMARY KEY (id)
);

CREATE TABLE user_acc.user_account
(
    id                                   UUID                        NOT NULL,
    username                             VARCHAR(150)                NOT NULL,
    password                             VARCHAR(255)                NOT NULL,
    email                                VARCHAR(255)                NOT NULL,
    first_name                           VARCHAR(80)                 NOT NULL,
    last_name                            VARCHAR(80),
    due_to_expire_date                   TIMESTAMP WITHOUT TIME ZONE,
    verification_pending_start_date      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    verification_pending_completion_date TIMESTAMP WITHOUT TIME ZONE,
    created_date                         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_credentials_expired               BOOLEAN,
    last_login_date                      TIMESTAMP WITHOUT TIME ZONE,
    status                               VARCHAR(75)                 NOT NULL,
    CONSTRAINT pk_user_account PRIMARY KEY (id)
);

CREATE TABLE user_acc.user_security_roles
(
    role_id UUID NOT NULL,
    user_id UUID NOT NULL,
    CONSTRAINT pk_user_security_roles PRIMARY KEY (role_id, user_id)
);

ALTER TABLE user_acc.security_role
    ADD CONSTRAINT uc_security_role_name UNIQUE (name);

ALTER TABLE user_acc.user_account
    ADD CONSTRAINT uc_user_account_email UNIQUE (email);

ALTER TABLE user_acc.user_account
    ADD CONSTRAINT uc_user_account_username UNIQUE (username);

ALTER TABLE user_acc.user_security_roles
    ADD CONSTRAINT fk_usesecrol_on_security_role FOREIGN KEY (role_id) REFERENCES user_acc.security_role (id);

ALTER TABLE user_acc.user_security_roles
    ADD CONSTRAINT fk_usesecrol_on_user_account FOREIGN KEY (user_id) REFERENCES user_acc.user_account (id);

CREATE TABLE ledger.fiscal_item
(
    id                 UUID           NOT NULL,
    name               VARCHAR(100)   NOT NULL,
    description        VARCHAR(400),
    amount             DECIMAL(19, 2) NOT NULL,
    currency           VARCHAR(3),
    status             VARCHAR(75)    NOT NULL,
    is_expenditure     BOOLEAN        NOT NULL,
    start_date         BYTEA,
    end_date           BYTEA,
    recurrence_rule_id UUID,
    ledger_id          UUID           NOT NULL,
    created_by         UUID           NOT NULL,
    CONSTRAINT pk_fiscal_item PRIMARY KEY (id)
);

CREATE TABLE ledger.fiscal_recurrence_rule
(
    id                   UUID NOT NULL,
    rule                 VARCHAR(255),
    timezone             VARCHAR(255),
    shift_after_weekend  BOOLEAN,
    shift_before_weekend BOOLEAN,
    CONSTRAINT pk_fiscal_recurrence_rule PRIMARY KEY (id)
);

CREATE TABLE ledger.ledger
(
    id               UUID         NOT NULL,
    name             VARCHAR(255) NOT NULL,
    comment          VARCHAR(500),
    notes            TEXT[],
    created_by       UUID         NOT NULL,
    default_currency VARCHAR(3),
    CONSTRAINT pk_ledger PRIMARY KEY (id)
);

ALTER TABLE ledger.fiscal_item
    ADD CONSTRAINT uc_fiscal_item_recurrence_rule UNIQUE (recurrence_rule_id);

ALTER TABLE ledger.fiscal_item
    ADD CONSTRAINT FK_FISCAL_ITEM_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES user_acc.user_account (id);

ALTER TABLE ledger.fiscal_item
    ADD CONSTRAINT FK_FISCAL_ITEM_ON_LEDGER FOREIGN KEY (ledger_id) REFERENCES ledger.ledger (id);

ALTER TABLE ledger.fiscal_item
    ADD CONSTRAINT FK_FISCAL_ITEM_ON_RECURRENCE_RULE FOREIGN KEY (recurrence_rule_id) REFERENCES ledger.fiscal_recurrence_rule (id);