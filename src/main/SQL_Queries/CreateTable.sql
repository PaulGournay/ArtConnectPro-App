CREATE DATABASE IF NOT EXISTS artconnect_db;
USE artconnect_db;

CREATE TABLE Gallery (
    gallery_id SMALLINT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    address VARCHAR(50),
    owner_name VARCHAR(50),
    opening_hours VARCHAR(50),
    contact_phone VARCHAR(50),
    rating VARCHAR(50),
    website VARCHAR(50)
);

CREATE TABLE Artist (
    artist_id INT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    birth_year SMALLINT,
    contact_email VARCHAR(50),
    phone VARCHAR(50),
    city VARCHAR(50),
    website VARCHAR(50),
    social_media VARCHAR(50),
    isActive BOOLEAN
);

CREATE TABLE Discipline (
    discipline_id INT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE CommunityMember (
    user_id INT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    phone VARCHAR(50),
    city VARCHAR(50),
    membership_type VARCHAR(50),
    birth_year SMALLINT
);

CREATE TABLE Tag (
    tag_id INT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);



CREATE TABLE Exhibition (
    exhibition_id SMALLINT PRIMARY KEY,
    title VARCHAR(50) NOT NULL,
    start_date DATE,
    end_date DATE,
    description VARCHAR(50),
    curator_name VARCHAR(50),
    theme VARCHAR(50),
    gallery_id SMALLINT,
    FOREIGN KEY (gallery_id) REFERENCES Gallery(gallery_id) ON DELETE CASCADE
);

CREATE TABLE Workshop (
    workshopid INT PRIMARY KEY,
    title VARCHAR(50) NOT NULL,
    date DATE,
    duration_minutes BIGINT,
    max_participants SMALLINT,
    price DECIMAL(15,2),
    location VARCHAR(50),
    description VARCHAR(50),
    level VARCHAR(50),
    artist_id INT,
    FOREIGN KEY (artist_id) REFERENCES Artist(artist_id) ON DELETE CASCADE
);

CREATE TABLE Artwork (
    artwork_id INT PRIMARY KEY,
    title VARCHAR(50) NOT NULL,
    type VARCHAR(50),
    medium VARCHAR(50),
    dimensions VARCHAR(50),
    description VARCHAR(50),
    status VARCHAR(50),
    creation_year SMALLINT,
    price DECIMAL(15,2),
    artist_id INT,
    FOREIGN KEY (artist_id) REFERENCES Artist(artist_id) ON DELETE CASCADE
);



CREATE TABLE Review (
    review_id INT PRIMARY KEY,
    rating TINYINT,
    comment VARCHAR(50),
    review_date DATE,
    artwork_id INT,
    user_id INT,
    FOREIGN KEY (artwork_id) REFERENCES Artwork(artwork_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES CommunityMember(user_id) ON DELETE CASCADE
);

CREATE TABLE Booking (
    booking_id INT PRIMARY KEY,
    booking_date DATETIME,
    payment_status VARCHAR(50),
    user_id INT,
    workshop_id INT,
    FOREIGN KEY (user_id) REFERENCES CommunityMember(user_id) ON DELETE CASCADE,
    FOREIGN KEY (workshop_id) REFERENCES Workshop(workshopid) ON DELETE CASCADE
);



CREATE TABLE Favors (
    discipline_id INT,
    user_id INT,
    PRIMARY KEY (discipline_id, user_id),
    FOREIGN KEY (discipline_id) REFERENCES Discipline(discipline_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES CommunityMember(user_id) ON DELETE CASCADE
);

CREATE TABLE features (
    exhibition_id SMALLINT,
    artwork_id INT,
    arrival_date DATE,
    PRIMARY KEY (exhibition_id, artwork_id),
    FOREIGN KEY (exhibition_id) REFERENCES Exhibition(exhibition_id) ON DELETE CASCADE,
    FOREIGN KEY (artwork_id) REFERENCES Artwork(artwork_id) ON DELETE CASCADE
);

CREATE TABLE practices (
    artist_id INT,
    discipline_id INT,
    PRIMARY KEY (artist_id, discipline_id),
    FOREIGN KEY (artist_id) REFERENCES Artist(artist_id) ON DELETE CASCADE,
    FOREIGN KEY (discipline_id) REFERENCES Discipline(discipline_id) ON DELETE CASCADE
);

CREATE TABLE tagged (
    artwork_id INT,
    tag_id INT,
    PRIMARY KEY (artwork_id, tag_id),
    FOREIGN KEY (artwork_id) REFERENCES Artwork(artwork_id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES Tag(tag_id) ON DELETE CASCADE
);