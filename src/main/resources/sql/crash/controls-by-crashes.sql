SELECT crash_id, control_code
FROM crash_traffic_control
WHERE crash_id IN (:crash_ids)