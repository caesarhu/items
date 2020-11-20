CREATE TABLE item_people
(id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY ,
 items_id BIGINT NOT NULL REFERENCES items (id) ON UPDATE CASCADE ON DELETE CASCADE ,
 kind TEXT NOT NULL ,
 people BIGINT NOT NULL ,
 piece BIGINT NOT NULL)
;
--;;
CREATE INDEX item_people_items_id_index ON item_people (items_id) ;