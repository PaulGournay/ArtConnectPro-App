CREATE DATABASE IF NOT EXISTS ArtGalleryDB;
USE ArtGalleryDB;

-- 1. Tables indépendantes (sans clés étrangères au début)
CREATE TABLE Gallery (
    gallery_id SMALLINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    address VARCHAR(50),
    owner_name VARCHAR(50),
    opening_hours VARCHAR(50),
    contact_phone VARCHAR(50),
    rating VARCHAR(50),
    website VARCHAR(50)
);

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
);

CREATE TABLE Tag (
    tag_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE Discipline (
    discipline_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE CommunityMember (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(50),
    phone VARCHAR(50),
    city VARCHAR(50),
    membership_type VARCHAR(50),
    birth_year SMALLINT
);

-- 2. Tables avec clés étrangères simples (1,n)
CREATE TABLE Exhibition (
    exhibition_id SMALLINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(50) NOT NULL,
    end_date DATE,
    start_date DATE,
    description VARCHAR(50),
    curator_name VARCHAR(50),
    theme VARCHAR(50),
    gallery_id SMALLINT NOT NULL,
    FOREIGN KEY (gallery_id) REFERENCES Gallery(gallery_id)
);

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
    FOREIGN KEY (artist_id) REFERENCES Artist(artist_id)
);

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
    FOREIGN KEY (artist_id) REFERENCES Artist(artist_id)
);

-- 3. Tables dépendantes de plusieurs entités
CREATE TABLE Booking (
    booking_id INT PRIMARY KEY AUTO_INCREMENT,
    booking_date DATETIME,
    payment_status VARCHAR(50),
    workshop_id INT NOT NULL,
    user_id INT NOT NULL,
    FOREIGN KEY (workshop_id) REFERENCES Workshop(workshop_id),
    FOREIGN KEY (user_id) REFERENCES CommunityMember(user_id)
);

CREATE TABLE Review (
    review_id INT PRIMARY KEY AUTO_INCREMENT,
    rating TINYINT, -- Correspond au type 'BYTE' du schéma
    comment VARCHAR(50),
    review_date DATE,
    user_id INT NOT NULL,
    artwork_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES CommunityMember(user_id),
    FOREIGN KEY (artwork_id) REFERENCES Artwork(artwork_id)
);

-- 4. Tables de jointure pour les relations (n,n)
-- Relation "features" entre Exhibition et Artwork
CREATE TABLE features (
    exhibition_id SMALLINT,
    artwork_id INT,
    arrival_date DATE,
    PRIMARY KEY (exhibition_id, artwork_id),
    FOREIGN KEY (exhibition_id) REFERENCES Exhibition(exhibition_id),
    FOREIGN KEY (artwork_id) REFERENCES Artwork(artwork_id)
);

-- Relation "tagged" entre Artwork et Tag
CREATE TABLE tagged (
    artwork_id INT,
    tag_id INT,
    PRIMARY KEY (artwork_id, tag_id),
    FOREIGN KEY (artwork_id) REFERENCES Artwork(artwork_id),
    FOREIGN KEY (tag_id) REFERENCES Tag(tag_id)
);

-- Relation "practices" entre Artist et Discipline
CREATE TABLE practices (
    artist_id INT,
    discipline_id INT,
    PRIMARY KEY (artist_id, discipline_id),
    FOREIGN KEY (artist_id) REFERENCES Artist(artist_id),
    FOREIGN KEY (discipline_id) REFERENCES Discipline(discipline_id)
);

-- Relation "Favors" entre CommunityMember et Discipline
CREATE TABLE Favors (
    user_id INT,
    discipline_id INT,
    PRIMARY KEY (user_id, discipline_id),
    FOREIGN KEY (user_id) REFERENCES CommunityMember(user_id),
    FOREIGN KEY (discipline_id) REFERENCES Discipline(discipline_id)
);