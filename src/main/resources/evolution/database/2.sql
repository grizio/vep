CREATE TABLE users (
  id               VARCHAR(255) NOT NULL,
  email            VARCHAR(255) NOT NULL,
  password         VARCHAR(255) NOT NULL,
  role             VARCHAR(255) NOT NULL,
  authentications  TEXT,
  activationKey    VARCHAR(255),
  resetPasswordKey VARCHAR(255),
  PRIMARY KEY (id)
)