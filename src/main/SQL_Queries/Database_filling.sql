-- ==========================================================
-- SCRIPT DE CRÉATION ET DE REMPLISSAGE DE LA BASE DE DONNÉES
-- ==========================================================

DROP DATABASE IF EXISTS ArtGalleryDB;
CREATE DATABASE ArtGalleryDB;
USE ArtGalleryDB;

-- 1. CRÉATION DES TABLES INDÉPENDANTES
-- ----------------------------------------------------------

CREATE TABLE Gallery (
    gallery_id SMALLINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    address VARCHAR(50),
    owner_name VARCHAR(50),
    opening_hours VARCHAR(50),
    contact_phone VARCHAR(50),
    rating VARCHAR(50),
    website VARCHAR(50)
) ENGINE=InnoDB;

CREATE TABLE Artist (
    artist_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    birth_year SMALLINT,
    contact_email VARCHAR(50),
    phone VARCHAR(50),
    city VARCHAR(50),
    website VARCHAR(50),
    social_media VARCHAR(50),
    isActive BOOLEAN
) ENGINE=InnoDB;

CREATE TABLE Tag (
    tag_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE Discipline (
    discipline_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE CommunityMember (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(50),
    phone VARCHAR(50),
    city VARCHAR(50),
    membership_type VARCHAR(50),
    birth_year SMALLINT
) ENGINE=InnoDB;

-- 2. CRÉATION DES TABLES AVEC RELATIONS (1,N)
-- ----------------------------------------------------------

CREATE TABLE Exhibition (
    exhibition_id SMALLINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(50) NOT NULL,
    end_date DATE,
    start_date DATE,
    description VARCHAR(50),
    curator_name VARCHAR(50),
    theme VARCHAR(50),
    gallery_id SMALLINT NOT NULL,
    FOREIGN KEY (gallery_id) REFERENCES Gallery(gallery_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE Artwork (
    artwork_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(50) NOT NULL,
    type VARCHAR(50),
    medium VARCHAR(50),
    dimensions VARCHAR(50),
    description VARCHAR(50),
    status VARCHAR(50),
    creation_year SMALLINT,
    price DECIMAL(15,2),
    artist_id INT NOT NULL,
    FOREIGN KEY (artist_id) REFERENCES Artist(artist_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE Workshop (
    workshop_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(50) NOT NULL,
    date DATE,
    duration_minutes BIGINT,
    max_participants SMALLINT,
    price DECIMAL(15,2),
    location VARCHAR(50),
    description VARCHAR(50),
    level VARCHAR(50),
    artist_id INT NOT NULL,
    FOREIGN KEY (artist_id) REFERENCES Artist(artist_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 3. CRÉATION DES TABLES DÉPENDANTES (BOOKING & REVIEW)
-- ----------------------------------------------------------

CREATE TABLE Booking (
    booking_id INT PRIMARY KEY AUTO_INCREMENT,
    booking_date DATETIME,
    payment_status VARCHAR(50),
    workshop_id INT NOT NULL,
    user_id INT NOT NULL,
    FOREIGN KEY (workshop_id) REFERENCES Workshop(workshop_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES CommunityMember(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE Review (
    review_id INT PRIMARY KEY AUTO_INCREMENT,
    rating TINYINT,
    comment VARCHAR(50),
    review_date DATE,
    user_id INT NOT NULL,
    artwork_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES CommunityMember(user_id) ON DELETE CASCADE,
    FOREIGN KEY (artwork_id) REFERENCES Artwork(artwork_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 4. CRÉATION DES TABLES DE JOINTURE (N,N)
-- ----------------------------------------------------------

CREATE TABLE features (
    exhibition_id SMALLINT,
    artwork_id INT,
    arrival_date DATE,
    PRIMARY KEY (exhibition_id, artwork_id),
    FOREIGN KEY (exhibition_id) REFERENCES Exhibition(exhibition_id) ON DELETE CASCADE,
    FOREIGN KEY (artwork_id) REFERENCES Artwork(artwork_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE tagged (
    artwork_id INT,
    tag_id INT,
    PRIMARY KEY (artwork_id, tag_id),
    FOREIGN KEY (artwork_id) REFERENCES Artwork(artwork_id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES Tag(tag_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE practices (
    artist_id INT,
    discipline_id INT,
    PRIMARY KEY (artist_id, discipline_id),
    FOREIGN KEY (artist_id) REFERENCES Artist(artist_id) ON DELETE CASCADE,
    FOREIGN KEY (discipline_id) REFERENCES Discipline(discipline_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE Favors (
    user_id INT,
    discipline_id INT,
    PRIMARY KEY (user_id, discipline_id),
    FOREIGN KEY (user_id) REFERENCES CommunityMember(user_id) ON DELETE CASCADE,
    FOREIGN KEY (discipline_id) REFERENCES Discipline(discipline_id) ON DELETE CASCADE
) ENGINE=InnoDB;


-- ==========================================================
-- INSERTION DES DONNÉES (PEUPLEMENT)
-- ==========================================================

-- Gallery
INSERT INTO Gallery (name, address, owner_name, opening_hours, contact_phone, rating, website) VALUES
('Le Louvre Moderne', '75001 Paris', 'Jean Dupont', '10h-18h', '0140205050', '5 stars', 'www.louvre-mod.fr'),
('Pixel Art Gallery', '69002 Lyon', 'Alice Vasseur', '11h-19h', '0472103030', '4 stars', 'www.pixelart.com');

-- Artist
INSERT INTO Artist (name, birth_year, contact_email, phone, city, website, social_media, isActive) VALUES
('Marc Chagall Jr', 1985, 'marc@art.com', '0601020304', 'Nice', 'www.marc-art.fr', '@marc_art', TRUE),
('Sonia Delaunay', 1992, 'sonia@canvas.com', '0612345678', 'Paris', 'www.sonia-paint.com', '@sonia_paint', TRUE),
('Yoko Ono', 1933, 'yoko@peace.com', '0101010101', 'Tokyo', 'www.imagine.jp', '@yoko_official', FALSE);

-- Tag
INSERT INTO Tag (name) VALUES ('Abstrait'), ('Moderne'), ('Sculpture'), ('Couleur'), ('Minimaliste');

-- Discipline
INSERT INTO Discipline (name) VALUES ('Peinture Huile'), ('Sculpture Marbre'), ('Photographie'), ('Art Digital');

-- CommunityMember
INSERT INTO CommunityMember (name, email, phone, city, membership_type, birth_year) VALUES
('Thomas Martin', 'thomas@email.com', '0788990011', 'Lyon', 'Premium', 1995),
('Julie Durand', 'julie@mail.fr', '0655443322', 'Paris', 'Standard', 1988),
('Lucie Bernard', 'lucie@web.com', '0622334455', 'Marseille', 'Student', 2002);

-- Exhibition
INSERT INTO Exhibition (title, start_date, end_date, description, curator_name, theme, gallery_id) VALUES
('Lumières du Sud', '2024-05-01', '2024-08-30', 'Exposition sur les couleurs de la Provence', 'Pierre Hermé', 'Impressionnisme', 1),
('Futur Digital', '2024-06-15', '2024-07-15', 'L''art à l''ère de l''IA', 'Sarah Connor', 'Numérique', 2);

-- Artwork
INSERT INTO Artwork (title, type, medium, dimensions, description, status, creation_year, price, artist_id) VALUES
('Rêve Azur', 'Peinture', 'Huile', '100x120', 'Un ciel bleu profond', 'Available', 2023, 1500.00, 1),
('Sphère de Vie', 'Sculpture', 'Acier', '50x50x50', 'Structure géométrique', 'Sold', 2022, 3200.00, 1),
('Nuit Urbaine', 'Photo', 'Numérique', '60x40', 'Photo de Lyon de nuit', 'Available', 2024, 450.00, 2),
('Chaos Organisé', 'Peinture', 'Acrylique', '200x200', 'Explosion de couleurs', 'Reserved', 2021, 5600.00, 2);

-- Workshop
INSERT INTO Workshop (title, date, duration_minutes, max_participants, price, location, description, level, artist_id) VALUES
('Initiation Aquarelle', '2024-09-10', 120, 10, 45.00, 'Atelier A', 'Apprendre les bases de l''eau', 'Beginner', 1),
('Masterclass Photo', '2024-10-05', 240, 5, 120.00, 'Studio Photo Lyon', 'Techniques avancées de nuit', 'Advanced', 2);

-- Booking
INSERT INTO Booking (booking_date, payment_status, workshop_id, user_id) VALUES
('2024-08-20 14:30:00', 'Paid', 1, 1),
('2024-08-21 09:15:00', 'Pending', 1, 3),
('2024-09-01 18:00:00', 'Paid', 2, 2);

-- Review
INSERT INTO Review (rating, comment, review_date, user_id, artwork_id) VALUES
(5, 'Magnifique utilisation de la couleur !', '2024-06-01', 1, 1),
(4, 'Très impressionnant en vrai.', '2024-06-05', 2, 2),
(3, 'Un peu trop abstrait pour moi.', '2024-07-01', 3, 4);

-- Many-to-Many: features (Exhibition <-> Artwork)
INSERT INTO features (exhibition_id, artwork_id, arrival_date) VALUES
(1, 1, '2024-04-25'),
(1, 2, '2024-04-26'),
(2, 3, '2024-06-10');

-- Many-to-Many: tagged (Artwork <-> Tag)
INSERT INTO tagged (artwork_id, tag_id) VALUES
(1, 1), (1, 4), -- Rêve Azur: Abstrait, Couleur
(2, 3), (2, 5), -- Sphère de Vie: Sculpture, Minimaliste
(3, 2), (3, 4); -- Nuit Urbaine: Moderne, Couleur

-- Many-to-Many: practices (Artist <-> Discipline)
INSERT INTO practices (artist_id, discipline_id) VALUES
(1, 1), (1, 2), -- Marc Chagall Jr: Peinture, Sculpture
(2, 3), (2, 4); -- Sonia Delaunay: Photographie, Art Digital

-- Many-to-Many: Favors (CommunityMember <-> Discipline)
INSERT INTO Favors (user_id, discipline_id) VALUES
(1, 1), (1, 4), -- Thomas aime la peinture et le digital
(2, 3),         -- Julie aime la photo
(3, 2);         