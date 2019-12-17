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
--;;
CREATE INDEX users_name_idx ON users(姓名);
CREATE INDEX users_unit_idx ON users(單位, 子單位);
--;;
INSERT INTO users (單位, 職稱, 姓名, email, 電話, admin, account, is_whole)
VALUES ('勤指中心', '警務正', '胡培舜', 'caesarhu@dns.apb.gov.tw', '736-6224', true, 'shun', true);
--;;
INSERT INTO users (單位, 職稱, 姓名, email, 電話, admin, account, is_whole)
VALUES ('航空保安科', '警務正', '陳啟文', 'bp691154@dns.apb.gov.tw', '736-6073', true, 'bp691154', true);
--;;
INSERT INTO users (單位, 職稱, 姓名, email, 電話, admin, account, is_whole)
VALUES ('保安大隊', '巡官', '黃愛春', 'ai631024@dns.apb.gov.tw', '736-6514', true, 'ai631024', false);
--;;
INSERT INTO users (單位, 職稱, 姓名, email, 電話, admin, account, is_whole)
VALUES ('刑警大隊', '偵查員', '廖書緯', '711182@dns.apb.gov.tw', '736-6326', true, '711182', false);
--;;
INSERT INTO users (單位, 職稱, 姓名, email, 電話, admin, account, is_whole)
VALUES ('臺北分局', '警員', '吳志卿', 'david555@dns.apb.gov.tw', '728-3104', true, 'david555', false);
--;;
INSERT INTO users (單位, 職稱, 姓名, email, 電話, admin, account, is_whole)
VALUES ('臺北分局', '警務員', '陳廣恩', 'pa761035@dns.apb.gov.tw', '728-3144', true, 'pa761035', false);
--;;
INSERT INTO users (單位, 職稱, 姓名, email, 電話, admin, account, is_whole)
VALUES ('臺北分局', '書記', '簡綵惠', 'zmd45@dns.apb.gov.tw', '728-3144', true, 'zmd45', false);
--;;
INSERT INTO users (單位, 職稱, 姓名, email, 電話, admin, account, is_whole)
VALUES ('高雄分局', '警員', '吳志宏', 'wuking@dns.apb.gov.tw', '736-3959', true, 'wuking', false);
--;;
INSERT INTO users (單位, 職稱, 姓名, email, 電話, admin, account, is_whole)
VALUES ('高雄分局', '書記', '王淑惠', 'nora0409@dns.apb.gov.tw', '736-3959', true, 'nora0409', false);
--;;
INSERT INTO users (單位, 職稱, 姓名, email, 電話, admin, account, is_whole)
VALUES ('安檢大隊', '警員', '黃郁翔', 'sid300@dns.apb.gov.tw', '736-6707', true, 'sid300', false);
--;;
INSERT INTO users (單位, 職稱, 姓名, email, 電話, admin, account, is_whole)
VALUES ('安檢大隊', '科員', '張寒白', 'b187167@dns.apb.gov.tw', '736-6707', true, 'b187167', false);
 --;;
INSERT INTO users (單位, 子單位, email) VALUES ('安檢大隊', '第一隊', 'lee5401@dns.apb.gov.tw');
--;;
INSERT INTO users (單位, 子單位, email) VALUES ('安檢大隊', '第一隊', 'sid101@dns.apb.gov.tw');
 --;;
INSERT INTO users (單位, 子單位, email) VALUES ('安檢大隊', '第二隊', 'dznrg2zu@dns.apb.gov.tw');
--;;
INSERT INTO users (單位, 子單位, email) VALUES ('安檢大隊', '第二隊', 'sec2team@dns.apb.gov.tw');
 --;;
INSERT INTO users (單位, 子單位, email) VALUES ('安檢大隊', '第三隊', 'secclear@dns.apb.gov.tw');
 --;;
