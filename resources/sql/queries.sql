-- :name get-items-period-record :? :*
-- :doc 取得期間內的table record 並結合item_list table
select items.*, t2.項目清單, t4.項目人數, t6.所有項目數量 from items
left join
(select t1.items_id, string_agg(t1.項目清單, ',') as 項目清單
from
(select item_list.items_id, (item_list.種類 || '-' || item_list.類別 || '-' || item_list.物品) as 項目清單
from item_list) t1
group by items_id) t2
on items.id = t2.items_id
left join
(select t3.items_id, string_agg(t3.項目人數, ',') as 項目人數
from
(select item_people.items_id, (item_people.種類 || '-' || item_people.件數 || '-' || item_people.人數) as 項目人數
from item_people) t3
group by items_id) t4
on items.id = t4.items_id
left join
(select t5.items_id, string_agg(t5.所有項目數量, ',') as 所有項目數量
from
(select all_list.items_id, (all_list.項目 || '-' || all_list.數量) as 所有項目數量
from all_list) t5
group by items_id) t6
on items.id = t6.items_id
WHERE 查獲時間 >= :start-date and 查獲時間 < :end-date
ORDER BY 單位, 子單位, 員警姓名, 查獲時間

-- :name get-stats-all :? :*
-- :doc 取得期間內全局的危險物品(危安物品)的統計資料
SELECT items.單位, items.子單位, 員警姓名, item_list.種類, item_list.類別,
	count(item_list.類別) as 合計
FROM items, item_list
WHERE item_list.items_id = items.id and 查獲時間 >= :start-date and 查獲時間 < :end-date
group by
GROUPING SETS (
(單位, 子單位, 員警姓名, 種類, 類別),
(單位, 子單位, 員警姓名, 種類),
(單位, 子單位, 種類, 類別),
(單位, 子單位, 種類),
(單位, 種類, 類別),
(單位, 種類),
(種類, 類別),
(種類)
)
order by 單位 nulls first, 子單位 nulls first, 員警姓名 nulls first, 種類 nulls first, 類別 nulls first

-- :name insert-table! :<!
-- :doc insert a table record
INSERT INTO :i:table (:i*:column-names) VALUES (:v*:column-vals) returning id

-- :name get-users :? :*
-- :doc 取得各單位的email名單
SELECT 單位, 子單位, email, is_whole
FROM users

-- :name get-units :? :*
-- :doc 取得唯一的單位名
SELECT units.id, units.單位, units.子單位
FROM units
order by id

-- :name get-items-by-id :? :1
-- :doc 取得指定id的table record 並結合item_list table
select items.*, t2.項目清單, t4.項目人數, t6.所有項目數量 from items
left join
(select t1.items_id, string_agg(t1.項目清單, ',') as 項目清單
from
(select item_list.items_id, (item_list.種類 || '-' || item_list.類別 || '-' || item_list.物品) as 項目清單
from item_list) t1
group by items_id) t2
on items.id = t2.items_id
left join
(select t3.items_id, string_agg(t3.項目人數, ',') as 項目人數
from
(select item_people.items_id, (item_people.種類 || '-' || item_people.件數 || '-' || item_people.人數) as 項目人數
from item_people) t3
group by items_id) t4
on items.id = t4.items_id
left join
(select t5.items_id, string_agg(t5.所有項目數量, ',') as 所有項目數量
from
(select all_list.items_id, (all_list.項目 || '-' || all_list.數量) as 所有項目數量
from all_list) t5
group by items_id) t6
on items.id = t6.items_id
WHERE id = :id