package com.solum.lidl.metrics.handler;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.solum.lidl.entity.CounterState;
import com.solum.lidl.entity.stats.ImportStatistics;
import com.solum.lidl.entity.stats.StatsPK;
import com.solum.lidl.respository.stats.StatisticsRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MetricsHandler{
	@Autowired
	private StatisticsRepository statisticsRepository;

	public  void updateMetrics(Map<StatsPK, CounterState> dataMap) {

		if(dataMap == null || dataMap.isEmpty()) {
			return;
		}

		dataMap.entrySet().stream().forEach(a->{
			int retryCounter = 1;
			counter: while(isRetry(retryCounter)) {
				if(loadAndSave(a)) {
					break counter;
				}else {
					retryCounter++; 
				}
			}
			if(!isRetry(retryCounter)) {
				log.error("Error in updating metrics, {} ", a);
			}
		});
	}

	private boolean isRetry(int retryCounter) {
		return retryCounter <= 2;
	}

	private synchronized boolean loadAndSave(Entry<StatsPK, CounterState> a) {
		try {
			final StatsPK key = a.getKey();
			CounterState val = a.getValue();
			Optional<ImportStatistics> data = statisticsRepository.findById(key);
			if(data.isPresent()) {
				ImportStatistics currentValFromDB = data.get();
				statisticsRepository.saveAndFlush(currentValFromDB.update(val));
			}else {
				ImportStatistics imp = new ImportStatistics();
				imp.init();
				imp.assign(key).update(val);
				statisticsRepository.saveAndFlush(imp);				
			}
			return true;
		} catch (Exception e) {
			log.error("Error in updating metrics, {} ", e);
			return false;
		}
	}

	/*
	 * @Override public void run(ApplicationArguments args) throws Exception {
	 * Map<StatsPK, CounterState> dataMap = new HashMap<>(); final StatsPK key = new
	 * StatsPK(); key.setDate(LocalDate.now()); key.setSenderSystemId("26");
	 * key.setSenderSystemName("WAWI"); key.setSenderCompany("LIDL");
	 * key.setSenderCountry("DK"); key.setSenderWarehouse("0006");
	 * key.setStoreCode("DE001001"); CounterState val = dataMap.get(key); if(val ==
	 * null) //if key already not exists { val = new CounterState();
	 * dataMap.put(key,val); } val.process("12345"); updateMetrics(dataMap);
	 * val.process("12346"); updateMetrics(dataMap);
	 * 
	 * }
	 */

}
