-- V3: Spring Modulith Event Publication table
-- Required for Transactional Outbox pattern

CREATE TABLE IF NOT EXISTS event_publication (
    id UUID NOT NULL PRIMARY KEY,
    listener_id VARCHAR(512) NOT NULL,
    event_type VARCHAR(512) NOT NULL,
    serialized_event TEXT NOT NULL,
    publication_date TIMESTAMPTZ NOT NULL,
    completion_date TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_event_publication_incomplete 
    ON event_publication (completion_date) WHERE completion_date IS NULL;

CREATE INDEX IF NOT EXISTS idx_event_publication_date 
    ON event_publication (publication_date);
