SELECT id, crash_id, vehicle_number, vehicle_type_code,
       make, model, model_year, engine_cc,
       special_function_code, manoeuvre_code
FROM vehicle
WHERE crash_id = :crash_id
ORDER BY vehicle_number
