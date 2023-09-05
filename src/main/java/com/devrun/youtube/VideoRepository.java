package com.devrun.youtube;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {

    // Corrected method to find a video by videoNo
    Video findByVideoNo(Long videoNo);

    // Corrected method to find a video by videoId
    Video findByVideoId(String videoId);
}
