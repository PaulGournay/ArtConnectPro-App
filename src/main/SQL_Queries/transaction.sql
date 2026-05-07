USE ArtGalleryDB;

-- =========================================================================
-- 3. TRANSACTIONS (Scénario complexe atomique)
-- =========================================================================

DROP PROCEDURE IF EXISTS book_multiple_workshops_transaction;

DELIMITER //

-- Procédure Transactionnelle : Réserver plusieurs ateliers en même temps pour un membre
-- Atomicité : Soit le membre est inscrit à TOUS les ateliers, soit la transaction est annulée (ROLLBACK).
CREATE PROCEDURE book_multiple_workshops_transaction(
    IN p_user_id INT,
    IN p_workshop_id_1 INT,
    IN p_workshop_id_2 INT
)
BEGIN
    -- Déclaration d'un gestionnaire d'erreurs (Si une erreur survient, ex: trigger capacité pleine)
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        -- On annule tout ce qui a été fait dans la transaction
        ROLLBACK;
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Transaction annulée : Impossible de valider toutes les réservations (vérifiez les capacités des ateliers).';
    END;

    -- Début de la transaction
    START TRANSACTION;

    -- Première réservation
    INSERT INTO Booking (booking_date, payment_status, workshop_id, user_id)
    VALUES (NOW(), 'Pending', p_workshop_id_1, p_user_id);

    -- Deuxième réservation
    -- Si ce workshop est plein, le trigger "check_workshop_capacity" va lancer une exception.
    -- L'exception sera captée par le EXIT HANDLER ci-dessus, et la première réservation sera annulée.
    INSERT INTO Booking (booking_date, payment_status, workshop_id, user_id)
    VALUES (NOW(), 'Pending', p_workshop_id_2, p_user_id);

    -- Si tout s'est bien passé, on valide les changements définitivement
    COMMIT;
    
END //

DELIMITER ;