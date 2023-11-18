package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {

    @Value("#{target.username + ' ' + target.age}")//open projection 전체 칼럼을 모두 조회하여 조합
    String getUsername();
}