INSERT INTO users (單位, 子單位, email) VALUES ('安檢大隊', '第四隊', 'joseph@dns.apb.gov.tw');
--;;
INSERT INTO users (單位, 子單位, email) VALUES ('安檢大隊', '第五隊', 'sec8team@dns.apb.gov.tw');
 --;;
INSERT INTO users (單位, 子單位, email) VALUES ('安檢大隊', '第六隊', 'hello123@dns.apb.gov.tw');
--;;
INSERT INTO users (單位, 子單位) VALUES ('保安大隊', '第1隊第2分隊');
--;;
INSERT INTO users (單位, 子單位) VALUES ('保安大隊', '第1隊第3分隊');
--;;
INSERT INTO users (單位, 子單位) VALUES ('保安大隊', '第2隊第1分隊');
--;;
INSERT INTO users (單位, 子單位) VALUES ('保安大隊', '第2隊第2分隊');
--;;
INSERT INTO users (單位, 子單位) VALUES ('保安大隊', '第2隊第3分隊');
--;;
INSERT INTO users (單位, 子單位) VALUES ('保安大隊', '第3隊第1分隊');
--;;
INSERT INTO users (單位, 子單位) VALUES ('保安大隊', '第3隊第2分隊');
--;;
INSERT INTO users (單位, 子單位) VALUES ('保安大隊', '第3隊第3分隊');
--;;
INSERT INTO users (單位, 子單位) VALUES ('刑警大隊', '偵一隊');
--;;
INSERT INTO users (單位, 子單位) VALUES ('刑警大隊', '偵二隊');
--;;
INSERT INTO users (單位, 子單位) VALUES ('刑警大隊', '偵三隊');
--;;
INSERT INTO users (單位, 子單位, 職稱, 姓名, email)
VALUES ('臺北分局', '安檢隊', '警員', '葉明誠', 'sc2977@dns.apb.gov.tw');
--;;
INSERT INTO users (單位, 子單位, 職稱, 姓名, email)
VALUES ('臺北分局', '金門所', '警員', '黃俊銘', 'kinmen@dns.apb.gov.tw');
--;;
INSERT INTO users (單位, 子單位, 職稱, 姓名, email)
VALUES ('臺北分局', '花蓮所', '警員', '甘瑞能', 'hualien@dns.apb.gov.tw');
--;;
INSERT INTO users (單位, 子單位, 職稱, 姓名, email)
VALUES ('臺北分局', '臺東所', '檢查員', '田瓊瑛', 'taitung@dns.apb.gov.tw');
--;;
INSERT INTO users (單位, 子單位, 職稱, 姓名, email)
VALUES ('臺北分局', '南竿所', '檢查員', '唐佳幼', 'nangan@dns.apb.gov.tw');
--;;
INSERT INTO users (單位, 子單位) VALUES ('臺北分局', '綠島所');
--;;
INSERT INTO users (單位, 子單位) VALUES ('臺北分局', '蘭嶼所');
--;;
INSERT INTO users (單位, 子單位, 職稱, 姓名, email)
VALUES ('臺北分局', '北竿所', '警員', '陳漢嘉', 'peigan@dns.apb.gov.tw');
--;;
INSERT INTO users (單位, 子單位) VALUES ('高雄分局', '安檢隊');
--;;
INSERT INTO users (單位, 子單位) VALUES ('高雄分局', '警備隊');
--;;
INSERT INTO users (單位, 子單位) VALUES ('高雄分局', '臺中所');
--;;
INSERT INTO users (單位, 子單位) VALUES ('高雄分局', '嘉義所');
--;;
INSERT INTO users (單位, 子單位) VALUES ('高雄分局', '臺南所');
--;;
INSERT INTO users (單位, 子單位) VALUES ('高雄分局', '恆春所');
--;;
INSERT INTO users (單位, 子單位) VALUES ('高雄分局', '馬公所');
--;;
INSERT INTO users (單位, 子單位) VALUES ('高雄分局', '七美所');
--;;
INSERT INTO users (單位, 子單位) VALUES ('高雄分局', '望安所');
