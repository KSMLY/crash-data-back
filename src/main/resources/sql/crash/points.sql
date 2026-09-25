SELECT id, police_ref, crash_date, latitude, longitude, severity_code
FROM crash
WHERE crash_date >= :from_date AND crash_date <= :to_date
AND latitude IS NOT NULL AND longitude IS NOT NULL
