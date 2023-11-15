package study.datajpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Item  implements Persistable<String>  {
    //extends BaseEntity

    @Id
    //@GeneratedValue
    private String id;

    @CreatedDate
    private LocalDateTime createDate;

    public Item(String id) {
        this.id = id;
    }

    /**
     * @GeneratedValue 어노테이션이 아닌 직접 pk를 입력하는 경우에는 id가 있어 jpa save 메서드에서 기존 엔티티로 판단을 하게 된다.
     * 이를 해결하기 위해 Persistable 인터페이스를 상속받아 isNew 메서드를 재정의한다.
     *
     * SimpleJpaRepository 에 isNew 메서드 호출은 아래와 같음..
     *
     *    @Transactional
     *    @Override
     *    public <S extends T> S save(S entity) {
     *
     * 		Assert.notNull(entity, "Entity must not be null");
     *
     * 		if (entityInformation.isNew(entity)) {  <------------------ isNew 로직이 문제가 된다.
     * 			entityManager.persist(entity);
     * 			return entity;
     *        } else {
     * 			return entityManager.merge(entity);
     *        }
     *    }
     * @return
     */
    @Override
    public boolean isNew() {
        return getCreateDate() == null;
    }


}
