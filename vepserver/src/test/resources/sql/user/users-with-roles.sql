-- We consider application salt is "somethingHere" (as in sample)
-- The password was defined with StringUtils.crypt with given password and salt.

INSERT INTO users (uid, email, password, salt, firstname, lastname, city, keylogin, expiration, roles)
VALUES
  (1, 'aui@aui.com', '4057EE9B2AC28E5DCD16179DCC66DBBB0C030AB6' /*'abc'*/, 'def', 'ghi', 'jkl', NULL, NULL,
   NULL, ''),
  (2, 'abc@def.com', '985B14B303BA8C5351F4C4B1C744A2A612E70EA0' /*'abc'*/, 'zyx', 'firstname', 'lastname',
   NULL, 'abcd', now(), 'user'),
  (3, 'admin@admin.com', '5861010B47D02A256FA12B699A2A778020E75784' /*'admin'*/, 'admin', 'admin', 'admin',
   NULL, 'admin', now(), 'user,user-manager,page-manager,theater-manager,company-manager')