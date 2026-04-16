CREATE VIEW view_artist_public_vcard AS
SELECT name, bio, city, website, social_media 
FROM Artist 
WHERE isActive = 1;

CREATE VIEW view_member_directory AS
SELECT name, city, birth_year 
FROM CommunityMember;

CREATE VIEW view_member_directory AS
SELECT name, city, birth_year 
FROM CommunityMember;

CREATE VIEW view_artwork_catalog_with_artist AS
SELECT a.title, a.type, a.price, a.status, art.name AS artist_name
FROM Artwork a
JOIN Artist art ON a.artist_id = art.artist_id;

CREATE VIEW view_workshop_details AS
SELECT w.title, w.date_, w.price, w.level, art.name AS instructor_name
FROM Workshop w
JOIN Artist art ON w.artist_id = art.artist_id;

CREATE VIEW view_workshop_availability AS
SELECT w.workshop_id, w.title, w.max_participants, 
       (w.max_participants - COUNT(b.booking_id)) AS seats_remaining
FROM Workshop w
LEFT JOIN Booking b ON w.workshop_id = b.workshop_id
GROUP BY w.workshop_id;

CREATE VIEW view_top_rated_artworks AS
SELECT a.artwork_id, a.title, AVG(r.rating) AS average_rating, COUNT(r.review_id) AS review_count
FROM Artwork a
JOIN Review r ON a.artwork_id = r.artwork_id
GROUP BY a.artwork_id
HAVING average_rating >= 4;

CREATE VIEW view_artist_productivity AS
SELECT art.name, COUNT(DISTINCT awk.artwork_id) AS total_artworks, 
       COUNT(DISTINCT w.workshop_id) AS total_workshops
FROM Artist art
LEFT JOIN Artwork awk ON art.artist_id = awk.artist_id
LEFT JOIN Workshop w ON art.artist_id = w.artist_id
GROUP BY art.artist_id;

CREATE VIEW view_active_exhibitions_now AS
SELECT * FROM view_full_exhibition_schedule
WHERE CURDATE() BETWEEN start_date AND end_date;