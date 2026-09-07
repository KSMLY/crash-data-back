package com.crashdata.back.controller;

import com.crashdata.back.entity.District;
import com.crashdata.back.entity.Governorate;
import com.crashdata.back.entity.Municipality;
import com.crashdata.back.service.LocationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LocationController.class)
class LocationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    LocationService locationService;

    // nameEn and nameAr were swapped once already, so the fixtures keep them tellable apart
    @Test
    void getGovernoratesMapsEveryField() throws Exception {
        when(locationService.getGovernorates())
                .thenReturn(List.of(new Governorate(1L, "Beirut", "بيروت")));

        mockMvc.perform(get("/governorates"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [{"id": 1, "nameEn": "Beirut", "nameAr": "بيروت"}]
                        """, JsonCompareMode.STRICT));
    }

    @Test
    void getDistrictsMapsEveryFieldAndPassesNullWhenGovernorateIdIsAbsent() throws Exception {
        when(locationService.getDistricts(null))
                .thenReturn(List.of(new District(2L, 1L, "Baabda", "بعبدا")));

        mockMvc.perform(get("/districts"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [{"id": 2, "governorateId": 1, "nameEn": "Baabda", "nameAr": "بعبدا"}]
                        """, JsonCompareMode.STRICT));

        verify(locationService).getDistricts(null);
    }

    @Test
    void getDistrictsPassesGovernorateIdWhenPresent() throws Exception {
        when(locationService.getDistricts(1L)).thenReturn(List.of());

        mockMvc.perform(get("/districts?governorateId=1"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]", JsonCompareMode.STRICT));

        verify(locationService).getDistricts(1L);
    }

    @Test
    void getMunicipalitiesMapsEveryFieldAndPassesNullWhenDistrictIdIsAbsent() throws Exception {
        when(locationService.getMunicipalities(null))
                .thenReturn(List.of(new Municipality(3L, 2L, "Hadath", "الحدث")));

        mockMvc.perform(get("/municipalities"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [{"id": 3, "districtId": 2, "nameEn": "Hadath", "nameAr": "الحدث"}]
                        """, JsonCompareMode.STRICT));

        verify(locationService).getMunicipalities(null);
    }

    @Test
    void getMunicipalitiesPassesDistrictIdWhenPresent() throws Exception {
        when(locationService.getMunicipalities(2L)).thenReturn(List.of());

        mockMvc.perform(get("/municipalities?districtId=2"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]", JsonCompareMode.STRICT));

        verify(locationService).getMunicipalities(2L);
    }
}
