CREATE SEQUENCE association_value_entry_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE domain_event_entry_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE association_value_entry
(
    id                BIGINT       NOT NULL,
    association_key   VARCHAR(255) NOT NULL,
    association_value VARCHAR(255),
    saga_id           VARCHAR(255) NOT NULL,
    saga_type         VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE dead_letter_entry
(
    enqueued_at          TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    last_touched         TIMESTAMP(6) WITH TIME ZONE,
    processing_started   TIMESTAMP(6) WITH TIME ZONE,
    sequence_index       BIGINT                      NOT NULL,
    sequence_number      BIGINT,
    cause_message        VARCHAR(1023),
    aggregate_identifier VARCHAR(255),
    cause_type           VARCHAR(255),
    dead_letter_id       VARCHAR(255)                NOT NULL,
    event_identifier     VARCHAR(255)                NOT NULL,
    message_type         VARCHAR(255)                NOT NULL,
    payload_revision     VARCHAR(255),
    payload_type         VARCHAR(255)                NOT NULL,
    processing_group     VARCHAR(255)                NOT NULL,
    sequence_identifier  VARCHAR(255)                NOT NULL,
    time_stamp           VARCHAR(255)                NOT NULL,
    token_type           VARCHAR(255),
    type                 VARCHAR(255),
    diagnostics          OID,
    meta_data            OID,
    payload              OID                         NOT NULL,
    token                OID,
    PRIMARY KEY (dead_letter_id),
    UNIQUE (processing_group, sequence_identifier, sequence_index)
);

CREATE TABLE domain_event_entry
(
    global_index         BIGINT       NOT NULL,
    sequence_number      BIGINT       NOT NULL,
    aggregate_identifier VARCHAR(255) NOT NULL,
    event_identifier     VARCHAR(255) NOT NULL UNIQUE,
    payload_revision     VARCHAR(255),
    payload_type         VARCHAR(255) NOT NULL,
    time_stamp           VARCHAR(255) NOT NULL,
    type                 VARCHAR(255),
    meta_data            OID,
    payload              OID          NOT NULL,
    PRIMARY KEY (global_index),
    UNIQUE (aggregate_identifier, sequence_number)
);

CREATE TABLE saga_entry
(
    revision        VARCHAR(255),
    saga_id         VARCHAR(255) NOT NULL,
    saga_type       VARCHAR(255),
    serialized_saga OID,
    PRIMARY KEY (saga_id)
);

CREATE TABLE snapshot_event_entry
(
    sequence_number      BIGINT       NOT NULL,
    aggregate_identifier VARCHAR(255) NOT NULL,
    event_identifier     VARCHAR(255) NOT NULL UNIQUE,
    payload_revision     VARCHAR(255),
    payload_type         VARCHAR(255) NOT NULL,
    time_stamp           VARCHAR(255) NOT NULL,
    type                 VARCHAR(255) NOT NULL,
    meta_data            OID,
    payload              OID          NOT NULL,
    PRIMARY KEY (sequence_number, aggregate_identifier, type)
);

CREATE TABLE token_entry
(
    segment        INTEGER      NOT NULL,
    owner          VARCHAR(255),
    processor_name VARCHAR(255) NOT NULL,
    timestamp      VARCHAR(255) NOT NULL,
    token_type     VARCHAR(255),
    token          OID,
    PRIMARY KEY (segment, processor_name)
);

CREATE INDEX idx_association_value_entry_kv ON association_value_entry (saga_type, association_key, association_value);
CREATE INDEX idx_association_value_entry_type ON association_value_entry (saga_id, saga_type);
CREATE INDEX idx_dead_letter_entry_group ON dead_letter_entry (processing_group);
CREATE INDEX idx_dead_letter_entry_id ON dead_letter_entry (processing_group, sequence_identifier);
