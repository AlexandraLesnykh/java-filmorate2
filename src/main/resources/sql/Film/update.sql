UPDATE films SET TITLE = ?, description = ?, release_date = ?, duration = ?, MPA_ID = ?
    WHERE film_id = ?;
DELETE FROM FILM_GENRE WHERE FILM_ID = ?;
UPDATE FILM_GENRE SET FILM_ID =?, GENRE_ID = ?;





