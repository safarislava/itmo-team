--liquibase formatted sql
--changeset itmo-team:06-application-reservation

CREATE TABLE application_reservation (
    application_id UUID PRIMARY KEY REFERENCES application(application_id) ON DELETE CASCADE,
    hr_id UUID NOT NULL REFERENCES person(person_id) ON DELETE CASCADE,
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_application_reservation_expires_at ON application_reservation(expires_at);

CREATE OR REPLACE FUNCTION trg_clear_application_reservation()
RETURNS TRIGGER AS $$
BEGIN
    DELETE FROM application_reservation WHERE application_id = NEW.application_id;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_delete_application_reservation_on_response
    AFTER INSERT ON application_response
    FOR EACH ROW
EXECUTE FUNCTION trg_clear_application_reservation();
