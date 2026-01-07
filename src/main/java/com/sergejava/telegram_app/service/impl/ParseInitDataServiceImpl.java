package com.sergejava.telegram_app.service.impl;

import com.sergejava.telegram_app.dto.InitDataUser;
import com.sergejava.telegram_app.service.ParseInitDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class ParseInitDataServiceImpl implements ParseInitDataService {

    private final ObjectMapper objectMapper;

    @Override
    public InitDataUser userFromInitData(String decodedInitData) {
        int userIndexStart = decodedInitData.indexOf("{") + 1;
        int userIndexEnd = decodedInitData.lastIndexOf("}");
        String userJSON = "{" + decodedInitData.substring(userIndexStart, userIndexEnd) + "}";

        return objectMapper.readValue(userJSON, InitDataUser.class);
    }

}
