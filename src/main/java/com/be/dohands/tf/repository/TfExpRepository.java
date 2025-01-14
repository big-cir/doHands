package com.be.dohands.tf.repository;

import com.be.dohands.tf.TfExp;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TfExpRepository extends JpaRepository<TfExp, Long> {

    List<TfExp> findTfExpsByEmployeeNumber(String employeeNumber);
    Optional<TfExp> findBySheetRow(Integer sheetRow);

    @Query(value = "select cast(coalesce(count(tf.tfExpId),0) as int) "
        + "from TfExp tf left join Member m on tf.employeeNumber = m.employeeNumber "
        + "where m.userId = :userId")
    Integer countIdsByUserId(@Param("userId") Long userId);
}
