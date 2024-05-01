package org.example.teamspark.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.teamspark.model.user.User;
import org.example.teamspark.model.user.UserStatus;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("avatar")
    private String avatar;

    @JsonProperty("status")
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    public static UserDto from(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setAvatar(user.getAvatar());
        dto.setStatus(user.getStatus());
        return dto;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", this.getName());
        map.put("email", this.getEmail());
        map.put("avatar", this.getAvatar());
        return map;
    }

}
