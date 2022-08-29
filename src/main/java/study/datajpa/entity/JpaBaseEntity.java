package study.datajpa.entity;

import javax.persistence.Column;
import java.time.LocalDateTime;

public class JpaBaseEntity {
    @Column(updatable = false)
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

}
