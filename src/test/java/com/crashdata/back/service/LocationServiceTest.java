package com.crashdata.back.service;

import com.crashdata.back.dao.DistrictDao;
import com.crashdata.back.dao.GovernorateDao;
import com.crashdata.back.dao.MunicipalityDao;
import com.crashdata.back.entity.District;
import com.crashdata.back.entity.Governorate;
import com.crashdata.back.entity.Municipality;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Mock
    private GovernorateDao governorateDao;

    @Mock
    private DistrictDao districtDao;

    @Mock
    private MunicipalityDao municipalityDao;

    @InjectMocks
    private LocationService service;

    @Test
    void returnsEveryGovernorate() {
        List<Governorate> all = List.of(new Governorate(1L, "Beirut", "بيروت"));
        when(governorateDao.findAll()).thenReturn(all);

        assertEquals(all, service.getGovernorates());
    }

    @Test
    void returnsEveryDistrictWhenNoGovernorateIsGiven() {
        List<District> all = List.of(new District(1L, 1L, "Baabda", "بعبدا"));
        when(districtDao.findAll()).thenReturn(all);

        assertEquals(all, service.getDistricts(null));
        verify(districtDao, never()).findByGovernorateId(null);
    }

    @Test
    void filtersDistrictsByGovernorate() {
        List<District> filtered = List.of(new District(1L, 5L, "Baabda", "بعبدا"));
        when(districtDao.findByGovernorateId(5L)).thenReturn(filtered);

        assertEquals(filtered, service.getDistricts(5L));
        verify(districtDao, never()).findAll();
    }

    @Test
    void returnsEveryMunicipalityWhenNoDistrictIsGiven() {
        List<Municipality> all = List.of(new Municipality(1L, 1L, "Hadath", "الحدث"));
        when(municipalityDao.findAll()).thenReturn(all);

        assertEquals(all, service.getMunicipalities(null));
        verify(municipalityDao, never()).findByDistrictId(null);
    }

    @Test
    void filtersMunicipalitiesByDistrict() {
        List<Municipality> filtered = List.of(new Municipality(1L, 5L, "Hadath", "الحدث"));
        when(municipalityDao.findByDistrictId(5L)).thenReturn(filtered);

        assertEquals(filtered, service.getMunicipalities(5L));
        verify(municipalityDao, never()).findAll();
    }
}
