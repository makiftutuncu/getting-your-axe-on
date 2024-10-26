CREATE TABLE games
(
    id          BIGSERIAL PRIMARY KEY,
    boards      TEXT                     NOT NULL,
    ships       TEXT                     NOT NULL,
    status      CHAR(8)                  NOT NULL,
    turn        CHAR(1)                  NOT NULL,
    winner      CHAR(1),
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    started_at  TIMESTAMP WITH TIME ZONE,
    finished_at TIMESTAMP WITH TIME ZONE
);
