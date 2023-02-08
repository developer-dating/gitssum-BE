package com.backend.gitssum.entity;


import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;

public class MessageTimeConversion {
    public static String timeConversion(LocalDateTime modifiedAt){
        //현재시간
        LocalDateTime currentTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        Long timeDiff = Duration.between(modifiedAt, currentTime).getSeconds();
        String resultConversion = "";

        if((timeDiff / 60 / 60 / 24 / 7) > 0){//주
            resultConversion = modifiedAt.getMonth()+"월 "+modifiedAt.getDayOfMonth()+"일";
        }
        else if ((timeDiff / 60 / 60 / 24) > 0) { // 일
            resultConversion = timeDiff / 60 / 60 / 24 + "일 전";
        } else{
            if(modifiedAt.get(ChronoField.AMPM_OF_DAY) == 0) {
                resultConversion = "오전 "+modifiedAt.getHour()+":"+String.format("%02d", modifiedAt.getMinute());
            } else {
                //12 시간 타입으로 포맷팅 후 전달
                String modifiedAtHour = modifiedAt.format(DateTimeFormatter.ofPattern("hh"));
                resultConversion = "오후 "+modifiedAtHour+":"+String.format("%02d", modifiedAt.getMinute());
            }
        }

        return resultConversion;

    }
}
