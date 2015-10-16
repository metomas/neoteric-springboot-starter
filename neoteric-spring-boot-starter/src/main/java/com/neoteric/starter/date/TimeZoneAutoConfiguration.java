package com.neoteric.starter.date;

import com.neoteric.starter.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.time.ZoneId;
import java.util.TimeZone;

@Configuration
@ConditionalOnClass(ZoneId.class)
public class TimeZoneAutoConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(TimeZoneAutoConfiguration.class);

    @PostConstruct
    public static void init() {
        LOG.debug("{}Setting default timezone to UTC", Constants.LOG_PREFIX);
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of(Constants.UTC)));
    }
}