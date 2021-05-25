/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50511
Source Host           : localhost:3306
Source Database       : udb_u

Target Server Type    : MYSQL
Target Server Version : 50511
File Encoding         : 65001

Date: 2016-09-07 10:09:13
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for uadmin
-- ----------------------------
DROP TABLE IF EXISTS `uadmin`;
CREATE TABLE `uadmin` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `permission` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of uadmin
-- ----------------------------
INSERT INTO `uadmin` VALUES ('1', 'u8sdk', '8145c6edcabaecdff8bb572f4fdf6616', '1');

-- ----------------------------
-- Table structure for uchannel
-- ----------------------------
DROP TABLE IF EXISTS `uchannel`;
CREATE TABLE `uchannel` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `channelID` int(11) NOT NULL,
  `appID` int(11) NOT NULL,
  `cpAppID` varchar(2048) DEFAULT NULL,
  `cpAppKey` varchar(2048) DEFAULT NULL,
  `cpAppSecret` varchar(2048) DEFAULT NULL,
  `cpID` varchar(2048) DEFAULT NULL,
  `cpPayID` varchar(2048) DEFAULT NULL,
  `cpPayKey` varchar(2048) DEFAULT NULL,
  `cpPayPriKey` varchar(2048) DEFAULT NULL,
  `masterID` int(11) NOT NULL,
  `cpConfig` varchar(1024) DEFAULT NULL,
  `authUrl` varchar(1024) DEFAULT NULL,
  `payCallbackUrl` varchar(1024) DEFAULT NULL,
  `orderUrl` varchar(1024) DEFAULT NULL,
  `verifyClass` varchar(255) DEFAULT NULL,
  `openPayFlag` int(11) NOT NULL DEFAULT '1',
  `platID` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_channelID` (`channelID`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of uchannel
-- ----------------------------
INSERT INTO `uchannel` VALUES ('1', '10', '1', '544156', '7f768353ed0854f58a8d7f3972949aac', '', '55398', '', '', '', '1', '', '', '', '', '', '0', null);
INSERT INTO `uchannel` VALUES ('2', '11', '1', '2576', 'Jy3ERn6c', '', '27', null, 'mDf3mu88jjGz', null, '2', null, null, null, null, null, '0', null);
INSERT INTO `uchannel` VALUES ('3', '12', '1', '5434671', 'E0OLS0UItr7XqI1BHQi1X6pm', 'mMNF67wSCTxB0tMtIsBzxOQ6VittNg3G', '', null, '', null, '4', null, null, null, null, null, '0', null);
INSERT INTO `uchannel` VALUES ('4', '16', '1', '202307546', '765b6459a63959a58fa7ef6a9396ad97', '54c85c3235cfbbbb8973bfccece34767', '', null, '', null, '3', null, null, null, null, null, '0', null);
INSERT INTO `uchannel` VALUES ('5', '17', '1', '2882303761517311003', '5991731177003', 'Q6YX7b+/t41Z8KB7Fv7Vsw==', '', null, '', null, '5', null, null, null, null, null, '0', null);
INSERT INTO `uchannel` VALUES ('6', '18', '1', '', '102124', 'e091face5ab794ef20edcb8b4600a162', '', null, '', null, '6', null, null, null, null, null, '0', null);
INSERT INTO `uchannel` VALUES ('7', '19', '1', '3637', '5v65ORVyib4sGkGk888wccKgs', '315C58f284c06b2232664e249881ae70', '', null, 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDtJvawWjhQhI+J3EnD3gvuh+t1zB4bOMW9PJUdk27YQDyiGVd42QdHLofdTN1yXKXYZR1Bmy4W1pZhucSoDdS7fGfkKHm3zRMsijNOiPWHg0spMEchI4YTlIC43iFVdzSPE/2sIZfrW/9MspXfuWqFySsTsf6c6qJc6A0bNKJhMwIDAQAB', null, '7', null, null, null, null, null, '0', null);
INSERT INTO `uchannel` VALUES ('8', '20', '1', 'bfae697b90d30d06ee752953b62a0cfa', '3a4db9242b8fa1ebf1dd7d095bf58437', '', '20150506180311792830', '', '', '', '8', '', null, null, null, null, '0', null);
INSERT INTO `uchannel` VALUES ('9', '21', '1', '', '9a10a0743c351d4cbccd974582aec612', '54f67e269a9c7', '', null, '', null, '9', null, null, null, null, null, '0', null);
INSERT INTO `uchannel` VALUES ('10', '22', '1', '', '100024851', '937c0b90cc4e8fb3b067a0c02f9383bb', '', null, 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDtJvawWjhQhI+J3EnD3gvuh+t1zB4bOMW9PJUdk27YQDyiGVd42QdHLofdTN1yXKXYZR1Bmy4W1pZhucSoDdS7fGfkKHm3zRMsijNOiPWHg0spMEchI4YTlIC43iFVdzSPE/2sIZfrW/9MspXfuWqFySsTsf6c6qJc6A0bNKJhMwIDAQAB', null, '10', null, null, null, null, null, '0', null);
INSERT INTO `uchannel` VALUES ('11', '23', '1', '1410232134070.app.ln', 'MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIOH4o9cE0zxHr/IvD7fdoeM/1oamSBwQjS61OqlmG6jhoZ1gmFMbdlH+HCO/EZ8KOQ9JSux9YhEGK+KVhxGdZ0lN/1YhRH0Ynk0zJooocXLq87S2scPem3GWAESlKsg605PutnoDyKwQvdns67nPZvwRRMeh7nfXMiFBWdXtwEzAgMBAAECgYA9mhjMF82aTZufKv6vW62B0tGNe8OX47u+QnqR4zi/KKtKsiJ8O3V/PCvpW65fvKrSKqkMC+75ARumq12lJILUhBIVj8Ygcn8BYy1BtNeCTwAxCwJtOEyaiFr0sglJbJRLN3OVJNFfO6zgmiqY1/ni6Lx/d2t6fUVU8qZu5TNP0QJBAOe5lCbbha7rHQxcFmEgOnDe3yYf7K8V7ZiM9qWzkEjuxpig9sZPu272R2eGzck+mkls3vqjNcNKl7BF4U2VTNUCQQCRT0pvkTm6yWnrCDxeOTT17XrmMxpXXqfN7tQ0WSk0+/Kw9Lesz6IhJ57YGRqtgvGu2zRblPdVkjsKMtMw9nnnAkA8q6stjVZwGODvJoE5ht2mRcQ5UCyBHwWpZmcBtYT2g4X92k8iVyflAphpc7MXmMt+pAGxr9/YtQQIRBOcY5XNAkBYoGfiDE2No3M6qtdHENVAegvPg7O5Pj5S2CwNkaQUcObhDyFIAYv9dNDpNMaUtZz67S/N+9mvE3V3DvDImExZAkBq6iJqFZx82UR1w+a+l56dlyPTX6It2px5sw2aW/QFaLHO1SClakXKvUHL4/xyo/gVHzDQ1M9JD9mKQAJ6vynb', '', '', null, 'MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJ5lk33XmLud/ci+t5xR9vfLj3jiUsfGZ9c45ZduCaeN7op5Dh1+bTv8AkMi3TY0MQp2oJCDW/J49aXGGPnxXEU8VmSja00haZwt1zHJ6C0ZcnkH+fPXY7/ehk3y5wc6O/0TTUH9qN4zrJkIODImork/C3EgVdR4xoyDzdQMdfx3AgMBAAECgYAFSIoU5n4Xs/tlGMdzFWDRfx63/4SXWUzZZvJxjKAR0LAmOkd4QGFQYOWKa71JM5B3MuHEMD1+5v36m5L1s1FKiGDcvUA8/cd/WrInWZrL3Q+OGA8staL+fqa422ie8YKCiHtlU6dPGyLPrcAgHFIa ECWtoRfaenWahFgpWAf2EQJBAOQjZ/5jej6n0THvog5sxciAO4cFIaqQxGSo2R+jRwY9stKj/PeD h7de1yiXaYA7FM+0D2Vt0iNcJX5w9CE1neUCQQCxvb9ELyTcJEQNS1U0yim70VCFQgXXmoU803FL8quTnkF4adGqfgR1DXx6tAGDP0/aaL1z/7cc9aXMIltUQysrAkA4c85y83i3cl1gpvSJ7z6N27Vf NtTa2RHP+rwniQa1M6VBXFTnhoOZy30gTT9M/NGsh9jbd25tU4rvm9G9OgBFAkBV2cfTwE22g1HXdgae6/MChBS3eO9nd3xZp+u2em1DbRJEfiBGkH3IqA3zGpUMdv00CplPuTkSJNjDY+jnJ1ZnAkAS++7RgFopMe9K9dWjLpmF/W4+vqM0SAAcV2akSjSBZqmCD8uNSm6Z9j2cHl06E2d1QZfEav7GVvsQjZoOV2Y4', null, '11', null, null, null, null, null, '0', null);
INSERT INTO `uchannel` VALUES ('12', '24', '1', '10281191', '', 'MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAK2yrlGlXXF6E+e2z8QHXB5cF+Eb434jGTf7H9AOOaSz4GceTSJakqKcrID6tntId4JbZXXhTNvMRubEXVwjlkhuH5kOLCN2dgaMzqyVtsKCf6YwR0KkRp84Cv84o9kyZDcmvPJvSNLOS+zGCzN7Iaxu7HPtauKlhGhGr3KrwWqdAgMBAAECgYAHgYEm5g5zqOLTUIMJ5YeFiFU/1QSvnrSoRqHJS9QR2fQIgLa0lVVg0YRiznK0QR1o9Kodve6kUN9/eVzPbno/9KUg3x2OYKLv7Sc3sDaLdM3u0PsTTOTmyKnLg7D8eTv12PP6N5vV5HvLLI8UCzyOvDleHBHp+Tc822scSe6aAQJBAO6Xo4LK1GRLF8oG9Ekd+AgvhAFo5V/uFNu3BxT2J99CxW4mZT+UCctbBWi18NQWo1XGXKS4AxAiA8E5kYQgW/UCQQC6Xvabbo1tKuG8WVyCRUufEZ04GUbaBcRvE/QHEXlwvbsKJAFKo6e3lF6nnYWyjzI/d5j2QYN1dKVdsHt0vxMJAkEAsSdspA+gNju/lSUmuyeCY+mL9VQChAEOAbnbi0fegRpd55Sgtt1fjFuwH3iAMaoBaw3W+gMbWx42dYEeN+GjBQJBAI7+C4IIGXSYASiM+6Br4HCEiDchla3z3NpI2eOOcbmhqN9H7sHAvQ7qRJGgF5N/sNLnRTIz49P7kmFG5gIWFFkCQDv4lcPjCffuW63qKn6exq6968v/J5IRYU0hBxV0OC6oFdZfI8QO/j8e46fwSLnsjGJDIzKRBp/8shY8N8BvNQM=', '', '900086000026798886', 'MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIjh2xE4Glq/P+B01rqHlozcPmBG3k85ySvNCJvdCaWKvDSVynTo9G1rCPEyV/6LtQ7pahVKZOBtPBKCPSo5NakCAwEAAQ==', 'MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAiOHbETgaWr8/4HTWuoeWjNw+YEbeTznJK80Im90JpYq8NJXKdOj0bWsI8TJX/ou1DulqFUpk4G08EoI9Kjk1qQIDAQABAkA8r+XwPG9yzFgFq8eH65VT3lHJXaIyfewy/zRR5i3gtnl9HQmQStCpWSZ8JfgGTJ75wdGHykAQAb1CXhJbEmYBAiEA3KzMh+0bYBi2nnvKmD126Gi2jUwigQeB8so6EaxwH2MCIQCey0C+LxhI4C2ChIUswZqsc7mHjkSUHAEpGu5UtvoigwIgXtp/x+VpIPM+e/zl5m51EA0CetXA9wlNGBlIwJQIVdUCIQCFzu5ylIrZNhcDaZozOb2V3jqTsN23FNYhhjEZL8sd8wIhAIpfSme5EBlLbADnWtNUhEQRk+v35OxkzB5B0xw2XUzg', '12', null, null, null, null, null, '0', null);
INSERT INTO `uchannel` VALUES ('13', '25', '1', '12185', 'vp7a4AOEt34d37L8', '', '', '5001256473', 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCobUMuqyJYuWv9ro5WCrlGuAcV6Kwwt313uyDwWuYDa5mTo89xP4SW8vfMjqtAFD/P4zbgyeukqbRfjarNZJISBf+8LfgQjCcyw72O1ez8sq2t2trP//PH5ANT9saPgbVpCJvUgHSGawDyiSF33llMqnN6KzetPPQtDvoXYUmM+wIDAQAB', '', '13', null, null, null, null, null, '0', null);
INSERT INTO `uchannel` VALUES ('14', '26', '1', 'CPG54F58B76B11B2', '741b65253193b6592c55c5f9269f7338', '', '', '', '', '', '14', null, null, null, null, null, '0', null);
INSERT INTO `uchannel` VALUES ('15', '27', '1', '', '1378375366Az26xatNyDOD5EM6D2ys', 'ug2KMdLi2JSr4naOE48XmL3h', '', '', '', '', '15', null, null, null, null, null, '0', null);
INSERT INTO `uchannel` VALUES ('16', '28', '1', '', '42bc87ca29a772d0mBj6Pxj1GtUIDqPfZg3ubUjpWeefTB8RPjVThxu4cKa5sB0', '', '', '', '', '', '16', null, null, null, null, null, '0', null);
INSERT INTO `uchannel` VALUES ('17', '29', '1', '101185', '', '87W02FS46OE04DQBWJCPTD47GYFEIA5SXID4B0SH1FQV6SGUVTJISRYMAHNIXRZH', '', '', '', '', '17', null, null, null, null, null, '0', null);
INSERT INTO `uchannel` VALUES ('18', '30', '1', '1161', 'jzLdOuDM68K4u50n717MlQxg25OXWm2t', '', '', '', '', '', '18', null, null, null, null, null, '0', null);
INSERT INTO `uchannel` VALUES ('19', '31', '1', '2487115', 'ca6326a22f214721948bc8f0da33962d', 'xuaut7WRmKttyMnkx4G1Tn6ulFwPKjue', '', '', '', '', '19', null, null, null, null, null, '0', null);
INSERT INTO `uchannel` VALUES ('20', '32', '1', '5000003221', 'c83110e596e94d6e938c73184dee2b5b', '', '', '', 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQChAzowRKhk76EuuOmRfDDc13l94vxmbIMgsPJreDm+yLLdwUuvBNQ1rpcXpsUGl/KEzZAwGn98ZqAm4z8SRgk+EgyN9iZc5gjiIcqEYnIREeWDzQYRXGPpnCK+MDJks6mg8O0LIb6Hv7SeAd7BUqPJBw3woONYZNDwg77NIuFghQIDAQAB', 'MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBANksWRSba/8ZfOmBaX8Knz6Yw4KWxYezaHMyy1n2G/Sw/JCxOXGIjsGsk9ur3TAfb4Qm0N0/m79PPJCnXWyWOdX8WynlGp10l2BKkUFRX1+cyBobciNYd2c1PcF5iljkGBeZ3KnTYprynybP3/Zfji7gLTgPTF3NEqADAgoC4OS/AgMBAAECgYEAl67/8QK1JZHZywYWdyMmIoR0WauQ/7zxQTTDD2x0Di3n2e/OnVuxydgN1apubQQn0XeC/mVFFRP0y1v6RrOOZHsJB/qbe4GAvEXK2W2KCD+af6+OvD3N9RM547i/EV1urzMT1sr4CFWAwiC7ornqzlRAoo5eZFHst4RR7DBgD8ECQQD9ZGghB58cLoIMdO99MOnNKx4PfXLmmgivWnu1THwyOn1X4L7z4PwGIEWKXXHmiDFTGe64QlFyPBEPb9axbiuvAkEA22iEj72jH3xgcAZVYjSUbWx3YGF6vZ9L7R+fozqplgCnb3r79bdFEv1YEwrtIw4cEfDxXH4PK/oKpRYbXlrL8QJBAIl0USQxvTcNZ5CYUl3IDdw1Go5uj8jWIAX3gIn9npWkst/b09n53tz33qFLRYE3ugIJXDzqBMKCIQMNuRnw2JMCQQDLvYWDWOy2GzAONsNo2zyNU/CSjlKE7Px7aCc8UwgRY/AXRFPeO14oIRt9K2NDbyG/w0bNxb1e7+Scoiet+ZBxAkBK6DSHL5KQaFlYESJQq4bwzGOAXAmTPlmTxbHhcLNxZxBtGvLGVINgg+sbGpxGO/Hjt1oczAEOErcFVT4cVLXJ', '20', '1', '', '', '', '', '0', null);
INSERT INTO `uchannel` VALUES ('21', '33', '1', 'b32a1d1fd1826545', '34fe34225d1ed6b0', 'c3657cdd837eb9dd', '', '', '', '', '21', null, null, null, null, null, '0', null);
INSERT INTO `uchannel` VALUES ('22', '34', '1', 'b32a1d1fd1826545', '23F28F98461143E7A75F96327B695734', 'BCD61AE8337C4270A3D7E597102DFD66', '', '', 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCSGer0HsTaWI12og+uRGP+XfGVaMVqK5c6/90eLT3DHSdiF6SpqbR5BmxfpEyOlwwMdVSHMw+BqqE1PiKH4fx1ZTV0/JFc33Y55PJ9jVZ5TKHKQqvr/hn12DNgD8ntmPCLkJwv0mD3X4Pu3V2Qo7lDbJDX/XXd91/wJDCq3GToAwIDAQAB', 'MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMHhEdFSgO0MDcWP0itaGMwXMJOYei5LMDkpoWdm3zreCd/y49op+73Sv6lTIb2HX2D/AvDxOjNq93L9su6c8q8y+tUs88BGCS3FHRjwHqKtInuE2AkLLPKMp8LNKSAyqOzFOBC3CHF6wMRzbzXYTQWwKkBCWb2B1wzqbEjPKqcvAgMBAAECgYAN/WikuxhUgo8n11XqDOlHKNE3hUQjvQcwyME9zd2DyOvbfhJU9ryUmPV9iWMg4vjN7fjPXAjAFxLd+FKGB0s3mvwpHSOur+SX54apbQN2eYu1Ps3aUU39s/XaDp0XKInmjp9feAKt2p/oju1CKUh2m81osFqftOw0IhdpO8w+wQJBAOoa9v4RXtSO0rCjIohkvvbWXap6lSz//OqJmcJmYWoJwQYpvAb2gvZusw+AuWzkkY122EH9isSf0zAoApTbg88CQQDUAv7dJyPqc9EIGmsiw+6Xs6Hbtoz6QSaubitmE7AiKARBFvzA1/YiIl2sluRvmL9CXkfpqTpJzob2AFGHzZ6hAkEAnHOE2WqWa4s/dtivPWPG04OTeVkO1NIHHl7zKR3uwETggNPnWufnwfVdKWEnioR+WYIxKHjfAmSlaKt0fjLttQJAcSZ8IUm/aGSRfjKjVTWexAYR73I5QWK+I0AXF26QhVo4EwkSslVQPp16wx7xIgIyqVgqJzGzMyrvaqF24fTCoQJAeRuF4M8c6TQrE7ELenwPbYpYOEq+ovMJrSS2d06JYL4u0+SA6Xvfa1+q3brt5XlM2MTxdiWHXdOjYbKaTvDdew==', '22', '', null, null, null, null, '0', null);
INSERT INTO `uchannel` VALUES ('24', '36', '1', '', '643501227', '', '643501227', '', '', '', '24', '', null, null, null, null, '0', null);
INSERT INTO `uchannel` VALUES ('25', '37', '1', '415', '75db13707d7a2a05904959d0b8f7efbb', '', '', '', '', '', '25', '', '', '', '', '', '0', null);
INSERT INTO `uchannel` VALUES ('26', '1000', '1', '1', '58C6A68DDDEE471AA43266E427F38D92', '', '', '', 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC2kcrRvxURhFijDoPpqZ/IgPlAgppkKrek6wSrua1zBiGTwHI2f+YCa5vC1JEiIi9uw4srS0OSCB6kY3bP2DGJagBoEgj/rYAGjtYJxJrEiTxVs5/GfPuQBYmU0XAtPXFzciZy446VPJLHMPnmTALmIOR5Dddd1Zklod9IQBMjjwIDAQAB', '', '23', null, null, null, null, null, '0', null);
INSERT INTO `uchannel` VALUES ('29', '38', '1', '1941', '201befdbdd428f50', 'c5b6e9d97ca4e5cfd7bf66f34cf9c2c2', '', 'cfb56ead9fd3b1fc30c0c18bad4a1a84', '', '', '26', '', null, null, null, null, '0', null);
INSERT INTO `uchannel` VALUES ('30', '39', '1', '1138', 'KU5DJ8fDjeHGRYui0G2khkIUrcJJ2Ii2', 'CFxI2KQeiL32S79HCcATlru5Ls6M4HLI', '10396', '', 'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzdhyi7xm3z2Tl9bAWrQEHHIo2NymADefIRl6FUviM4NK/OV/Q2eZw+rausF6BviYm2ehifpGm8Zud9g+vshGqNX4gcQS3F7TohGVbqq8WFxG5wNu4fcCLE15OyxqBDtYrlWIfsG3x8vmHi+wXBQMSemZp/CtHx3jY5D1wPw//7lj97lrRWdS9WCPMiY5H+v/qwsj2TA8ZR+Ui9ic+DHRXqD/MJBx/Ie1yHc1nYsWMBiwogbhXt2umkElhVIjXFUY/xNRRTM/dXt1Elgr7cJCdNP3Z0R3tNiVne3p8920kcCtDAhVw6X32TNiRZY7I6E3vfAk9SrdLT/sxtuq4v4MRQIDAQAB', '', '27', '', '', '', '', '', '0', null);
INSERT INTO `uchannel` VALUES ('31', '40', '1', '10104', '', '', '', '', 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC+GY2/8wJuINxzJo9uWoMRUDcxONuK/48Fikze8EFpKWLLr6mBpqeoDVvZQoqGhGKn5wdtHujiCUYSn6pcWKY2Fz2Rxw6/1uA1gzKcLE36KLUkqvFbA3gItSiO3ADNCwJ1ochhdfcEnH2dtbiv5+f7m+xv5B1aEP142v2CtYKFFQIDAQAB', 'MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMcigZ7OFyX4vVlNNHBJyTDQ3dDGWGOhRwiL4NmN/oE7q69lUMdiD2orwiMjYVDK78pkxMakZ+/8B0uQ4ZfJJK5Zp+tLvwVmkqC9rkL671I7NLJ1IB+agx12CtOzXrt684bLnFgkH47+cU/wNr3/VC+h2UGb/hF0itBfj/jNUMw1AgMBAAECgYBPMVLZI96i1DQBIqn7Ce0AA+Bi2a5nGMlWBcLK4iRXorJZU3J3iHDW6UYQkU5vQfVwyPhUgydukg0YqSp+IZj7JONs09g9JgZaw9EFpwQvDbjilOWVwrRFdj3RgpoZgTay4E2eYV1BzrdROzO/qHk6JOS59sx9xJDGj5hFB+NQVQJBAPmBxQ3jr6zwHF8FCDtPS3RMB9P5yywzLRPKriklazRpbxfyWo2E/qk3dhjDD/vEBkjOenD4XBjyO82mpfglcwsCQQDMUSeJGGkLCZ1TofTSpI3LxYJjsw7bOOJhxDry03Hk9lB8HR8aMoiX2YsUtErk9TKwDL5uSocxMojgsrwO2kW/AkB3ut8tyBnwOhTTQB3wICAMQcPr482sey5hdfxXQF6Oex3VdvYfPTfbSgMA5PIRlcKSQ4scFQJU8kDicdrpFkd7AkEAsEY8E93JkspCwAr0zF9ILsxCiH3NjXlwZuUD7shadS3Flq8RTGrYyhnYEo7SJuMdTiJQkZsfNGTjDullsnLCxQJBANZaAm3jqcy5DduwFbGRybHnauMSeaFA4jNwho5Wu/9loQvYbr2zHvAwDWnn/rlvDCPmuPmL8xBsFzp9L2CazmY=', '28', '', null, null, null, null, '0', null);
INSERT INTO `uchannel` VALUES ('32', '41', '1', '880000130', '', '4e2c266a499807b8083edfc7ff04a5f3', '', '', '', '', '29', '', null, null, null, null, '0', null);
INSERT INTO `uchannel` VALUES ('39', '44', '1', '35', 'be17611ee63323a9b674151154e10785', '', '', '', '', '', '30', '', null, null, null, null, '0', null);
INSERT INTO `uchannel` VALUES ('40', '45', '1', '224388', '8414e216762c4d27878c5eec7837840a', '', '', '', '', '', '31', '', null, null, null, null, '0', null);
INSERT INTO `uchannel` VALUES ('41', '46', '1', '188507', 'C430581260C50C4ACCE7C0CA22250AB2', '', '', '', '', '', '32', '', null, null, null, null, '0', null);
INSERT INTO `uchannel` VALUES ('42', '47', '1', '', 'y6549xerd2l7ecebi7249w7e', 'rs87639u95p923q561cbdrmwyprxcy23', '', '', '', '', '33', '', null, null, null, null, '0', null);
INSERT INTO `uchannel` VALUES ('43', '48', '1', '197', 'DG24IPYY5502#6DL', '', '211', '', '', '', '34', '', null, null, null, null, '0', null);
INSERT INTO `uchannel` VALUES ('44', '49', '1', '3456', '74974bf301ff7e270d0e1e6860735f38', '', '', '', '', '', '35', '', null, null, null, null, '0', null);
INSERT INTO `uchannel` VALUES ('45', '50', '1', '', '', '', 'xsg_m', '1d5302ae1f902201bc804c58f2b03ab0', '', '', '36', '', null, null, null, null, '0', null);
INSERT INTO `uchannel` VALUES ('46', '35', '1', '1104556695', 'DDebGjkcpZzO4GEM', '', '', '', '', '', '37', '', null, null, null, null, '0', null);
INSERT INTO `uchannel` VALUES ('47', '1686', '1', '759', '1648dcdff97d4b358d611f850b3d8668', '', '', '', '', '', '38', '', '', '', '', '', '0', null);
INSERT INTO `uchannel` VALUES ('48', '1700', '1', '100005730', 't2XZKzpbmECn3fkrWsw1nxAvMt47pPts', '', '', 'AoSwmlTp0wUsNRVEqBSFMbjq4MLls79P', '', '', '39', '', '', '', '', '', '0', null);
INSERT INTO `uchannel` VALUES ('49', '1003', '1', '5521', '3e5bfafa43962dd739d1dc1c4939593a', '', '', '', '', '', '40', '', '', '', '', '', '0', null);
INSERT INTO `uchannel` VALUES ('50', '51', '1', '1105283027', '2ehLpU6KzkKvdqVC5BO6QBCEOoFVPpDl', '', '', '', '', '', '41', '10', '', '', '', '', '0', null);
INSERT INTO `uchannel` VALUES ('51', '1701', '1', '', '', '', '', '', '', '', '42', '', '', '', '', '', '0', null);
INSERT INTO `uchannel` VALUES ('52', '52', '1', '964', 'e6091c7d360371f0465c11bbada4ccaf4868e4716e32fd3901315e04c2e14224', 'd11dc709deac038a0a8681058f353582634de90955686e03cbf50633ff91c7a7', '', '{D251941B-8D3A-4394-B6CC-3BF8E87CE453}', '', '', '44', '', '', '', '', '', '0', null);
INSERT INTO `uchannel` VALUES ('53', '53', '1', 'GAME1602240954607017', 'ttj8ZcnI3idAiore8mjXew==', 'R6OPo0d2WtSXdivp4rX/tA==', 'DEV1602232022111921', '', '', '', '45', '', '', '', '', '', '0', null);
INSERT INTO `uchannel` VALUES ('54', '85', '1', '1511171001', '', '1511171001', '', '', '', '', '47', '', '', '', '', '', '0', null);

-- ----------------------------
-- Table structure for uchannelmaster
-- ----------------------------
DROP TABLE IF EXISTS `uchannelmaster`;
CREATE TABLE `uchannelmaster` (
  `masterID` int(11) NOT NULL,
  `authUrl` varchar(1024) DEFAULT NULL,
  `masterName` varchar(255) DEFAULT NULL,
  `sdkName` varchar(255) DEFAULT NULL,
  `nameSuffix` varchar(255) DEFAULT NULL,
  `payCallbackUrl` varchar(1024) DEFAULT NULL,
  `verifyClass` varchar(1024) DEFAULT NULL,
  `orderUrl` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`masterID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of uchannelmaster
