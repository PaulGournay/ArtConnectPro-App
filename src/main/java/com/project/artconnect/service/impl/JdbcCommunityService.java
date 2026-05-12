package com.project.artconnect.service.impl;

import com.project.artconnect.dao.CommunityMemberDao;
import com.project.artconnect.model.Artist;
import com.project.artconnect.model.CommunityMember;
import com.project.artconnect.model.Review;
import com.project.artconnect.service.CommunityService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class JdbcCommunityService implements CommunityService {

    private final CommunityMemberDao memberDao;

    // Constructor Injection
    public JdbcCommunityService(CommunityMemberDao memberDao) {
        this.memberDao = memberDao;
    }

    @Override
    public List<CommunityMember> getAllMembers() {
        return memberDao.findAll();
    }

    @Override
    public Optional<CommunityMember> getMemberByName(String name) {
        return memberDao.findAll().stream()
                .filter(m -> m.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public List<Review> getReviewsByMember(CommunityMember member) {
        // Return an empty list for now until a ReviewDao is implemented
        return Collections.emptyList();
    }

        @Override
    public void createMember(CommunityMember member) {
        memberDao.save(member);
    }

    @Override
    public void updateMember(CommunityMember member) {
        memberDao.update(member);
    }

    @Override
    public void deleteMember(String name) {
        memberDao.delete(name);
    }
}