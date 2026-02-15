USE `chernykh-nazar-course-2026-np`;

SET FOREIGN_KEY_CHECKS=0;

-- Видалення таблиць якщо існують
DROP TABLE IF EXISTS `boarding_passengers`;
DROP TABLE IF EXISTS `boarding_lists`;
DROP TABLE IF EXISTS `tickets`;
DROP TABLE IF EXISTS `users`;
DROP TABLE IF EXISTS `bus_routes`;

SET FOREIGN_KEY_CHECKS=1;

-- Таблиця автобусних рейсів
CREATE TABLE `bus_routes` (
                              `id` BIGINT NOT NULL AUTO_INCREMENT,
                              `route_number` VARCHAR(50) NOT NULL UNIQUE,
                              `final_destination` VARCHAR(255) NOT NULL,
                              `intermediate_stops` TEXT,
                              `departure_time` TIME NOT NULL,
                              `total_seats` INT NOT NULL DEFAULT 45,
                              `available_seats` INT NOT NULL DEFAULT 45,
                              `ticket_price` DOUBLE NOT NULL DEFAULT 0.0,
                              `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Таблиця користувачів
CREATE TABLE `users` (
                         `id` BIGINT NOT NULL AUTO_INCREMENT,
                         `full_name` VARCHAR(255) NOT NULL,
                         `phone` VARCHAR(20) NOT NULL UNIQUE,
                         `password` VARCHAR(255) NOT NULL,
                         `role` VARCHAR(20) NOT NULL DEFAULT 'PASSENGER',
                         `created_at` DATETIME NOT NULL,
                         `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Таблиця квитків
CREATE TABLE `tickets` (
                           `id` BIGINT NOT NULL AUTO_INCREMENT,
                           `route_id` BIGINT NOT NULL,
                           `passenger_name` VARCHAR(255) NOT NULL,
                           `passenger_phone` VARCHAR(20) NOT NULL,
                           `seat_number` INT NOT NULL,
                           `travel_date` DATE NOT NULL,
                           `destination` VARCHAR(255) NOT NULL,
                           `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                           `purchase_date_time` DATETIME NOT NULL,
                           `is_advance_purchase` BOOLEAN NOT NULL DEFAULT FALSE,
                           PRIMARY KEY (`id`),
                           FOREIGN KEY (`route_id`) REFERENCES `bus_routes`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Таблиця посадкових відомостей
CREATE TABLE `boarding_lists` (
                                  `id` BIGINT NOT NULL AUTO_INCREMENT,
                                  `route_id` BIGINT NOT NULL,
                                  `travel_date` DATE NOT NULL,
                                  `created_at` DATETIME NOT NULL,
                                  `is_closed` BOOLEAN NOT NULL DEFAULT FALSE,
                                  PRIMARY KEY (`id`),
                                  FOREIGN KEY (`route_id`) REFERENCES `bus_routes`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Таблиця пасажирів у посадковій відомості
CREATE TABLE `boarding_passengers` (
                                       `id` BIGINT NOT NULL AUTO_INCREMENT,
                                       `boarding_list_id` BIGINT NOT NULL,
                                       `ticket_id` BIGINT NOT NULL,
                                       `has_boarded` BOOLEAN NOT NULL DEFAULT FALSE,
                                       PRIMARY KEY (`id`),
                                       FOREIGN KEY (`boarding_list_id`) REFERENCES `boarding_lists`(`id`) ON DELETE CASCADE,
                                       FOREIGN KEY (`ticket_id`) REFERENCES `tickets`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Індекси для оптимізації
CREATE INDEX idx_tickets_route_date ON tickets(route_id, travel_date, status);
CREATE INDEX idx_users_phone ON users(phone);
CREATE INDEX idx_boarding_lists_route_date ON boarding_lists(route_id, travel_date);