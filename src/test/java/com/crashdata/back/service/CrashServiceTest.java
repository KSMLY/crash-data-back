package com.crashdata.back.service;

import com.crashdata.back.code.CrashSeverity;
import com.crashdata.back.code.InjurySeverity;
import com.crashdata.back.dao.*;
import com.crashdata.back.entity.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrashServiceTest {

    @Mock
    private CrashDao crashDao;

    @Mock
    private VehicleDao vehicleDao;

    @Mock
    private PersonDao personDao;

    @Mock
    private AlcoholTestDao alcoholTestDao;

    @InjectMocks
    private CrashService service;

    // Nothing under test reads a crash field, so every column is left null
    private static Crash crash() {
        return new Crash(null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null);
    }

    private static Person person(Short personNumber, InjurySeverity injury) {
        return new Person(null, null, personNumber, null, null, null, null, null, null, null,
                injury, null, null, null, null, null, null, null);
    }

    private static Vehicle vehicle(Short vehicleNumber) {
        return new Vehicle(null, null, vehicleNumber, null, null, null, null, null, null, null);
    }

    private static AlcoholTest alcoholTest() {
        return new AlcoholTest(null, null, null, null, null);
    }

    private static PersonSubmission submission(Person person) {
        return new PersonSubmission(person, null, null, null);
    }

    @Test
    void rejectsCrashWithNoPersons() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, ()
                -> service.createCrash(crash(), List.of(), List.of()));

        assertTrue(thrown.getMessage().contains("at least one person"), thrown.getMessage());
        verify(crashDao, never()).insert(any());
    }

    @Test
    void rejectsCrashWithNoInjuries() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, ()
                -> service.createCrash(crash(), List.of(),
                List.of(submission(person((short) 1, InjurySeverity.NO_INJURY)))));

        assertTrue(thrown.getMessage().contains("Only injury crashes are recorded"), thrown.getMessage());
        verify(crashDao, never()).insert(any());
    }

    @Test
    void rejectsCrashWhereAllInjuriesAreUnknown() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, ()
                -> service.createCrash(crash(), List.of(),
                List.of(submission(person((short) 1, InjurySeverity.UNKNOWN)))));

        assertTrue(thrown.getMessage().contains("must be known"), thrown.getMessage());
        verify(crashDao, never()).insert(any());
    }

    @Test
    void rejectsCrashWhereAPersonHasNoInjurySeverityAtAll() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, ()
                -> service.createCrash(crash(), List.of(),
                List.of(submission(person((short) 1, null)))));

        assertTrue(thrown.getMessage().contains("must be known"), thrown.getMessage());
        verify(crashDao, never()).insert(any());
    }

    @Test
    void rejectsDuplicateVehicleNumbersBeforeInsertingAnyVehicle() {
        List<Vehicle> vehicles = List.of(vehicle((short) 1), vehicle((short) 1));
        List<PersonSubmission> persons =
                List.of(submission(person((short) 1, InjurySeverity.SLIGHT)));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> service.createCrash(crash(), vehicles, persons));

        assertTrue(thrown.getMessage().contains("Duplicate vehicleNumber"), thrown.getMessage());
        verify(vehicleDao, never()).insert(any());
    }

    @Test
    void rejectsPersonNamingAVehicleNumberThatIsNotInThePayload() {
        when(vehicleDao.insert(any())).thenReturn(10L);
        List<Vehicle> vehicles = List.of(vehicle((short) 1));
        List<PersonSubmission> persons = List.of(new PersonSubmission(
                person((short) 1, InjurySeverity.SLIGHT), (short) 2, null, null));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> service.createCrash(crash(), vehicles, persons));

        assertTrue(thrown.getMessage().contains("No vehicle with vehicleNumber 2"), thrown.getMessage());
        verify(personDao, never()).insert(any());
    }

    @Test
    void derivesSeverityFromTheWorstInjury() {
        when(crashDao.insert(any())).thenReturn(7L);
        when(crashDao.findById(7L)).thenReturn(Optional.of(crash()));

        service.createCrash(crash(), List.of(), List.of(
                submission(person((short) 1, InjurySeverity.SLIGHT)),
                submission(person((short) 2, InjurySeverity.FATAL))));

        ArgumentCaptor<Crash> inserted = ArgumentCaptor.forClass(Crash.class);
        verify(crashDao).insert(inserted.capture());
        assertEquals(CrashSeverity.FATAL, inserted.getValue().getSeverity());
    }

    @Test
    void ignoresUninjuredAndUnknownPersonsWhenDerivingSeverity() {
        when(crashDao.insert(any())).thenReturn(7L);
        when(crashDao.findById(7L)).thenReturn(Optional.of(crash()));

        service.createCrash(crash(), List.of(), List.of(
                submission(person((short) 1, InjurySeverity.NO_INJURY)),
                submission(person((short) 2, InjurySeverity.SERIOUS)),
                submission(person((short) 3, InjurySeverity.UNKNOWN))));

        ArgumentCaptor<Crash> inserted = ArgumentCaptor.forClass(Crash.class);
        verify(crashDao).insert(inserted.capture());
        assertEquals(CrashSeverity.SERIOUS, inserted.getValue().getSeverity());
    }

    @Test
    void resolvesVehicleNumbersToTheIdsOfTheInsertedVehicles() {
        when(crashDao.insert(any())).thenReturn(7L);
        when(crashDao.findById(7L)).thenReturn(Optional.of(crash()));
        when(vehicleDao.insert(any())).thenReturn(10L, 20L);

        service.createCrash(crash(),
                List.of(vehicle((short) 1), vehicle((short) 2)),
                List.of(new PersonSubmission(
                        person((short) 1, InjurySeverity.SLIGHT), (short) 1, (short) 2, null)));

        ArgumentCaptor<Vehicle> vehicles = ArgumentCaptor.forClass(Vehicle.class);
        verify(vehicleDao, times(2)).insert(vehicles.capture());
        assertEquals(List.of(7L, 7L),
                vehicles.getAllValues().stream().map(Vehicle::getCrashId).toList());

        ArgumentCaptor<Person> person = ArgumentCaptor.forClass(Person.class);
        verify(personDao).insert(person.capture());
        assertEquals(7L, person.getValue().getCrashId());
        assertEquals(10L, person.getValue().getOccupantVehicleId());
        assertEquals(20L, person.getValue().getStruckByVehicleId());
    }

    @Test
    void insertsAnAlcoholTestOnlyForThePersonThatHasOne() {
        when(crashDao.insert(any())).thenReturn(7L);
        when(crashDao.findById(7L)).thenReturn(Optional.of(crash()));
        when(personDao.insert(any())).thenReturn(100L, 200L);

        service.createCrash(crash(), List.of(), List.of(
                new PersonSubmission(person((short) 1, InjurySeverity.SLIGHT), null, null, alcoholTest()),
                submission(person((short) 2, InjurySeverity.SLIGHT))));

        ArgumentCaptor<AlcoholTest> test = ArgumentCaptor.forClass(AlcoholTest.class);
        verify(alcoholTestDao, times(1)).insert(test.capture());
        assertEquals(100L, test.getValue().getPersonId());
    }
}
