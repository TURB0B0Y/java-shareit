SET MODE PostgreSQL;

CREATE TABLE IF NOT EXISTS USERS (
    USER_ID     BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    NAME        VARCHAR(255)                            NOT NULL,
    EMAIL       VARCHAR(512)                            NOT NULL,
    CONSTRAINT PK_USERS PRIMARY KEY (USER_ID),
    CONSTRAINT UQ_USERS_EMAIL UNIQUE (EMAIL)
);

CREATE TABLE IF NOT EXISTS REQUESTS (
    REQUEST_ID      BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    DESCRIPTION     VARCHAR(512)                            NOT NULL,
    REQUESTER_ID    BIGINT                                  NOT NULL,
    CREATED         TIMESTAMP                               NOT NULL,
    CONSTRAINT PK_REQUEST PRIMARY KEY (REQUEST_ID),
    CONSTRAINT REQUESTS_REQUESTER_FK FOREIGN KEY (REQUESTER_ID) REFERENCES USERS(USER_ID)
    ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS ITEMS (
    ITEM_ID         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    NAME            VARCHAR(255)                            NOT NULL,
    DESCRIPTION     VARCHAR(512)                            NOT NULL,
    IS_AVAILABLE    BOOLEAN                                 NOT NULL,
    OWNER_ID        BIGINT                                  NOT NULL,
    REQUEST_ID        BIGINT                                NOT NULL,
    CONSTRAINT PK_ITEMS PRIMARY KEY (ITEM_ID),
    CONSTRAINT UQ_ITEMS_OWNER UNIQUE (OWNER_ID, NAME),
    CONSTRAINT ITEMS_OWNER_FK FOREIGN KEY (OWNER_ID) REFERENCES USERS(USER_ID)
    ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT ITEMS_REQUEST_FK FOREIGN KEY (REQUEST_ID) REFERENCES REQUESTS(REQUEST_ID)
    ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS BOOKINGS (
    BOOKING_ID  BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    START_DATE  TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    END_DATE    TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    ITEM_ID     BIGINT                                  NOT NULL,
    BOOKER_ID   BIGINT                                  NOT NULL,
    STATUS      VARCHAR(10)                             NOT NULL,
    CONSTRAINT PK_BOOKINGS PRIMARY KEY (BOOKING_ID),
    CONSTRAINT BOOKINGS_ITEM_FK FOREIGN KEY (ITEM_ID) REFERENCES ITEMS(ITEM_ID)
    ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT BOOKINGS_BOOKER_FK FOREIGN KEY (BOOKER_ID) REFERENCES USERS(USER_ID)
    ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS COMMENTS (
    COMMENT_ID  BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    TEXT        VARCHAR(2048)                           NOT NULL,
    ITEM_ID     BIGINT                                  NOT NULL,
    AUTHOR_ID   BIGINT                                  NOT NULL,
    CREATED     TIMESTAMP                               NOT NULL,
    CONSTRAINT PK_COMMENTS PRIMARY KEY (COMMENT_ID),
    CONSTRAINT COMMENTS_ITEM_FK FOREIGN KEY (ITEM_ID) REFERENCES ITEMS(ITEM_ID)
    ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT COMMENTS_AUTHOR_FK FOREIGN KEY (AUTHOR_ID) REFERENCES USERS(USER_ID)
    ON DELETE CASCADE ON UPDATE CASCADE
);