package com.myecommerce.MyECommerce.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    // @Configuration 어노테이션을 받는 config 클래스에서 빈을 주입받아 현재 로그인한 사용자의 정보 매핑가능.
    @CreatedBy
    @Column(updatable = false)
    private Long createId;

    @CreatedDate
    @Column(updatable = false)
    @JsonSerialize(using = LocalDateTimeSerializer.class) // 직렬화
    @JsonDeserialize(using = LocalDateTimeDeserializer.class) // 역직렬화
    @JsonFormat(pattern = "yyyy-MM-dd kk-mm:ss") // 원하는 형태의 LocalDateTime 설정
    private LocalDateTime createDt;

    @LastModifiedBy
    private Long updateId;

    @LastModifiedDate
    @JsonSerialize(using = LocalDateTimeSerializer.class) // 직렬화
    @JsonDeserialize(using = LocalDateTimeDeserializer.class) // 역직렬화
    @JsonFormat(pattern = "yyyy-MM-dd kk-mm:ss") // 원하는 형태의 LocalDateTime 설정
    private LocalDateTime updateDt;

}
