select f.film_id, f.title, f.description, f.release_date, f.duration, r.RATING_NAME
FROM FILMS AS f
    LEFT JOIN FILM_GENRE FG on f.FILM_ID = FG.FILM_ID
         LEFT JOIN RATINGS R on f.RATING_ID = R.RATING_ID
GROUP BY f.FILM_ID;

SELECT *
FROM GENRES
ORDER BY GENRE_ID