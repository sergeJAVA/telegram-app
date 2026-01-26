package com.sergejava.telegram_app.mapper;

import com.sergejava.telegram_app.dto.InitDataUser;
import com.sergejava.telegram_app.dto.UserDTO;
import com.sergejava.telegram_app.entity.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {

    public static UserDTO toDto(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .username(user.getUsername())
                .languageCode(user.getLanguageCode())
                .allowsWriteToPM(user.getAllowsWriteToPM())
                .firstName(user.getFirstName())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public static UserDTO toDto(InitDataUser user) {
        return UserDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .languageCode(user.getLanguageCode())
                .allowsWriteToPM(user.getAllowsWriteToPM())
                .firstName(user.getFirstName())
                .build();
    }

    public static User toEntity(UserDTO userDto) {
        return User.builder()
                .id(userDto.getId())
                .userId(userDto.getUserId())
                .username(userDto.getUsername())
                .allowsWriteToPM(userDto.getAllowsWriteToPM())
                .firstName(userDto.getFirstName())
                .languageCode(userDto.getLanguageCode())
                .createdAt(userDto.getCreatedAt())
                .build();
    }

}
