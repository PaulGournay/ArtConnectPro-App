USE ArtGalleryDB;

-- 1. Public V-Card for Active Artists
-- Affiche les infos de contact uniquement pour les artistes marqués comme actifs.
CREATE OR REPLACE VIEW view_artist_public_vcard AS
SELECT 
    name, 
    city, 
    website, 
    social_media 
FROM Artist 
WHERE isActive = 1;

-- 2. Member Directory
-- Liste simple des membres de la communauté.
CREATE OR REPLACE VIEW view_member_directory AS
SELECT 
    name, 
    city, 
    birth_year 
FROM CommunityMember;

-- 3. Artwork Catalog with Artist Name
-- Jointure pour afficher le nom de l'artiste au lieu de son ID.
CREATE OR REPLACE VIEW view_artwork_catalog_with_artist AS
SELECT 
    a.title, 
    a.type, 
    a.price, 
    a.status, 
    art.name AS artist_name
FROM Artwork a
JOIN Artist art ON a.artist_id = art.artist_id;

-- 4. Workshop Details with Instructor
-- Détails des ateliers avec le nom de l'instructeur (artiste).
CREATE OR REPLACE VIEW view_workshop_details AS
SELECT 
    w.title, 
    w.date, 
    w.price, 
    w.level, 
    art.name AS instructor_name
FROM Workshop w
JOIN Artist art ON w.artist_id = art.artist_id;

-- 5. Workshop Availability (calculating remaining seats)
-- Correction : Changement de workshopid par workshop_id pour correspondre au schéma.
CREATE OR REPLACE VIEW view_workshop_availability AS
SELECT 
    w.workshop_id, 
    w.title, 
    w.max_participants, 
    (w.max_participants - COUNT(b.booking_id)) AS seats_remaining
FROM Workshop w
LEFT JOIN Booking b ON w.workshop_id = b.workshop_id
GROUP BY w.workshop_id, w.title, w.max_participants;

-- 6. Top Rated Artworks (Average rating 4 or higher)
-- Filtre les œuvres ayant une moyenne de notes supérieure ou égale à 4.
CREATE OR REPLACE VIEW view_top_rated_artworks AS
SELECT 
    a.artwork_id, 
    a.title, 
    AVG(r.rating) AS average_rating, 
    COUNT(r.review_id) AS review_count
FROM Artwork a
JOIN Review r ON a.artwork_id = r.artwork_id
GROUP BY a.artwork_id, a.title
HAVING average_rating >= 4;

-- 7. Artist Productivity (Counting works and workshops)
-- Correction : workshopid remplacé par workshop_id.
-- Utilisation de DISTINCT pour éviter les doublons lors des jointures multiples.
CREATE OR REPLACE VIEW view_artist_productivity AS
SELECT 
    art.name, 
    COUNT(DISTINCT awk.artwork_id) AS total_artworks, 
    COUNT(DISTINCT w.workshop_id) AS total_workshops
FROM Artist art
LEFT JOIN Artwork awk ON art.artist_id = awk.artist_id
LEFT JOIN Workshop w ON art.artist_id = w.artist_id
GROUP BY art.artist_id, art.name;

-- 8. Currently Active Exhibitions
-- Correction : Création d'une vue basée directement sur Exhibition car 'view_full_exhibition_schedule' n'existait pas.
CREATE OR REPLACE VIEW view_active_exhibitions_now AS
SELECT 
    exhibition_id, 
    title, 
    start_date, 
    end_date, 
    theme, 
    curator_name
FROM Exhibition
WHERE CURDATE() BETWEEN start_date AND end_date;