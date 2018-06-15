-- phpMyAdmin SQL Dump
-- version 4.7.4
-- https://www.phpmyadmin.net/
--
-- 主機: localhost
-- 產生時間： 2018 年 06 月 14 日 19:23
-- 伺服器版本: 10.1.28-MariaDB
-- PHP 版本： 7.1.11

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- 資料庫： ` medical`
--

-- --------------------------------------------------------

--
-- 資料表結構 `hospital`
--

CREATE TABLE `hospital` (
  `id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `level` enum('national','local') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- 資料表的匯出資料 `hospital`
--

INSERT INTO `hospital` (`id`, `name`, `level`) VALUES
(1, '宏孕診所', 'local'),
(2, '靚漾診所', 'local'),
(3, '琦美診所', 'local'),
(4, '費洛佳皮膚科', 'local'),
(5, '費名耳鼻喉科', 'local'),
(6, '衛生福利部臺北醫院', 'national'),
(7, '衛生福利部雙和醫院', 'national'),
(8, '衛生福利部桃園醫院', 'national'),
(9, '衛生福利部苗栗醫院', 'national'),
(10, '衛生福利部臺中醫院', 'national');

-- --------------------------------------------------------

--
-- 資料表結構 `medical_personnel`
--

CREATE TABLE `medical_personnel` (
  `id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `job` enum('nurse','pharmacist','doctor') NOT NULL,
  `hospital_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- 資料表的匯出資料 `medical_personnel`
--

INSERT INTO `medical_personnel` (`id`, `name`, `job`, `hospital_id`) VALUES
(1, '張三', 'nurse', 3),
(2, '張四', 'pharmacist', 4),
(3, '李五', 'nurse', 2),
(4, '李六', 'pharmacist', 2),
(5, '廖偉倫', 'pharmacist', 1),
(6, '鄭宗翰', 'nurse', 3),
(7, '王淑苓', 'nurse', 4),
(8, '蔡佩瑋', 'nurse', 4),
(9, '吳武梅', 'doctor', 1),
(10, '宋法慧', 'doctor', 2),
(11, '陳宗斌', 'pharmacist', 1),
(12, '吳漢琇', 'nurse', 1),
(13, '吳明亨', 'doctor', 3),
(14, '李宗翰', 'doctor', 4),
(15, '郭惠萍', 'nurse', 2),
(16, '楊惠芳', 'pharmacist', 3),
(17, '黃慧全', 'nurse', 1);

-- --------------------------------------------------------

--
-- 資料表結構 `pharmacy`
--

CREATE TABLE `pharmacy` (
  `id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `hospital_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- 資料表的匯出資料 `pharmacy`
--

INSERT INTO `pharmacy` (`id`, `name`, `hospital_id`) VALUES
(1, '易昌大藥局', 1),
(2, '南和藥局 ', 2),
(3, '日生大藥局 ', 3),
(4, '三元藥局', 4),
(5, '大安藥局', 5),
(6, '上海聯合藥局', 6),
(7, '中心藥局 ', 7),
(8, '楊藥局 ', 8),
(9, '和康大藥局', 9),
(10, '新獻安藥局', 10);

--
-- 已匯出資料表的索引
--

--
-- 資料表索引 `hospital`
--
ALTER TABLE `hospital`
  ADD PRIMARY KEY (`id`);

--
-- 資料表索引 `medical_personnel`
--
ALTER TABLE `medical_personnel`
  ADD PRIMARY KEY (`id`),
  ADD KEY `work_for` (`hospital_id`) USING BTREE;

--
-- 資料表索引 `pharmacy`
--
ALTER TABLE `pharmacy`
  ADD PRIMARY KEY (`id`),
  ADD KEY `inside` (`hospital_id`);

--
-- 在匯出的資料表使用 AUTO_INCREMENT
--

--
-- 使用資料表 AUTO_INCREMENT `hospital`
--
ALTER TABLE `hospital`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- 使用資料表 AUTO_INCREMENT `medical_personnel`
--
ALTER TABLE `medical_personnel`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- 使用資料表 AUTO_INCREMENT `pharmacy`
--
ALTER TABLE `pharmacy`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- 已匯出資料表的限制(Constraint)
--

--
-- 資料表的 Constraints `medical_personnel`
--
ALTER TABLE `medical_personnel`
  ADD CONSTRAINT `kkkkkk` FOREIGN KEY (`hospital_id`) REFERENCES `hospital` (`id`);

--
-- 資料表的 Constraints `pharmacy`
--
ALTER TABLE `pharmacy`
  ADD CONSTRAINT `inside` FOREIGN KEY (`hospital_id`) REFERENCES `hospital` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
