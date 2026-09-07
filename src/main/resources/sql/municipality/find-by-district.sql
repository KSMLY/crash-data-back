SELECT id, district_id, name_en, name_ar
FROM municipality
WHERE district_id = :district_id
ORDER BY name_en
