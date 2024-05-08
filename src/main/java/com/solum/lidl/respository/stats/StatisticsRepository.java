package com.solum.lidl.respository.stats;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.solum.lidl.entity.stats.ImportStatistics;
import com.solum.lidl.entity.stats.StatsPK;
 

@Repository
public interface StatisticsRepository extends JpaRepository<ImportStatistics, StatsPK> {

    @Transactional
    void deleteBySenderCountry(String senderCountry);
	
}
