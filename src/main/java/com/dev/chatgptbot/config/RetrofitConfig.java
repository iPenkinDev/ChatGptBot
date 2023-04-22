package com.dev.chatgptbot.config;

import com.dev.chatgptbot.service.VoiceService;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Configuration
public class RetrofitConfig {

    @Bean
    public Retrofit retrofit() {
        return new Retrofit.Builder()
                .baseUrl("https://api.openai.com/v1/audio/transcriptions/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Bean
    public VoiceService voiceService() {
        return retrofit().create(VoiceService.class);
    }
}
