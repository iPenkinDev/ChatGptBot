package com.dev.chatgptbot.model;

import com.dev.chatgptbot.config.TelegramBotConfig;
import com.dev.chatgptbot.converter.OggToWavConverter;
import com.dev.chatgptbot.service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Voice;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
@Log4j
public class TelegramBot extends TelegramLongPollingBot {

    private final MessageService sendMessageService;

    private final TelegramBotConfig telegramBotConfig;

    private final ChatGpt chatGpt;
    private final OggToWavConverter oggToWavConverter;

    public TelegramBot(MessageService sendMessageService, TelegramBotConfig telegramBotConfig, ChatGpt chatGpt, OggToWavConverter oggToWavConverter) {
        this.sendMessageService = sendMessageService;
        this.telegramBotConfig = telegramBotConfig;
        this.chatGpt = chatGpt;
        this.oggToWavConverter = oggToWavConverter;
    }

    @Override
    public String getBotUsername() {
        return telegramBotConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return telegramBotConfig.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage()) {
            return;
        }
        String messageText = update.getMessage().getText();


        try {
            handleCommand(messageText, update.getMessage().getChatId());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (update.hasMessage() && update.getMessage().hasVoice()) {
            Voice voice = update.getMessage().getVoice();
            getVoiceFile(voice);

            try {
                String text = chatGpt.sendVoiceMessageToChatGptBot(messageText);
                oggToWavConverter.convertTelegramVoiceToWav();
                System.out.println("After convert");
                handleVoiceCommand(text, update.getMessage().getChatId());
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void handleVoiceCommand(String messageText, Long chatId) throws IOException {

        if (messageText.equals("/start")) {
            sendTextMessage(chatId, "Hello, I'm ChatGptBot!");
        } else {
            sendTextMessage(chatId, String.valueOf(chatGpt.sendVoiceMessageToChatGptBot(messageText)));
        }

    }

    private void getVoiceFile(Voice voice) {
        String fileId = voice.getFileId();

        GetFile getFile = new GetFile();
        getFile.setFileId(fileId);

        try {
            File file = execute(getFile);
            InputStream is = new URL("https://api.telegram.org/file/bot" + getBotToken() + "/" + file.getFilePath()).openStream();

            // сохраняем голосовое сообщение в файл
            Files.copy(is, Paths.get("voice.ogg"), StandardCopyOption.REPLACE_EXISTING);
        } catch (TelegramApiException | IOException e) {
            e.printStackTrace();
        }
    }


    private void handleCommand(String messageText, long chatId) throws JsonProcessingException {
        if (messageText == null) {
            return;
        }
        if (messageText.equals("/start")) {
            sendTextMessage(chatId, "Hello, I'm ChatGptBot!");
        } else {
            sendTextMessage(chatId, String.valueOf(chatGpt.sendMessageToChatGptBot(messageText)));
        }
    }

    private void sendTextMessage(long chatId, String messageText) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(messageText);
        log.debug("response: " + messageText);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
