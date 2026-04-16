USE ArtGalleryDB;

-- 1. Index de recherche textuelle
-- Accélère les recherches de membres de la communauté ou d'artistes par leur nom
CREATE INDEX idx_artist_name ON Artist(name);
CREATE INDEX idx_artist_city ON Artist(city);

-- 2. Index d'intégrité et de performance
-- L'index UNIQUE garantit qu'un email ne peut pas être utilisé deux fois
CREATE UNIQUE INDEX idx_member_email ON CommunityMember(email);

-- 3. Index de filtrage et de tri (Composite)
-- Très utile pour les catalogues d'œuvres (ex: filtrer par 'Available' et trier par 'price')
CREATE INDEX idx_artwork_status_price ON Artwork(status, price);

-- 4. Index temporel
-- Optimise l'affichage des expositions actuelles ou futures
CREATE INDEX idx_exhibition_start_date ON Exhibition(start_date);

-- 5. Index sur les clés étrangères (Foreign Keys)
-- Bien que MySQL crée souvent des index automatiques pour les FK, 
-- les définir explicitement garantit des performances optimales lors des JOIN.
CREATE INDEX idx_booking_workshop_fk ON Booking(workshop_id);
CREATE INDEX idx_review_artwork_fk ON Review(artwork_id);