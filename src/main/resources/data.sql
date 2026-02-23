INSERT INTO drivers (status, latitude, longitude, region, vehicle_type, updated_at)
SELECT 'AVAILABLE', 28.6139, 77.2090, 'Delhi', 'Sedan', now()
    WHERE NOT EXISTS (SELECT 1 FROM drivers WHERE region='Delhi' AND vehicle_type='Sedan');

INSERT INTO drivers (status, latitude, longitude, region, vehicle_type, updated_at)
SELECT 'AVAILABLE', 28.5355, 77.3910, 'Noida', 'SUV', now()
    WHERE NOT EXISTS (SELECT 1 FROM drivers WHERE region='Noida');

INSERT INTO drivers (status, latitude, longitude, region, vehicle_type, updated_at)
SELECT 'AVAILABLE', 28.4595, 77.0266, 'Gurgaon', 'Mini', now()
    WHERE NOT EXISTS (SELECT 1 FROM drivers WHERE region='Gurgaon');

INSERT INTO drivers (status, latitude, longitude, region, vehicle_type, updated_at)
SELECT 'AVAILABLE', 28.7041, 77.1025, 'Delhi', 'Sedan', now()
    WHERE NOT EXISTS (SELECT 1 FROM drivers WHERE latitude=28.7041);

INSERT INTO drivers (status, latitude, longitude, region, vehicle_type, updated_at)
SELECT 'AVAILABLE', 28.4089, 77.3178, 'Faridabad', 'SUV', now()
    WHERE NOT EXISTS (SELECT 1 FROM drivers WHERE region='Faridabad');