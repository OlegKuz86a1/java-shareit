INSERT INTO users (name, email)
VALUES ('Андрей', 'andrew@yandex.ru');
INSERT INTO users (name, email)
VALUES ('Петр', 'petr@yandex.ru');
INSERT INTO users (name, email)
VALUES ('Иван', 'ivan@yandex.ru');
INSERT INTO users (name, email)
VALUES ('Геннадий', 'gena@yandex.ru');
INSERT INTO users (name, email)
VALUES ('Драник', 'drani@yandex.ru');

INSERT INTO item_requests (requestor_id, description, created)
VALUES (5, 'нужен утюг', '2024-01-01 00:00:01');
INSERT INTO item_requests (requestor_id, description, created)
VALUES (2, 'хотелось бы чайник', '2024-09-01 08:00:00');
INSERT INTO item_requests (requestor_id, description, created)
VALUES (4, 'Срочно у кого есть сушилка', '2024-05-01 10:00:10');
INSERT INTO item_requests (requestor_id, description, created)
VALUES (2, 'нужен ноутбук', '2024-08-30 15:51:01');
INSERT INTO item_requests (requestor_id, description, created)
VALUES (1, 'нужен лом', '2024-04-14 16:00:41');

INSERT INTO items (owner_id, name, description, available, request_id)
VALUES (1, 'Дрель ударная', 'Нормальная такая дрель', true, 1);
INSERT INTO items (owner_id, name, description, available, request_id)
VALUES (4, 'Шуруповерт', 'Аккумуляторный шуруповерт', false, 2);
INSERT INTO items (owner_id, name, description, available, request_id)
VALUES (3, 'Строительный фен', 'Мощность 1100 Вт', true, 3);
INSERT INTO items (owner_id, name, description, available, request_id)
VALUES (2, 'Сварочный аппарат', 'Мощность сварки 250А', true, 4);
INSERT INTO items (owner_id, name, description, available, request_id)
VALUES (4, 'Болгарка', 'Диск 125мм', true, 5);
INSERT INTO items (owner_id, name, description, available, request_id)
VALUES (5, 'Уровень', 'Длинна 1,5м', true, 5);

INSERT INTO bookings (item_id, start_booking, end_booking, booker_id)
VALUES (3, '2024-03-01 10:00:10', '2024-03-08 09:00:10', 3);
INSERT INTO bookings (item_id, start_booking, end_booking, booker_id)
VALUES (4, '2024-03-10 11:00:10', '2024-03-17 12:00:10', 5);
INSERT INTO bookings (item_id, start_booking, end_booking, booker_id)
VALUES (2, '2024-03-18 12:00:10', '2024-03-25 13:00:10', 4);
INSERT INTO bookings (item_id, start_booking, end_booking, booker_id)
VALUES (3, '2024-04-01 10:00:10', '2024-04-08 11:00:10', 1);
INSERT INTO bookings (item_id, start_booking, end_booking, booker_id)
VALUES (3, '2024-04-09 13:00:10', '2024-04-11 14:00:00', 2);

INSERT INTO statuses (name)
VALUES ('WAITING');
INSERT INTO statuses (name)
VALUES ('APPROVED');
INSERT INTO statuses (name)
VALUES ('REJECTED');
INSERT INTO statuses (name)
VALUES ('CANCELED');

INSERT INTO booking_status (status_id, booking_id)
VALUES (4, 5);
INSERT INTO booking_status (status_id, booking_id)
VALUES (3, 2);
INSERT INTO booking_status (status_id, booking_id)
VALUES (4, 1);
INSERT INTO booking_status (status_id, booking_id)
VALUES (3, 5);
INSERT INTO booking_status (status_id, booking_id)
VALUES (1, 5);
INSERT INTO booking_status (status_id, booking_id)
VALUES (4, 2);
INSERT INTO booking_status (status_id, booking_id)
VALUES (4, 3);
INSERT INTO booking_status (status_id, booking_id)
VALUES (1, 1);

INSERT INTO comments (item_id, created, author_id, text)
VALUES (3, '2024-03-01 10:00:10', 3, 'Все супер!');
INSERT INTO comments (item_id, created, author_id, text)
VALUES (4, '2024-03-17 12:00:10', 5, 'Так то нормально');
INSERT INTO comments (item_id, created, author_id, text)
VALUES (2, '2024-03-18 12:00:10', 4, 'Вспе плохо, все ненравится');
INSERT INTO comments (item_id, created, author_id, text)
VALUES (3, '2024-04-01 10:00:10', 1, 'У меня лапки');
INSERT INTO comments (item_id, created, author_id, text)
VALUES (3, '2024-04-09 13:00:10', 2, 'ВАУ это было великолепно!!!');

select * from public.users