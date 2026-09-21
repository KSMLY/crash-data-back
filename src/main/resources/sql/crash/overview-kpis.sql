SELECT SUM(CASE WHEN crash_date >= :from_date THEN 1 ELSE 0 END) AS total,
       SUM(CASE WHEN crash_date >= :from_date AND severity_code = 1 THEN 1 ELSE 0 END) AS fatal,
       SUM(CASE WHEN crash_date >= :from_date AND severity_code = 2 THEN 1 ELSE 0 END) AS serious,
       SUM(CASE WHEN crash_date < :from_date THEN 1 ELSE 0 END) AS previous_total,
       SUM(CASE WHEN crash_date < :from_date AND severity_code = 1 THEN 1 ELSE 0 END) AS previous_fatal,
       SUM(CASE WHEN crash_date < :from_date AND severity_code = 2 THEN 1 ELSE 0 END) AS previous_serious
FROM crash
WHERE crash_date >= :previous_from AND crash_date <= :to_date