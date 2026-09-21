SELECT DATETRUNC(%s, crash_date) AS period_start, COUNT(*) AS total,
    SUM(CASE WHEN severity_code = 1 THEN 1 ELSE 0 END) AS fatal,
    SUM(CASE WHEN severity_code = 2 THEN 1 ELSE 0 END) AS serious,
    SUM(CASE WHEN severity_code = 3 THEN 1 ELSE 0 END) AS slight
FROM crash WHERE crash_date >= :from_date AND crash_date <= :to_date
GROUP BY DATETRUNC(%s, crash_date)
ORDER BY period_start
