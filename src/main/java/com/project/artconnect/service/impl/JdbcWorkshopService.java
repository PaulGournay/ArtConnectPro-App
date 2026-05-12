package com.project.artconnect.service.impl;

import com.project.artconnect.dao.WorkshopDao;
import com.project.artconnect.model.Booking;
import com.project.artconnect.model.CommunityMember;
import com.project.artconnect.model.Gallery;
import com.project.artconnect.model.Workshop;
import com.project.artconnect.service.WorkshopService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class JdbcWorkshopService implements WorkshopService {

    private final WorkshopDao workshopDao;

    public JdbcWorkshopService(WorkshopDao workshopDao) {
        this.workshopDao = workshopDao;
    }

    @Override
    public List<Workshop> getAllWorkshops() {
        return workshopDao.findAll();
    }

    @Override
    public Optional<Workshop> getWorkshopByTitle(String title) {
        return workshopDao.findAll().stream()
                .filter(w -> w.getTitle().equalsIgnoreCase(title))
                .findFirst();
    }

    @Override
    public void bookWorkshop(Workshop workshop, CommunityMember member) {
        // You will implement the JDBC Booking transaction here later!
        System.out.println("JDBC Mode: Booking saved for " + member.getName() + " in workshop: " + workshop.getTitle());
    }

    @Override
    public List<Booking> getBookingsByMember(CommunityMember member) {
        // Return an empty list for now until a BookingDao is implemented
        return Collections.emptyList();
    }

        @Override
    public void createWorkshop(Workshop workshop) {
        workshopDao.save(workshop);
    }

    @Override
    public void updateWorkshop(Workshop workshop) {
        workshopDao.update(workshop);
    }

    @Override
    public void deleteWorkshop(String name) {
        workshopDao.delete(name);
    }
}