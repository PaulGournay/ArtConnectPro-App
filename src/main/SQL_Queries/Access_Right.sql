
CREATE USER IF NOT EXISTS 'artconnect_user'@'localhost' IDENTIFIED BY 'ArtSecure_2026!';

GRANT SELECT, INSERT, UPDATE ON Artist TO 'artconnect_user'@'localhost';
GRANT SELECT, INSERT, UPDATE ON Artwork TO 'artconnect_user'@'localhost';
GRANT SELECT, INSERT, UPDATE ON CommunityMember TO 'artconnect_user'@'localhost';

GRANT DELETE ON Review TO 'artconnect_user'@'localhost';
GRANT DELETE ON Booking TO 'artconnect_user'@'localhost';

GRANT SELECT ON view_artwork_inventory_summary TO 'artconnect_user'@'localhost';
GRANT SELECT ON view_artist_productivity TO 'artconnect_user'@'localhost';

GRANT EXECUTE ON PROCEDURE * TO 'artconnect_user'@'localhost';

REVOKE DROP, ALTER, GRANT OPTION ON *.* FROM 'artconnect_user'@'localhost';

FLUSH PRIVILEGES;