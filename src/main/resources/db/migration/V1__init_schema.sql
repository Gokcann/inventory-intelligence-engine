-- Project Antigravity - Initial Schema Migration
-- V1: Base schema with placeholder for Flyway validation

-- Enable required extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Placeholder table to allow Flyway validation
-- Real tables will be added per module (inventory, order)
CREATE TABLE IF NOT EXISTS flyway_placeholder (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