-- ----------------------------
INSERT INTO `uchannelmaster` VALUES ('1', 'http://sdk.g.uc.cn/ss/account.verifySession', 'UC', 'uc', '.uc', 'http://localhost:8080/uc/payCallback', 'com.u8.server.sdk.uc.UCSDK', '');
INSERT INTO `uchannelmaster` VALUES ('2', 'http://ngsdk.d.cn/api/cp/checkToken', '当乐', 'downjoy', '.dl', 'http://localhost:8080/downjoy/payCallback', 'com.u8.server.sdk.downjoy.DownjoySDK', null);
INSERT INTO `uchannelmaster` VALUES ('3', 'https://openapi.360.cn/user/me', '360', 'qihoo360', '.qihoo', 'http://192.168.3.32:8080/qihoo360/payCallback', 'com.u8.server.sdk.qihoo360.Qihoo360SDK', null);
INSERT INTO `uchannelmaster` VALUES ('4', 'http://querysdkapi.baidu.com/query/cploginstatequery', '百度', 'baidu', '.baidu', 'http://localhost:8080/baidu/payCallback', 'com.u8.server.sdk.baidu.BaiduSDK', '');
INSERT INTO `uchannelmaster` VALUES ('5', 'http://mis.migc.xiaomi.com/api/biz/service/verifySession.do', '小米', 'xiaomi', '.xiaomi', 'http://localhost:8080/xiaomi/payCallback', 'com.u8.server.sdk.xiaomi.XiaoMiSDK', '');
INSERT INTO `uchannelmaster` VALUES ('6', 'http://m.4399api.com/openapi/oauth-check.html', '4399', 'm4399', '.m4399', 'http://localhost:8080/m4399/payCallback', 'com.u8.server.sdk.m4399.M4399SDK', '');
INSERT INTO `uchannelmaster` VALUES ('7', 'http://i.open.game.oppomobile.com/gameopen/user/fileIdInfo', 'oppo', 'oppo', '.oppo', 'http://localhost:8080/oppo/payCallback', 'com.u8.server.sdk.oppo.OppoSDK', '');
INSERT INTO `uchannelmaster` VALUES ('8', 'https://usrsys.inner.bbk.com/auth/user/info', 'ViVo', 'vivo', '.vivo', 'http://localhost:8080/vivo/payCallback', 'com.u8.server.sdk.vivo.VivoSDK', 'https://pay.vivo.com.cn/vcoin/trade');
INSERT INTO `uchannelmaster` VALUES ('9', 'http://sdk.muzhiwan.com/oauth2/getuser.php', '拇指玩', 'muzhiwan', '.mzw', 'http://localhost:8080/muzhiwan/payCallback', 'com.u8.server.sdk.muzhiwan.MuZhiWanSDK', '');
INSERT INTO `uchannelmaster` VALUES ('10', 'https://pay.wandoujia.com/api/uid/check', '豌豆荚', 'wandoujia', '.wdj', 'http://localhost:8080/wandoujia/payCallback', 'com.u8.server.sdk.wandoujia.WanDouJiaSDK', '');
INSERT INTO `uchannelmaster` VALUES ('11', 'http://passport.lenovo.com/interserver/authen/1.2/getaccountid', '联想', 'lenovo', '.lenovo', 'http://localhost:8080/lenovo/payCallback', 'com.u8.server.sdk.lenovo.LenovoSDK', '');
INSERT INTO `uchannelmaster` VALUES ('12', 'https://api.vmall.com/rest.php', '华为', 'huawei', '.huawei', 'http://localhost:8080/huawei/payCallback', 'com.u8.server.sdk.huawei.HuaWeiSDK', '');
INSERT INTO `uchannelmaster` VALUES ('13', 'http://api.appchina.com/appchina-usersdk/user/v2/get.json', '应用汇', 'appchina', '.appchina', 'http://localhost:8080/appchina/payCallback', 'com.u8.server.sdk.appchina.AppChinaSDK', '');
INSERT INTO `uchannelmaster` VALUES ('14', 'http://server.cpo2o.com/Verify/login.html', '友游', 'cloudpoint', '.cloudpoint', 'http://localhost:8080/cloudpoint/payCallback', 'com.u8.server.sdk.cloudpoint.CloudPointSDK', '');
INSERT INTO `uchannelmaster` VALUES ('15', 'http://user.anzhi.com/web/api/sdk/third/1/queryislogin', '安智', 'anzhi', '.anzhi', 'http://localhost:8080/anzhi/payCallback', 'com.u8.server.sdk.anzhi.AnzhiSDK', '');
INSERT INTO `uchannelmaster` VALUES ('16', 'http://pay.mumayi.com/user/index/validation', '木蚂蚁', 'mumayi', '.mumayi', 'http://localhost:8080/mumayi/payCallback', 'com.u8.server.sdk.mumayi.MuMaYiSDK', '');
INSERT INTO `uchannelmaster` VALUES ('17', 'http://guopan.cn/gamesdk/verify', '叉叉助手', 'guopan', '.guopan', 'http://localhost:8080/guopan/payCallback', 'com.u8.server.sdk.guopan.GuoPanSDK', '');
INSERT INTO `uchannelmaster` VALUES ('18', 'http://ng.sdk.paojiao.cn/api/user/token.do', '泡椒', 'paojiao', '.paojiao', 'http://localhost:8080/paojiao/payCallback', 'com.u8.server.sdk.paojiao.PaoJiaoSDK', '');
INSERT INTO `uchannelmaster` VALUES ('19', 'https://api.game.meizu.com/game/security/checksession', '魅族', 'meizu', '.meizu', 'http://localhost:8080/meizu/payCallback', 'com.u8.server.sdk.meizu.MeizuSDK', '');
INSERT INTO `uchannelmaster` VALUES ('20', 'https://openapi.coolyun.com/oauth2/token', '酷派', 'coolpad', '.coolpad', 'http://localhost:8080/coolpad/payCallback', 'com.u8.server.sdk.coolpad.CoolPadSDK', 'http://pay.coolyun.com:6988/payapi/order');
INSERT INTO `uchannelmaster` VALUES ('21', '', '偶玩', 'ouwan', '.ouwan', 'http://localhost:8080/ouwan/payCallback', 'com.u8.server.sdk.ouwan.OuWanSDK', '');
INSERT INTO `uchannelmaster` VALUES ('22', 'https://id.gionee.com/account/verify.do', '金立', 'jinli', '.am', 'http://localhost:8080/jinli/payCallback', 'com.u8.server.sdk.jinli.JinLiSDK', 'https://pay.gionee.com/order/create');
INSERT INTO `uchannelmaster` VALUES ('23', 'https://pay.slooti.com/?r=auth/verify', 'Itools', 'itools', '.itools', 'http://localhost:8080/itools/payCallback', 'com.u8.server.sdk.itools.ItoolsSDK', '');
INSERT INTO `uchannelmaster` VALUES ('24', 'http://api.gfan.com/uc1/common/verify_token', '机锋', 'gfan', '.gfan', 'http://localhost:8080/pay/gfan/payCallback/36', 'com.u8.server.sdk.gfan.GFanSDK', '');
INSERT INTO `uchannelmaster` VALUES ('25', 'http://api.qcwan.com/info/uc', '安峰(56Game)', 'anfeng', '.anfeng', 'http://localhost:8080/pay/anfeng/payCallback/37', 'com.u8.server.sdk.anfeng.AnFengSDK', '');
INSERT INTO `uchannelmaster` VALUES ('26', 'http://sdk.api.gamex.mobile.youku.com/game/user/infomation', '优酷', 'youku', '.youku', 'localhost:8080/pay/youku/payCallback', 'com.u8.server.sdk.youku.YouKuSDK', '');
INSERT INTO `uchannelmaster` VALUES ('27', 'http://sdk.game.kugou.com/index.php?r=ValidateIsLogined/CheckToken', '酷狗', 'kugou', '.kugou', 'http://localhost:8080/pay/kugou/payCallback', 'com.u8.server.sdk.kugou.KuGouSDK', '');
INSERT INTO `uchannelmaster` VALUES ('28', 'http://lewanduo.com/mobile/user/verifyToken.html', '乐玩', 'lewan', '.lewan', 'localhost:8080/pay/lewan/payCallback', 'com.u8.server.sdk.lewan.LewanSDK', '');
INSERT INTO `uchannelmaster` VALUES ('29', 'http://fh.sdo.com/fh/open/ticket', '逗逗(洪金宝)', 'doudou', '.doudou', 'localhost:8080/pay/doudou/payCallback', 'com.u8.server.sdk.doudou.DouDouSDK', '');
INSERT INTO `uchannelmaster` VALUES ('30', 'no_login_verify', '猎宝游戏', 'liebao', '.liebao', 'http://localhost:8080/pay/liebao/payCallback', 'com.u8.server.sdk.liebao.LiebaoSDK', '');
INSERT INTO `uchannelmaster` VALUES ('31', 'https://sso.letv.com/oauthopen/userbasic', '乐视', 'letv', '.letv', 'http://localhost:8080/pay/letv/payCallback', 'com.u8.server.sdk.letv.LetvSDK', '');
INSERT INTO `uchannelmaster` VALUES ('32', ' http://api.app.snail.com/store/platform/sdk/ap', '免商店(蜗牛)', 'mianshangdian', '.msd', 'localhost:8080/pay/mianshangdian/payCallback', 'com.u8.server.sdk.mianshangdian.MianShangDianSDK', '');
INSERT INTO `uchannelmaster` VALUES ('33', 'http://api.rnler.com/out.json', '趣游', 'quyou', '.quyou', 'localhost:8080/pay/quyou/payCallback', 'com.u8.server.sdk.quyou.QuYouSDK', '');
INSERT INTO `uchannelmaster` VALUES ('34', 'http://sdk.07073sy.com/index.php/User/v4', '武汉楚游(07073)', 'chuyou', '.chuyou', 'localhost:8080/pay/chuyou/payCallback', 'com.u8.server.sdk.chuyou.ChuYouSDK', '');
INSERT INTO `uchannelmaster` VALUES ('35', 'no verify url', 'PPS', 'pps', '.pps', 'localhost:8080/pay/pps/payCallback', 'com.u8.server.sdk.pps.PPSSDK', '');
INSERT INTO `uchannelmaster` VALUES ('36', 'http://api.user.vas.pptv.com/c/v2/cksession.php', 'PPTV', 'pptv', '.pptv', 'localhost:8080/pay/pptv/payCallback', 'com.u8.server.sdk.pptv.PPTVSDK', '');
INSERT INTO `uchannelmaster` VALUES ('37', 'none', '应用宝', 'txmsdk', '.txmsdk', 'none', 'com.u8.server.sdk.txmsdk.TXMSDK', 'http://msdktest.qq.com');
INSERT INTO `uchannelmaster` VALUES ('38', 'https://pay.i4.cn/member_third.action', '爱思', 'i4', '.i4', 'http://localhost:8080/pay/i4/payCallback', 'com.u8.server.sdk.i4.I4SDK', '');
INSERT INTO `uchannelmaster` VALUES ('39', 'http://passport.xyzs.com/checkLogin.php', 'XY苹果助手', 'xy', '.xy', 'http://localhost:8080/pay/xy/payCallback', 'com.u8.server.sdk.xy.XYSDK', '');
INSERT INTO `uchannelmaster` VALUES ('40', 'http://passport_i.25pp.com:8080/account?tunnel-command=2852126760', 'PP助手', 'pp', '.pp', 'http://localhost:8080/pay/pp/payCallback', 'com.u8.server.sdk.pp.PPSDK', '');
INSERT INTO `uchannelmaster` VALUES ('41', 'https://ysdktest.qq.com', '应用宝YSDK', 'ysdk', '.ysdk', 'none', 'com.u8.server.sdk.ysdk.YSDK', 'https://ysdktest.qq.com');
INSERT INTO `uchannelmaster` VALUES ('42', 'none', 'Demo测试渠道', 'demo', '.demo', 'none', 'com.u8.server.sdk.demo.DemoSDK', 'none');
INSERT INTO `uchannelmaster` VALUES ('43', 'http://api.haimawan.com/index.php?m=api&a=validate_token', '海马玩(iOS)', 'haima', '.haima', 'http://localhost:8080/pay/haima/payCallback', 'com.u8.server.sdk.haima.HaimaSDK', '');
INSERT INTO `uchannelmaster` VALUES ('44', 'http://api.app.wan.sogou.com/api/v1/login/verify', '搜狗', 'sougou', '.sougou', 'http://localhost:8080/pay/sougou/payCallback', 'com.u8.server.sdk.sougou.SouGouSDK', '');
INSERT INTO `uchannelmaster` VALUES ('45', 'http://web.quanmin.la/sdk/game.htm', '全民助手', 'quanmingzhushou', '.qm', 'http://localhost:8080/pay/quanmingzhushou/payCallback', 'com.u8.server.sdk.quanmingzhushou.QMSDK', '');
INSERT INTO `uchannelmaster` VALUES ('46', 'http://passport_i.25pp.com:8080/account?tunnel-command=2852126760', 'PP助手', 'pp', '.pp', 'http://localhost:8080/pay/pp/payCallback', 'com.u8.server.sdk.pp.PPSDK', '');
INSERT INTO `uchannelmaster` VALUES ('47', 'http://sdk.douwanweb.com/api.php/index/checktoken', '都玩(易乐)', 'yile', '.yile', 'localhost', 'com.u8.server.sdk.yile.YiLeSDK', '');

