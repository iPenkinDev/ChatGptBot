package com.dev.chatgptbot.model.pojo.text2text;

public class MessageMarkdownEntity {
    public static String escapeMarkdown(String text) {
        return text.replaceAll("\\\\", "\\\\\\\\");
    }
}
