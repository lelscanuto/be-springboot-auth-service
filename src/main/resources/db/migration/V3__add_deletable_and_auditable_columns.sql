-- Step 1: Add audit and soft-delete columns (initially allowing nulls)
ALTER TABLE public.user_account
    ADD COLUMN created_at timestamptz(6) DEFAULT NOW(),
    ADD COLUMN updated_at timestamptz(6),
    ADD COLUMN created_by varchar(255),
    ADD COLUMN updated_by varchar(255),
    ADD COLUMN is_deleted boolean DEFAULT false;

ALTER TABLE public.role
    ADD COLUMN created_at timestamptz(6) DEFAULT NOW(),
    ADD COLUMN updated_at timestamptz(6),
    ADD COLUMN created_by varchar(255),
    ADD COLUMN updated_by varchar(255),
    ADD COLUMN is_deleted boolean DEFAULT false;

ALTER TABLE public.permission
    ADD COLUMN created_at timestamptz(6) DEFAULT NOW(),
    ADD COLUMN updated_at timestamptz(6),
    ADD COLUMN created_by varchar(255),
    ADD COLUMN updated_by varchar(255),
    ADD COLUMN is_deleted boolean DEFAULT false;

-- Step 2: Initialize audit fields for existing data
UPDATE public.role
SET
    created_at = NOW(),
    updated_at = NOW(),
    created_by = 'system',
    updated_by = 'system',
    is_deleted = false
WHERE created_at IS NULL;

UPDATE public.permission
SET
    created_at = NOW(),
    updated_at = NOW(),
    created_by = 'system',
    updated_by = 'system',
    is_deleted = false
WHERE created_at IS NULL;

UPDATE public.user_account
SET
    created_at = NOW(),
    updated_at = NOW(),
    created_by = 'system',
    updated_by = 'system',
    is_deleted = false
WHERE created_at IS NULL;

-- Step 3: Enforce non-null constraints now that all data is populated
ALTER TABLE public.user_account
    ALTER COLUMN created_at SET NOT NULL,
    ALTER COLUMN is_deleted SET NOT NULL;

ALTER TABLE public.role
    ALTER COLUMN created_at SET NOT NULL,
    ALTER COLUMN is_deleted SET NOT NULL;

ALTER TABLE public.permission
    ALTER COLUMN created_at SET NOT NULL,
    ALTER COLUMN is_deleted SET NOT NULL;
