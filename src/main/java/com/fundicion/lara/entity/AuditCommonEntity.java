package com.fundicion.lara.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.sql.Timestamp;


/**
 * @author Daniel Humberto Ramírez Juárez
 * @version 1.0.0
 * date 24/04/21
 **/
@Getter
@Setter
@SuperBuilder
@MappedSuperclass
@RequiredArgsConstructor
public class AuditCommonEntity implements Serializable {

	@Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@CreationTimestamp
	private Timestamp createdAt;

	@Column(name = "update_Date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@UpdateTimestamp
	private Timestamp updateDate;

}
