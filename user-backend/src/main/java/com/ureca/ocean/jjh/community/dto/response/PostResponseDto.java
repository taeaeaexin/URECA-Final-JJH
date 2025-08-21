package com.ureca.ocean.jjh.community.dto.response;

import com.ureca.ocean.jjh.community.entity.Post;
import com.ureca.ocean.jjh.user.dto.response.UserResponseDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PostResponseDto {
    private UUID postId;
    private String title;
    private String content;

    private UserResponseDto author;
    private String category;
    private String brandName;
    private String brandImgUrl;
    private String benefitName;
    private LocalDateTime promiseDate;

    private String location;
    private String storeName;
    private Double storeLongitude;
    private Double storeLatitude;

    public static PostResponseDto of(Post post) {
        return PostResponseDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .location(post.getLocation())
                .promiseDate(post.getPromiseDate())
                .brandName(post.getBrandName())
                .brandImgUrl (post.getBrandImgUrl())
                .benefitName(post.getBenefitName())
                .category(post.getCategory())
                .author(UserResponseDto.of(post.getAuthor()))
                .storeName(post.getStoreName())
                .storeLatitude(post.getStoreLatitude())
                .storeLongitude(post.getStoreLongitude())
                .build();
    }
}
