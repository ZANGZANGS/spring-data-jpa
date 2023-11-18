package study.datajpa.repository;

import lombok.Getter;

public record UsernameOnlyDto(String username) {

    public String getUsername() {
        return username;
    }
}
