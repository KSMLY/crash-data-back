SELECT c.id, police_ref, ref_year, crash_date, crash_time,
       c.district_id, d.governorate_id, d.name_en AS district_name_en, d.name_ar AS district_name_ar,
       c.municipality_id, m.name_en AS municipality_name_en, m.name_ar AS municipality_name_ar,
       latitude, longitude, crash_type_code, impact_type_code, weather_code, light_code, severity_code,
       roadway_type_code, functional_class_code, speed_limit_kmh,
       obstacle_present_code, surface_condition_code, junction_type_code,
       curve_code, grade_code
FROM crash c
JOIN district d ON d.id = c.district_id
LEFT JOIN municipality m ON m.id = c.municipality_id
WHERE c.id = :id
