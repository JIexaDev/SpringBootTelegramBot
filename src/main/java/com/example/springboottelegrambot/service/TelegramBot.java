package com.example.springboottelegrambot.service;

import com.example.springboottelegrambot.config.BotConfig;
import com.example.springboottelegrambot.model.User;
import com.example.springboottelegrambot.model.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserRepository userRepository;
    final BotConfig botConfig;

    public TelegramBot(BotConfig botConfig) {
        this.botConfig = botConfig;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getFrom().getUserName());
                    registerUser(update.getMessage());
                    break;
                default:
                    sendMessage(chatId, "Команда не поддерживается!");
            }
        }
    }

    private void startCommandReceived(long chatId, String name) {
        String answer = "Hi " + name + "!!!";
        log.info("Replied to user" + name);

        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textMessage) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textMessage);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }

    private void registerUser(Message msg) {
        if (userRepository.findById(msg.getChatId()).isEmpty()) {
            var chatId = msg.getChatId();
            var chat = msg.getChat();

            User user = new User();
            user.setChatId(chatId);
            user.setUserName(chat.getUserName());
            user.setFirstName(chat.getFirstName());

            userRepository.save(user);
            log.info("User saved " + user);
        }
    }
}
