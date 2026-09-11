package com.lingo_leap.repository;

import com.lingo_leap.dto.AttachmentDTO;
import com.lingo_leap.enums.Language;
import com.lingo_leap.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    @Modifying
    @Query("DELETE FROM Attachment a WHERE a.wordId = :wordId")
    void deleteAttachmentsByWordId(@Param("wordId") Long wordId);

    @Query("SELECT a FROM Attachment a WHERE a.wordId = :wordId")
    List<Attachment> findByWordId(@Param("wordId") Long wordId);

    @Query("SELECT a FROM Attachment a WHERE a.wordId = :wordId AND a.language = :language")
    Optional<Attachment> findByWordIdAndLanguage(@Param("wordId") Long wordId,
                                                 @Param("language") Language language);

    @Query("SELECT new com.lingo_leap.dto.AttachmentDTO(a.id, a.fileName, a.wordId, a.language) FROM Attachment a")
    List<AttachmentDTO> findAllWithOutData();

    @Query("SELECT new com.lingo_leap.dto.AttachmentDTO(a.id, a.fileName, a.wordId, a.language) FROM Attachment a WHERE a.wordId = :wordId AND a.language = :language")
    AttachmentDTO findByWordIdAndLanguageWithOutData(@Param("wordId") Long wordId, @Param("language") Language language);;

}
