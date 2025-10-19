package com.example.my_be.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.my_be.model.summary;
import com.example.my_be.model.user;

@Repository
public interface summaryrepository extends JpaRepository<summary, String> {
    List<summary> findByCreatedBy(user createdBy);

    List<summary> findByStatus(String status);

    List<summary> findByGrade(String grade);
    List<summary> findTop10ByOrderByReadCountDesc(); // Fetch top 10 summaries ordered by readCount
    List<summary> findByTitleContainingOrContentContainingOrStatusContainingOrMethodContainingOrGradeContaining(
        String title, String content, String status, String method, String grade
    );

    List<summary> findByTitleContainingIgnoreCase(@Param("title") String title);

    @Query("SELECT s FROM summary s WHERE (LOWER(s.title) LIKE %:searchTerm%) AND (LOWER(s.grade) = :grade OR :grade = '')")
    List<summary> findByTitleContainingIgnoreCaseAndGrade(
        @Param("searchTerm") String searchTerm, @Param("grade") String grade);



    List<summary> findByMethod(String method); // Filter summaries by method (PHOBERT or T5_DIEN_GIAI)

    @Query("SELECT s FROM summary s LEFT JOIN FETCH s.created_by WHERE s.summary_id = :id")
Optional<summary> getSummaryByIdwoStatus(@Param("id") String id);
}
