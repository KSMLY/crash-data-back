package com.crashdata.back.service;

import com.crashdata.back.code.CrashSeverity;
import com.crashdata.back.code.InjurySeverity;
import com.crashdata.back.dao.AlcoholTestDao;
import com.crashdata.back.dao.CrashDao;
import com.crashdata.back.dao.PersonDao;
import com.crashdata.back.dao.VehicleDao;
import com.crashdata.back.entity.AlcoholTest;
import com.crashdata.back.entity.Crash;
import com.crashdata.back.entity.CrashDetail;
import com.crashdata.back.entity.CrashPoint;
import com.crashdata.back.entity.CrashSearch;
import com.crashdata.back.entity.Municipality;
import com.crashdata.back.entity.Page;
import com.crashdata.back.entity.Person;
import com.crashdata.back.entity.PersonSubmission;
import com.crashdata.back.entity.Vehicle;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@Service
@AllArgsConstructor
public class CrashService {

    private final CrashDao crashDao;
    private final VehicleDao vehicleDao;
    private final PersonDao personDao;
    private final AlcoholTestDao alcoholTestDao;
    private final LocationService locationService;

    public Page<Crash> searchCrashes(CrashSearch search) {
        return new Page<>(crashDao.search(search), crashDao.count(search));
    }

    public void exportCrashes(CrashSearch search, Consumer<Crash> sink) {
        crashDao.export(search, sink);
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
    public CrashDetail createCrash(Crash crash, List<Vehicle> vehicles, List<PersonSubmission> persons) {
        requireMunicipalityInDistrict(crash);
        requireOneVehicleRolePerPerson(persons);

        Long crashId = crashDao.insert(crash.withSeverity(deriveSeverity(persons)));

        Map<Short, Long> vehicleIdsByNumber = insertVehicles(crashId, vehicles);
        insertPersons(crashId, persons, vehicleIdsByNumber);

        return getCrashDetail(crashId).orElseThrow();
    }

    // The request only carries the municipality id, so the district it belongs to has to be
    // looked up; going through the cached list also turns an unknown id into a 400 rather
    // than a foreign-key failure
    private void requireMunicipalityInDistrict(Crash crash) {
        if (crash.getMunicipality() == null) return;
        Long municipalityId = crash.getMunicipality().getId();
        Long districtId = crash.getDistrict().getId();
        boolean inDistrict = locationService.getMunicipalities(districtId).stream()
                .map(Municipality::getId)
                .anyMatch(municipalityId::equals);
        if (!inDistrict) {
            throw new InvalidCrashException(
                    "Municipality " + municipalityId + " is not in district " + districtId + ".");
        }
    }

    private static void requireOneVehicleRolePerPerson(List<PersonSubmission> persons) {
        for (int i = 0; i < persons.size(); i++) {
            PersonSubmission submission = persons.get(i);
            if (submission.occupantVehicleNumber() != null && submission.struckByVehicleNumber() != null) {
                throw new InvalidCrashException("persons[" + i
                        + "] cannot have both occupantVehicleNumber and struckByVehicleNumber.");
            }
        }
    }

    private Map<Short, Long> insertVehicles(Long crashId, List<Vehicle> vehicles) {
        Set<Short> numbers = new HashSet<>();
        for (Vehicle vehicle : vehicles) {
            if (!numbers.add(vehicle.getVehicleNumber())) {
                throw new InvalidCrashException(
                        "Duplicate vehicleNumber in request: " + vehicle.getVehicleNumber());
            }
        }

        Map<Short, Long> idsByNumber = new HashMap<>();
        for (Vehicle vehicle : vehicles) {
            idsByNumber.put(vehicle.getVehicleNumber(), vehicleDao.insert(vehicle.withCrashId(crashId)));
        }
        return idsByNumber;
    }

    private void insertPersons(Long crashId, List<PersonSubmission> persons,
                               Map<Short, Long> vehicleIdsByNumber) {
        for (PersonSubmission submission : persons) {
            Person person = submission.person().withIds(
                    crashId,
                    resolveVehicle(submission.occupantVehicleNumber(), vehicleIdsByNumber),
                    resolveVehicle(submission.struckByVehicleNumber(), vehicleIdsByNumber));
            Long personId = personDao.insert(person);

            AlcoholTest test = submission.alcoholTest();
            if (test != null) {
                alcoholTestDao.insert(test.withPersonId(personId));
            }
        }
    }

    private static Long resolveVehicle(Short vehicleNumber, Map<Short, Long> vehicleIdsByNumber) {
        if (vehicleNumber == null) return null;
        Long id = vehicleIdsByNumber.get(vehicleNumber);
        if (id == null) {
            throw new InvalidCrashException("No vehicle with vehicleNumber " + vehicleNumber);
        }
        return id;
    }

    /**
     * CD1 is the worst injury among the persons. Only injury crashes are recorded
     * (WHO section 3.3), so a crash with nothing to derive from is rejected.
     */
    private static CrashSeverity deriveSeverity(List<PersonSubmission> persons) {
        if (persons.isEmpty()) {
            throw new InvalidCrashException("A crash must have at least one person.");
        }
        return persons.stream()
                .map(submission -> submission.person().getInjurySeverity())
                .map(CrashService::toCrashSeverity)
                .filter(severity -> severity != null)
                .min(Comparator.comparing(CrashSeverity::getCode))
                .orElseThrow(() -> new InvalidCrashException(unknownSeverityMessage(persons)));
    }

    private static CrashSeverity toCrashSeverity(InjurySeverity injury) {
        if (injury == null) return null;
        return switch (injury) {
            case FATAL -> CrashSeverity.FATAL;
            case SERIOUS -> CrashSeverity.SERIOUS;
            case SLIGHT -> CrashSeverity.SLIGHT;
            case NO_INJURY, UNKNOWN -> null;
        };
    }

    private static String unknownSeverityMessage(List<PersonSubmission> persons) {
        boolean anyUnknown = persons.stream()
                .anyMatch(submission -> submission.person().getInjurySeverity() != InjurySeverity.NO_INJURY);
        return anyUnknown
                ? "At least one person's injurySeverityCode must be known."
                : "Only injury crashes are recorded: at least one person must be injured.";
    }

    public List<CrashPoint> getPoints(LocalDate from, LocalDate to) {
        return crashDao.points(from, to);
    }
}
