CREATE TABLE IF NOT EXISTS last_time
(file_time TIMESTAMP PRIMARY KEY,
 total INTEGER NOT NULL,
 success INTEGER NOT NULL,
 fail INTEGER NOT NULL);
--;;
INSERT INTO last_time (file_time, total, success, fail) VALUES ('0001-01-01', 0, 0, 0);