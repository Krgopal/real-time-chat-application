package com.demo.business;

import com.demo.model.UserInfoDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsBO {
    private String username;
    private String name;
    private String email;
    private String password;
    private List<UserJoinedGroupBO> userJoinedGroups;

    public UserDetailsBO(UserInfoDetails userInfoDetails) {
        this.email = userInfoDetails.getEmail();
        this.username = userInfoDetails.getUsername();
        this.name = userInfoDetails.getName();
    }
}
