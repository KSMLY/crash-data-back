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
import static org.junit.jupiter.api.Assertions.assertNull;
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

    @Mock
    private LocationService locationService;

    @InjectMocks
    private CrashService service;

    // Nothing under test reads a crash field, so every column is left null
    private static Crash crash() {
        return new Crash(null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null);
    }

    private static Crash crashIn(Long districtId, Long municipalityId) {
        return new Crash(null, null, null, null, null, District.ref(districtId),
                Municipality.ref(municipalityId), null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null);
    }

    private static Municipality municipality(Long id, Long districtId) {
        return new Municipality(id, districtId, null, null);
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
    void searchCrashesPairsThePageWithTheTotalCount() {
        CrashSearch search = new CrashSearch(null, null, null, null, null, null, null, 0, 20, "crashDate", true);
        List<Crash> rows = List.of(crash());
        when(crashDao.search(search)).thenReturn(rows);
        when(crashDao.count(search)).thenReturn(57L);

        Page<Crash> page = service.searchCrashes(search);

        assertEquals(rows, page.content());
        assertEquals(57L, page.totalElements());
    }

    @Test
    void rejectsCrashWithNoPersons() {
        InvalidCrashException thrown = assertThrows(InvalidCrashException.class, ()
                -> service.createCrash(crash(), List.of(), List.of()));

        assertTrue(thrown.getMessage().contains("at least one person"), thrown.getMessage());
        verify(crashDao, never()).insert(any());
    }

    @Test
    void rejectsCrashWithNoInjuries() {
        InvalidCrashException thrown = assertThrows(InvalidCrashException.class, ()
                -> service.createCrash(crash(), List.of(),
                List.of(submission(person((short) 1, InjurySeverity.NO_INJURY)))));

        assertTrue(thrown.getMessage().contains("Only injury crashes are recorded"), thrown.getMessage());
        verify(crashDao, never()).insert(any());
    }

    @Test
    void rejectsCrashWhereAllInjuriesAreUnknown() {
        InvalidCrashException thrown = assertThrows(InvalidCrashException.class, ()
                -> service.createCrash(crash(), List.of(),
                List.of(submission(person((short) 1, InjurySeverity.UNKNOWN)))));

        assertTrue(thrown.getMessage().contains("must be known"), thrown.getMessage());
        verify(crashDao, never()).insert(any());
    }

    @Test
    void rejectsCrashWhereAPersonHasNoInjurySeverityAtAll() {
        InvalidCrashException thrown = assertThrows(InvalidCrashException.class, ()
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

        InvalidCrashException thrown = assertThrows(InvalidCrashException.class,
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

        InvalidCrashException thrown = assertThrows(InvalidCrashException.class,
                () -> service.createCrash(crash(), vehicles, persons));

        assertTrue(thrown.getMessage().contains("No vehicle with vehicleNumber 2"), thrown.getMessage());
        verify(personDao, never()).insert(any());
    }

    @Test
    void rejectsMunicipalityOutsideTheDistrict() {
        when(locationService.getMunicipalities(3L)).thenReturn(List.of(municipality(11L, 3L)));

        InvalidCrashException thrown = assertThrows(InvalidCrashException.class, () -> service.createCrash(
                crashIn(3L, 12L), List.of(), List.of(submission(person((short) 1, InjurySeverity.SLIGHT)))));

        assertTrue(thrown.getMessage().contains("Municipality 12 is not in district 3"), thrown.getMessage());
        verify(crashDao, never()).insert(any());
    }

    @Test
    void acceptsMunicipalityInsideTheDistrict() {
        when(locationService.getMunicipalities(3L)).thenReturn(List.of(municipality(12L, 3L)));
        when(crashDao.insert(any())).thenReturn(7L);
        when(crashDao.findById(7L)).thenReturn(Optional.of(crash()));

        service.createCrash(crashIn(3L, 12L), List.of(),
                List.of(submission(person((short) 1, InjurySeverity.SLIGHT))));

        verify(crashDao).insert(any());
    }

    @Test
    void rejectsPersonWhoIsBothOccupantAndStruckBy() {
        InvalidCrashException thrown = assertThrows(InvalidCrashException.class, () -> service.createCrash(
                crash(),
                List.of(vehicle((short) 1), vehicle((short) 2)),
                List.of(
                        submission(person((short) 1, InjurySeverity.SLIGHT)),
                        new PersonSubmission(person((short) 2, InjurySeverity.SLIGHT), (short) 1, (short) 2, null))));

        assertTrue(thrown.getMessage().contains("persons[1]"), thrown.getMessage());
        verify(crashDao, never()).insert(any());
        verify(vehicleDao, never()).insert(any());
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

        // One occupant and one pedestrian: a person cannot hold both roles (CD-33)
        service.createCrash(crash(),
                List.of(vehicle((short) 1), vehicle((short) 2)),
                List.of(
                        new PersonSubmission(person((short) 1, InjurySeverity.SLIGHT), (short) 1, null, null),
                        new PersonSubmission(person((short) 2, InjurySeverity.SLIGHT), null, (short) 2, null)));

        ArgumentCaptor<Vehicle> vehicles = ArgumentCaptor.forClass(Vehicle.class);
        verify(vehicleDao, times(2)).insert(vehicles.capture());
        assertEquals(List.of(7L, 7L),
                vehicles.getAllValues().stream().map(Vehicle::getCrashId).toList());

        ArgumentCaptor<Person> persons = ArgumentCaptor.forClass(Person.class);
        verify(personDao, times(2)).insert(persons.capture());
        Person occupant = persons.getAllValues().get(0);
        Person pedestrian = persons.getAllValues().get(1);
        assertEquals(7L, occupant.getCrashId());
        assertEquals(10L, occupant.getOccupantVehicleId());
        assertNull(occupant.getStruckByVehicleId());
        assertNull(pedestrian.getOccupantVehicleId());
        assertEquals(20L, pedestrian.getStruckByVehicleId());
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
