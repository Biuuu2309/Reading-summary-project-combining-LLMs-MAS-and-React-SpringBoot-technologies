package com.example.my_be.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;

@Configuration
public class cloudinaryconfig {
    String envUrl = System.getenv("CLOUDINARY_URL");
    String cloudinaryKey = System.getenv("CLOUDINARY_API_KEY");
    String cloudinarySecret = System.getenv("CLOUDINARY_API_KEY_SECRET");
    String cloudinaryUrl = "cloudinary://" + cloudinaryKey + ":" + cloudinarySecret + "@" + envUrl;

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(cloudinaryUrl);
    }
}
