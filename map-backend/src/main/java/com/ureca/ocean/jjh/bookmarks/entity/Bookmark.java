package com.ureca.ocean.jjh.bookmarks.entity;

import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import com.ureca.ocean.jjh.common.entity.BaseEntity;
import com.ureca.ocean.jjh.store.entity.Store;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "bookmarks")
@Getter @Setter
public class Bookmark extends BaseEntity{
	
	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
	private UUID id;
	
	@Column(name = "user_id")
	private UUID userId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;
	
}
