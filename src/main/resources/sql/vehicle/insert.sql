INSERT INTO vehicle (crash_id, vehicle_number, vehicle_type_code,
                     make, model, model_year, engine_cc,
                     special_function_code, manoeuvre_code)
VALUES (:crash_id, :vehicle_number, :vehicle_type_code,
        :make, :model, :model_year, :engine_cc,
        :special_function_code, :manoeuvre_code)
