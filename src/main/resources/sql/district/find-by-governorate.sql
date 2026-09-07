SELECT id, governorate_id, name_en, name_ar
FROM district
WHERE governorate_id = :governorate_id
ORDER BY name_en
