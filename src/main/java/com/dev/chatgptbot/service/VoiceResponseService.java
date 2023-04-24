package com.dev.chatgptbot.service;

import okhttp3.Response;

import java.io.IOException;

public interface VoiceResponseService {

    String voiceToString(String response) throws IOException;


}
