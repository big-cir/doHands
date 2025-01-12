package com.be.dohands.tf.repository;

import com.be.dohands.tf.TfExp;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TfExpRepository extends JpaRepository<TfExp, Long> {

    List<TfExp> findTfExpsByEmployeeNumber(String employeeNumber);
    Optional<TfExp> findBySheetRow(Integer sheetRow);
}
