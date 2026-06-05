DROP TABLE IF EXISTS pricing_rules;

CREATE TABLE pricing_rules AS
SELECT 
    f.route_no,
    s.fare_conditions,
    MIN(s.price) AS min_price,
    ROUND(AVG(s.price), 2) AS avg_price,
    MAX(s.price) AS max_price,
    COUNT(s.price) AS total_segments
FROM bookings.flights f
JOIN bookings.segments s ON s.flight_id = f.flight_id
GROUP BY f.route_no, s.fare_conditions;

SELECT * FROM pricing_rules LIMIT 20;