DECLARE max_id NUMBER;
BEGIN
    -- Get the current max recipe_id
    SELECT COALESCE(MAX(recipe_id), 0) + 1 INTO max_id FROM recipe;

    -- Drop the sequence if it exists (handle in PL/SQL to avoid errors)
    BEGIN
        EXECUTE IMMEDIATE 'DROP SEQUENCE recipe_id_seq';
    EXCEPTION
        WHEN OTHERS THEN
            NULL; -- Ignore if the sequence does not exist
    END;

    -- Create the sequence with the correct starting value
    EXECUTE IMMEDIATE 'CREATE SEQUENCE recipe_id_seq START WITH ' || max_id || ' INCREMENT BY 1 NOCACHE';
END;
/
