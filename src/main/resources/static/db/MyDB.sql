USE `chernykh-nazar-course-2026-np`;

SET FOREIGN_KEY_CHECKS=0;

TRUNCATE TABLE boarding_passengers;
TRUNCATE TABLE boarding_lists;
TRUNCATE TABLE tickets;
TRUNCATE TABLE bus_routes;

SET FOREIGN_KEY_CHECKS=1;

INSERT INTO `bus_routes` 
(`route_number`, `final_destination`, `intermediate_stops`, `departure_time`, 
 `total_seats`, `available_seats`, `ticket_price`, `is_active`) 
VALUES
('101', 'Київ', 'Бориспіль, Бровари', '08:00:00', 45, 43, 250.00, 1),
('102', 'Львів', 'Рівне, Луцьк', '09:30:00', 50, 49, 450.00, 1),
('103', 'Одеса', 'Умань, Вінниця', '10:15:00', 40, 39, 380.00, 1),
('104', 'Харків', 'Полтава, Кременчук', '12:00:00', 45, 45, 320.00, 1),
('105', 'Дніпро', 'Черкаси, Кривий Ріг', '14:30:00', 48, 48, 290.00, 1),
('106', 'Запоріжжя', 'Дніпро, Павлоград', '16:00:00', 45, 45, 310.00, 1);

INSERT INTO `tickets` 
(`route_id`, `passenger_name`, `passenger_phone`, `seat_number`, 
 `travel_date`, `destination`, `status`, `purchase_date_time`, `is_advance_purchase`) 
VALUES
(1, 'Іванов Іван Іванович', '+380501234567', 1, '2026-02-10', 'Київ', 'ACTIVE', '2026-02-08 10:00:00', 1),
(1, 'Петренко Марія Олегівна', '+380671234567', 2, '2026-02-10', 'Бровари', 'ACTIVE', '2026-02-08 10:15:00', 1),
(2, 'Сидоренко Олександр Петрович', '+380931234567', 1, '2026-02-11', 'Львів', 'ACTIVE', '2026-02-08 11:00:00', 0),
(3, 'Коваленко Ірина Миколаївна', '+380631234567', 5, '2026-02-12', 'Одеса', 'ACTIVE', '2026-02-07 14:30:00', 1),
(4, 'Шевченко Андрій Васильович', '+380991234567', 3, '2026-02-09', 'Харків', 'RETURNED', '2026-02-07 09:00:00', 0),
(1, 'Мельник Олена Петрівна', '+380505555555', 10, '2026-02-10', 'Київ', 'ACTIVE', '2026-02-08 12:00:00', 0),
(2, 'Ткаченко Василь Миколайович', '+380666666666', 15, '2026-02-11', 'Луцьк', 'ACTIVE', '2026-02-08 13:30:00', 1),
(3, 'Бондаренко Наталія Сергіївна', '+380777777777', 8, '2026-02-12', 'Вінниця', 'ACTIVE', '2026-02-08 14:00:00', 0);

INSERT INTO `boarding_lists` 
(`route_id`, `travel_date`, `created_at`, `is_closed`) 
VALUES
(1, '2026-02-10', NOW(), 0),
(2, '2026-02-11', NOW(), 0),
(3, '2026-02-12', NOW(), 0);

INSERT INTO `boarding_passengers` 
(`boarding_list_id`, `ticket_id`, `has_boarded`) 
VALUES
(1, 1, 0),
(1, 2, 0),
(1, 6, 0),
(2, 3, 0),
(2, 7, 0),
(3, 4, 0),
(3, 8, 0);

SELECT 'bus_routes' AS Таблиця, COUNT(*) AS 'Кількість записів' FROM bus_routes
UNION ALL
SELECT 'tickets', COUNT(*) FROM tickets
UNION ALL
SELECT 'boarding_lists', COUNT(*) FROM boarding_lists
UNION ALL
SELECT 'boarding_passengers', COUNT(*) FROM boarding_passengers;