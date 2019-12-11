CREATE TABLE IF NOT EXISTS item_people
(id SERIAL PRIMARY KEY,
 items_id INTEGER NOT NULL REFERENCES items (id) ON UPDATE CASCADE,
 種類 TEXT,
 件數 INTEGER,
 人數 INTEGER);
--;;
CREATE INDEX item_people_id_idx ON item_people(items_id);
--;;
CREATE INDEX item_people_kind_idx ON item_people(種類);