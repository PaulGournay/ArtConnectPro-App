-- Use the previously defined database
USE ArtGalleryDB;

-- 1. User creation
CREATE USER IF NOT EXISTS 'artconnect_user'@'localhost' IDENTIFIED BY 'ArtSecure_2026!';

-- 2. Grant rights on main tables (Read/Write)
GRANT SELECT, INSERT, UPDATE ON Artist TO 'artconnect_user'@'localhost';
GRANT SELECT, INSERT, UPDATE ON Artwork TO 'artconnect_user'@'localhost';
GRANT SELECT, INSERT, UPDATE ON CommunityMember TO 'artconnect_user'@'localhost';

-- 3. Specific deletion rights (Moderation)
GRANT DELETE ON Review TO 'artconnect_user'@'localhost';
GRANT DELETE ON Booking TO 'artconnect_user'@'localhost';

-- 4. Access to views
-- Note: 'view_artwork_catalog_with_artist' replaces 'view_artwork_inventory_summary' 
-- to match exactly the views created in the previous step.
GRANT SELECT ON view_artwork_catalog_with_artist TO 'artconnect_user'@'localhost';
GRANT SELECT ON view_artist_productivity TO 'artconnect_user'@'localhost';

-- 5. Stored procedures execution
-- Limit execution to ArtGalleryDB database procedures only
GRANT EXECUTE ON ArtGalleryDB.* TO 'artconnect_user'@'localhost';

-- 6. Security: Revoke critical rights
-- Ensure the user cannot modify the structure or drop the DB
REVOKE DROP, ALTER, GRANT OPTION ON ArtGalleryDB.* FROM 'artconnect_user'@'localhost';

-- 7. Apply changes
FLUSH PRIVILEGES;