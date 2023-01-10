select f.film_id, f.title, f.description, f.release_date, f.duration, f.RATING_ID, r.RATING_NAME
FROM FILMS AS f
         LEFT OUTER JOIN RATINGS R on f.RATING_ID = R.RATING_ID
WHERE f.FILM_ID = ?;



