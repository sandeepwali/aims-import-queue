package com.solum.lidl.metrics.handler;

import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.solum.lidl.respository.stats.StatisticsRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DailyPurgeScheduler {
	/**
	 * 
	 * @author sandeep
	 *
	 */
	
    private final String dailyPurgeSchedule;
    private final StatisticsRepository statisticsRepository;
    private final Map<String, LocalTime> scheduleMap = new HashMap<>();

    @Autowired
    public DailyPurgeScheduler(@Value("${aims.import.queue.statistics.dailypurge}") String dailyPurgeSchedule,
                                StatisticsRepository statisticsRepository) {
        this.dailyPurgeSchedule = dailyPurgeSchedule;
        this.statisticsRepository = statisticsRepository;
        initScheduleMap();
    }

    private void initScheduleMap() {
        String[] schedules = dailyPurgeSchedule.split(",");
        for (String schedule : schedules) {
            String[] parts = schedule.split("=");
            String country = parts[0];
            String time = parts[1];
            LocalTime localTime = LocalTime.parse(time);
            scheduleMap.put(country, localTime);
        }
    }

    @Scheduled(cron = "${daily.purge.scheduler.cron.expression}", zone = "UTC") // Run every hour at minute 0 // Run every hour at minute 0
    public void executeDailyPurge() {
        LocalTime currentTime = LocalTime.now(ZoneOffset.UTC);
        int currentHour = currentTime.getHour();
        
        for (Map.Entry<String, LocalTime> entry : scheduleMap.entrySet()) {
            String country = entry.getKey();
            LocalTime scheduledTime = entry.getValue();
            int scheduledHour = scheduledTime.getHour();
            
            if (currentHour == scheduledHour) {
                try {
                    log.info("Deleting records for {} at {} (UTC)", country, scheduledTime);
                    statisticsRepository.deleteBySenderCountry(country);
                } catch (Exception e) {
                    log.error("Error occurred while deleting records for {}: {}", country, e.getMessage(), e);
                }
            }
        }
    }
}