-- 为st_report_package_custom表添加remark字段
ALTER TABLE `st_report_package_custom` 
ADD COLUMN `remark` varchar(500) DEFAULT NULL COMMENT '备注' AFTER `update_time`;
