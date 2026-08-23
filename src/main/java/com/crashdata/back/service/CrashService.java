package com.crashdata.back.service;

import com.crashdata.back.dao.CrashDao;
import com.crashdata.back.entity.Crash;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CrashService {

    private final CrashDao crashDao;

    public CrashService(CrashDao crashDao) {
        this.crashDao = crashDao;
    }

    public List<Crash> getCrashes() {
        return crashDao.findAll();
    }

    public Optional<Crash> getCrash(Long crashId) {
        return crashDao.findById(crashId);
    }


}
