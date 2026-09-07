INSERT INTO person (crash_id, person_number, occupant_vehicle_id, struck_by_vehicle_id, date_of_birth,
                    sex_code, road_user_type_code, seat_row_code, seat_position_code, injury_severity_code,
                    restraint_code, helmet_code, ped_manoeuvre_code, alcohol_suspected_code, drug_use_code,
                    licence_status_code, licence_issue_date)
VALUES (:crash_id, :person_number, :occupant_vehicle_id, :struck_by_vehicle_id, :date_of_birth,
        :sex_code, :road_user_type_code, :seat_row_code, :seat_position_code, :injury_severity_code,
        :restraint_code, :helmet_code, :ped_manoeuvre_code, :alcohol_suspected_code, :drug_use_code,
        :licence_status_code, :licence_issue_date)
