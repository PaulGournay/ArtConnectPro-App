USE artconnect_db;

-- ====================================================================
-- 1. BASE TABLES
-- ====================================================================

-- Insert Galleries
INSERT INTO Gallery (gallery_id, name, address, owner_name, opening_hours, contact_phone, rating, website) VALUES
(1, 'Louvre Art House', 'Rue de Rivoli, Paris', 'Jean-Luc Picard', '09:00-18:00', '+3312345678', '4.9', 'www.louvrehouse.fr'),
(2, 'The British Gallery', 'Great Russell St, London', 'Arthur Pendragon', '10:00-17:30', '+44207123456', '4.7', 'www.britishgallery.co.uk'),
(3, 'Metropolitan Hub', '1000 5th Ave, New York', 'Elena Rossi', '10:00-17:00', '+12125550198', '4.8', 'www.methub.org'),
(4, 'Tokyo Digital Canvas', 'Shibuya, Tokyo', 'Kenji Sato', '11:00-20:00', '+8135550123', '4.6', 'www.tokyocanvas.jp'),
(5, 'Berlin Underground', 'Mitte, Berlin', 'Hans Muller', '12:00-22:00', '+49305550987', '4.5', 'www.berlinart.de');

-- Insert Artists
INSERT INTO Artist (artist_id, name, birth_year, contact_email, phone, city, website, social_media, isActive) VALUES
(101, 'Leonardo Vinci', 1452, 'leo@vincistudio.it', '+39055123456', 'Florence', 'www.vincistudio.it', '@davinci_art', TRUE),
(102, 'Claude Monet', 1840, 'claude@monet.fr', '+3319876543', 'Giverny', 'www.monetclassics.fr', '@monet_colors', TRUE),
(103, 'Frida Kahlo', 1907, 'frida@kahlo.mx', '+52551234567', 'Mexico City', 'www.kahloart.mx', '@frida_official', TRUE),
(104, 'Ansel Adams', 1902, 'ansel@adams.co', '+14155550198', 'San Francisco', 'www.anseladams.co', '@ansel_nature', FALSE),
(105, 'Auguste Rodin', 1840, 'auguste@rodin.fr', '+3315550123', 'Paris', 'www.rodin.fr', '@rodin_sculpts', TRUE),
(106, 'Yayoi Kusama', 1929, 'yayoi@kusama.jp', '+8135550456', 'Tokyo', 'www.kusamadots.jp', '@yayoi_dots', TRUE),
(107, 'Banksy', 1974, 'anon@banksy.co.uk', 'UNKNOWN', 'Bristol', 'www.banksy.co.uk', '@banksy', TRUE),
(108, 'Sarah Jenkins', 1985, 'sarah.j@modernart.com', '+12125550789', 'New York', 'www.sarahjenkins.com', '@sarah_j_art', TRUE),
(109, 'Marcus Thorne', 1978, 'marcus@thorne.co.uk', '+44207555012', 'London', 'www.mthorne.co.uk', '@thorne_sculpture', TRUE),
(110, 'Elena Petrova', 1990, 'elena@digitalart.ru', '+74955550123', 'Moscow', 'www.epetrova.ru', '@elena_pixels', TRUE);

-- Insert Disciplines
INSERT INTO Discipline (discipline_id, name) VALUES
(1, 'Painting'),
(2, 'Sculpture'),
(3, 'Photography'),
(4, 'Digital Art'),
(5, 'Street Art'),
(6, 'Installation Art');

-- Insert Community Members
INSERT INTO CommunityMember (user_id, name, email, phone, city, membership_type, birth_year) VALUES
(1001, 'Alice Wonderland', 'alice@art.com', '+3312345001', 'Paris', 'Premium', 1992),
(1002, 'Bob Ross Fan', 'bob@happytrees.com', '+44207123002', 'London', 'Free', 1985),
(1003, 'Charlie Brown', 'charlie@peanuts.com', '+1212555003', 'New York', 'Standard', 1990),
(1004, 'Diana Prince', 'diana@amazon.com', '+1415555004', 'San Francisco', 'Premium', 1988),
(1005, 'Evan Wright', 'evan@wright.com', '+4930555005', 'Berlin', 'Free', 1995),
(1006, 'Fiona Gallagher', 'fiona@gallagher.com', '+1212555006', 'Chicago', 'Standard', 1998),
(1007, 'George Lucas', 'george@lucas.com', '+1415555007', 'Los Angeles', 'Premium', 1970),
(1008, 'Hannah Abbott', 'hannah@abbott.co.uk', '+44207123008', 'London', 'Free', 2000),
(1009, 'Ian Malcolm', 'ian@chaos.org', '+1212555009', 'New York', 'Premium', 1980),
(1010, 'Julia Child', 'julia@kitchen.fr', '+3312345010', 'Paris', 'Standard', 1975);

