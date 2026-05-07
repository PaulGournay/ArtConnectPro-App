USE ArtGalleryDB;

-- =========================================================================
-- 1. TRIGGERS (Automation and integrity)
-- =========================================================================

DROP TRIGGER IF EXISTS check_exhibition_dates_insert;
DROP TRIGGER IF EXISTS check_exhibition_dates_update;
DROP TRIGGER IF EXISTS check_workshop_capacity;

DELIMITER //

-- Verify date consistency during an INSERT
CREATE TRIGGER check_exhibition_dates_insert
BEFORE INSERT ON Exhibition
FOR EACH ROW
BEGIN
    IF NEW.end_date < NEW.start_date THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Error: The exhibition end date cannot precede the start date.';
    END IF;
END //

-- Verify date consistency during an UPDATE
CREATE TRIGGER check_exhibition_dates_update
BEFORE UPDATE ON Exhibition
FOR EACH ROW
BEGIN
    IF NEW.end_date < NEW.start_date THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Error: The exhibition end date cannot precede the start date.';
    END IF;
END //

-- Verify workshop capacity before booking
CREATE TRIGGER check_workshop_capacity
BEFORE INSERT ON Booking
FOR EACH ROW
BEGIN
    DECLARE current_participants INT;
    DECLARE max_cap SMALLINT;
    
    -- Count the number of existing bookings for this workshop
    SELECT COUNT(*) INTO current_participants 
    FROM Booking 
    WHERE workshop_id = NEW.workshop_id;
    
    -- Retrieve the maximum capacity of the workshop
    SELECT max_participants INTO max_cap 
    FROM Workshop 
    WHERE workshop_id = NEW.workshop_id;
    
    -- If capacity is reached or exceeded, block the insertion
    IF current_participants >= max_cap THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Error: This workshop has already reached its maximum capacity.';
    END IF;
END //

DELIMITER ;

-- =========================================================================
-- 2. STORED PROCEDURES & FUNCTIONS (Common operations)
-- =========================================================================

DROP FUNCTION IF EXISTS get_workshop_participants_count;
DROP PROCEDURE IF EXISTS create_workshop_with_artist;

DELIMITER //

-- Function: Return the current number of participants for a given workshop
CREATE FUNCTION get_workshop_participants_count(p_workshop_id INT)
RETURNS INT
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE participant_count INT;
    
    SELECT COUNT(*) INTO participant_count
    FROM Booking
    WHERE workshop_id = p_workshop_id;
    
    RETURN participant_count;
END //

-- Procedure: Create a workshop and automatically register the artist if they do not exist
CREATE PROCEDURE create_workshop_with_artist(
    IN p_artist_name VARCHAR(50),
    IN p_artist_email VARCHAR(50),
    IN p_workshop_title VARCHAR(50),
    IN p_workshop_date DATE,
    IN p_max_participants SMALLINT,
    IN p_price DECIMAL(15,2)
)
BEGIN
    DECLARE v_artist_id INT;
    
    -- Check if the artist already exists via their email
    SELECT artist_id INTO v_artist_id 
    FROM Artist 
    WHERE contact_email = p_artist_email LIMIT 1;
    
    -- If the artist does not exist, create them
    IF v_artist_id IS NULL THEN
        INSERT INTO Artist (name, contact_email, isActive) 
        VALUES (p_artist_name, p_artist_email, TRUE);
        
        -- Retrieve the ID of the newly inserted artist
        SET v_artist_id = LAST_INSERT_ID();
    END IF;
    
    -- Create the workshop linked to the artist (existing or new)
    INSERT INTO Workshop (title, date, max_participants, price, artist_id)
    VALUES (p_workshop_title, p_workshop_date, p_max_participants, p_price, v_artist_id);
    
END //

DELIMITER ;