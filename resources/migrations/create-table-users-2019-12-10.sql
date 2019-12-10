CREATE TABLE IF NOT EXISTS users
(id SERIAL PRIMARY KEY,
 單位 TEXT NOT NULL,
 子單位 TEXT,
 職稱  TEXT,
 姓名  TEXT,
 email TEXT,
 電話  TEXT,
 admin BOOLEAN DEFAULT false,
 account TEXT,
 last_login TIMESTAMP,
 is_whole BOOLEAN DEFAULT false,
 pass TEXT);
CREATE INDEX users_name_idx ON users(姓名);
CREATE INDEX users_unit_idx ON users(單位, 子單位);
INSERT INTO users (單位, 職稱, 姓名, email, 電話, admin, account, is_whole)
VALUES ('勤指中心', '警務正', '胡培舜', 'caesarhu@dns.apb.gov.tw', '736-6224', true, 'shun', true);
