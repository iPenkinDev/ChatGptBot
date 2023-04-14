package com.dev.chatgptbot.model;

import com.dev.chatgptbot.config.TelegramBotConfig;
import com.dev.chatgptbot.service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Log4j
public class TelegramBot extends TelegramLongPollingBot {

    private final MessageService sendMessageService;

    private final TelegramBotConfig telegramBotConfig;

    private final ChatGpt chatGpt;

    public TelegramBot(MessageService sendMessageService, TelegramBotConfig telegramBotConfig, ChatGpt chatGpt) {
        this.sendMessageService = sendMessageService;
        this.telegramBotConfig = telegramBotConfig;
        this.chatGpt = chatGpt;
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
    }

    private void handleCommand(String messageText, long chatId) throws JsonProcessingException {
        if (messageText.equals("/start")) {
            sendTextMessage(chatId, "Hello, I'm ChatGptBot!");
        } else {
            sendTextMessage(chatId, chatGpt.sendMessageToChatGptBot(messageText));
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
