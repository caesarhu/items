CREATE TABLE IF NOT EXISTS item_list
(id SERIAL PRIMARY KEY,
 items_id INTEGER NOT NULL REFERENCES items (id) ON UPDATE CASCADE,
 種類 TEXT,
 類別 TEXT,
 物品 TEXT default '');
--;;
CREATE INDEX item_list_id_idx ON item_list(items_id);
--;;
CREATE INDEX item_list_item_idx ON item_list(種類, 類別, 物品);