
CREATE INDEX idx_artist_name ON Artist(name);

CREATE INDEX idx_artwork_status_price ON Artwork(status, price);

CREATE INDEX idx_exhibition_start_date ON Exhibition(start_date);

CREATE UNIQUE INDEX idx_member_email ON CommunityMember(email);

CREATE INDEX idx_booking_workshop_fk ON Booking(workshop_id);

CREATE INDEX idx_review_artwork_fk ON Review(artwork_id);

CREATE INDEX idx_artist_city ON Artist(city);