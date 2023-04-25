package com.dev.chatgptbot.model.pojo.text2text;

import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class MessageSpliterator {
    public ArrayList<String> splitMessage(String text, int maxLength) {
        ArrayList<String> messages = new ArrayList<>();

        String[] words = text.split("\\s+"); //разбиваем сообщение на отдельные слова

        if (text.length() > maxLength) {
            StringBuilder message = new StringBuilder();

            for (String word : words) {
                if (message.length() + word.length() + 1 > maxLength) {
                    messages.add(message.toString().trim());
                    message = new StringBuilder(word);
                } else {
                    if (message.length() != 0) {
                        message.append(" ");
                    }
                    message.append(word);
                }
            }
            messages.add(message.toString().trim());
        } else {
            messages.add(text);
        }

        return messages;
    }
}