-- Insert Tags
INSERT INTO Tag (tag_id, name) VALUES
(1, 'Abstract'), (2, 'Realism'), (3, 'Modern'), (4, 'Renaissance'),
(5, 'Nature'), (6, 'Portrait'), (7, 'Surrealism'), (8, '3D Modeling'),
(9, 'Monochrome'), (10, 'Colorful');

-- ====================================================================
-- 2. CHILD TABLES (Level 1)
-- ====================================================================

-- Insert Exhibitions
INSERT INTO Exhibition (exhibition_id, title, start_date, end_date, description, curator_name, theme, gallery_id) VALUES
(10, 'Renaissance Revival', '2025-01-15', '2025-03-15', 'Classic art reborn.', 'Dr. Rossi', 'Classics', 1),
(20, 'Sculpting the Soul', '2025-02-01', '2025-04-01', 'Modern sculptures.', 'Marcus Thorne', 'Form', 2),
(30, 'Impressionist Dreams', '2025-03-10', '2025-05-10', 'Light and color.', 'Sarah Jenkins', 'Light', 3),
(40, 'Neon Horizons', '2025-04-05', '2025-06-05', 'Digital future.', 'Kenji Sato', 'Cyberpunk', 4),
(50, 'Urban Voices', '2025-05-20', '2025-07-20', 'Street art showcase.', 'Hans Muller', 'Rebellion', 5),
(60, 'Infinite Dots', '2025-06-01', '2025-08-01', 'Immersive installations.', 'Yayoi Kusama', 'Infinity', 4);

-- Insert Workshops
INSERT INTO Workshop (workshopid, title, date, duration_minutes, max_participants, price, location, description, level, artist_id) VALUES
(501, 'Mastering Oil Painting', '2025-08-15', 180, 10, 150.00, 'Florence Studio', 'Learn from the master.', 'Intermediate', 101),
(502, 'Impressionist Landscapes', '2025-08-20', 120, 15, 120.00, 'Giverny Gardens', 'Plein air painting.', 'Beginner', 102),
(503, 'Sculpting Modernity', '2025-08-25', 240, 8, 200.00, 'Paris Workshop', 'Clay modeling basics.', 'Advanced', 105),
(504, 'Digital Art 101', '2025-09-01', 120, 20, 80.00, 'Online / Moscow', 'Intro to Procreate.', 'Beginner', 110),
(505, 'Stenciling Secrets', '2025-09-10', 150, 12, 95.00, 'Bristol Streets', 'Make your own stencil.', 'Intermediate', 107),
(506, 'Self-Portraiture', '2025-09-15', 180, 10, 140.00, 'Mexico City Studio', 'Express your inner self.', 'Advanced', 103);

-- Insert Artworks
INSERT INTO Artwork (artwork_id, title, type, medium, dimensions, description, status, creation_year, price, artist_id) VALUES
(201, 'Mona Lisa', 'Painting', 'Oil on Canvas', '77x53 cm', 'A legendary masterpiece.', 'EXHIBITED', 1503, 850000000.00, 101),
(202, 'The Last Supper', 'Painting', 'Tempera', '460x880 cm', 'Iconic religious scene.', 'EXHIBITED', 1498, 450000000.00, 101),
(203, 'Water Lilies', 'Painting', 'Oil on Canvas', '200x200 cm', 'Serene garden pond.', 'FOR_SALE', 1919, 40000000.00, 102),
(204, 'The Two Fridas', 'Painting', 'Oil on Canvas', '173x173 cm', 'Dual self-portrait.', 'EXHIBITED', 1939, 5000000.00, 103),
(205, 'The Thinker', 'Sculpture', 'Bronze', '189x98 cm', 'Man in deep thought.', 'EXHIBITED', 1904, 15000000.00, 105),
(206, 'Monolith, Half Dome', 'Photography', 'Silver Gelatin', '20x16 in', 'Yosemite landscape.', 'SOLD', 1927, 100000.00, 104),
(207, 'Girl with Balloon', 'Street Art', 'Spray Paint', 'Varies', 'Hope is not lost.', 'SOLD', 2002, 1400000.00, 107),
(208, 'Infinity Mirror Room', 'Installation', 'Mirrors, LEDs', 'Room Size', 'Endless space illusion.', 'EXHIBITED', 1965, NULL, 106),
(209, 'Cyber Cityscape', 'Digital Art', 'Pixels', '4K Resolution', 'Neon lit future city.', 'FOR_SALE', 2023, 500.00, 110),
(210, 'Modern Bronze I', 'Sculpture', 'Bronze', '50x30 cm', 'Abstract forms.', 'FOR_SALE', 2021, 12000.00, 109),
(211, 'Sunrise at Sea', 'Painting', 'Watercolor', '40x60 cm', 'Morning ocean view.', 'FOR_SALE', 2024, 800.00, 108);

