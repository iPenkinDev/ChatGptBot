package com.dev.chatgptbot.service;

import java.io.IOException;

public interface VoiceRequestService {

    String voiceToString(String response) throws IOException;

    String voiceNotString();


}