-- ----------------------------
-- Table structure for ugame
-- ----------------------------
DROP TABLE IF EXISTS `ugame`;
CREATE TABLE `ugame` (
  `appID` int(11) NOT NULL,
  `appkey` varchar(255) DEFAULT NULL,
  `appSecret` varchar(255) DEFAULT NULL,
  `appRSAPubKey` varchar(1024) DEFAULT NULL,
  `appRSAPriKey` varchar(1024) DEFAULT NULL,
  `createTime` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `payCallback` varchar(255) NOT NULL,
  `payCallbackDebug` varchar(255) DEFAULT NULL,
  `msdkPayCallback` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`appID`,`payCallback`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ugame
-- ----------------------------
INSERT INTO `ugame` VALUES ('1', 'f32fdc02123a82524eb4ea95e1383d0b', '7513a2c235647e3213538c6eb329eec9', 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDtJvawWjhQhI+J3EnD3gvuh+t1zB4bOMW9PJUdk27YQDyiGVd42QdHLofdTN1yXKXYZR1Bmy4W1pZhucSoDdS7fGfkKHm3zRMsijNOiPWHg0spMEchI4YTlIC43iFVdzSPE/2sIZfrW/9MspXfuWqFySsTsf6c6qJc6A0bNKJhMwIDAQAB', 'MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAO0m9rBaOFCEj4ncScPeC+6H63XMHhs4xb08lR2TbthAPKIZV3jZB0cuh91M3XJcpdhlHUGbLhbWlmG5xKgN1Lt8Z+QoebfNEyyKM06I9YeDSykwRyEjhhOUgLjeIVV3NI8T/awhl+tb/0yyld+5aoXJKxOx/pzqolzoDRs0omEzAgMBAAECgYBGzwt5PHb0E6CIGS4tPW9ymULEuV2D4z+ncR9U5WCDUSrJe6eSfbqellYazYiRTPh31DkYDa2FRC1CoKUHSJnrjeNR2TMw0WUBFvNcqYe2qOJZg3iOhyUDhIChhQiWWC9VrzAvqSU6tuyKGMy5rAWbfTneEnL7NHsTgRRDC+0JAQJBAPlRGW6T4TnRBtbOpRcMU+jdCyJAK3zwuRO13alhexDLq105D1osg2uP1d3+XvTQudwCGo1qRfBSp/W72fynz5kCQQDzgmLyxGzO1rugtJNMLQTqsRGg8ZUoUPmsEVGbmnHwRzd2OGHWbT1JuIEEb+ivrZV3PfeEObv7fDAT6qIhyiarAkAcd4ka2iG+U0KfpkqtXgf6r7qEt6T/iBDp0js0CuBdY5P2efxpxGlhD7RQu6ml9Gs0Vr0nZnoD3bw1z7QtKBAJAkBiqBjesqZCxs0NtxtWaYbsbwDta/M6elQtWnbtzA0NhEz8IKvC7E9AZvgejBiB1JoRzZFSiPGYWiBAcXduqTAxAkEAqG24ePhjesKoF1Us2ViqgJC7zDd96v+LI5eausw3TfKjO4jj5oMoQiyc+hZFxHYlkyZRfA6XEraF1Rdgngf65w==', '0', '测试游戏', '', '', null);

-- ----------------------------
-- Table structure for umsdkorder
-- ----------------------------
DROP TABLE IF EXISTS `umsdkorder`;
CREATE TABLE `umsdkorder` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `allMoney` int(11) NOT NULL,
  `appID` int(11) NOT NULL,
  `channelID` int(11) NOT NULL,
  `channelOrderID` varchar(255) DEFAULT NULL,
  `coinNum` int(11) NOT NULL,
  `createdTime` datetime DEFAULT NULL,
  `firstPay` int(11) NOT NULL,
  `state` int(11) NOT NULL,
  `userID` int(11) NOT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of umsdkorder
-- ----------------------------

-- ----------------------------
-- Table structure for uorder
-- ----------------------------
DROP TABLE IF EXISTS `uorder`;
CREATE TABLE `uorder` (
  `orderID` bigint(20) NOT NULL,
  `appID` int(11) NOT NULL,
  `channelID` int(11) NOT NULL,
  `channelOrderID` varchar(255) DEFAULT NULL,
  `currency` varchar(255) DEFAULT NULL,
  `extension` varchar(255) DEFAULT NULL,
  `money` int(11) NOT NULL,
  `realMoney` int(11) DEFAULT NULL,
  `state` int(11) NOT NULL,
  `username` varchar(255) NOT NULL,
  `userID` int(11) NOT NULL,
  `roleID` varchar(255) DEFAULT NULL,
  `roleName` varchar(255) DEFAULT NULL,
  `serverID` varchar(255) DEFAULT NULL,
  `serverName` varchar(255) DEFAULT NULL,
  `createdTime` datetime DEFAULT NULL,
  `sdkOrderTime` varchar(255) DEFAULT NULL,
  `completeTime` datetime DEFAULT NULL,
  `productID` varchar(255) DEFAULT NULL,
  `productName` varchar(255) DEFAULT NULL,
  `productDesc` varchar(255) DEFAULT NULL,
  `notifyUrl` varchar(2048) DEFAULT NULL,
  `platID` int(11) DEFAULT NULL,
  PRIMARY KEY (`orderID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of uorder
-- ----------------------------
INSERT INTO `uorder` VALUES ('654898395705507841', '1', '39', null, 'RMB', null, '100', '100', '4', '', '3', '1', '?????', '1', '??', null, '23453435', '2016-01-18 17:21:05', null, null, null, null, null);
INSERT INTO `uorder` VALUES ('654898640518643714', '1', '503', '', 'RMB', null, '100', null, '1', '', '3', '1', '?????', '1', '??', null, null, null, null, null, null, null, null);
INSERT INTO `uorder` VALUES ('654901505261830147', '1', '503', '', 'RMB', null, '100', null, '1', '', '3', '1', '?????', '1', '??', null, null, null, null, null, null, null, null);
INSERT INTO `uorder` VALUES ('654901677060521988', '1', '503', '', 'RMB', null, '100', null, '1', '', '3', '1', '?????', '1', '??', null, null, null, null, null, null, null, null);
INSERT INTO `uorder` VALUES ('654906289855397889', '1', '503', '', 'RMB', null, '100', null, '1', '', '3', '1', '?????', '1', '??', null, null, null, null, null, null, null, null);
INSERT INTO `uorder` VALUES ('654907385072058370', '1', '503', '', 'RMB', null, '100', null, '1', '', '3', '1', '?????', '1', '??', null, null, null, null, null, null, null, null);
INSERT INTO `uorder` VALUES ('654907569755652099', '1', '503', '', 'RMB', null, '100', null, '1', '', '3', '1', '?????', '1', '??', null, null, null, null, null, null, null, null);
INSERT INTO `uorder` VALUES ('654914501832867844', '1', '503', '', 'RMB', null, '100', null, '1', '', '3', '1', '测试角色名', '1', '测试', null, null, null, null, null, null, null, null);
INSERT INTO `uorder` VALUES ('656016779419582465', '1', '503', '', 'RMB', '1428891376228', '100', null, '1', '', '3', '1', '测试角色名', '1', '测试', null, null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('656068817243340801', '1', '503', '', 'RMB', '1428902015786', '100', null, '1', '', '3', '1', '测试角色名', '1', '测试', null, null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('656069040581640194', '1', '503', '656069040581640194', 'RMB', '1428902064311', '10000', null, '2', '', '3', '1', '测试角色名', '1', '测试', null, null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('658356789271592961', '1', '11', '', 'RMB', '1429255035989', '100', null, '1', '', '4', '1', '测试角色名', '1', '测试', null, null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('658896421847564289', '1', '503', '', 'RMB', '1429336831312', '100', null, '1', '', '5', '1', '测试角色名', '1', '测试', null, null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('659988610556100609', '1', '11', '11342423', 'RMB', '1429502514494', '10000', null, '2', '', '4', '1', '测试角色名', '1', '测试', null, null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('660073230001766401', '1', '16', '', 'RMB', '1429520025537', '100', null, '1', '', '7', '1', '测试角色名', '1', '测试', null, null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('660076983803183106', '1', '16', '', 'RMB', '1429520601543', '100', null, '1', '', '2', '1', '测试角色名', '1', '测试', null, null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('660081308835250177', '1', '16', '', 'RMB', '1429521547973', '100', null, '1', '', '2', '1', '测试角色名', '1', '测试', null, null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('660588278184935425', '1', '12', '235234554', 'RMB', '1429596446912', '100', null, '4', '', '9', '1', '测试角色名', '1', '测试', null, null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('660707025440735233', '1', '17', '8908009089', 'RMB', '1429620926766', '10000', null, '2', '', '10', '1', '测试角色名', '1', '测试', null, null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('661185128315224065', '1', '18', '34543545', 'RMB', '1429689773941', '100', null, '2', '', '11', '1', '测试角色名', '1', '测试', null, null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('661636280269930497', '1', '19', '43253454354', 'RMB', '1429753452230', '10000', null, '2', '', '12', '1', '测试角色名', '1', '测试', null, null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('661646034140659713', '1', '19', '', 'RMB', '1429755340542', '100', null, '1', '', '12', '1', '测试角色名', '1', '测试', null, null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('662260334723072001', '1', '20', '', 'RMB', '1429852229817', '100', null, '1', '', '14', '1', '测试角色名', '1', '测试', null, null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('662260944608428033', '1', '20', '', 'RMB', '1429852363141', '100', null, '1', '', '14', '1', '测试角色名', '1', '测试', '2015-04-24 13:11:18', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('662262684070182913', '1', '20', '', 'RMB', '1429852744334', '100', null, '1', '', '14', '1', '测试角色名', '1', '测试', '2015-04-24 13:17:39', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('662263066322272258', '1', '20', '', 'RMB', '1429852826621', '100', null, '1', '', '14', '1', '测试角色名', '1', '测试', '2015-04-24 13:19:00', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('662263439984427011', '1', '20', '', 'RMB', '1429852909172', '100', null, '1', '', '14', '1', '测试角色名', '1', '测试', '2015-04-24 13:20:23', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('662265226690822148', '1', '20', '', 'RMB', '1429853300922', '100', null, '1', '', '14', '1', '测试角色名', '1', '测试', '2015-04-24 13:26:55', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('662265982605066241', '1', '20', '', 'RMB', '1429853464556', '100', null, '1', '', '14', '1', '测试角色名', '1', '测试', '2015-04-24 13:29:39', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('662266193058463746', '1', '20', '', 'RMB', '1429853509977', '100', null, '1', '', '14', '1', '测试角色名', '1', '测试', '2015-04-24 13:30:24', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('662267039167021059', '1', '20', '', 'RMB', '1429853694257', '100', null, '1', '', '14', '1', '测试角色名', '1', '测试', '2015-04-24 13:33:29', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('662267941110153220', '1', '20', '', 'RMB', '1429853893283', '100', null, '1', '', '14', '1', '测试角色名', '1', '测试', '2015-04-24 13:36:47', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('662269891025305605', '1', '20', '', 'RMB', '1429854318860', '100', null, '1', '', '14', '1', '测试角色名', '1', '测试', '2015-04-24 13:43:53', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('662270006989422598', '1', '20', '', 'RMB', '1429854341767', '100', null, '1', '', '14', '1', '测试角色名', '1', '测试', '2015-04-24 13:44:16', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('662286259145670663', '1', '20', '34544345435', 'RMB', '1429857650169', '10000', null, '2', '', '14', '1', '测试角色名', '1', '测试', '2015-04-24 14:39:24', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('662286332160114696', '1', '20', '', 'RMB', '1429857667094', '10000', null, '1', '', '14', '1', '测试角色名', '1', '测试', '2015-04-24 14:39:41', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('662294917799739393', '1', '20', '', 'RMB', '1429859302535', '10000', null, '1', '', '14', '1', '测试角色名', '1', '测试', '2015-04-24 15:06:56', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('662294986519216130', '1', '20', '', 'RMB', '1429859314687', '10000', null, '1', '', '14', '1', '测试角色名', '1', '测试', '2015-04-24 15:07:08', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('662295033763856387', '1', '20', '', 'RMB', '1429859325232', '10000', null, '1', '', '14', '1', '测试角色名', '1', '测试', '2015-04-24 15:07:19', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('662295072418562052', '1', '20', '34544345435', 'RMB', '1429859334392', '10000', null, '2', '', '14', '1', '测试角色名', '1', '测试', '2015-04-24 15:07:28', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('662773862487818241', '1', '20', '', 'RMB', '1429928565714', '10000', null, '1', '', '15', '1', '测试角色名', '1', '测试', '2015-04-25 10:21:17', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('662775855352643586', '1', '20', '', 'RMB', '1429929001823', '10000', null, '1', '', '15', '1', '测试角色名', '1', '测试', '2015-04-25 10:28:33', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('662795139755802625', '1', '21', '23453454354', 'RMB', '1429932972301', '10000', null, '2', '', '16', '1', '测试角色名', '1', '测试', '2015-04-25 11:34:43', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('662849492566933505', '1', '22', '345435435', 'RMB', '1429944114939', '10000', null, '2', '', '17', '1', '测试角色名', '1', '测试', '2015-04-25 14:40:26', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('664070427215134721', '1', '24', '', 'RMB', '1430136220108', '10000', null, '1', '', '19', '1', '测试角色名', '1', '测试', '2015-04-27 20:02:09', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('664071522431795201', '1', '24', '', 'RMB', '1430136458764', '10000', null, '1', '', '19', '1', '测试角色名', '1', '测试', '2015-04-27 20:06:08', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('664071668460683266', '1', '24', '', 'RMB', '1430136493363', '10000', null, '1', '', '19', '1', '测试角色名', '1', '测试', '2015-04-27 20:06:42', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('664074159541714947', '1', '24', '', 'RMB', '1430137035192', '10000', null, '1', '', '19', '1', '测试角色名', '1', '测试', '2015-04-27 20:15:46', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('664074636283084804', '1', '24', '', 'RMB', '1430137138234', '10000', null, '1', '', '19', '1', '测试角色名', '1', '测试', '2015-04-27 20:17:29', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('664078991379922949', '1', '24', '', 'RMB', '1430138088360', '10000', null, '1', '', '19', '1', '测试角色名', '1', '测试', '2015-04-27 20:33:19', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('664444544636420097', '1', '24', '', 'RMB', '1430184044385', '10000', null, '1', '', '19', '1', '测试角色名', '1', '测试', '2015-04-28 09:19:11', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('664445433694650370', '1', '24', '', 'RMB', '1430184239937', '10000', null, '1', '', '19', '1', '测试角色名', '1', '测试', '2015-04-28 09:22:26', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('664446984177844227', '1', '24', '', 'RMB', '1430184576560', '10000', null, '1', '', '19', '1', '测试角色名', '1', '测试', '2015-04-28 09:28:03', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('664451287735074817', '1', '24', '', 'RMB', '1430185518777', '10000', null, '1', '', '19', '1', '测试角色名', '1', '测试', '2015-04-28 09:43:45', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('664551566631501825', '1', '25', '', 'RMB', '1430205967061', '10000', null, '1', '', '20', '1', '测试角色名', '1', '测试', '2015-04-28 15:24:33', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('664646085976784897', '1', '26', '345435345', 'RMB', '1430225395635', '10000', null, '2', '', '21', '1', '测试角色名', '1', '测试', '2015-04-28 20:48:24', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('664650990829436930', '1', '16', '', 'RMB', '1430226225721', '10000', null, '1', '', '2', '1', '测试角色名', '1', '测试', '2015-04-28 21:02:14', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('664654864889937923', '1', '10', '', 'RMB', '1430227072591', '10000', null, '1', '', '22', '1', '测试角色名', '1', '测试', '2015-04-28 21:16:20', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('665655343161802753', '1', '27', '', 'RMB', '1430374179468', '10000', null, '1', '', '23', '1', '测试角色名', '1', '测试', '2015-04-30 14:08:02', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('665674460061237249', '1', '28', '', 'RMB', '1430378112507', '10000', null, '1', '', '24', '1', '测试角色名', '1', '测试', '2015-04-30 15:13:37', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('665702862679965697', '1', '30', '', 'RMB', '1430384074782', '10000', null, '1', '', '25', '1', '测试角色名', '1', '测试', '2015-04-30 16:52:58', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('668974012851814401', '1', '30', '', 'RMB', '1430707860967', '10000', null, '1', '', '25', '1', '测试角色名', '1', '测试', '2015-05-04 10:49:18', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('668992266462822401', '1', '29', '', 'RMB', '1430711607352', '10000', null, '1', '', '26', '1', '测试角色名', '1', '测试', '2015-05-04 11:51:44', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('669055462611615745', '1', '31', '', 'RMB', '1430724441428', '10000', null, '1', '', '27', '1', '测试角色名', '1', '测试', '2015-05-04 15:25:38', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('669154328463802369', '1', '33', '', 'RMB', '1430744577958', '10000', null, '1', '', '28', '1', '测试角色名', '1', '测试', '2015-05-04 21:01:17', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('669509243086307329', '1', '33', '', 'RMB', '1430788450844', '10000', null, '1', '', '28', '1', '测试角色名', '1', '测试', '2015-05-05 09:12:28', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('669509320395718658', '1', '33', '', 'RMB', '1430788467997', '10000', null, '1', '', '28', '1', '测试角色名', '1', '测试', '2015-05-05 09:12:46', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('669509749892448259', '1', '33', '', 'RMB', '1430788560801', '10000', null, '1', '', '28', '1', '测试角色名', '1', '测试', '2015-05-05 09:14:18', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('670082350637383681', '1', '34', '', 'RMB', '1430877071806', '10000', null, '1', '', '29', '1', '测试角色名', '1', '测试', '2015-05-06 09:49:25', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('670082397882023938', '1', '34', '', 'RMB', '1430877083005', '10000', null, '1', '', '29', '1', '测试角色名', '1', '测试', '2015-05-06 09:49:36', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('670086774453698561', '1', '34', '', 'RMB', '1430877797785', '10000', null, '1', '', '29', '1', '测试角色名', '1', '测试', '2015-05-06 10:01:31', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('670097932778733569', '1', '34', '', 'RMB', '1430880232098', '10000', null, '1', '', '29', '1', '测试角色名', '1', '测试', '2015-05-06 10:42:05', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('670100526938980353', '1', '34', '', 'RMB', '1430880799686', '10000', null, '1', '', '29', '1', '测试角色名', '1', '测试', '2015-05-06 10:51:33', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('670101510486491137', '1', '34', '', 'RMB', '1430881013318', '10000', null, '1', '', '29', '1', '测试角色名', '1', '测试', '2015-05-06 10:55:06', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('670106737461690370', '1', '32', '', 'RMB', '1430881914216', '10000', null, '1', '', '30', '1', '测试角色名', '1', '测试', '2015-05-06 11:10:07', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('670114107625570305', '1', '32', '', 'RMB', '1430883525743', '10000', null, '1', '', '30', '1', '测试角色名', '1', '测试', '2015-05-06 11:36:59', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('670115159892557825', '1', '32', '', 'RMB', '1430883754639', '10000', null, '1', '', '30', '1', '测试角色名', '1', '测试', '2015-05-06 11:40:48', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('670117324556075009', '1', '32', '', 'RMB', '1430884227289', '10000', null, '1', '', '30', '1', '测试角色名', '1', '测试', '2015-05-06 11:48:40', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('670144438684614657', '1', '32', '', 'RMB', '1430889663280', '10000', null, '1', '', '30', '1', '测试角色名', '1', '测试', '2015-05-06 13:19:17', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('670145864613756930', '1', '32', '', 'RMB', '1430889975160', '10000', null, '1', '', '30', '1', '测试角色名', '1', '测试', '2015-05-06 13:24:29', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('670146182441336835', '1', '32', '', 'RMB', '1430890044859', '10000', null, '1', '', '30', '1', '测试角色名', '1', '测试', '2015-05-06 13:25:39', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('670148896860667908', '1', '32', '', 'RMB', '1430890637438', '10000', null, '1', '', '30', '1', '测试角色名', '1', '测试', '2015-05-06 13:35:31', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('670150112336412673', '1', '32', '', 'RMB', '1430890903744', '10000', null, '1', '', '30', '1', '测试角色名', '1', '测试', '2015-05-06 13:39:58', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('670152216870387713', '1', '32', '', 'RMB', '1430891361948', '10000', null, '1', '', '30', '1', '测试角色名', '1', '测试', '2015-05-06 13:47:36', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('670154768080961537', '1', '32', '', 'RMB', '1430891920585', '10000', null, '1', '', '30', '1', '测试角色名', '1', '测试', '2015-05-06 13:56:54', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('670155420915990529', '1', '32', '', 'RMB', '1430892060400', '10000', null, '1', '', '30', '1', '测试角色名', '1', '测试', '2015-05-06 13:59:14', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('672890219866882049', '1', '32', '', 'RMB', '1431307580742', '10000', null, '1', '', '30', '1', '测试角色名', '1', '测试', '2015-05-11 09:24:23', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('672893509811830785', '1', '32', '', 'RMB', '1431308298532', '10000', null, '1', '', '30', '1', '测试角色名', '1', '测试', '2015-05-11 09:36:21', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('672895425367244801', '1', '32', '', 'RMB', '1431308716741', '10000', null, '1', '', '30', '1', '测试角色名', '1', '测试', '2015-05-11 09:43:19', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('672897212073639937', '1', '32', '', 'RMB', '1431309109066', '10000', null, '1', '', '30', '1', '测试角色名', '1', '测试', '2015-05-11 09:49:51', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('672898341650038785', '1', '32', '', 'RMB', '1431309355940', '10000', null, '1', '', '30', '1', '测试角色名', '1', '测试', '2015-05-11 09:53:58', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('672906691066462209', '1', '32', '', 'RMB', '1431310936517', '10000', null, '1', '', '30', '1', '测试角色名', '1', '测试', '2015-05-11 10:20:18', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('672910311723892737', '1', '32', '', 'RMB', '1431311727630', '10000', null, '1', '', '30', '1', '测试角色名', '1', '测试', '2015-05-11 10:33:29', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('672915027597983745', '1', '32', '', 'RMB', '1431312758564', '10000', null, '1', '', '30', '1', '测试角色名', '1', '测试', '2015-05-11 10:50:39', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('672916273138499585', '1', '32', '', 'RMB', '1431313028463', '10000', null, '1', '', '30', '1', '测试角色名', '1', '测试', '2015-05-11 10:55:09', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('672916956038299649', '1', '32', '', 'RMB', '1431313179550', '10000', null, '1', '', '30', '1', '测试角色名', '1', '测试', '2015-05-11 10:57:40', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('672918987557830657', '1', '32', '', 'RMB', '1431313380135', '10000', null, '1', '', '30', '1', '测试角色名', '1', '测试', '2015-05-11 11:01:01', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('672919979695276033', '1', '32', '', 'RMB', '1431313600119', '10000', null, '1', '', '30', '1', '测试角色名', '1', '测试', '2015-05-11 11:04:40', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('672920976127688705', '1', '32', '', 'RMB', '1431313815855', '10000', null, '1', '', '30', '1', '测试角色名', '1', '测试', '2015-05-11 11:08:16', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('672921521588535297', '1', '32', '', 'RMB', '1431313934537', '10000', null, '1', '', '30', '1', '测试角色名', '1', '测试', '2015-05-11 11:10:15', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('672924356266950658', '1', '32', '', 'RMB', '1431314555043', '10000', null, '1', '', '30', '1', '测试角色名', '1', '测试', '2015-05-11 11:20:35', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('673003748737417219', '1', '32', '', 'RMB', '1431330920185', '10000', null, '1', '', '30', '1', '测试角色名', '1', '测试', '2015-05-11 15:53:24', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('673004341442904068', '1', '32', '', 'RMB', '1431331049136', '10000', null, '1', '', '30', '1', '测试角色名', '1', '测试', '2015-05-11 15:55:34', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('673007463884128257', '1', '32', '', 'RMB', '1431331493631', '10000', null, '1', '', '30', '1', '测试角色名', '1', '测试', '2015-05-11 16:02:57', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('673008649295101954', '1', '32', '', 'RMB', '1431331749444', '10000', null, '1', '', '30', '1', '测试角色名', '1', '测试', '2015-05-11 16:07:13', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('694418605999128577', '1', '16', '', 'RMB', '1434532104099', '10000', null, '1', '', '2', '1', '测试角色名', '1', '测试', '2015-06-17 17:08:11', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('694423205909102594', '1', '12', '', 'RMB', '1434533112024', '10000', null, '1', '', '32', '1', '测试角色名', '1', '测试', '2015-06-17 17:24:58', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('694423343348056067', '1', '12', '', 'RMB', '1434533139501', '10000', null, '1', '', '32', '1', '测试角色名', '1', '测试', '2015-06-17 17:25:26', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('694423467902107652', '1', '12', '', 'RMB', '1434533169429', '10000', null, '1', '', '33', '1', '测试角色名', '1', '测试', '2015-06-17 17:25:55', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('694429987662462981', '1', '17', '', 'RMB', '1434534590449', '10000', null, '1', '', '10', '1', '测试角色名', '1', '测试', '2015-06-17 17:49:37', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('694876041490989057', '1', '24', '', 'RMB', '1434596914978', '10000', null, '1', '', '34', '1', '测试角色名', '1', '测试', '2015-06-18 11:08:20', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('694876170340007938', '1', '24', '', 'RMB', '1434596945965', '10000', null, '1', '', '34', '1', '测试角色名', '1', '测试', '2015-06-18 11:08:50', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('694876389383340035', '1', '24', '', 'RMB', '1434596993520', '10000', null, '1', '', '34', '1', '测试角色名', '1', '测试', '2015-06-18 11:09:37', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('694880757365080068', '1', '24', '', 'RMB', '1434597945905', '10000', null, '1', '', '34', '1', '测试角色名', '1', '测试', '2015-06-18 11:25:30', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('701798296116527105', '1', '10', '', 'RMB', '1435668001774', '10000', null, '1', '', '22', '1', '测试角色名', '1', '测试', '2015-06-30 20:39:21', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('706207367808679937', '1', '12', '', 'RMB', '1436166980543', '10000', null, '1', '', '33', '1', '测试角色名', '1', '测试', '2015-07-06 15:15:28', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('793941923189489665', '1', '12', '', 'RMB', '1449023088424', '10000', null, '1', '1434533153312.baidu', '33', '1', '测试角色名', '10', '测试', '2015-12-02 10:23:57', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('793942554549682178', '1', '17', '', 'RMB', '1449023224258', '10000', null, '1', '1429620637444.xiaomi', '10', '1', '测试角色名', '10', '测试', '2015-12-02 10:26:12', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('793958445928677379', '1', '30', '', 'RMB', '1449026451590', '10000', null, '1', '1449026379630.paojiao', '35', '1', '测试角色名', '10', '测试', '2015-12-02 11:20:00', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('793967087402876932', '1', '23', '', 'RMB', '1449028340470', '10000', null, '1', '1430115992021.lenovo', '18', '1', '测试角色名', '10', '测试', '2015-12-02 11:51:28', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('794010831644786693', '1', '20', '', 'RMB', '1449037168976', '10000', null, '1', '1429852091204.vivo', '14', '1', '测试角色名', '10', '测试', '2015-12-02 14:18:37', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('794012940473729025', '1', '20', '', 'RMB', '1449037627597', '10000', null, '1', '1429852091204.vivo', '14', '1', '测试角色名', '10', '测试', '2015-12-02 14:26:16', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('794013524589281282', '1', '20', '', 'RMB', '1449037756335', '10000', null, '1', '1429852091204.vivo', '14', '1', '测试角色名', '10', '测试', '2015-12-02 14:28:24', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('794014357812936705', '1', '20', '', 'RMB', '1449037938043', '10000', null, '1', '1429852091204.vivo', '14', '1', '测试角色名', '10', '测试', '2015-12-02 14:31:26', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('794015895411228674', '1', '20', '', 'RMB', '1449038271927', '10000', null, '1', '1429852091204.vivo', '14', '1', '测试角色名', '10', '测试', '2015-12-02 14:37:00', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('794015942655868931', '1', '20', '', 'RMB', '1449038283594', '10000', null, '1', '1429852091204.vivo', '14', '1', '测试角色名', '10', '测试', '2015-12-02 14:37:11', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('794019301320294401', '1', '20', '', 'RMB', '1449039016968', '10000', null, '1', '1429852091204.vivo', '14', '1', '测试角色名', '10', '测试', '2015-12-02 14:49:25', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('794019945565388801', '1', '20', '', 'RMB', '1449039159606', '10000', null, '1', '1429852091204.vivo', '14', '1', '测试角色名', '10', '测试', '2015-12-02 14:51:47', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('797316491878858753', '1', '20', '', 'RMB', '1449540823193', '10000', '0', '1', '1449217321152.vivo', '38', '1', '测试角色名', '10', '测试', '2015-12-08 10:12:32', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('797316556303368194', '1', '20', '', 'RMB', '1449540837083', '10000', '0', '1', '1449217321152.vivo', '38', '1', '测试角色名', '10', '测试', '2015-12-08 10:12:47', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('797317011569901571', '1', '20', '', 'RMB', '1449540937055', '10000', '0', '1', '1449217321152.vivo', '38', '1', '测试角色名', '10', '测试', '2015-12-08 10:14:25', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('805800383728320513', '1', '20', '', 'RMB', '1450844925440', '10000', '0', '1', '1449217321152.vivo', '38', '1', '测试角色名', '10', '测试', '2015-12-23 12:28:46', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('805901178020823041', '1', '20', '', 'RMB', '1450865484927', '10000', '0', '1', '1449217321152.vivo', '38', '1', '测试角色名', '10', '测试', '2015-12-23 18:11:26', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('886427760371695617', '1', '35', '', 'RMB', '1452080296655', '10000', '0', '1', '1452080230704.txmsdk', '48', '1', '测试角色名', '10', '测试', '2016-01-06 19:37:26', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('886428249997967362', '1', '35', '', 'RMB', '1452080402957', '10000', '0', '1', '1452080230704.txmsdk', '48', '1', '测试角色名', '10', '测试', '2016-01-06 19:39:12', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('886429306559922179', '1', '35', '', 'RMB', '1452080632930', '10000', '0', '1', '1452080230704.txmsdk', '48', '1', '测试角色名', '10', '测试', '2016-01-06 19:43:02', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('886429542783123460', '1', '35', '', 'RMB', '1452080687933', '10000', '0', '1', '1452080230704.txmsdk', '48', '1', '测试角色名', '10', '测试', '2016-01-06 19:43:57', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('886429766121422853', '1', '35', '', 'RMB', '1452080736157', '10000', '0', '1', '1452080230704.txmsdk', '48', '1', '测试角色名', '10', '测试', '2016-01-06 19:44:45', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('886429959394951174', '1', '35', '', 'RMB', '1452080776224', '10000', '0', '1', '1452080230704.txmsdk', '48', '1', '测试角色名', '10', '测试', '2016-01-06 19:45:26', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('886431973734612999', '1', '35', '', 'RMB', '1452081217310', '10000', '0', '1', '1452080230704.txmsdk', '48', '1', '测试角色名', '10', '测试', '2016-01-06 19:52:47', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('886432141238337544', '1', '35', '', 'RMB', '1452081253239', '10000', '0', '1', '1452080230704.txmsdk', '48', '1', '测试角色名', '10', '测试', '2016-01-06 19:53:22', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('887523110176161793', '1', '12', '', 'RMB', '1452246910713', '10000', '0', '1', '1434533153312.baidu', '33', '1', '测试角色名', '10', '测试', '2016-01-08 17:54:17', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('898676899725705217', '1', '38', '', 'RMB', '1453953371161', '10000', null, '1', '1449563378170.youku', '39', '1', '测试角色名', '10', '测试', '2016-01-28 11:55:33', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('898780782099693570', '1', '50', '', 'RMB', '1453974651541', '10000', null, '1', '1450841036321.pptv', '46', '1', '测试角色名', '10', '测试', '2016-01-28 17:49:28', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('912166803591921665', '1', '29', '', 'RMB', '1455936308327', '100', null, '1', '1455936204042.guopan', '50', '1', '测试角色名', '10', '测试', '2016-02-20 10:43:32', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('912174891015340034', '1', '16', '', 'RMB', '1455937835377', '100', null, '1', '1455937732110.qihoo', '51', '1', '测试角色名', '10', '测试', '2016-02-20 11:08:59', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('920629830490259457', '1', '18', '', 'RMB', '1456976947389', '100', null, '1', '1456976849166.m4399', '53', '1', '测试角色名', '10', '测试', '2016-03-03 11:47:52', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('923397180998287361', '1', '34', '', 'RMB', '1457399092431', '100', null, '1', '1430876912124.am', '29', '1', '测试角色名', '10', '测试', '2016-03-08 09:03:24', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('923545378844835841', '1', '20', '', 'RMB', '1457429525073', '100', null, '1', '1429852091204.vivo', '14', '1', '测试角色名', '10', '测试', '2016-03-08 17:30:33', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('937516001220624385', '1', '32', '', 'RMB', '1459482008019', '100', null, '1', '1430881797104.coolpad', '30', '1', '测试角色名', '10', '测试', '2016-04-01 11:39:22', null, null, null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('941485186657288193', '1', '1701', '', 'RMB', 'none', '10000', null, '1', '1460085825988.demo', '57', 'dev-1111', '我擦', '1', '桃园结义', '2016-04-08 13:15:10', null, null, null, '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('941485766477873154', '1', '1701', '', 'RMB', 'none', '10000', null, '1', '1460085825988.demo', '57', 'dev-1111', '我擦', '1', '桃园结义', '2016-04-08 13:17:17', null, null, null, '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('941495103736774659', '1', '1701', '', 'RMB', 'none', '10000', null, '1', '1460085825988.demo', '57', 'dev-1111', '我擦', '1', '桃园结义', '2016-04-08 13:51:15', null, null, null, '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('947807773653991425', '1', '28', '', 'RMB', 'none', '100', null, '1', '1461069622345.mumayi', '58', 'dev-1111', '我擦', '1', '桃园结义', '2016-04-19 20:40:38', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('947809465871106050', '1', '28', '', 'RMB', 'none', '100', null, '1', '1461070000406.mumayi', '59', 'dev-1111', '我擦', '1', '桃园结义', '2016-04-19 20:46:48', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('948269684501774337', '1', '49', '', 'RMB', 'none', '100', null, '1', '1461135412792.pps', '60', 'dev-1111', '我擦', '1', '桃园结义', '2016-04-20 14:57:01', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('948367425072529410', '1', '32', '', 'RMB', 'none', '100', null, '1', '1430881797104.coolpad', '30', 'dev-1111', '我擦', '1', '桃园结义', '2016-04-20 20:28:38', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('948370727902380035', '1', '32', '', 'RMB', 'none', '100', null, '1', '1430881797104.coolpad', '30', 'dev-1111', '我擦', '1', '桃园结义', '2016-04-20 20:40:39', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('948744488841379841', '1', '17', '', 'RMB', 'none', '100', null, '1', '1456306757555.xiaomi', '52', 'dev-1111', '我擦', '1', '桃园结义', '2016-04-21 09:56:22', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('948932380775677953', '1', '36', '', 'RMB', 'none', '100', null, '1', '1461242149314.gfan', '61', 'dev-1111', '我擦', '1', '桃园结义', '2016-04-21 20:35:57', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('948935017885597698', '1', '36', '', 'RMB', 'none', '100', null, '1', '1461242149314.gfan', '61', 'dev-1111', '我擦', '1', '桃园结义', '2016-04-21 20:45:31', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('948941267063013379', '1', '36', '', 'RMB', 'none', '100', null, '1', '1461242149314.gfan', '61', 'dev-1111', '我擦', '1', '桃园结义', '2016-04-21 21:04:14', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('949288394909810689', '1', '36', '', 'RMB', 'none', '100', null, '1', '1461242149314.gfan', '61', 'dev-1111', '我擦', '1', '桃园结义', '2016-04-22 08:51:04', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('949290748551888898', '1', '36', '', 'RMB', 'none', '100', null, '1', '1461242149314.gfan', '61', 'dev-1111', '我擦', '1', '桃园结义', '2016-04-22 08:59:40', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('949300983458955267', '1', '36', '', 'RMB', 'none', '100', null, '1', '1461242149314.gfan', '61', 'dev-1111', '我擦', '1', '桃园结义', '2016-04-22 09:32:55', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('949870427402928129', '1', '16', '', 'RMB', 'none', '100', null, '1', '1425101806170.qihoo', '2', 'dev-1111', '我擦', '1', '桃园结义', '2016-04-23 09:56:31', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('949870526187175938', '1', '16', '', 'RMB', 'none', '100', null, '1', '1425101806170.qihoo', '2', 'dev-1111', '我擦', '1', '桃园结义', '2016-04-23 09:56:54', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('960007447869652993', '1', '51', '', 'RMB', 'none', '100', null, '1', '1462759602008.ysdk', '62', 'dev-1111', '我擦', '1', '桃园结义', '2016-05-09 10:06:48', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('960009084252192769', '1', '51', '', 'RMB', 'none', '100', null, '1', '1462759602008.ysdk', '62', 'dev-1111', '我擦', '1', '桃园结义', '2016-05-09 10:12:45', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('960017545337765889', '1', '51', '', 'RMB', 'none', '100', null, '1', '1462759602008.ysdk', '62', 'dev-1111', '我擦', '1', '桃园结义', '2016-05-09 10:43:31', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('960018241122467842', '1', '51', '', 'RMB', 'none', '100', null, '1', '1462759602008.ysdk', '62', 'dev-1111', '我擦', '1', '桃园结义', '2016-05-09 10:46:01', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('960019082936057859', '1', '51', '', 'RMB', 'none', '100', null, '1', '1462759602008.ysdk', '62', 'dev-1111', '我擦', '1', '桃园结义', '2016-05-09 10:49:05', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('960079139463757825', '1', '51', '', 'RMB', 'none', '100', null, '1', '1462759602008.ysdk', '62', 'dev-1111', '我擦', '1', '桃园结义', '2016-05-09 14:11:36', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('960081810933415937', '1', '51', '', 'RMB', 'none', '100', null, '1', '1462759602008.ysdk', '62', 'dev-1111', '我擦', '1', '桃园结义', '2016-05-09 14:21:18', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('960081948372369410', '1', '51', '', 'RMB', 'none', '100', null, '1', '1462759602008.ysdk', '62', 'dev-1111', '我擦', '1', '桃园结义', '2016-05-09 14:21:50', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('960083722193862657', '1', '51', '', 'RMB', 'none', '100', null, '1', '1462759602008.ysdk', '62', 'dev-1111', '我擦', '1', '桃园结义', '2016-05-09 14:28:15', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('960084207525167106', '1', '51', '', 'RMB', 'none', '100', '100', '2', '1462759602008.ysdk', '62', 'dev-1111', '我擦', '1', '桃园结义', '2016-05-09 14:30:00', '20160509143011', '2016-05-09 14:30:11', '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('960084314899349507', '1', '51', '', 'RMB', 'none', '100', '100', '2', '1462759602008.ysdk', '62', 'dev-1111', '我擦', '1', '桃园结义', '2016-05-09 14:30:25', '20160509143114', '2016-05-09 14:31:14', '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('960109685271166977', '1', '51', '', 'RMB', 'none', '100', null, '1', '1462759602008.ysdk', '62', 'dev-1111', '我擦', '1', '桃园结义', '2016-05-09 15:58:44', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('960114018893168642', '1', '51', '', 'RMB', 'none', '100', null, '1', '1462759602008.ysdk', '62', 'dev-1111', '我擦', '1', '桃园结义', '2016-05-09 16:10:29', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('960117012485373955', '1', '51', '', 'RMB', 'none', '100', null, '1', '1462759602008.ysdk', '62', 'dev-1111', '我擦', '1', '桃园结义', '2016-05-09 16:21:22', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('960564560962519041', '1', '20', '', 'RMB', 'none', '100', null, '1', '1462763279387.vivo', '63', 'dev-1111', '我擦', '1', '桃园结义', '2016-05-10 09:49:33', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('960564805775654914', '1', '20', '', 'RMB', 'none', '100', null, '1', '1462763279387.vivo', '63', 'dev-1111', '我擦', '1', '桃园结义', '2016-05-10 09:50:26', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('960657791817613313', '1', '52', '', 'RMB', 'none', '100', null, '1', '1462863680503.sougou', '64', 'dev-1111', '我擦', '1', '桃园结义', '2016-05-10 15:04:44', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('961883429455003649', '1', '30', '', 'RMB', 'none', '100', null, '1', '1463056867091.paojiao', '65', 'dev-1111', '我擦', '1', '桃园结义', '2016-05-12 20:47:34', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('962358319693955073', '1', '51', '', 'RMB', 'none', '100', null, '1', '1463125621042.ysdk', '66', 'dev-1111', '我擦', '1', '桃园结义', '2016-05-13 15:47:11', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('962358628931600386', '1', '51', '', 'RMB', 'none', '100', '100', '2', '1463125621042.ysdk', '66', 'dev-1111', '我擦', '1', '桃园结义', '2016-05-13 15:48:19', '20160513154833', '2016-05-13 15:48:33', '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('962358787845390339', '1', '51', '', 'RMB', 'none', '100', '100', '2', '1463125621042.ysdk', '66', 'dev-1111', '我擦', '1', '桃园结义', '2016-05-13 15:48:56', '20160513154918', '2016-05-13 15:49:18', '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('962358985413885956', '1', '51', '', 'RMB', 'none', '100', '100', '2', '1463125621042.ysdk', '66', 'dev-1111', '我擦', '1', '桃园结义', '2016-05-13 15:49:38', '20160513154948', '2016-05-13 15:49:48', '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('962359036953493509', '1', '51', '', 'RMB', 'none', '100', '100', '2', '1463125621042.ysdk', '66', 'dev-1111', '我擦', '1', '桃园结义', '2016-05-13 15:49:50', '20160513154957', '2016-05-13 15:49:57', '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('962917339752300545', '1', '51', '', 'RMB', 'none', '100', null, '1', '1463211168144.ysdk', '67', 'dev-1111', '我擦', '1', '桃园结义', '2016-05-14 15:32:56', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('962918336184713218', '1', '51', '', 'RMB', 'none', '100', null, '1', '1463211168144.ysdk', '67', 'dev-1111', '我擦', '1', '桃园结义', '2016-05-14 15:36:32', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('962918409199157251', '1', '51', '', 'RMB', 'none', '100', null, '1', '1463211168144.ysdk', '67', 'dev-1111', '我擦', '1', '桃园结义', '2016-05-14 15:36:49', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('968481474179235842', '1', '23', '2160524115213122227904357', 'RMB', '1430119175223', '10000', '600', '2', '', '18', '1', '测试角色名', '1', '测试', '2015-04-27 15:18:04', '2016-05-24 11:52:13', '2016-05-24 16:49:41', null, '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('969108745562882049', '1', '11', '', 'RMB', 'none', '100', null, '1', '1464161338877.dl', '72', 'dev-1111', '我擦', '1', '桃园结义', '2016-05-25 15:29:05', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('971829010344443905', '1', '12', '', 'RMB', 'none', '100', null, '1', '1434533153312.baidu', '33', 'dev-1111', '我擦', '1', '桃园结义', '2016-05-30 10:05:22', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('971848273272766466', '1', '51', '', 'RMB', 'none', '100', null, '1', '1463125621042.ysdk', '66', 'dev-1111', '我擦', '1', '桃园结义', '2016-05-30 11:11:27', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('971848286157668355', '1', '51', '', 'RMB', 'none', '100', null, '1', '1463125621042.ysdk', '66', 'dev-1111', '我擦', '1', '桃园结义', '2016-05-30 11:11:30', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('971859564741787649', '1', '51', '', 'RMB', 'none', '100', null, '1', '1464580346818.ysdk', '75', 'dev-1111', '我擦', '1', '桃园结义', '2016-05-30 11:52:32', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('971875022329085953', '1', '51', '', 'RMB', 'none', '100', null, '1', '1464580346818.ysdk', '75', 'dev-1111', '我擦', '1', '桃园结义', '2016-05-30 12:44:47', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('973654569013739521', '1', '17', '', 'RMB', 'none', '100', null, '1', '1456306757555.xiaomi', '52', 'dev-1111', '我擦', '1', '桃园结义', '2016-06-01 17:54:44', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('974139904613154817', '1', '12', '', 'RMB', '1464845308907', '100', null, '1', '1434533153312.baidu', '33', '1', '测试角色名', '10', '测试', '2016-06-02 13:28:21', null, null, '1', '元宝', '购买100元宝', null, null);
INSERT INTO `uorder` VALUES ('977535308188876801', '1', '19', '', 'RMB', 'none', '100', null, '1', '1429753350836.oppo', '12', 'dev-1111', '我擦', '1', '桃园结义', '2016-06-08 14:28:47', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('977545633290256386', '1', '44', '', 'RMB', 'none', '100', null, '1', '1465369336234.liebao', '78', 'dev-1111', '我擦', '1', '桃园结义', '2016-06-08 15:02:19', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('980867953702469633', '1', '34', '980867953702469633', 'RMB', 'none', '100', null, '1', '1430876912124.am', '29', 'dev-1111', '我擦', '1', '桃园结义', '2016-06-14 11:56:53', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('980890102848815106', '1', '33', '', 'RMB', 'none', '100', null, '1', '1465880957463.ouwan', '79', 'dev-1111', '我擦', '1', '桃园结义', '2016-06-14 13:09:26', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('981557536471646209', '1', '20', '', 'RMB', 'none', '100', null, '1', '1462763279387.vivo', '63', 'dev-1111', '我擦', '1', '桃园结义', '2016-06-15 19:05:33', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('982097379501015041', '1', '20', '', 'RMB', 'none', '100', null, '1', '1462763279387.vivo', '63', 'dev-1111', '我擦', '1', '桃园结义', '2016-06-16 17:49:29', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('983135868233449473', '1', '20', '', 'RMB', 'none', '100', null, '1', '1462763279387.vivo', '63', 'dev-1111', '我擦', '1', '桃园结义', '2016-06-18 12:51:29', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('983176275285770242', '1', '20', '', 'RMB', 'none', '100', null, '1', '1462763279387.vivo', '63', 'dev-1111', '我擦', '1', '桃园结义', '2016-06-18 15:06:29', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('983195035702919169', '1', '20', '', 'RMB', '1466237460966', '100', null, '1', '1462763279387.vivo', '63', '1', '测试角色名', '10', '测试', '2016-06-18 16:10:45', null, null, '1', '元宝', '购买100元宝', 'http://192.168.18.9:8080/pay/game', null);
INSERT INTO `uorder` VALUES ('983198166734077954', '1', '20', '', 'RMB', '1466238142592', '100', null, '1', '1462763279387.vivo', '63', '1', '测试角色名', '10', '测试', '2016-06-18 16:22:06', null, null, '1', '元宝', '购买100元宝', 'http://192.168.18.9:8080/pay/game', null);
INSERT INTO `uorder` VALUES ('983205137465999363', '1', '20', '', 'RMB', 'none', '100', null, '1', '1462763279387.vivo', '63', 'dev-1111', '我擦', '1', '桃园结义', '2016-06-18 16:47:29', null, null, '1', '元宝', '100元宝，送20', null, null);
INSERT INTO `uorder` VALUES ('997714060977897473', '1', '16', '', 'RMB', '1468287105218', '100', null, '1', '1468287031678.qihoo', '83', '1', '测试角色名', '10', '测试', '2016-07-12 09:30:39', null, null, '1', '元宝', '购买100元宝', 'http://192.168.18.9:8080/pay/game', null);
INSERT INTO `uorder` VALUES ('997714692338089986', '1', '22', '', 'RMB', '1468287244322', '100', null, '1', '1452082210043.wdj', '49', '1', '测试角色名', '10', '测试', '2016-07-12 09:32:58', null, null, '1', '元宝', '购买100元宝', 'http://192.168.18.9:8080/pay/game', null);
INSERT INTO `uorder` VALUES ('1011359438365261825', '1', '16', '', 'RMB', '1470301751848', '100', null, '1', '1468287031678.qihoo', '83', '1', '测试角色名', '10', '测试', '2016-08-04 17:08:13', null, null, '1', '元宝', '购买100元宝', 'http://192.168.18.9:8080/pay/game', null);
INSERT INTO `uorder` VALUES ('1015169529623347201', '1', '24', '', 'RMB', '1470879935393', '100', null, '1', '1434596900138.huawei', '34', '1', '测试角色名', '10', '测试', '2016-08-11 09:45:15', null, null, '1', '元宝', '购买100元宝', 'http://192.168.18.9:8080/pay/game', null);
INSERT INTO `uorder` VALUES ('1015169606932758530', '1', '24', '', 'RMB', '1470879953233', '100', null, '1', '1434596900138.huawei', '34', '1', '测试角色名', '10', '测试', '2016-08-11 09:45:33', null, null, '1', '元宝', '购买100元宝', 'http://192.168.18.9:8080/pay/game', null);
INSERT INTO `uorder` VALUES ('1015169817386156035', '1', '24', '', 'RMB', '1470879998846', '100', null, '1', '1434596900138.huawei', '34', '1', '测试角色名', '10', '测试', '2016-08-11 09:46:18', null, null, '1', '元宝', '购买100元宝', 'http://192.168.18.9:8080/pay/game', null);
INSERT INTO `uorder` VALUES ('1015170070789226500', '1', '24', '', 'RMB', '1470880053963', '100', null, '1', '1434596900138.huawei', '34', '1', '测试角色名', '10', '测试', '2016-08-11 09:47:13', null, null, '1', '元宝', '购买100元宝', 'http://192.168.18.9:8080/pay/game', null);
INSERT INTO `uorder` VALUES ('1027588891416199169', '1', '85', '', 'RMB', '1472701387544', '100', null, '1', '1472701355404.yile', '85', '1', '测试角色名', '10', '测试', '2016-09-01 11:42:39', null, null, '1', '元宝', '购买100元宝', 'http://192.168.18.9:8080/pay/game', null);

-- ----------------------------
-- Table structure for uuser
-- ----------------------------
DROP TABLE IF EXISTS `uuser`;
CREATE TABLE `uuser` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `appID` int(11) NOT NULL,
  `channelID` int(11) NOT NULL,
  `channelUserID` varchar(255) DEFAULT NULL,
  `channelUserName` varchar(255) DEFAULT NULL,
  `channelUserNick` varchar(255) DEFAULT NULL,
  `createTime` datetime DEFAULT NULL,
  `lastLoginTime` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=86 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of uuser
-- ----------------------------
INSERT INTO `uuser` VALUES ('1', '1', '16', null, null, null, '2015-02-28 11:23:50', '1425093830573', '1425093830573.qihoo', '1262599d7f5e71544d4956e7b1406e24');
INSERT INTO `uuser` VALUES ('2', '1', '16', '746123316', 'chenjie115521', '哈哈哈哇身上', '2015-02-28 13:36:46', '1461376564964', '1425101806170.qihoo', '5cbfc8a572494a701b9bdeb947283509');
INSERT INTO `uuser` VALUES ('3', '1', '503', '1347463825', '1347463825', '九游玩家743863934', '2015-04-10 11:07:02', '1425093830573', '1428635222022.uc', '973c5f13242e0fde7fbaefa4dc8d899b');
INSERT INTO `uuser` VALUES ('4', '1', '11', '0', null, null, '2015-04-17 15:15:48', '1459238545245', '1429254948678.dl', '4b243adcef7961fb96784bf718c19136');
INSERT INTO `uuser` VALUES ('5', '1', '503', '1999722123', '1999722123', '九游玩家602532480', '2015-04-17 17:32:29', '1425093830573', '1429263149059.uc', '674c88acb1de07c33ae9b4100bb69334');
INSERT INTO `uuser` VALUES ('6', '1', '503', '1630153510', '1630153510', '九游玩家602972457', '2015-04-18 14:00:02', '1425093830573', '1429336802222.uc', '21e15ab5b0b5efaf3837164167af0eaa');
INSERT INTO `uuser` VALUES ('7', '1', '16', '1460968533', 'test99009', '', '2015-04-20 16:52:18', '1425093830573', '1429519938126.qihoo', 'b82d72961c6ce2928ead64cdd759918d');
INSERT INTO `uuser` VALUES ('8', '1', '12', '0', '', '', '2015-04-21 10:48:13', '1425093830573', '1429584493936.baidu', 'a70fb4981c8f82db22a6ebd37d8bf423');
INSERT INTO `uuser` VALUES ('9', '1', '12', '644294698', '', '', '2015-04-21 14:05:08', '1425093830573', '1429596308390.baidu', '824e97660e1afc9c8f9a145aa61d59f7');
INSERT INTO `uuser` VALUES ('10', '1', '17', '9516133', '', '', '2015-04-21 20:50:37', '1425093830573', '1429620637444.xiaomi', '675b04978104c1a91735ef61389587b9');
INSERT INTO `uuser` VALUES ('11', '1', '18', '1580331340', '894664603', '', '2015-04-22 15:32:17', '1425093830573', '1429687937425.m4399', 'c14338360058dff4c0650609f942d79a');
INSERT INTO `uuser` VALUES ('12', '1', '19', '71798862', 'MI371798862', '', '2015-04-23 09:42:30', '1465367065220', '1429753350836.oppo', '9a25a93945f06b6404f7325a9c6229b8');
INSERT INTO `uuser` VALUES ('13', '1', '19', '78094833', 'NM78094833', '', '2015-04-23 10:39:35', '1425093830573', '1429756775598.oppo', 'c55a18e0004b5bad46bb7eeb7988f7ca');
INSERT INTO `uuser` VALUES ('14', '1', '20', 'f342eecf444db1f0', 'MI364081467oi', '', '2015-04-24 13:08:11', '1457429429717', '1429852091204.vivo', 'ba068a4caf03a2618229ea6f1cd2f186');
INSERT INTO `uuser` VALUES ('15', '1', '20', 'ca6146091ac58fda', 'TianTian0220946lzf', '', '2015-04-25 10:19:32', '1425093830573', '1429928372968.vivo', 'd9e1c7a379f28ef75b71130a02e7666b');
INSERT INTO `uuser` VALUES ('16', '1', '21', '8714739', 'dev_553b0b9e418bc', '', '2015-04-25 11:34:31', '1425093830573', '1429932871370.mzw', '7ce4e5817f058ff0db4b7b6a33f4ba88');
INSERT INTO `uuser` VALUES ('17', '1', '22', '110734509', '', '', '2015-04-25 14:40:09', '1425093830573', '1429944009342.wdj', '1805717266464386ceabd44ed12b806f');
INSERT INTO `uuser` VALUES ('18', '1', '23', '10041284767', '18202116067', '', '2015-04-27 14:26:32', '1425093830573', '1430115992021.lenovo', '7d62f54f707a4026e25b3dead0bea2e1');
INSERT INTO `uuser` VALUES ('19', '1', '24', '27385150', '7****7244@qq.com', '', '2015-04-27 20:01:59', '1425093830573', '1430136119062.huawei', 'a7b7d464adf34121a76859417bec4493');
INSERT INTO `uuser` VALUES ('20', '1', '25', '3585225', 'chenjie19891109', 'chenjie19891109', '2015-04-28 15:19:49', '1425093830573', '1430205589927.appchina', 'e92002c0f5f19598f9a49b55fd57f3d1');
INSERT INTO `uuser` VALUES ('21', '1', '26', '2391023', 'ydld_0547729022', '', '2015-04-28 20:48:13', '1425093830573', '1430225293775.cloudpoint', '84244201d8e962fa1660918b0c58b41e');
INSERT INTO `uuser` VALUES ('22', '1', '10', '1724519942', '1724519942', '九游玩家537886537', '2015-04-28 21:16:19', '1425093830573', '1430226979251.uc', '4999b685ee4423597145de8b5b406dc9');
INSERT INTO `uuser` VALUES ('23', '1', '27', '20150430135342ibmqnatu5i', '', '', '2015-04-30 14:07:57', '1425093830573', '1430374077861.anzhi', '4ed9bf0768873f9aee64b8b686e642bf');
INSERT INTO `uuser` VALUES ('24', '1', '28', '7473756', 'fhdhdhdh', '', '2015-04-30 15:13:27', '1425093830573', '1430378007315.mumayi', 'c582511173dcc04d9e63f7ce456f5fcd');
INSERT INTO `uuser` VALUES ('25', '1', '30', '2606856', 'J187994162', 'J187994162', '2015-04-30 16:51:47', '1425093830573', '1430383907741.paojiao', 'aa2ce48b7dfa7251ff583349f4d880fd');
INSERT INTO `uuser` VALUES ('26', '1', '29', '0S5I3N2KCJLQD8CI', 'sdfdsf', '', '2015-05-04 11:50:39', '1425093830573', '1430711439348.guopan', 'cd8ea3aa201dcd609f197b1da456d4fe');
INSERT INTO `uuser` VALUES ('27', '1', '31', '16409735', '', '', '2015-05-04 15:25:18', '1425093830573', '1430724318289.meizu', 'e60a3c305791b1e2e3e94c7da6df650c');
INSERT INTO `uuser` VALUES ('28', '1', '33', '291628d0790721ca', '', '', '2015-05-04 21:01:11', '1425093830573', '1430744471243.ouwan', 'a768d2b637b7afa2d8ddae30d240623a');
INSERT INTO `uuser` VALUES ('29', '1', '34', 'E59EDE98622248A3B8B6BBE187656B5C', 'Amigo_44104', '', '2015-05-06 09:48:32', '1472723794514', '1430876912124.am', '366d0e596f293b36a38c5fb5dfe45cc4');
INSERT INTO `uuser` VALUES ('30', '1', '32', '29682286', 'CCNK1428383254363', 'CCNK1428383254363', '2015-05-06 11:09:57', '1461155307264', '1430881797104.coolpad', '6ade3af1d15180db02a843aee3f47669');
INSERT INTO `uuser` VALUES ('31', '1', '10', '1324439506', '1324439506', '九游玩家_ib0i44ofb2', '2015-06-17 17:18:42', '1425093830573', '1434532722195.uc', '03a60052c404ff5ac65d77fb1cb6dd6b');
INSERT INTO `uuser` VALUES ('32', '1', '12', '2003198165', '', '', '2015-06-17 17:24:55', '1425093830573', '1434533095191.baidu', 'd50087b987e7c52f44ba7a33ebbd0012');
INSERT INTO `uuser` VALUES ('33', '1', '12', '1292623589', '', '', '2015-06-17 17:25:53', '1467875962328', '1434533153312.baidu', '664432d4ae4a57e2e92bc78210714a90');
INSERT INTO `uuser` VALUES ('34', '1', '24', '900086000023101422', '182****6067', '', '2015-06-18 11:08:20', '1470879892973', '1434596900138.huawei', 'baba898f3a65af7df7c517a5d3d5498d');
INSERT INTO `uuser` VALUES ('35', '1', '30', '3168457', 'jieziqqqq', 'jieziqqqq', '2015-12-02 11:19:39', '1425093830573', '1449026379630.paojiao', '6b5a7d86dd24ee04a84c0162e5e192ee');
INSERT INTO `uuser` VALUES ('36', '1', '36', '28847428', 'jL6gffgQfbgUY_', '', '2015-12-03 19:04:52', '1425093830573', '1449140692768.gfan', '9f18b682f87a889d085c2b444ec084f1');
INSERT INTO `uuser` VALUES ('37', '1', '37', '928707', '2848004717@qq.com', '', '2015-12-03 19:27:42', '1425093830573', '1449142062491.anfeng', '6467bb15f5ddc98fffdc45a8ef0064b4');
INSERT INTO `uuser` VALUES ('38', '1', '20', 'e05d4b83e044f8ff', '18202116067', '', '2015-12-04 16:22:01', '1450865480609', '1449217321152.vivo', '7783a38cbdf2008cd09c814dd2919e75');
INSERT INTO `uuser` VALUES ('39', '1', '38', '5463551', '', '', '2015-12-08 16:29:38', '1453953320060', '1449563378170.youku', '9f09a61e1faad358e8be0bb7c736e12e');
INSERT INTO `uuser` VALUES ('40', '1', '40', '4713462', 'lewan5785793', '', '2015-12-08 19:23:13', '1449573829375', '1449573793250.lewan', '1538c3e1d0d3ec8bf663464544e7eb9a');
INSERT INTO `uuser` VALUES ('41', '1', '41', '61669', '+86-1820****067', '', '2015-12-08 20:08:07', '1449576511076', '1449576487770.doudou', '01cc436eabb0ffca63ff4a5e7f88ea77');
INSERT INTO `uuser` VALUES ('42', '1', '44', 'd231494327', '', '', '2015-12-21 20:16:31', '1450700237928', '1450700191688.liebao', '9b69c7d784fe9a82f889c7640321a8d3');
INSERT INTO `uuser` VALUES ('43', '1', '45', '151947985', 'xiaohei_419', '', '2015-12-21 21:03:42', '1450703022027', '1450703022027.letv', 'b47a4226e5d0e011b1c62ffeec50e3e7');
INSERT INTO `uuser` VALUES ('44', '1', '46', '269848072', '', '', '2015-12-22 11:32:20', '1450755140256', '1450755140256.msd', 'f5f33f2928d2cee72f9fb18bcfac8384');
INSERT INTO `uuser` VALUES ('45', '1', '48', '361394', 'cy15122219575454', '', '2015-12-22 19:57:59', '1450785479528', '1450785479528.chuyou', 'ec9dfb94005a3ae1ed2dff84ce1d27f0');
INSERT INTO `uuser` VALUES ('46', '1', '50', '43505149', 'yangzz3', '', '2015-12-23 11:23:56', '1453974561930', '1450841036321.pptv', '4ef230e6f3eab0f38cdbdb670935c12c');
INSERT INTO `uuser` VALUES ('47', '1', '16', '2603271986', 'GP151223120537', '', '2015-12-23 12:06:08', '1450853657276', '1450843568438.qihoo', 'a9e309afe6185b4cfb7c010260a65d96');
INSERT INTO `uuser` VALUES ('48', '1', '35', 'F7B0CCE4D37010FD7D7D78CC21C61A54', 'qq-F7B0CCE4D37010FD7D7D78CC21C61A54', '', '2016-01-06 19:37:10', '1452081159017', '1452080230704.txmsdk', '81ec8e3be7dbbb5ba5053567349561dc');
INSERT INTO `uuser` VALUES ('49', '1', '22', '125900067', '', '', '2016-01-06 20:10:10', '1468287171120', '1452082210043.wdj', '9cfb479d2f9b75d0531154f0134c6912');
INSERT INTO `uuser` VALUES ('50', '1', '29', '855QMME9F7FWI43T', '18202116067', '', '2016-02-20 10:43:24', '1467779793785', '1455936204042.guopan', '76aa84f64f48014e0f3be8fc803a86c3');
INSERT INTO `uuser` VALUES ('51', '1', '16', '2639783407', 'GQ160220111021', '', '2016-02-20 11:08:52', '1455937732110', '1455937732110.qihoo', 'be11ae54c5578dd72591da7063fd1709');
INSERT INTO `uuser` VALUES ('52', '1', '17', '118770409', '', '', '2016-02-24 17:39:17', '1464774880626', '1456306757555.xiaomi', '13e0c7d7e1d9a04682d1e50174f2cb0e');
INSERT INTO `uuser` VALUES ('53', '1', '18', '1940709767', '1193356121', '', '2016-03-03 11:47:29', '1456976849166', '1456976849166.m4399', 'dfe40cf6020b0d50b7dff655ceae4531');
INSERT INTO `uuser` VALUES ('54', '1', '11', '181422924', '', '', '2016-03-29 16:35:27', '1459240527423', '1459240527423.dl', '10101dedb13ecee95aa54e422525d5ee');
INSERT INTO `uuser` VALUES ('55', '1', '25', '2673447', 'w5447150', 'liner115521', '2016-03-29 20:12:35', '1464749451208', '1459253555669.appchina', 'adca0f728bc18c6a9aa63457efbf3e5c');
INSERT INTO `uuser` VALUES ('56', '1', '28', '9694527', 'hahha00o', '', '2016-04-06 12:40:05', '1459917622377', '1459917605685.mumayi', 'd219c49deecd22f7e46ea9d1aa8a00ee');
INSERT INTO `uuser` VALUES ('57', '1', '1701', 'demo0000001', 'test', 'demo_user', '2016-04-08 11:23:45', '1460094683723', '1460085825988.demo', '1b0fbbf3f01fc6900c53a8d9eab40e04');
INSERT INTO `uuser` VALUES ('58', '1', '28', '9730228', '小蚂蚁mm134509', '', '2016-04-19 20:40:22', '1461069622346', '1461069622345.mumayi', 'a69358d43ad91e0172856e6d134e6a66');
INSERT INTO `uuser` VALUES ('59', '1', '28', '9730300', '小蚂蚁mm134541', '', '2016-04-19 20:46:40', '1461070000406', '1461070000406.mumayi', 'd655b7082aca1ca5bb5d7fcb5cbc1f2e');
INSERT INTO `uuser` VALUES ('60', '1', '49', '1220898226', '', '', '2016-04-20 14:56:52', '1461135412792', '1461135412792.pps', 'a2bd8a457674036d08fcc77355988733');
INSERT INTO `uuser` VALUES ('61', '1', '36', '30633010', '2584c38a6056c39c5ac36186b7179f88', '', '2016-04-21 20:35:49', '1461288773257', '1461242149314.gfan', 'fd14fcbbd0a05af178a7a14ad6e2274a');
INSERT INTO `uuser` VALUES ('62', '1', '51', 'E4D2A4C5BF53B7F8356E395F7638D34A', 'qq-E4D2A4C5BF53B7F8356E395F7638D34A', '', '2016-05-09 10:06:42', '1462782074938', '1462759602008.ysdk', '086c2f4c4c4520b600934918f8cc035f');
INSERT INTO `uuser` VALUES ('63', '1', '20', '5ff0ab3b2da955a5', '13564120165', '', '2016-05-09 11:07:59', '1466239645654', '1462763279387.vivo', '831a7ea6764d03fcd97d874493d2ea88');
INSERT INTO `uuser` VALUES ('64', '1', '52', '65569013', '', '', '2016-05-10 15:01:20', '1462863680503', '1462863680503.sougou', '10e15e68f65e5c5658ba2ccfdc494b9f');
INSERT INTO `uuser` VALUES ('65', '1', '30', '8153352', 'fghhhhhjjjjj', 'fghhhhhjjjjj', '2016-05-12 20:41:07', '1463057252043', '1463056867091.paojiao', '30b68c54b9da6b0359ee71009b89609e');
INSERT INTO `uuser` VALUES ('66', '1', '51', '038F83DFA4FC9822AB67BD407B546F67', 'qq-038F83DFA4FC9822AB67BD407B546F67', '', '2016-05-13 15:47:01', '1464577870553', '1463125621042.ysdk', 'ecef1a0769318534b3e3e53bddc59104');
INSERT INTO `uuser` VALUES ('67', '1', '51', '1FEA044F75E91CE8D8F45F509FC1B2A9', 'qq-1FEA044F75E91CE8D8F45F509FC1B2A9', '', '2016-05-14 15:32:48', '1463211275535', '1463211168144.ysdk', '0e080e4a6e81fb2ac943080e66b9df5e');
INSERT INTO `uuser` VALUES ('68', '1', '51', 'oR1t3s1KWGh4n7frRLTrfO3Fl3yo', 'wx-oR1t3s1KWGh4n7frRLTrfO3Fl3yo', '', '2016-05-16 16:15:12', '1463390938903', '1463386512309.ysdk', '6672ce03df18070f17e667127fc652ea');
INSERT INTO `uuser` VALUES ('69', '1', '51', '059CF9784852F5B55039533F416B4E38', 'qq-059CF9784852F5B55039533F416B4E38', '', '2016-05-16 20:31:21', '1463401881525', '1463401881525.ysdk', 'ebd4d57740c2edefaca226d60606129a');
INSERT INTO `uuser` VALUES ('70', '1', '51', 'oRzrIvvcMrP8atSgvzXkkbAeLXv8', 'wx-oRzrIvvcMrP8atSgvzXkkbAeLXv8', '', '2016-05-17 13:37:53', '1463463473366', '1463463473366.ysdk', '732ed229380d24dccf6bd36a459d8bd8');
INSERT INTO `uuser` VALUES ('71', '1', '37', '1191337', 'afM2RmOGFhNmJj', '', '2016-05-24 19:37:51', '1464089871998', '1464089871998.anfeng', '253f638aa468a5484cf7c024f3e22eec');
INSERT INTO `uuser` VALUES ('72', '1', '11', '201428142', '', '', '2016-05-25 15:28:58', '1464167297208', '1464161338877.dl', '5dfea8b5ae2575c5a335293636a4a37a');
INSERT INTO `uuser` VALUES ('73', '1', '11', '207424849', 'docsaibxd1', 'docsaibxd1', '2016-05-25 17:14:58', '1464167698155', '1464167698155.dl', '42b106be67e56571e0476a9792a6a1b3');
INSERT INTO `uuser` VALUES ('74', '1', '53', 'QM_GAME1602240954607017_0000339838', 'QM_GAME1602240954607017_0000339838', '娓稿', '2016-05-25 17:47:16', '1464170716071', '1464169636727.qm', 'e6fbfb7cef4716852c94424cd69f514b');
INSERT INTO `uuser` VALUES ('75', '1', '51', 'B650F2E88CB94BBD11ACABF330872A76', 'qq-B650F2E88CB94BBD11ACABF330872A76', '', '2016-05-30 11:52:26', '1464583511597', '1464580346818.ysdk', 'f7112209fe39f21965a5eb69e4417f78');
INSERT INTO `uuser` VALUES ('76', '1', '25', '6112775', 'YYH6112828769', 'YYH6112828769', '2016-06-01 08:59:34', '1464742774572', '1464742774572.appchina', 'c8d98db2a690c9c17fb196a407da5e0e');
INSERT INTO `uuser` VALUES ('77', '1', '25', '6113019', 'YYH6113072458', 'YYH6113072458', '2016-06-01 10:48:42', '1464749322589', '1464749322589.appchina', '1d553388174468a8a0f9a01423f5993b');
INSERT INTO `uuser` VALUES ('78', '1', '44', 'lb6562337218', '', '', '2016-06-08 15:02:16', '1465369336234', '1465369336234.liebao', '60854dbba3f244be35354d8e870cc054');
INSERT INTO `uuser` VALUES ('79', '1', '33', 'b8c4c9bd4a761820', '', '', '2016-06-14 13:09:17', '1465880957463', '1465880957463.ouwan', 'ef39ae8f8b90231f8e09998e3a5eceed');
INSERT INTO `uuser` VALUES ('80', '1', '19', '117862977', 'U37364224', '', '2016-06-15 12:49:10', '1466653227834', '1465966150308.oppo', 'a5393e5f8d556378f4b2103fd3cc4f3c');
INSERT INTO `uuser` VALUES ('81', '1', '24', '910086000007185902', '182****6067\\马甲1', '', '2016-06-17 17:55:09', '1466157383614', '1466157309433.huawei', 'aea90ffb02521a72c7cf03fc2bba0f13');
INSERT INTO `uuser` VALUES ('82', '1', '1003', '30e4ece6a03af3fe2512256bc1b1f80e', '', 'zackr001', '2016-07-11 16:42:16', '1468226536202', '1468226536202.pp', '3f24a88ba1186144375b5c6519114bd0');
INSERT INTO `uuser` VALUES ('83', '1', '16', '2524854450', 'mfcs123456', '', '2016-07-12 09:30:31', '1470301685431', '1468287031678.qihoo', '85b83a8a588f13d137ffadeafef0777f');
INSERT INTO `uuser` VALUES ('84', '1', '1000', '5129262', 'wolegeyun', '', '2016-07-12 11:14:26', '1468300695117', '1468293266292.itools', '6e11f44674ce4e084a6d86e0972165d7');
INSERT INTO `uuser` VALUES ('85', '1', '85', '14726954441', '', '', '2016-09-01 11:42:35', '1472701355404', '1472701355404.yile', 'ddcddec16168214e9a6fa298fb115314');
