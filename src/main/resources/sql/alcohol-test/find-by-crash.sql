SELECT a.person_id, a.test_status_code, a.test_type_code,
       a.result_status_code, a.result_value
FROM alcohol_test a
JOIN person p ON p.id = a.person_id
WHERE p.crash_id = :crash_id
