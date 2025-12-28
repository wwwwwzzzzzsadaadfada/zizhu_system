-- 添加报表预览菜单SQL脚本
-- 执行此脚本后，在"报表管理"目录下会出现"报表预览"菜单项

-- 1. 确保"报表管理"目录存在（如果不存在则创建）
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '报表管理', 0, 5, 'report', NULL, 1, 0, 'M', '0', '0', '', 'chart', 'admin', NOW(), '报表管理目录'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE menu_name = '报表管理' AND parent_id = 0
);

-- 2. 获取"报表管理"目录的menu_id
SET @parent_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '报表管理' AND parent_id = 0 LIMIT 1);

-- 3. 添加"报表预览"菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '报表预览', @parent_id, 2, 'reportPreview', 'system/report/reportPreview', 1, 0, 'C', '0', '0', 'system:report:preview', 'eye-open', 'admin', NOW(), '报表预览页面'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE menu_name = '报表预览' AND path = 'reportPreview'
);

-- 4. 获取"报表预览"菜单的menu_id
SET @menu_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '报表预览' AND path = 'reportPreview' LIMIT 1);

-- 5. 添加报表预览相关按钮权限（可选）
-- 查询权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '报表查询', @menu_id, 1, '', '', 1, 0, 'F', '0', '0', 'system:report:list', '#', 'admin', NOW(), ''
WHERE NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE menu_name = '报表查询' AND parent_id = @menu_id
);

-- 预览权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '报表预览', @menu_id, 2, '', '', 1, 0, 'F', '0', '0', 'system:report:preview', '#', 'admin', NOW(), ''
WHERE NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE menu_name = '报表预览' AND parent_id = @menu_id AND menu_type = 'F'
);

-- 6. 查询结果（用于验证）
SELECT 
    menu_id,
    menu_name,
    parent_id,
    order_num,
    path,
    component,
    perms,
    icon
FROM sys_menu
WHERE menu_name IN ('报表管理', '报表预览', '报表设计')
ORDER BY parent_id, order_num;


