-- Utilisation de la base de données définie précédemment
USE ArtGalleryDB;

-- 1. Création de l'utilisateur
CREATE USER IF NOT EXISTS 'artconnect_user'@'localhost' IDENTIFIED BY 'ArtSecure_2026!';

-- 2. Attribution des droits sur les tables principales (Lecture/Écriture)
GRANT SELECT, INSERT, UPDATE ON Artist TO 'artconnect_user'@'localhost';
GRANT SELECT, INSERT, UPDATE ON Artwork TO 'artconnect_user'@'localhost';
GRANT SELECT, INSERT, UPDATE ON CommunityMember TO 'artconnect_user'@'localhost';

-- 3. Droits spécifiques de suppression (Modération)
GRANT DELETE ON Review TO 'artconnect_user'@'localhost';
GRANT DELETE ON Booking TO 'artconnect_user'@'localhost';

-- 4. Accès aux vues
-- Note : 'view_artwork_catalog_with_artist' remplace 'view_artwork_inventory_summary' 
-- pour correspondre exactement aux vues créées à l'étape précédente.
GRANT SELECT ON view_artwork_catalog_with_artist TO 'artconnect_user'@'localhost';
GRANT SELECT ON view_artist_productivity TO 'artconnect_user'@'localhost';

-- 5. Exécution des procédures stockées
-- On limite l'exécution aux procédures de la base ArtGalleryDB uniquement
GRANT EXECUTE ON ArtGalleryDB.* TO 'artconnect_user'@'localhost';

-- 6. Sécurité : Révocation des droits critiques
-- On s'assure que l'utilisateur ne peut pas modifier la structure ou supprimer la DB
REVOKE DROP, ALTER, GRANT OPTION ON ArtGalleryDB.* FROM 'artconnect_user'@'localhost';

-- 7. Application des changements
FLUSH PRIVILEGES;