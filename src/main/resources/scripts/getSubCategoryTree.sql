WITH RECURSIVE InitialCategory AS (
    SELECT category_id
    FROM categories
    WHERE category_id = ?
),
               RecursiveCategoryHierarchy AS (
                   SELECT category_id
                   FROM InitialCategory
                   UNION ALL
                   SELECT c.category_id
                   FROM categories c
                            INNER JOIN RecursiveCategoryHierarchy ch ON c.parent_category_id = ch.category_id
               )
SELECT category_id FROM RecursiveCategoryHierarchy;
