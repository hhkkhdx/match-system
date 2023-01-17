/*
 Navicat Premium Data Transfer

 Source Server         : 本地Win Mysql
 Source Server Type    : MySQL
 Source Server Version : 50738 (5.7.38)
 Source Host           : localhost:3306
 Source Schema         : match_system

 Target Server Type    : MySQL
 Target Server Version : 50738 (5.7.38)
 File Encoding         : 65001

 Date: 17/01/2023 17:48:23
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `tagname` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标签名称',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '用户id',
  `parent_id` bigint(20) NULL DEFAULT NULL COMMENT '父标签id',
  `is_parent` tinyint(4) NULL DEFAULT NULL COMMENT '0 - 不是，1  -父标签',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint(4) NOT NULL COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniIdx_tagName`(`tagname`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '标签表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tag
-- ----------------------------

-- ----------------------------
-- Table structure for team
-- ----------------------------
DROP TABLE IF EXISTS `team`;
CREATE TABLE `team`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '队伍名称',
  `description` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
  `max_num` int(11) NOT NULL DEFAULT 1 COMMENT '最大人数',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '过期时间',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '用户id（队长id）',
  `status` int(11) NOT NULL DEFAULT 0 COMMENT '队伍状态, 0 - 公开, 1 - 私有, 2 - 加密',
  `password` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '队伍表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of team
-- ----------------------------
INSERT INTO `team` VALUES (2, '队伍1', '描述1', 5, '2023-01-15 08:00:00', 2, 0, '', '2023-01-15 17:01:30', '2023-01-16 14:14:44', 0);
INSERT INTO `team` VALUES (3, '测试队伍名称', '测试描述', 5, '2023-01-17 08:00:00', 2, 2, '123456', '2023-01-15 17:02:35', '2023-01-16 14:18:23', 0);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `user_account` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户账号',
  `user_password` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户密码',
  `avatar_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户头像',
  `gender` tinyint(4) NULL DEFAULT NULL COMMENT '性别',
  `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '电话',
  `email` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `user_status` int(11) NOT NULL DEFAULT 0 COMMENT '用户状态, 0-正常',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除',
  `user_role` int(10) NULL DEFAULT NULL COMMENT '用户角色，0-普通用户，1-管理员',
  `tags` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标签列表',
  `profile` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '个人简介',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', 'admin', '123123', 'https://i02piccdn.sogoucdn.com/010cd487eecffeed', 0, '123', '456', 0, '2022-12-21 16:14:52', '2022-12-22 18:24:43', 0, 0, NULL, NULL);
INSERT INTO `user` VALUES (2, 'xuan', 'xuan', 'ef05a018dcd8f417978bd6501afa79a7', 'http://5b0988e595225.cdn.sohucs.com/images/20181212/4bf8991e2e4c44c8a6bd0d5f1cc822cc.jpeg', NULL, NULL, NULL, 0, '2022-12-21 17:43:12', '2023-01-04 21:51:26', 0, 0, '[\"java\", \"c++\", \"男\"]', NULL);
INSERT INTO `user` VALUES (3, 'xxxuan', 'xxxuan', 'ef05a018dcd8f417978bd6501afa79a7', 'http://5b0988e595225.cdn.sohucs.com/images/20200423/913279d5ab634e56a3f905e34e486ce8.png', NULL, NULL, NULL, 0, '2022-12-21 17:46:25', '2023-01-04 23:20:03', 0, 1, '[\"java\", \"emo\", \"男\"]', NULL);
INSERT INTO `user` VALUES (4, 'aaaa', 'aaaa', 'ef05a018dcd8f417978bd6501afa79a7', 'http://5b0988e595225.cdn.sohucs.com/images/20181229/5815a94cf8e84363bed3262ecf5c285a.jpeg', NULL, NULL, NULL, 0, '2022-12-22 13:44:12', '2023-01-04 23:20:17', 0, 0, '[\"打工仔\", \"emo\", \"男\"]', NULL);

-- ----------------------------
-- Table structure for user_team
-- ----------------------------
DROP TABLE IF EXISTS `user_team`;
CREATE TABLE `user_team`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '用户id',
  `team_id` bigint(20) NULL DEFAULT NULL COMMENT '队伍id',
  `join_time` datetime NULL DEFAULT NULL COMMENT '加入时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户队伍关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_team
-- ----------------------------
INSERT INTO `user_team` VALUES (1, 3, 2, '2023-01-15 17:01:30', '2023-01-15 17:01:30', '2023-01-15 17:01:30', 0);
INSERT INTO `user_team` VALUES (2, 3, 3, '2023-01-15 17:02:35', '2023-01-15 17:02:35', '2023-01-16 14:18:23', 1);
INSERT INTO `user_team` VALUES (3, 2, 3, '2023-01-15 17:02:35', '2023-01-15 17:02:35', '2023-01-15 17:02:35', 0);

SET FOREIGN_KEY_CHECKS = 1;
