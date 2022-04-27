package com.hraczynski.trains.information.timetable;

import com.hraczynski.trains.stoptime.StopTimeExtendedDto;
import org.springframework.data.domain.Page;

public interface TimetableService {

    Page<StopTimeExtendedDto> getTimetableForCity(Long id, String localDateTime, String localDateTimeEnd, int pageNum, int resultsNum);
}