-- ====================================================================
-- 3. CHILD TABLES (Level 2)
-- ====================================================================

-- Insert Reviews
INSERT INTO Review (review_id, rating, comment, review_date, artwork_id, user_id) VALUES
(301, 5, 'Unbelievable detail!', '2025-01-20', 201, 1001),
(302, 4, 'The colors are stunning.', '2025-02-15', 203, 1002),
(303, 5, 'Deeply moving.', '2025-02-18', 205, 1003),
(304, 5, 'A brilliant illusion of space.', '2025-03-01', 208, 1004),
(305, 3, 'A bit too abstract for me.', '2025-03-05', 210, 1005),
(306, 5, 'Classic Banksy, brilliant.', '2025-03-10', 207, 1009),
(307, 4, 'Beautiful use of light.', '2025-03-12', 211, 1008);

-- Insert Bookings (Cross-participations: Members booking multiple workshops)
INSERT INTO Booking (booking_id, booking_date, payment_status, user_id, workshop_id) VALUES
(401, '2025-07-01 10:00:00', 'PAID', 1001, 501),
(402, '2025-07-02 11:30:00', 'PAID', 1001, 503), -- Alice booked 2 workshops
(403, '2025-07-03 09:15:00', 'PENDING', 1002, 502),
(404, '2025-07-05 14:00:00', 'PAID', 1003, 504),
(405, '2025-07-06 16:45:00', 'PAID', 1003, 505), -- Charlie booked 2 workshops
(406, '2025-07-10 10:20:00', 'CANCELLED', 1004, 501),
(407, '2025-07-12 08:00:00', 'PAID', 1005, 506),
(408, '2025-07-15 13:10:00', 'PAID', 1009, 505),
(409, '2025-07-20 09:30:00', 'PENDING', 1010, 502);

-- ====================================================================
-- 4. JUNCTION TABLES (Many-to-Many)
-- ====================================================================

-- Members favoring Disciplines
INSERT INTO Favors (user_id, discipline_id) VALUES
(1001, 1), (1001, 2), -- Alice favors Painting & Sculpture
(1002, 1), 
(1003, 4), (1003, 5), -- Charlie favors Digital & Street Art
(1004, 6),
(1005, 3), (1005, 4),
(1009, 5);

-- Exhibitions featuring Artworks (Cross-participations: Artworks in specific events)
INSERT INTO features (exhibition_id, artwork_id, arrival_date) VALUES
(10, 201, '2025-01-10'),
(10, 202, '2025-01-10'),
(20, 205, '2025-01-25'),
(20, 210, '2025-01-28'),
(30, 203, '2025-03-05'),
(40, 209, '2025-03-30'),
(50, 207, '2025-05-15'),
(60, 208, '2025-05-25');

-- Artists practicing Disciplines
INSERT INTO practices (artist_id, discipline_id) VALUES
(101, 1), (101, 2), -- Da Vinci paints and sculpts
(102, 1),
(103, 1),
(104, 3),
(105, 2),
(106, 1), (106, 6), -- Kusama paints and does installations
(107, 5),
(108, 1),
(109, 2),
(110, 4);

-- Artworks tagged with Tags
INSERT INTO tagged (artwork_id, tag_id) VALUES
(201, 4), (201, 6), -- Mona Lisa: Renaissance, Portrait
(203, 5), (203, 10), -- Water Lilies: Nature, Colorful
(204, 7), (204, 6), -- Two Fridas: Surrealism, Portrait
(205, 2), -- Thinker: Realism
(206, 5), (206, 9), -- Half Dome: Nature, Monochrome
(207, 5), -- Balloon Girl: Street Art
(208, 1), (208, 3), -- Infinity Mirror: Abstract, Modern
(209, 4), (209, 8); -- Cyber City: Modern, 3D Modeling