package com.crashdata.back.repository;

import com.crashdata.back.code.*;
import com.crashdata.back.dao.DistrictDao;
import com.crashdata.back.entity.Crash;
import com.crashdata.back.entity.District;
import com.crashdata.back.entity.Person;
import com.crashdata.back.entity.Vehicle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CrashPersistenceIntegrationTest {

    @Autowired private DistrictDao districtDao;
    @Autowired private CrashRepository crashRepository;
    @Autowired private VehicleRepository vehicleRepository;
    @Autowired private PersonRepository personRepository;

    @Test
    void savesCrashWithVehicleAndPersonAndReadsItBack() {

        District district = districtDao.findAll().getFirst();

        Crash crash = new Crash(
                "REF-001", (short) 2026, LocalDate.of(2026, 1, 1),
                LocalTime.of(14, 30), district.getId(), null, null,
                null, CrashType.ANIMAL, ImpactType.HEAD_ON, Weather.CLEAR,
                Light.DARK_STREET_LIGHTS_UNLIT, CrashSeverity.FATAL, RoadwayType.MOTORWAY,
                null, (short) 50, ObstaclePresent.NO, SurfaceCondition.DRY,
                JunctionType.NOT_AT_JUNCTION, Curve.NONE, Grade.UNKNOWN);
        crash = crashRepository.saveAndFlush(crash);

        Vehicle vehicle = new Vehicle(crash, (short)7342, VehicleType.PASSENGER_CAR,
                "BMW", "M5", (short)2025,4395, SpecialFunction.NONE,
                Manoeuvre.STRAIGHT_FORWARD);
        vehicle = vehicleRepository.saveAndFlush(vehicle);

        Person person = new Person(crash, (short) 1, vehicle, null,
                LocalDate.of(2004,7,23), Sex.MALE, RoadUserType.DRIVER,
                SeatRow.FRONT, SeatPosition.LEFT, InjurySeverity.SERIOUS, Restraint.UNKNOWN,
                Helmet.NOT_APPLICABLE, PedManoeuvre.OTHER, AlcoholSuspected.NO, DrugUse.NO_SUSPICION,
                LicenceStatus.ISSUED, LocalDate.of(2022,9,23));
        person = personRepository.saveAndFlush(person);

        Crash foundCrash = crashRepository.findById(crash.getId()).orElseThrow();
        Vehicle foundVehicle = vehicleRepository.findById(vehicle.getId()).orElseThrow();
        Person foundPerson = personRepository.findById(person.getId()).orElseThrow();

        assertThat(foundCrash.getDistrictId()).isEqualTo(district.getId());
        assertThat(foundVehicle.getCrash().getId()).isEqualTo(crash.getId());
        assertThat(foundPerson.getCrash().getId()).isEqualTo(crash.getId());
        assertThat(foundPerson.getOccupantVehicle().getId()).isEqualTo(vehicle.getId());


    }
}