USE ArtGalleryDB;

-- =========================================================================
-- 1. TRIGGERS (Automatisation et intégrité)
-- =========================================================================

DROP TRIGGER IF EXISTS check_exhibition_dates_insert;
DROP TRIGGER IF EXISTS check_exhibition_dates_update;
DROP TRIGGER IF EXISTS check_workshop_capacity;

DELIMITER //

-- Vérification de la cohérence des dates lors d'un INSERT
CREATE TRIGGER check_exhibition_dates_insert
BEFORE INSERT ON Exhibition
FOR EACH ROW
BEGIN
    IF NEW.end_date < NEW.start_date THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Erreur : La date de fin de l''exposition ne peut pas précéder la date de début.';
    END IF;
END //

-- Vérification de la cohérence des dates lors d'un UPDATE
CREATE TRIGGER check_exhibition_dates_update
BEFORE UPDATE ON Exhibition
FOR EACH ROW
BEGIN
    IF NEW.end_date < NEW.start_date THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Erreur : La date de fin de l''exposition ne peut pas précéder la date de début.';
    END IF;
END //

-- Vérification de la capacité d'un atelier avant une réservation
CREATE TRIGGER check_workshop_capacity
BEFORE INSERT ON Booking
FOR EACH ROW
BEGIN
    DECLARE current_participants INT;
    DECLARE max_cap SMALLINT;
    
    -- Compte le nombre de réservations existantes pour cet atelier
    SELECT COUNT(*) INTO current_participants 
    FROM Booking 
    WHERE workshop_id = NEW.workshop_id;
    
    -- Récupère la capacité maximale de l'atelier
    SELECT max_participants INTO max_cap 
    FROM Workshop 
    WHERE workshop_id = NEW.workshop_id;
    
    -- Si la capacité est atteinte ou dépassée, on bloque l'insertion
    IF current_participants >= max_cap THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Erreur : Cet atelier a déjà atteint sa capacité maximale.';
    END IF;
END //

DELIMITER ;

-- =========================================================================
-- 2. STORED PROCEDURES & FUNCTIONS (Opérations courantes)
-- =========================================================================

DROP FUNCTION IF EXISTS get_workshop_participants_count;
DROP PROCEDURE IF EXISTS create_workshop_with_artist;

DELIMITER //

-- Fonction : Retourner le nombre de participants actuels pour un atelier donné
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

-- Procédure : Créer un atelier (Workshop) et enregistrer automatiquement l'artiste s'il n'existe pas
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
    
    -- Cherche si l'artiste existe déjà via son email
    SELECT artist_id INTO v_artist_id 
    FROM Artist 
    WHERE contact_email = p_artist_email LIMIT 1;
    
    -- Si l'artiste n'existe pas, on le crée
    IF v_artist_id IS NULL THEN
        INSERT INTO Artist (name, contact_email, isActive) 
        VALUES (p_artist_name, p_artist_email, TRUE);
        
        -- On récupère l'ID du nouvel artiste inséré
        SET v_artist_id = LAST_INSERT_ID();
    END IF;
    
    -- Création de l'atelier rattaché à l'artiste (existant ou nouveau)
    INSERT INTO Workshop (title, date, max_participants, price, artist_id)
    VALUES (p_workshop_title, p_workshop_date, p_max_participants, p_price, v_artist_id);
    
END //

DELIMITER ;