CREATE TABLE item_list
(id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY ,
 items_id BIGINT NOT NULL REFERENCES items (id) ON UPDATE CASCADE ON DELETE CASCADE ,
 kind TEXT NOT NULL ,
 object TEXT NOT NULL ,
 subkind TEXT NOT NULL)
;
--;;
CREATE INDEX item_list_items_id_index ON item_list (items_id) ;