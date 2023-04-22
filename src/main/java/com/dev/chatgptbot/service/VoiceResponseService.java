package com.dev.chatgptbot.service;

import com.google.gson.JsonObject;
import retrofit2.Call;

import java.io.IOException;

public interface VoiceResponseService {
   // String messageFromTranscriptionText(Call<JsonObject> call);

    Call<JsonObject> requestVoiceToChatGpt() throws IOException;

    String voiceToString(String response) throws IOException;
}
