USE ArtGalleryDB;

-- 1. Text search index
-- Speeds up community member or artist searches by name
CREATE INDEX idx_artist_name ON Artist(name);
CREATE INDEX idx_artist_city ON Artist(city);

-- 2. Integrity and performance index
-- The UNIQUE index ensures an email cannot be used twice
CREATE UNIQUE INDEX idx_member_email ON CommunityMember(email);

-- 3. Filtering and sorting index (Composite)
-- Very useful for artwork catalogs (e.g., filter by 'Available' and sort by 'price')
CREATE INDEX idx_artwork_status_price ON Artwork(status, price);

-- 4. Temporal index
-- Optimizes the display of current or future exhibitions
CREATE INDEX idx_exhibition_start_date ON Exhibition(start_date);

-- 5. Foreign Key indexes
-- Although MySQL often creates automatic indexes for FKs, 
-- defining them explicitly ensures optimal performance during JOINs.
CREATE INDEX idx_booking_workshop_fk ON Booking(workshop_id);
CREATE INDEX idx_review_artwork_fk ON Review(artwork_id);