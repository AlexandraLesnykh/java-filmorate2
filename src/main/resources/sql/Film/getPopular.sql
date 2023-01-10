SELECT f.film_id, f.TITLE, f.description, f.release_date, f.duration, f.MPA_ID,
    m.MPA_NAME, ( SELECT COUNT(FILM_LIKES.USER_ID)
                  FROM FILM_LIKES
                  GROUP BY f.FILM_ID) as count
                        FROM films AS f
                        LEFT JOIN MPA AS m ON f.MPA_ID = m.MPA_ID
                        LEFT OUTER JOIN FILM_LIKES FL on f.FILM_ID = FL.FILM_ID
                        ORDER BY count DESC
                        LIMIT ?