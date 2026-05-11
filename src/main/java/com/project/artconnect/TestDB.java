package com.project.artconnect;

import com.project.artconnect.service.ArtistService;
import com.project.artconnect.util.ServiceProvider;
import com.project.artconnect.model.Artist;

public class TestDB {
    public static void main(String[] args) {
        ArtistService as = ServiceProvider.getArtistService();
        System.out.println("Artists in DB:");
        for(Artist a : as.getAllArtists()) {
            System.out.println(a.getName());
        }
    }
}
