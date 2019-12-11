CREATE TABLE IF NOT EXISTS all_list
(id SERIAL PRIMARY KEY,
 items_id INTEGER NOT NULL REFERENCES items (id) ON UPDATE CASCADE,
 項目 TEXT,
 數量 INTEGER);
--;;
CREATE INDEX all_list_id_idx ON all_list(items_id);
--;;
CREATE INDEX all_list_item_idx ON all_list(項目);