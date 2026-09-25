package com.crashdata.back.controller;

import com.crashdata.back.code.*;
import com.crashdata.back.entity.AlcoholTest;
import com.crashdata.back.entity.Crash;
import com.crashdata.back.entity.CrashDetail;
import com.crashdata.back.entity.CrashPoint;
import com.crashdata.back.entity.CrashSearch;
import com.crashdata.back.entity.District;
import com.crashdata.back.entity.Municipality;
import com.crashdata.back.entity.Page;
import com.crashdata.back.entity.Person;
import com.crashdata.back.entity.PersonSubmission;
import com.crashdata.back.entity.Vehicle;
import com.crashdata.back.service.CrashService;
import com.crashdata.back.service.InvalidCrashException;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CrashController.class)
@AutoConfigureMockMvc(addFilters = false)
class CrashControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CrashService crashService;

    // Every field gets a distinct value: the same-typed pairs (refYear/speedLimitKmh,
    // latitude/longitude, and each nested id/name pair) swap silently in a 23-arg constructor
    private static Crash crash() {
        return new Crash(
                7L,
                "PR-100",
                (short) 2024,
                LocalDate.of(2024, 3, 14),
                LocalTime.of(13, 45),
                new District(11L, 9L, "Baabda", "بعبدا"),
                new Municipality(22L, 11L, "Hadath", "الحدث"),
                new BigDecimal("33.5"),
                new BigDecimal("35.25"),
                CrashType.ANIMAL,
                ImpactType.REAR_TO_SIDE,
                Weather.FOG,
                Light.TWILIGHT,
                CrashSeverity.SLIGHT,
                RoadwayType.RESTRICTED_ROAD,
                FunctionalClass.COLLECTOR,
                (short) 80,
                ObstaclePresent.UNKNOWN,
                SurfaceCondition.FLOOD,
                JunctionType.NOT_AT_GRADE,
                Curve.OPEN,
                Grade.NO,
                Set.of(TrafficControl.OTHER, TrafficControl.GIVE_WAY, TrafficControl.SIGNAL_WORKING));
    }

    private static Vehicle vehicle() {
        return new Vehicle(101L, 7L, (short) 1, VehicleType.BUS, "Toyota", "Corolla",
                (short) 2019, 1600, SpecialFunction.TAXI, Manoeuvre.OVERTAKING);
    }

    private static Person person() {
        return new Person(201L, 7L, (short) 1, 101L, 102L, LocalDate.of(1990, 6, 5),
                Sex.FEMALE, RoadUserType.PASSENGER, SeatRow.REAR, SeatPosition.RIGHT,
                InjurySeverity.SLIGHT, Restraint.NO_RESTRAINTS_USED, Helmet.NOT_APPLICABLE,
                PedManoeuvre.CROSSING, AlcoholSuspected.YES, DrugUse.EVIDENCE,
                LicenceStatus.ISSUED, LocalDate.of(2010, 1, 20));
    }

    private static AlcoholTest alcoholTest() {
        return new AlcoholTest(201L, TestStatus.GIVEN, TestType.BREATH, ResultStatus.AVAILABLE,
                new BigDecimal("0.85"));
    }

    private static CrashDetail detail() {
        return new CrashDetail(crash(), List.of(vehicle()), List.of(person()),
                Map.of(201L, alcoholTest()));
    }

    // Braces are left off so the same fields can be reused flat inside the detail response,
    // which is what @JsonUnwrapped produces
    private static final String CRASH_FIELDS = """
            "id": 7,
            "policeRef": "PR-100",
            "refYear": 2024,
            "crashDate": "2024-03-14",
            "crashTime": "13:45:00",
            "district": {"id": 11, "governorateId": 9, "nameEn": "Baabda", "nameAr": "بعبدا"},
            "municipality": {"id": 22, "districtId": 11, "nameEn": "Hadath", "nameAr": "الحدث"},
            "latitude": 33.5,
            "longitude": 35.25,
            "crashType": "ANIMAL",
            "impactType": "REAR_TO_SIDE",
            "weather": "FOG",
            "light": "TWILIGHT",
            "severity": "SLIGHT",
            "roadwayType": "RESTRICTED_ROAD",
            "functionalClass": "COLLECTOR",
            "speedLimitKmh": 80,
            "obstaclePresent": "UNKNOWN",
            "surfaceCondition": "FLOOD",
            "junctionType": "NOT_AT_GRADE",
            "curve": "OPEN",
            "grade": "NO",
            "trafficControls": ["GIVE_WAY", "SIGNAL_WORKING", "OTHER"]
            """;

    private static final String CRASH_JSON = "{" + CRASH_FIELDS + "}";

    private static final String DETAIL_JSON = "{" + CRASH_FIELDS + """
            ,
            "vehicles": [
              {
                "id": 101,
                "vehicleNumber": 1,
                "vehicleType": "BUS",
                "make": "Toyota",
                "model": "Corolla",
                "modelYear": 2019,
                "engineCc": 1600,
                "specialFunction": "TAXI",
                "manoeuvre": "OVERTAKING"
              }
            ],
            "persons": [
              {
                "id": 201,
                "personNumber": 1,
                "occupantVehicleId": 101,
                "struckByVehicleId": 102,
                "dateOfBirth": "1990-06-05",
                "sex": "FEMALE",
                "roadUserType": "PASSENGER",
                "seatRow": "REAR",
                "seatPosition": "RIGHT",
                "injurySeverity": "SLIGHT",
                "restraint": "NO_RESTRAINTS_USED",
                "helmet": "NOT_APPLICABLE",
                "pedManoeuvre": "CROSSING",
                "alcoholSuspected": "YES",
                "drugUse": "EVIDENCE",
                "licenceStatus": "ISSUED",
                "licenceIssueDate": "2010-01-20",
                "alcoholTest": {
                  "testStatus": "GIVEN",
                  "testType": "BREATH",
                  "resultStatus": "AVAILABLE",
                  "resultValue": 0.85
                }
              }
            ]
            """ + "}";

    // Mirrors crash()/vehicle()/person(), minus the fields the server owns (id, severity).
    // trafficControls are deliberately unsorted, so the response proves the controller sorts.
    private static final String REQUEST_JSON = """
            {
              "policeRef": "PR-100",
              "refYear": 2024,
              "crashDate": "2024-03-14",
              "crashTime": "13:45:00",
              "districtId": 11,
              "municipalityId": 22,
              "latitude": 33.5,
              "longitude": 35.25,
              "crashType": "ANIMAL",
              "impactType": "REAR_TO_SIDE",
              "weather": "FOG",
              "light": "TWILIGHT",
              "roadwayType": "RESTRICTED_ROAD",
              "functionalClass": "COLLECTOR",
              "speedLimitKmh": 80,
              "obstaclePresent": "UNKNOWN",
              "surfaceCondition": "FLOOD",
              "junctionType": "NOT_AT_GRADE",
              "curve": "OPEN",
              "grade": "NO",
              "trafficControls": ["OTHER", "GIVE_WAY", "SIGNAL_WORKING"],
              "vehicles": [
                {
                  "vehicleNumber": 1,
                  "vehicleType": "BUS",
                  "make": "Toyota",
                  "model": "Corolla",
                  "modelYear": 2019,
                  "engineCc": 1600,
                  "specialFunction": "TAXI",
                  "manoeuvre": "OVERTAKING"
                }
              ],
              "persons": [
                {
                  "personNumber": 1,
                  "occupantVehicleNumber": 1,
                  "struckByVehicleNumber": 2,
                  "dateOfBirth": "1990-06-05",
                  "sex": "FEMALE",
                  "roadUserType": "PASSENGER",
                  "seatRow": "REAR",
                  "seatPosition": "RIGHT",
                  "injurySeverity": "SLIGHT",
                  "restraint": "NO_RESTRAINTS_USED",
                  "helmet": "NOT_APPLICABLE",
                  "pedManoeuvre": "CROSSING",
                  "alcoholSuspected": "YES",
                  "drugUse": "EVIDENCE",
                  "licenceStatus": "ISSUED",
                  "licenceIssueDate": "2010-01-20",
                  "alcoholTest": {
                    "testStatus": "GIVEN",
                    "testType": "BREATH",
                    "resultStatus": "AVAILABLE",
                    "resultValue": 0.85
                  }
                }
              ]
            }
            """;

    // Only the @NotNull fields: no optional scalars, no vehicles, and a person with no alcohol test
    private static final String MINIMAL_REQUEST_JSON = """
            {
              "policeRef": "PR-100",
              "refYear": 2024,
              "districtId": 11,
              "crashType": "ANIMAL",
              "impactType": "REAR_TO_SIDE",
              "weather": "FOG",
              "light": "TWILIGHT",
              "roadwayType": "RESTRICTED_ROAD",
              "speedLimitKmh": 80,
              "obstaclePresent": "UNKNOWN",
              "surfaceCondition": "FLOOD",
              "junctionType": "NOT_AT_GRADE",
              "curve": "OPEN",
              "grade": "NO",
              "persons": [
                {
                  "personNumber": 1,
                  "sex": "FEMALE",
                  "roadUserType": "PASSENGER",
                  "seatRow": "REAR",
                  "seatPosition": "RIGHT",
                  "injurySeverity": "SLIGHT",
                  "restraint": "NO_RESTRAINTS_USED",
                  "helmet": "NOT_APPLICABLE",
                  "alcoholSuspected": "YES",
                  "drugUse": "EVIDENCE"
                }
              ]
            }
            """;

    // Every field dropped this way sits before the last property, so the JSON stays valid
    private static String without(String... fields) {
        List<String> dropped = List.of(fields);
        return REQUEST_JSON.lines()
                .filter(line -> dropped.stream().noneMatch(f -> line.trim().startsWith("\"" + f + "\"")))
                .collect(Collectors.joining("\n"));
    }

    @Test
    void searchCrashesWrapsTheRowsInAPageWithDefaults() throws Exception {
        when(crashService.searchCrashes(any())).thenReturn(new Page<>(List.of(crash()), 41));

        mockMvc.perform(get("/crashes"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"content\": [" + CRASH_JSON + "], "
                        + "\"page\": 0, \"size\": 20, \"totalElements\": 41}", JsonCompareMode.STRICT));

        CrashSearch search = searchPassedToService();
        assertNull(search.q());
        assertEquals("crashDate", search.sort());
        assertEquals(true, search.descending());
    }

    @Test
    void searchCrashesPassesEveryFilterToTheService() throws Exception {
        when(crashService.searchCrashes(any())).thenReturn(new Page<>(List.of(), 0));

        mockMvc.perform(get("/crashes")
                        .param("q", " PR-1 ")
                        .param("severity", "FATAL")
                        .param("crashType", "ANIMAL")
                        .param("districtId", "3")
                        .param("municipalityId", "9")
                        .param("from", "2024-01-01")
                        .param("to", "2024-12-31")
                        .param("page", "2")
                        .param("size", "50")
                        .param("sort", "policeRef")
                        .param("order", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(2))
                .andExpect(jsonPath("$.size").value(50));

        CrashSearch search = searchPassedToService();
        assertEquals("PR-1", search.q());
        assertEquals(CrashSeverity.FATAL, search.severity());
        assertEquals(CrashType.ANIMAL, search.crashType());
        assertEquals(3L, search.districtId());
        assertEquals(9L, search.municipalityId());
        assertEquals(LocalDate.of(2024, 1, 1), search.from());
        assertEquals(LocalDate.of(2024, 12, 31), search.to());
        assertEquals(2, search.page());
        assertEquals(50, search.size());
        assertEquals("policeRef", search.sort());
        assertEquals(true, search.descending());
    }

    @Test
    void searchCrashesSortsAscendingByDefaultExceptForDates() throws Exception {
        when(crashService.searchCrashes(any())).thenReturn(new Page<>(List.of(), 0));

        mockMvc.perform(get("/crashes").param("sort", "severity")).andExpect(status().isOk());

        assertEquals(false, searchPassedToService().descending());
    }

    @Test
    void searchCrashesRejectsAnOversizedPage() throws Exception {
        mockMvc.perform(get("/crashes").param("size", "500"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("size must be less than or equal to 100"));

        verify(crashService, never()).searchCrashes(any());
    }

    @Test
    void searchCrashesRejectsASortKeyOutsideTheWhitelist() throws Exception {
        mockMvc.perform(get("/crashes").param("sort", "crash_date"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("sort must match \"crashDate|severity|policeRef\""));
    }

    @Test
    void searchCrashesListsTheAllowedValuesForABadEnum() throws Exception {
        mockMvc.perform(get("/crashes").param("severity", "BOGUS"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("severity must be one of: FATAL, SERIOUS, SLIGHT"));
    }

    @Test
    void searchCrashesRejectsANonNumericPage() throws Exception {
        mockMvc.perform(get("/crashes").param("page", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("page is not valid."));
    }

    private CrashSearch searchPassedToService() {
        ArgumentCaptor<CrashSearch> captor = ArgumentCaptor.forClass(CrashSearch.class);
        verify(crashService).searchCrashes(captor.capture());
        return captor.getValue();
    }

    @Test
    void getCrashFlattensCrashFieldsAndNestsVehiclesAndPersons() throws Exception {
        when(crashService.getCrashDetail(7L)).thenReturn(Optional.of(detail()));

        mockMvc.perform(get("/crashes/7"))
                .andExpect(status().isOk())
                .andExpect(content().json(DETAIL_JSON, JsonCompareMode.STRICT));
    }

    @Test
    void getCrashReturnsNotFoundForUnknownId() throws Exception {
        when(crashService.getCrashDetail(404L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/crashes/404"))
                .andExpect(status().isNotFound());
    }

    // NumberFormatException extends IllegalArgumentException, so before CD-32 this leaked
    // the raw 'For input string: "abc"' message as the response body
    @Test
    void getCrashRejectsNonNumericId() throws Exception {
        mockMvc.perform(get("/crashes/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("id must be a number."));

        verify(crashService, never()).getCrashDetail(any());
    }

    @Test
    void getPointsMapsEveryField() throws Exception {
        LocalDate from = LocalDate.of(2026, 8, 23);
        LocalDate to = LocalDate.of(2026, 9, 21);
        when(crashService.getPoints(from, to)).thenReturn(List.of(new CrashPoint(
                7L, "2026-000123", LocalDate.of(2026, 9, 2),
                new BigDecimal("33.888630"), new BigDecimal("35.495480"), CrashSeverity.FATAL)));

        mockMvc.perform(get("/crashes/points").param("from", "2026-08-23").param("to", "2026-09-21"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(7))
                .andExpect(jsonPath("$[0].policeRef").value("2026-000123"))
                .andExpect(jsonPath("$[0].crashDate").value("2026-09-02"))
                .andExpect(jsonPath("$[0].latitude").value(33.888630))
                .andExpect(jsonPath("$[0].longitude").value(35.495480))
                .andExpect(jsonPath("$[0].severity").value("FATAL"));
    }

    @Test
    void getPointsDefaultsToTheLastThirtyDays() throws Exception {
        when(crashService.getPoints(any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/crashes/points"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(crashService).getPoints(LocalDate.now().minusDays(29), LocalDate.now());
    }

    @Test
    void getPointsRejectsABadDate() throws Exception {
        mockMvc.perform(get("/crashes/points").param("from", "yesterday"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("from is not valid."));

        verify(crashService, never()).getPoints(any(), any());
    }

    @Test
    void exportCrashesWritesAHeaderAndOneLinePerCrashAsAnAttachment() throws Exception {
        doAnswer(invocation -> {
            Consumer<Crash> sink = invocation.getArgument(1);
            sink.accept(crash());
            return null;
        }).when(crashService).exportCrashes(any(), any());

        mockMvc.perform(get("/crashes/export"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/csv;charset=UTF-8"))
                .andExpect(header().string("Content-Disposition",
                        "attachment; filename=\"crashes-" + LocalDate.now() + ".csv\""))
                .andExpect(content().string(
                        "police_ref,crash_date,crash_time,district,municipality,severity,crash_type,latitude,longitude\r\n"
                                + "PR-100,2024-03-14,13:45,Baabda,Hadath,SLIGHT,ANIMAL,33.5,35.25\r\n"));
    }

    @Test
    void exportCrashesPassesTheSameFiltersAsSearch() throws Exception {
        mockMvc.perform(get("/crashes/export")
                        .param("q", "PR").param("severity", "FATAL").param("districtId", "11")
                        .param("from", "2024-01-01").param("sort", "policeRef"))
                .andExpect(status().isOk());

        ArgumentCaptor<CrashSearch> captor = ArgumentCaptor.forClass(CrashSearch.class);
        verify(crashService).exportCrashes(captor.capture(), any());
        CrashSearch search = captor.getValue();
        assertEquals("PR", search.q());
        assertEquals(CrashSeverity.FATAL, search.severity());
        assertEquals(11L, search.districtId());
        assertEquals(LocalDate.of(2024, 1, 1), search.from());
        assertEquals("policeRef", search.sort());
    }

    @Test
    void exportCrashesRejectsABadFilter() throws Exception {
        mockMvc.perform(get("/crashes/export").param("sort", "id"))
                .andExpect(status().isBadRequest());

        verify(crashService, never()).exportCrashes(any(), any());
    }

    @Test
    void createCrashReturnsCreatedWithLocationAndBody() throws Exception {
        when(crashService.createCrash(any(), any(), any())).thenReturn(detail());

        mockMvc.perform(post("/crashes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/crashes/7"))
                .andExpect(content().json(DETAIL_JSON, JsonCompareMode.STRICT));
    }

    @Test
    void createCrashMapsRequestToEntities() throws Exception {
        when(crashService.createCrash(any(), any(), any())).thenReturn(detail());

        mockMvc.perform(post("/crashes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_JSON))
                .andExpect(status().isCreated());

        ArgumentCaptor<Crash> crashArg = ArgumentCaptor.forClass(Crash.class);
        ArgumentCaptor<List<Vehicle>> vehiclesArg = ArgumentCaptor.captor();
        ArgumentCaptor<List<PersonSubmission>> personsArg = ArgumentCaptor.captor();
        verify(crashService).createCrash(crashArg.capture(), vehiclesArg.capture(), personsArg.capture());

        Crash sent = crashArg.getValue();
        assertNull(sent.getId());
        assertEquals("PR-100", sent.getPoliceRef());
        assertEquals((short) 2024, sent.getRefYear());
        assertEquals((short) 80, sent.getSpeedLimitKmh());
        assertEquals(11L, sent.getDistrict().getId());
        assertEquals(22L, sent.getMunicipality().getId());
        assertEquals(new BigDecimal("33.5"), sent.getLatitude());
        assertEquals(new BigDecimal("35.25"), sent.getLongitude());
        assertEquals(LocalDate.of(2024, 3, 14), sent.getCrashDate());
        assertEquals(LocalTime.of(13, 45), sent.getCrashTime());
        assertEquals(CrashType.ANIMAL, sent.getCrashType());
        assertEquals(ImpactType.REAR_TO_SIDE, sent.getImpactType());
        assertEquals(Weather.FOG, sent.getWeather());
        assertEquals(Light.TWILIGHT, sent.getLight());
        assertEquals(RoadwayType.RESTRICTED_ROAD, sent.getRoadwayType());
        assertEquals(FunctionalClass.COLLECTOR, sent.getFunctionalClass());
        assertEquals(ObstaclePresent.UNKNOWN, sent.getObstaclePresent());
        assertEquals(SurfaceCondition.FLOOD, sent.getSurfaceCondition());
        assertEquals(JunctionType.NOT_AT_GRADE, sent.getJunctionType());
        assertEquals(Curve.OPEN, sent.getCurve());
        assertEquals(Grade.NO, sent.getGrade());
        // Severity is derived by the service, so the controller must not send one
        assertNull(sent.getSeverity());
        assertEquals(Set.of(TrafficControl.OTHER, TrafficControl.GIVE_WAY, TrafficControl.SIGNAL_WORKING),
                sent.getTrafficControls());

        Vehicle sentVehicle = vehiclesArg.getValue().getFirst();
        assertEquals((short) 1, sentVehicle.getVehicleNumber());
        assertEquals(VehicleType.BUS, sentVehicle.getVehicleType());
        assertEquals("Toyota", sentVehicle.getMake());
        assertEquals("Corolla", sentVehicle.getModel());
        assertEquals((short) 2019, sentVehicle.getModelYear());
        assertEquals(1600, sentVehicle.getEngineCc());
        assertEquals(SpecialFunction.TAXI, sentVehicle.getSpecialFunction());
        assertEquals(Manoeuvre.OVERTAKING, sentVehicle.getManoeuvre());

        PersonSubmission sentPerson = personsArg.getValue().getFirst();
        assertEquals((short) 1, sentPerson.occupantVehicleNumber());
        assertEquals((short) 2, sentPerson.struckByVehicleNumber());
        assertEquals(TestType.BREATH, sentPerson.alcoholTest().getTestType());
        assertEquals(new BigDecimal("0.85"), sentPerson.alcoholTest().getResultValue());
        assertEquals((short) 1, sentPerson.person().getPersonNumber());
        assertEquals(Sex.FEMALE, sentPerson.person().getSex());
        assertEquals(InjurySeverity.SLIGHT, sentPerson.person().getInjurySeverity());
        assertEquals(LocalDate.of(1990, 6, 5), sentPerson.person().getDateOfBirth());
        assertEquals(LocalDate.of(2010, 1, 20), sentPerson.person().getLicenceIssueDate());
    }

    @Test
    void createCrashAcceptsRequestWithoutOptionalFields() throws Exception {
        when(crashService.createCrash(any(), any(), any()))
                .thenReturn(new CrashDetail(crash(), List.of(), List.of(person()), Map.of()));

        mockMvc.perform(post("/crashes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MINIMAL_REQUEST_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.vehicles", hasSize(0)))
                .andExpect(jsonPath("$.persons[0].alcoholTest").isEmpty());

        ArgumentCaptor<Crash> crashArg = ArgumentCaptor.forClass(Crash.class);
        ArgumentCaptor<List<Vehicle>> vehiclesArg = ArgumentCaptor.captor();
        ArgumentCaptor<List<PersonSubmission>> personsArg = ArgumentCaptor.captor();
        verify(crashService).createCrash(crashArg.capture(), vehiclesArg.capture(), personsArg.capture());

        // An omitted array becomes an empty one rather than a null, so the service never sees a null list
        assertEquals(List.of(), vehiclesArg.getValue());
        assertEquals(Set.of(), crashArg.getValue().getTrafficControls());
        assertNull(crashArg.getValue().getFunctionalClass());
        assertNull(crashArg.getValue().getCrashDate());
        assertNull(crashArg.getValue().getMunicipality());
        assertNull(personsArg.getValue().getFirst().alcoholTest());
        assertNull(personsArg.getValue().getFirst().person().getPedManoeuvre());
    }

    @Test
    void createCrashRejectsMissingRequiredFields() throws Exception {
        mockMvc.perform(post("/crashes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(without("policeRef", "districtId")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("districtId must not be null, policeRef must not be null"));

        verify(crashService, never()).createCrash(any(), any(), any());
    }

    // @Valid sits on the List field, not on its element type, so this proves it still cascades
    @Test
    void createCrashRejectsMissingFieldsInsideVehiclesAndPersons() throws Exception {
        mockMvc.perform(post("/crashes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_JSON
                                .replace("\"vehicleNumber\": 1,", "")
                                .replace("\"sex\": \"FEMALE\",", "")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        "persons[0].sex must not be null, vehicles[0].vehicleNumber must not be null"));

        verify(crashService, never()).createCrash(any(), any(), any());
    }

    @Test
    void createCrashRejectsUnknownEnumValue() throws Exception {
        mockMvc.perform(post("/crashes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_JSON.replace("\"weather\": \"FOG\"", "\"weather\": \"GOAT\"")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        "weather must be one of: CLEAR, RAIN, SNOW, FOG, SLEET, SEVERE_WINDS, OTHER, UNKNOWN"));

        verify(crashService, never()).createCrash(any(), any(), any());
    }

    @Test
    void createCrashReturnsBadRequestWhenServiceRejects() throws Exception {
        when(crashService.createCrash(any(), any(), any()))
                .thenThrow(new InvalidCrashException("A crash must have at least one person."));

        mockMvc.perform(post("/crashes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("A crash must have at least one person."));
    }

    // A plain IllegalArgumentException is a bug below the controller, not a client error;
    // with no handler for it MockMvc rethrows it instead of producing a 400
    @Test
    void createCrashDoesNotReportUnexpectedIllegalArgumentAsClientError() {
        when(crashService.createCrash(any(), any(), any()))
                .thenThrow(new IllegalArgumentException("internal detail"));

        ServletException thrown = assertThrows(ServletException.class, () -> mockMvc.perform(post("/crashes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(REQUEST_JSON)));

        assertInstanceOf(IllegalArgumentException.class, thrown.getCause());
    }

    @Test
    void createCrashReturnsConflictOnDuplicatePoliceRef() throws Exception {
        when(crashService.createCrash(any(), any(), any()))
                .thenThrow(new DuplicateKeyException("unique index violated"));

        mockMvc.perform(post("/crashes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().string("A crash with this police_ref already exists for that year."));
    }
}
