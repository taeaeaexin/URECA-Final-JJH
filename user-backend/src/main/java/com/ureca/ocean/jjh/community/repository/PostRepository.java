package com.ureca.ocean.jjh.community.repository;

import com.ureca.ocean.jjh.community.entity.Post;
import com.ureca.ocean.jjh.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository  extends JpaRepository<Post, UUID> {
//    Post findById(UUID id);
    Page<Post> findAll(Pageable pageable);
    Page<Post> findByLocation(Pageable pageable,String location);

    Page<Post> findByAuthor(User user,Pageable pageable);
    @Query("SELECT DISTINCT p.location FROM Post p")
    List<String> findDistinctLocations();

}
