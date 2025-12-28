-- 添加积木报表菜单SQL脚本
-- 执行此脚本后，在系统菜单中会出现"报表设计"菜单项

-- 1. 添加"报表管理"目录（如果不存在）
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '报表管理', 0, 5, 'report', NULL, 1, 0, 'M', '0', '0', '', 'chart', 'admin', sysdate(), '报表管理目录'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE menu_name = '报表管理' AND parent_id = 0
);

-- 2. 获取"报表管理"目录的menu_id
SET @parent_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '报表管理' AND parent_id = 0 LIMIT 1);

-- 3. 添加"报表设计"菜单（积木报表）
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '报表设计', @parent_id, 1, 'jimuReport', 'system/report/jimuReport', 1, 0, 'C', '0', '0', 'system:report:view', 'edit', 'admin', sysdate(), '积木报表设计器'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE menu_name = '报表设计' AND path = 'jimuReport'
);

-- 4. 添加报表设计相关按钮权限（可选）
SET @menu_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '报表设计' AND path = 'jimuReport' LIMIT 1);

-- 查看权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '报表查询', @menu_id, 1, '', '', 1, 0, 'F', '0', '0', 'system:report:query', '#', 'admin', sysdate(), ''
WHERE NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE menu_name = '报表查询' AND parent_id = @menu_id
);

-- 新增权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '报表新增', @menu_id, 2, '', '', 1, 0, 'F', '0', '0', 'system:report:add', '#', 'admin', sysdate(), ''
WHERE NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE menu_name = '报表新增' AND parent_id = @menu_id
);

-- 修改权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '报表修改', @menu_id, 3, '', '', 1, 0, 'F', '0', '0', 'system:report:edit', '#', 'admin', sysdate(), ''
WHERE NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE menu_name = '报表修改' AND parent_id = @menu_id
);

-- 删除权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '报表删除', @menu_id, 4, '', '', 1, 0, 'F', '0', '0', 'system:report:remove', '#', 'admin', sysdate(), ''
WHERE NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE menu_name = '报表删除' AND parent_id = @menu_id
);

-- 导出权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '报表导出', @menu_id, 5, '', '', 1, 0, 'F', '0', '0', 'system:report:export', '#', 'admin', sysdate(), ''
WHERE NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE menu_name = '报表导出' AND parent_id = @menu_id
);

-- 查询结果
SELECT '菜单添加完成！' AS result;
SELECT menu_id, menu_name, parent_id, path, component, menu_type, visible, status 
FROM sys_menu 
WHERE menu_name IN ('报表管理', '报表设计') 
ORDER BY parent_id, order_num;

