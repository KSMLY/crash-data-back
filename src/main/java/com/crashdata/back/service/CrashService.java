package com.crashdata.back.service;

import com.crashdata.back.dao.AlcoholTestDao;
import com.crashdata.back.dao.CrashDao;
import com.crashdata.back.dao.PersonDao;
import com.crashdata.back.dao.VehicleDao;
import com.crashdata.back.entity.Crash;
import com.crashdata.back.entity.CrashDetail;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CrashService {

    private final CrashDao crashDao;
    private final VehicleDao vehicleDao;
    private final PersonDao personDao;
    private final AlcoholTestDao alcoholTestDao;

    public List<Crash> getCrashes() {
        return crashDao.findAll();
    }

    public Optional<CrashDetail> getCrashDetail(Long crashId) {
        return crashDao.findById(crashId)
                .map(crash -> new CrashDetail(
                        crash,
                        vehicleDao.findByCrashId(crashId),
                        personDao.findByCrashId(crashId),
                        alcoholTestDao.findByCrashId(crashId)));
    }

    @Transactional
    public Crash createCrash(Crash crash) {
        Long id = crashDao.insert(crash);
        return crashDao.findById(id).orElseThrow();
    }
}
