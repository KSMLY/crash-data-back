SELECT c.district_id, d.governorate_id, d.name_en, d.name_ar,
    SUM(CASE WHEN crash_date >= :from_date THEN 1 ELSE 0 END) AS total,
    SUM(CASE WHEN crash_date >= :from_date AND severity_code = 1 THEN 1 ELSE 0 END) AS fatal,
    SUM(CASE WHEN crash_date >= :from_date AND severity_code = 2 THEN 1 ELSE 0 END) AS serious,
    SUM(CASE WHEN crash_date < :from_date THEN 1 ELSE 0 END) AS previous_total
FROM crash c JOIN district d ON d.id = c.district_id
WHERE crash_date >= :previous_from AND crash_date <= :to_date
GROUP BY c.district_id, d.governorate_id, d.name_en, d.name_ar
ORDER BY total DESC, c.district_id
OFFSET 0 ROWS FETCH NEXT :limit ROWS ONLY