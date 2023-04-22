package com.dev.chatgptbot.model.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class VoiceToString {

    @SerializedName("text")
    @Expose
    private String text;
}
