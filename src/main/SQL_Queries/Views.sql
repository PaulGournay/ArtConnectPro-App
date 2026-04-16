USE artconnect_db;

-- 1. Public V-Card for Active Artists
CREATE OR REPLACE VIEW view_artist_public_vcard AS
SELECT name, city, website, social_media 
FROM Artist 
WHERE isActive = 1;

-- 2. Member Directory
CREATE OR REPLACE VIEW view_member_directory AS
SELECT name, city, birth_year 
FROM CommunityMember;

-- 3. Artwork Catalog with Artist Name
CREATE OR REPLACE VIEW view_artwork_catalog_with_artist AS
SELECT a.title, a.type, a.price, a.status, art.name AS artist_name
FROM Artwork a
JOIN Artist art ON a.artist_id = art.artist_id;

-- 4. Workshop Details with Instructor
CREATE OR REPLACE VIEW view_workshop_details AS
SELECT w.title, w.date, w.price, w.level, art.name AS instructor_name
FROM Workshop w
JOIN Artist art ON w.artist_id = art.artist_id;

-- 5. Workshop Availability (calculating remaining seats)
CREATE OR REPLACE VIEW view_workshop_availability AS
SELECT w.workshopid, w.title, w.max_participants, 
       (w.max_participants - COUNT(b.booking_id)) AS seats_remaining
FROM Workshop w
LEFT JOIN Booking b ON w.workshopid = b.workshopid
GROUP BY w.workshopid;

-- 6. Top Rated Artworks (Average rating 4 or higher)
CREATE OR REPLACE VIEW view_top_rated_artworks AS
SELECT a.artwork_id, a.title, AVG(r.rating) AS average_rating, COUNT(r.review_id) AS review_count
FROM Artwork a
JOIN Review r ON a.artwork_id = r.artwork_id
GROUP BY a.artwork_id
HAVING average_rating >= 4;

-- 7. Artist Productivity (Counting works and workshops)
CREATE OR REPLACE VIEW view_artist_productivity AS
SELECT art.name, COUNT(DISTINCT awk.artwork_id) AS total_artworks, 
       COUNT(DISTINCT w.workshopid) AS total_workshops
FROM Artist art
LEFT JOIN Artwork awk ON art.artist_id = awk.artist_id
LEFT JOIN Workshop w ON art.artist_id = w.artist_id
GROUP BY art.artist_id;

-- 8. Currently Active Exhibitions
CREATE OR REPLACE VIEW view_active_exhibitions_now AS
SELECT * FROM view_full_exhibition_schedule
WHERE CURDATE() BETWEEN start_date AND end_date;