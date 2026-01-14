package com.sergejava.telegram_app.mapper;

import com.sergejava.telegram_app.dto.InitDataUser;
import com.sergejava.telegram_app.dto.UserDto;
import com.sergejava.telegram_app.entity.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .languageCode(user.getLanguageCode())
                .allowsWriteToPM(user.getAllowsWriteToPM())
                .firstName(user.getFirstName())
                .build();
    }

    public static UserDto toDto(InitDataUser user) {
        return UserDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .languageCode(user.getLanguageCode())
                .allowsWriteToPM(user.getAllowsWriteToPM())
                .firstName(user.getFirstName())
                .build();
    }

    public static User toEntity(UserDto userDto) {
        return User.builder()
                .userId(userDto.getUserId())
                .username(userDto.getUsername())
                .allowsWriteToPM(userDto.getAllowsWriteToPM())
                .firstName(userDto.getFirstName())
                .languageCode(userDto.getLanguageCode())
                .build();
    }

}
