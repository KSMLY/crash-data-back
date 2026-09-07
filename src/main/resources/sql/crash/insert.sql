INSERT INTO crash (police_ref, ref_year, crash_date, crash_time,
                   district_id, municipality_id, latitude, longitude,
                   crash_type_code, impact_type_code, weather_code, light_code, severity_code,
                   roadway_type_code, functional_class_code, speed_limit_kmh,
                   obstacle_present_code, surface_condition_code, junction_type_code,
                   curve_code, grade_code)
VALUES (:police_ref, :ref_year, :crash_date, :crash_time,
        :district_id, :municipality_id, :latitude, :longitude,
        :crash_type_code, :impact_type_code, :weather_code, :light_code, :severity_code,
        :roadway_type_code, :functional_class_code, :speed_limit_kmh,
        :obstacle_present_code, :surface_condition_code, :junction_type_code,
        :curve_code, :grade_code)
