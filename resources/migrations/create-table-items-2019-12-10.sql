CREATE TABLE IF NOT EXISTS items
(id SERIAL PRIMARY KEY,
 原始檔 TEXT NOT NULL UNIQUE,
 攜帶方式 TEXT,
 查獲時間 TIMESTAMP,
 班次  TEXT,
 單位 TEXT,
 子單位 TEXT,
 員警姓名 TEXT,
 處理情形 TEXT,
 查獲人簽章 TEXT,
 旅客簽章 TEXT,
 航空貨運業者簽章 TEXT,
 所有備註 TEXT);
CREATE INDEX items_file_idx ON items(原始檔);
CREATE INDEX items_name_idx ON items(員警姓名);
CREATE INDEX items_unit_idx ON items(單位, 子單位);
CREATE INDEX items_time_idx ON items(查獲時間);
CREATE INDEX items_stat_idx ON items(單位, 子單位, 員警姓名, 查獲時間);
