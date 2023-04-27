package com.dev.chatgptbot.model;

import com.dev.chatgptbot.config.TelegramBotConfig;
import com.dev.chatgptbot.converter.OggToWavConverter;
import com.dev.chatgptbot.model.pojo.telegramPojo.Message;
import com.dev.chatgptbot.model.pojo.text2text.MessageMarkdownEntity;
import com.dev.chatgptbot.model.pojo.text2text.MessageSpliterator;
import com.dev.chatgptbot.util.TelegramBotUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
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
import java.util.ArrayList;

@Component
@Getter
@Setter
@RequiredArgsConstructor
@Log4j
public class TelegramBot extends TelegramLongPollingBot {

    private final TelegramBotConfig telegramBotConfig;
    private final ObjectMapper objectMapper;
    private final TelegramBotUtils telegramBotUtils;
    private final ChatGpt chatGpt;
    private final SendMessage sendMessage;
    private final OggToWavConverter oggToWavConverter;
    private final MessageSpliterator messageSpliterator;

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
        textToJson(update);

        try {
            handleCommand(messageText, update.getMessage().getChatId());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (update.hasMessage() && update.getMessage().hasVoice()) {
            messageText = update.getMessage().getText();
            Voice voice = update.getMessage().getVoice();
            getVoiceFile(voice);
            try {
                oggToWavConverter.convertTelegramVoiceToWav();
                messageText = chatGpt.sendVoiceMessageToChatGptBot(messageText);
                handleCommand(messageText, update.getMessage().getChatId());
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void textToJson(Update update) {
        String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(update.getMessage());
            Message message = objectMapper.readValue(jsonString, Message.class);
            System.out.println("message.getFrom().getFirst_name() = " + message.getFrom().getFirst_name());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void getVoiceFile(Voice voice) {
        String fileId = voice.getFileId();

        GetFile getFile = new GetFile();
        getFile.setFileId(fileId);

        try {
            File file = execute(getFile);
            InputStream is = new URL(telegramBotUtils.getBOT_GET_VOICE_URL()
                    + getBotToken()
                    + "/"
                    + file.getFilePath()).openStream();

            // сохраняем голосовое сообщение в файл
            Files.copy(is, Paths.get("voice/voice.ogg"), StandardCopyOption.REPLACE_EXISTING);
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

        int maxLength = 4096; // максимальная длина сообщения

        if (messageText.length() > maxLength) {
            // разделение сообщения на несколько частей
            ArrayList<String> messages = messageSpliterator.splitMessage(messageText, maxLength);

            // отправка каждой части сообщения отдельно
            for (String msg : messages) {
                        sendMessage.setChatId(chatId);
                        sendMessage.setText(msg);
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        } else {
            // отправка сообщения целиком
            sendMessage.setChatId(chatId);
            sendMessage.setText(messageText);
            sendMessage.setText(MessageMarkdownEntity.escapeMarkdown(sendMessage.getText()));
            sendMessage.setParseMode("Markdown");
            log.debug("response: " + messageText);
            try {
                execute(sendMessage); // отправка сообщения
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }


}
