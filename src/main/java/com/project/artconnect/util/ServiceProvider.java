package com.project.artconnect.util;

import com.project.artconnect.dao.*;
import com.project.artconnect.persistence.*;
import com.project.artconnect.service.*;
import com.project.artconnect.service.impl.*;

/**
 * Service Provider to manage singleton instances of services and handle their
 * initialization.
 * * FULLY UPDATED FOR JDBC DATABASE ARCHITECTURE.
 */
public class ServiceProvider {

    // ---------------------------------------------------------
    // 1. Initialize the JDBC DAOs (Persistence Layer)
    // ---------------------------------------------------------
    private static final ArtistDao jdbcArtistDao = new JdbcArtistDao();
    private static final ArtworkDao jdbcArtworkDao = new JdbcArtworkDao();
    private static final GalleryDao jdbcGalleryDao = new JdbcGalleryDao();
    private static final ExhibitionDao jdbcExhibitionDao = new JdbcExhibitionDao();
    private static final WorkshopDao jdbcWorkshopDao = new JdbcWorkshopDao();
    private static final CommunityMemberDao jdbcMemberDao = new JdbcCommunityMemberDao();

    private static final UserContext userContext = new UserContext();

    // ---------------------------------------------------------
    // 2. Initialize the Services (Business Layer)
    // ---------------------------------------------------------
    // We inject the DAOs into the Services using Constructor Injection.
    // Note: If you named your files "DatabaseArtistService", change the class names
    // below.
    private static final ArtistService artistService = new JdbcArtistService(jdbcArtistDao);
    private static final ArtworkService artworkService = new JdbcArtworkService(jdbcArtworkDao);
    private static final GalleryService galleryService = new JdbcGalleryService(jdbcGalleryDao, jdbcExhibitionDao);
    private static final WorkshopService workshopService = new JdbcWorkshopService(jdbcWorkshopDao);
    private static final CommunityService communityService = new JdbcCommunityService(jdbcMemberDao);
    private static final ExhibitionService exhibitionService = new JdbcExhibitionService(jdbcExhibitionDao);

    // ---------------------------------------------------------
    // 3. Initialization Block
    // ---------------------------------------------------------
    static {
        // All InMemory initData() methods have been completely removed!
        // The JavaFX UI will now pull all information directly from your MySQL
        // database.
        System.out.println("ServiceProvider initialized successfully with MySQL JDBC connections.");
    }

    // ---------------------------------------------------------
    // 4. Public Getters for the UI Controllers
    // ---------------------------------------------------------
    public static ArtistService getArtistService() {
        return artistService;
    }

    public static ArtworkService getArtworkService() {
        return artworkService;
    }

    public static GalleryService getGalleryService() {
        return galleryService;
    }

    public static WorkshopService getWorkshopService() {
        return workshopService;
    }

    public static CommunityService getCommunityService() {
        return communityService;
    }

    public static ExhibitionService getExhibitionService() {
        return exhibitionService;
    }

    public static UserContext getUserContext() {
        return userContext;
    }
}