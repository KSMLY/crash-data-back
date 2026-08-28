package com.crashdata.back.service;

import com.crashdata.back.dao.CrashDao;
import com.crashdata.back.entity.Crash;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CrashService {

    private final CrashDao crashDao;

    public List<Crash> getCrashes() {
        return crashDao.findAll();
    }

    public Optional<Crash> getCrash(Long crashId) {
        return crashDao.findById(crashId);
    }

    @Transactional
    public Crash createCrash(Crash crash) {
        Long id = crashDao.insert(crash);
        return crashDao.findById(id).orElseThrow();
    }
}
