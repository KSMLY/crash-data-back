SELECT COUNT(*)
FROM crash c
JOIN district d ON d.id = c.district_id
LEFT JOIN municipality m ON m.id = c.municipality_id
WHERE (:severity IS NULL OR severity_code = :severity)
  AND (:crash_type IS NULL OR crash_type_code = :crash_type)
  AND (:district_id IS NULL OR c.district_id = :district_id)
  AND (:municipality_id IS NULL OR c.municipality_id = :municipality_id)
  AND (:from_date IS NULL OR crash_date >= :from_date)
  AND (:to_date IS NULL OR crash_date <= :to_date)
  AND (:q IS NULL OR police_ref LIKE :q OR d.name_en LIKE :q OR m.name_en LIKE :q)
