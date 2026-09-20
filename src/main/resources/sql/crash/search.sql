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
WHERE (:severity IS NULL OR severity_code = :severity)
  AND (:crash_type IS NULL OR crash_type_code = :crash_type)
  AND (:district_id IS NULL OR c.district_id = :district_id)
  AND (:from_date IS NULL OR crash_date >= :from_date)
  AND (:to_date IS NULL OR crash_date <= :to_date)
  AND (:q IS NULL OR police_ref LIKE :q OR d.name_en LIKE :q OR m.name_en LIKE :q)
