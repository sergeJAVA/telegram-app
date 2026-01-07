package com.sergejava.telegram_app.service;

import com.sergejava.telegram_app.dto.InitDataUser;

public interface ParseInitDataService {

    InitDataUser userFromInitData(String decodedInitData);

}
