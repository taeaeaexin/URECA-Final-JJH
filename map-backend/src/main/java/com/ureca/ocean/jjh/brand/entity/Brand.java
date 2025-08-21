package com.ureca.ocean.jjh.brand.entity;

import com.ureca.ocean.jjh.benefit.entity.Benefit;
import com.ureca.ocean.jjh.store.entity.Store;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="partner_brand")
@Setter @Getter
public class Brand {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "image_url")
    private String imageUrl;

    // 연관관계
    @OneToMany(mappedBy = "brand")
    private List<Store> stores = new ArrayList<>();

    @OneToMany(mappedBy = "brand")
    private List<Benefit> benefits = new ArrayList<>();
}