package com.crashdata.back.dao;

import com.crashdata.back.entity.District;
import com.crashdata.back.entity.Governorate;
import com.crashdata.back.entity.Municipality;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The three administrative-division DAOs share a context here because they are one
 * layer and hold no logic beyond a select and a mapper. Flyway already seeded roughly
 * a thousand rows, so every test seeds its own and filters by the ZZ prefix, which
 * also sorts last under ORDER BY name_en.
 */
@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({GovernorateDao.class, DistrictDao.class, MunicipalityDao.class})
class LocationDaoTest {

    private static final String PREFIX = "ZZ CD28";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    GovernorateDao governorateDao;

    @Autowired
    DistrictDao districtDao;

    @Autowired
    MunicipalityDao municipalityDao;

    private long seedGovernorate(String suffix) {
        jdbcTemplate.update("INSERT INTO governorate (name_en, name_ar) VALUES (?, ?)",
                PREFIX + " " + suffix + " En", PREFIX + " " + suffix + " Ar");
        return jdbcTemplate.queryForObject("SELECT id FROM governorate WHERE name_en = ?",
                Long.class, PREFIX + " " + suffix + " En");
    }

    private long seedDistrict(long governorateId, String suffix) {
        jdbcTemplate.update("INSERT INTO district (governorate_id, name_en, name_ar) VALUES (?, ?, ?)",
                governorateId, PREFIX + " " + suffix + " En", PREFIX + " " + suffix + " Ar");
        return jdbcTemplate.queryForObject("SELECT id FROM district WHERE name_en = ?",
                Long.class, PREFIX + " " + suffix + " En");
    }

    private long seedMunicipality(long districtId, String suffix) {
        jdbcTemplate.update("INSERT INTO municipality (district_id, name_en, name_ar) VALUES (?, ?, ?)",
                districtId, PREFIX + " " + suffix + " En", PREFIX + " " + suffix + " Ar");
        return jdbcTemplate.queryForObject("SELECT id FROM municipality WHERE name_en = ?",
                Long.class, PREFIX + " " + suffix + " En");
    }

    private static <T> List<T> seeded(List<T> all, java.util.function.Function<T, String> nameEn) {
        return all.stream().filter(row -> nameEn.apply(row).startsWith(PREFIX)).toList();
    }

    // name_en and name_ar have the same type and sit next to each other, so a swap here
    // compiles and passes every unit test — it shipped once already
    @Test
    void governorateFindAllMapsEveryColumn() {
        long id = seedGovernorate("A");

        Governorate governorate = seeded(governorateDao.findAll(), Governorate::getNameEn).getFirst();

        assertEquals(id, governorate.getId());
        assertEquals(PREFIX + " A En", governorate.getNameEn());
        assertEquals(PREFIX + " A Ar", governorate.getNameAr());
    }

    @Test
    void governorateFindAllOrdersByNameEn() {
        seedGovernorate("B");
        seedGovernorate("A");

        List<String> names = seeded(governorateDao.findAll(), Governorate::getNameEn).stream()
                .map(Governorate::getNameEn)
                .toList();

        assertEquals(List.of(PREFIX + " A En", PREFIX + " B En"), names);
    }

    @Test
    void districtFindAllMapsEveryColumn() {
        long governorateId = seedGovernorate("G");
        long id = seedDistrict(governorateId, "D");

        District district = seeded(districtDao.findAll(), District::getNameEn).getFirst();

        assertEquals(id, district.getId());
        assertEquals(governorateId, district.getGovernorateId());
        assertEquals(PREFIX + " D En", district.getNameEn());
        assertEquals(PREFIX + " D Ar", district.getNameAr());
    }

    @Test
    void districtFindByGovernorateIdReturnsOnlyThatGovernoratesDistricts() {
        long governorateId = seedGovernorate("G1");
        long otherGovernorateId = seedGovernorate("G2");
        seedDistrict(governorateId, "D1");
        seedDistrict(otherGovernorateId, "D2");

        List<District> districts = districtDao.findByGovernorateId(governorateId);

        assertEquals(1, districts.size());
        assertEquals(PREFIX + " D1 En", districts.getFirst().getNameEn());
        assertEquals(governorateId, districts.getFirst().getGovernorateId());
    }

    @Test
    void districtFindByGovernorateIdOrdersByNameEn() {
        long governorateId = seedGovernorate("G");
        seedDistrict(governorateId, "DB");
        seedDistrict(governorateId, "DA");

        List<String> names = districtDao.findByGovernorateId(governorateId).stream()
                .map(District::getNameEn)
                .toList();

        assertEquals(List.of(PREFIX + " DA En", PREFIX + " DB En"), names);
    }

    @Test
    void districtFindByGovernorateIdReturnsEmptyForAGovernorateWithNoDistricts() {
        assertEquals(List.of(), districtDao.findByGovernorateId(seedGovernorate("G")));
    }

    @Test
    void municipalityFindAllMapsEveryColumn() {
        long districtId = seedDistrict(seedGovernorate("G"), "D");
        long id = seedMunicipality(districtId, "M");

        Municipality municipality = seeded(municipalityDao.findAll(), Municipality::getNameEn).getFirst();

        assertEquals(id, municipality.getId());
        assertEquals(districtId, municipality.getDistrictId());
        assertEquals(PREFIX + " M En", municipality.getNameEn());
        assertEquals(PREFIX + " M Ar", municipality.getNameAr());
    }

    @Test
    void municipalityFindByDistrictIdReturnsOnlyThatDistrictsMunicipalities() {
        long governorateId = seedGovernorate("G");
        long districtId = seedDistrict(governorateId, "D1");
        long otherDistrictId = seedDistrict(governorateId, "D2");
        seedMunicipality(districtId, "M1");
        seedMunicipality(otherDistrictId, "M2");

        List<Municipality> municipalities = municipalityDao.findByDistrictId(districtId);

        assertEquals(1, municipalities.size());
        assertEquals(PREFIX + " M1 En", municipalities.getFirst().getNameEn());
        assertEquals(districtId, municipalities.getFirst().getDistrictId());
    }

    @Test
    void municipalityFindByDistrictIdOrdersByNameEn() {
        long districtId = seedDistrict(seedGovernorate("G"), "D");
        seedMunicipality(districtId, "MB");
        seedMunicipality(districtId, "MA");

        List<String> names = municipalityDao.findByDistrictId(districtId).stream()
                .map(Municipality::getNameEn)
                .toList();

        assertEquals(List.of(PREFIX + " MA En", PREFIX + " MB En"), names);
    }

    // findAll on these tables is what the cached /governorates and /districts endpoints
    // serve, so it has to reach the Flyway-seeded rows too, not just the test's own
    @Test
    void findAllReachesTheSeededAdministrativeDivisions() {
        assertTrue(governorateDao.findAll().size() > 1);
        assertTrue(districtDao.findAll().size() > 1);
        assertTrue(municipalityDao.findAll().size() > 1);
    }
}
