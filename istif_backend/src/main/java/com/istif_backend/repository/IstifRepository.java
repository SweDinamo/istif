package com.istif_backend.repository;

import com.istif_backend.model.Istif;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface IstifRepository extends CrudRepository<Istif,Long> {
    List<Istif> findAll();
    List<Istif> findAllByOrderByIdDesc();

    List<Istif> findByUserIdOrderByIdDesc(Long userId);

    List<Istif> findByTitleContainingIgnoreCase(String query);

    List<Istif> findByLabelsContainingIgnoreCase(String query);

    List<Istif> findByShareFlagOrderByCreatedAtDesc(int shareFlag);

    List<Istif> findByUserIdAndShareFlagOrderByIdDesc(Long userId, int shareFlag);

    List<Istif> findByCreatedAtBetween(Date startDate, Date endDate);

    List<Istif> findByRelevantDateBetween(Date formattedStartDate, Date formattedEndDate);

    List<Istif> findByCreatedAt(Date formattedDate);

    List<Istif> findByRelevantDate(Date formattedDate);
}
