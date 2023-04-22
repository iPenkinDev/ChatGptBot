package com.dev.chatgptbot.service;

import com.google.gson.JsonObject;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface VoiceService {

    @Multipart
    @POST
    Call<JsonObject> requestAudioTranscription(@Header("Authorization: Bearer ") String gptToken,
                                               @Header("Content-Type: multipart/form-data")
                                               @Part MultipartBody.Part audioFile,
                                               @Field("model") String model);

}
