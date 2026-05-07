USE ArtGalleryDB;

-- =========================================================================
-- 3. TRANSACTIONS (Atomic complex scenario)
-- =========================================================================

DROP PROCEDURE IF EXISTS book_multiple_workshops_transaction;

DELIMITER //

-- Transactional Procedure: Book multiple workshops at the same time for a member
-- Atomicity: Either the member is registered for ALL workshops, or the transaction is canceled (ROLLBACK).
CREATE PROCEDURE book_multiple_workshops_transaction(
    IN p_user_id INT,
    IN p_workshop_id_1 INT,
    IN p_workshop_id_2 INT
)
BEGIN
    -- Declaration of an error handler (If an error occurs, e.g., full capacity trigger)
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        -- Rollback everything done in the transaction
        ROLLBACK;
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Transaction canceled: Cannot validate all bookings (check workshop capacities).';
    END;

    -- Start of transaction
    START TRANSACTION;

    -- First booking
    INSERT INTO Booking (booking_date, payment_status, workshop_id, user_id)
    VALUES (NOW(), 'Pending', p_workshop_id_1, p_user_id);

    -- Second booking
    -- If this workshop is full, the "check_workshop_capacity" trigger will throw an exception.
    -- The exception will be caught by the EXIT HANDLER above, and the first booking will be rolled back.
    INSERT INTO Booking (booking_date, payment_status, workshop_id, user_id)
    VALUES (NOW(), 'Pending', p_workshop_id_2, p_user_id);

    -- If everything went well, permanently commit the changes
    COMMIT;
    
END //

DELIMITER ;