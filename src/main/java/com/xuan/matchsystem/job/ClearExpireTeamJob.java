package com.xuan.matchsystem.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

//@Component
@Slf4j
public class ClearExpireTeamJob {

    @Scheduled(cron = "")
    public void clearExpireTeam() {
        // todo 根据过期时间清除队伍 维护队伍关系表
    }

}
