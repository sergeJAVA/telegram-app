package com.sergejava.telegram_app.util;

import lombok.experimental.UtilityClass;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class ParseInitData {

    public static Map<String, String> parseInitData(String initData) {
        return Arrays.stream(initData.split("&"))
                .map(s -> s.split("=", 2))
                .collect(Collectors.toMap(
                        a -> URLDecoder.decode(a[0], StandardCharsets.UTF_8),
                        a -> URLDecoder.decode(a[1], StandardCharsets.UTF_8)
                ));
    }

}
