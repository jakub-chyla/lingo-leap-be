package com.lingo_leap.repository;

import com.lingo_leap.model.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {

    @Query("SELECT h FROM History h WHERE h.userId = :userId AND h.isCorrect = false")
    List<History> findIncorrectByUserId(@Param("userId") Long userId);

    @Query("SELECT h FROM History h WHERE h.userId = :userId AND h.wordAskedId = :wordId AND h.created >= CURRENT_DATE")
    Optional<History> findByUserIdAndWordId(@Param("userId") Long userId, @Param("wordId") Long wordId);

    @Query(value = "SELECT h.word_asked_id FROM histories h WHERE h.user_id = :userId AND" +
            " h.is_correct = false AND h.created >= CURRENT_DATE ORDER BY h.created ASC LIMIT :limit"
            , nativeQuery = true)
    List<Long> findByUserIdTodaysInCorrect(
            @Param("userId") Long userId,
            @Param("limit") Integer limit
    );
    @Query(value = "SELECT h.* FROM histories h WHERE h.user_id = :userId ORDER BY h.created DESC"
            , nativeQuery = true)
    List<History> findByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT h.* FROM histories h WHERE h.user_id = :userId AND h.created < CURRENT_DATE ORDER BY h.created DESC"
            , nativeQuery = true)
    List<History> findByUserIdOlderThanToday(@Param("userId") Long userId);

    @Query(value = "SELECT COUNT(*) FROM histories h WHERE h.user_id = :userId AND h.is_correct = false AND h.created >= CURRENT_DATE", nativeQuery = true)
    Integer findCountOfIncorrect(@Param("userId") Long userId);

    @Query(value = """
        SELECT h.word_asked_id
        FROM histories h
        WHERE h.user_id = :userId
          AND h.created >= NOW() - INTERVAL '30 days'
        ORDER BY h.created DESC
        """, nativeQuery = true)
    List<Long> findLastMonthByUserId(@Param("userId") Long userId);

    @Query(value = """
              SELECT h.* FROM histories h WHERE h.user_id = :userId 
              AND h.created >= CURRENT_DATE ORDER BY h.created DESC
              """
            , nativeQuery = true)
    List<History> findTodayHistoryByUser(@Param("userId") Long userId);

    @Query(value = """
              SELECT h.* FROM histories h WHERE h.user_id = :userId 
              AND is_correct = false;
              """
            , nativeQuery = true)
    List<History> findAllWrongHistoryByUser(@Param("userId") Long userId);

    @Query(value = """
              SELECT h.* FROM histories h WHERE h.user_id = :userId 
              ORDER BY word_asked_id ASC;
              """
            , nativeQuery = true)
    List<History> findAllHistoryByUser(@Param("userId") Long userId);


    @Query(value = """
              SELECT h.* FROM histories h WHERE h.user_id = :userId 
              AND h.created < CURRENT_DATE ORDER BY word_asked_id ASC LIMIT 9000;
              """
            , nativeQuery = true)
    List<History> findAllExceptTodayHistoryByUser(@Param("userId") Long userId);

    @Query(value = """
      SELECT h.* 
      FROM histories h 
      WHERE h.user_id = :userId 
        AND h.created >= CURRENT_DATE
        AND h.created < CURRENT_DATE + INTERVAL '1 DAY'
        AND h.is_correct = true 
      ORDER BY word_asked_id ASC;
      """, nativeQuery = true)
    List<History> findAllOnlyCorrectTodayByUser(@Param("userId") Long userId);

    @Query(value = "SELECT h.word_asked_id FROM histories h WHERE h.user_id = :userId AND" +
            " h.is_correct = false AND h.created >= CURRENT_DATE ORDER BY h.created ASC LIMIT :limit"
            , nativeQuery = true)
    List<Long> findByUserIdAndWordIdLastInCorrect(
            @Param("userId") Long userId,
            @Param("limit") Integer limit
    );

    @Query(value = """
            SELECT DISTINCT ON (h.word_asked_id)
                   h.word_asked_id
            FROM histories h
            WHERE h.user_id = :userId
              AND h.is_correct = false
            ORDER BY h.word_asked_id, h.created ASC
            """,
            nativeQuery = true
    )
    List<Long> findLatestDistinctWordAskedIds(@Param("userId") Long userId);

}
