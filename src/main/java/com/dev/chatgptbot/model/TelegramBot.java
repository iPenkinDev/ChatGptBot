package com.dev.chatgptbot.model;

import com.dev.chatgptbot.config.TelegramBotConfig;
import com.dev.chatgptbot.converter.OggToWavConverter;
import com.dev.chatgptbot.entity.User;
import com.dev.chatgptbot.model.pojo.telegramPojo.Messages;
import com.dev.chatgptbot.model.pojo.text2text.MessageMarkdownEntity;
import com.dev.chatgptbot.model.pojo.text2text.MessageSpliterator;
import com.dev.chatgptbot.service.impl.MessageService;
import com.dev.chatgptbot.service.impl.UserService;
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
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Voice;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
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
import java.util.List;
import java.util.Objects;

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
    private final UserService userService;
    private final MessageService messageService;

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

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":
                    String firstName = update.getMessage().getChat().getFirstName();
                    startCommandReceived(chatId, firstName);
                    break;

                default:
                    addTextMessageToDb(update);
                    sendMessage(chatId, String.valueOf(chatGpt.sendMessageToChatGptBot(messageText)));
            }

        //for voice messages
        } else if (update.hasMessage() && update.getMessage().hasVoice()) {
            botCommand();
            Long chatId = update.getMessage().getChatId();

            Voice voice = update.getMessage().getVoice();
            getVoiceFile(voice);
            String messageTextFromVoice;
            try {
                oggToWavConverter.convertTelegramVoiceToWav();
                messageTextFromVoice = chatGpt.sendVoiceMessageToChatGptBot();
                addVoiceMessageToDb(update, messageTextFromVoice);
                sendMessage(chatId, String.valueOf(chatGpt.sendMessageToChatGptBot(messageTextFromVoice)));
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                throw new RuntimeException(e);
            }
        }
        botCommand();
    }

    private void startCommandReceived(Long chatId, String firstName) {

        String answer = "Hi " + firstName + ", welcome to ChatGPT Bot!";
        sendMessage(chatId, answer);

    }

    private void addVoiceMessageToDb(Update update, String messageText) {
        Messages messages = textToJson(update);
        if (!Objects.isNull(userService.getByTelegramId(messages.getFrom().getId()))) {
            messageService.createFromVoice(messageText, messages.getFrom().getId());
        } else {
            User user = userService.create(messages);
            messageService.createFromVoice(messageText, user.getTelegramId());
        }
    }

    private void addTextMessageToDb(Update update) {
        Messages messages = textToJson(update);
        if (!Objects.isNull(userService.getByTelegramId(messages.getFrom().getId()))) {
            messageService.create(messages, messages.getFrom().getId());
        } else {
            User user = userService.create(messages);
            messageService.create(messages, user.getTelegramId());
        }
    }

    public Messages textToJson(Update update) {
        String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(update.getMessage());
            return objectMapper.readValue(jsonString, Messages.class);
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


    private void sendMessage(Long chatId, String messageText) {

        int maxLength = 4096; //max length message
        if (messageText.length() > maxLength) {
            //split message
            ArrayList<String> messages = messageSpliterator.splitMessage(messageText, maxLength);

            //send chunk message
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
            // send full message
            sendMessage.setChatId(chatId);
            sendMessage.setText(messageText);
            sendMessage.setText(MessageMarkdownEntity.escapeMarkdown(sendMessage.getText()));
            sendMessage.setParseMode("Markdown");
            log.info("response: " + messageText);

            final int maxTries = 3;
            int tryCount = 0;
            boolean sent = false;
            while (!sent && tryCount < maxTries) {
                try {
                    execute(sendMessage);
                    sent = true;
                } catch (TelegramApiException e) {
                    log.error("Error when send message: " + e.getMessage());
                    tryCount++;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            if (tryCount == maxTries && !sent) {
                log.error("Failed to send message after " + tryCount + " tries.");
            }
        }
    }

    private void botCommand(){
        List<BotCommand> botCommands = new ArrayList<>();
        botCommands.add(new BotCommand("/start", "get a welcome message"));
        try {
            execute(new SetMyCommands(botCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot command list: " + e.getMessage());
        }
    }
}
