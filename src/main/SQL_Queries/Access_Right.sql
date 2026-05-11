-- Use the previously defined database
USE ArtGalleryDB;

-- 1. User creation
CREATE USER IF NOT EXISTS 'artconnect_user'@'localhost' IDENTIFIED BY 'ArtSecure_2026!';

-- 2. Grant rights on main tables (Full CRUD for app entities)
GRANT SELECT, INSERT, UPDATE, DELETE ON ArtGalleryDB.Artist TO 'artconnect_user'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON ArtGalleryDB.Artwork TO 'artconnect_user'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON ArtGalleryDB.CommunityMember TO 'artconnect_user'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON ArtGalleryDB.Exhibition TO 'artconnect_user'@'localhost';
GRANT SELECT ON ArtGalleryDB.Gallery TO 'artconnect_user'@'localhost';
GRANT SELECT ON ArtGalleryDB.Workshop TO 'artconnect_user'@'localhost';
GRANT SELECT ON ArtGalleryDB.Discipline TO 'artconnect_user'@'localhost';
GRANT SELECT ON ArtGalleryDB.Tag TO 'artconnect_user'@'localhost';

-- 3. Specific deletion rights (Moderation)
GRANT DELETE ON ArtGalleryDB.Review TO 'artconnect_user'@'localhost';
GRANT DELETE ON ArtGalleryDB.Booking TO 'artconnect_user'@'localhost';

-- 3b. Join tables access
GRANT SELECT ON ArtGalleryDB.practices TO 'artconnect_user'@'localhost';
GRANT SELECT ON ArtGalleryDB.tagged TO 'artconnect_user'@'localhost';
GRANT SELECT ON ArtGalleryDB.features TO 'artconnect_user'@'localhost';
GRANT SELECT ON ArtGalleryDB.Favors TO 'artconnect_user'@'localhost';

-- 4. Access to views
GRANT SELECT ON ArtGalleryDB.view_artwork_catalog_with_artist TO 'artconnect_user'@'localhost';
GRANT SELECT ON ArtGalleryDB.view_artist_productivity TO 'artconnect_user'@'localhost';
GRANT SELECT ON ArtGalleryDB.view_artist_public_vcard TO 'artconnect_user'@'localhost';
GRANT SELECT ON ArtGalleryDB.view_member_directory TO 'artconnect_user'@'localhost';
GRANT SELECT ON ArtGalleryDB.view_workshop_details TO 'artconnect_user'@'localhost';
GRANT SELECT ON ArtGalleryDB.view_workshop_availability TO 'artconnect_user'@'localhost';
GRANT SELECT ON ArtGalleryDB.view_top_rated_artworks TO 'artconnect_user'@'localhost';
GRANT SELECT ON ArtGalleryDB.view_active_exhibitions_now TO 'artconnect_user'@'localhost';

-- 5. Stored procedures execution
-- Limit execution to ArtGalleryDB database procedures only
GRANT EXECUTE ON ArtGalleryDB.* TO 'artconnect_user'@'localhost';

