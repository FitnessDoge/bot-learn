package bot_learn.bot;

import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Bot implements LongPollingSingleThreadUpdateConsumer {
    private final OkHttpTelegramClient client;

    public Bot() {
        client = new OkHttpTelegramClient(System.getenv("TG_BOT_TOKEN"));
    }

    @Override
    public void consume(Update update) {
        Message message;
        if (update.hasMessage()) {
            message = update.getMessage();
        } else if (update.hasCallbackQuery()) {
            handleCallback(update);
            return;
        } else {
            return;
        }

        User from = message.getFrom();
        Long chatId = message.getChatId();
        System.out.println("Message: " + message.getText() + " from: @" + from);

        if (message.hasText()) {
            if (message.isCommand()) {
                // command
                handleCommand(message, chatId);
            } else {
                // text
                sendSimpleMessage(chatId, message.getText());
            }
        } else if (message.hasPhoto()) {
            handlePhoto(message, chatId);
        }
    }

    private void handleCallback(Update update) {
        CallbackQuery callback = update.getCallbackQuery();
        String callbackData = callback.getData();
        Long chatId = callback.getMessage().getChatId();
        Integer messageId = callback.getMessage().getMessageId();

        if ("prev".equals(callbackData)) {
            InlineKeyboardButton next = InlineKeyboardButton.builder()
                    .text("Next")
                    .callbackData("/menu")
                    .build();
            InlineKeyboardMarkup inlineKeyboardMarkup = InlineKeyboardMarkup.builder()
                    .keyboardRow(new InlineKeyboardRow(next))
                    .build();
            EditMessageReplyMarkup editMessageReplyMarkup = EditMessageReplyMarkup.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .replyMarkup(inlineKeyboardMarkup)
                    .build();
            try {
                client.execute(editMessageReplyMarkup);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if ("next".equals(callbackData)) {
            InlineKeyboardButton prev = InlineKeyboardButton.builder()
                    .text("Previous")
                    .callbackData("/menu")
                    .build();
            InlineKeyboardMarkup inlineKeyboardMarkup = InlineKeyboardMarkup.builder()
                    .keyboardRow(new InlineKeyboardRow(prev))
                    .build();
            EditMessageReplyMarkup editMessageReplyMarkup = EditMessageReplyMarkup.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .replyMarkup(inlineKeyboardMarkup)
                    .build();
            try {
                client.execute(editMessageReplyMarkup);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendSimpleMessage(Long chatId, String message) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId.toString())
                .text(message)
                .build();
        try {
            client.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleCommand(Message message, Long chatId) {
        String cmd = message.getText();
        if ("/pic".equals(cmd)) {
            String fileId = "AgACAgUAAxkBAAMUZkYeNzWfCtwt3xeN0-j_QCY7-JMAAp-7MRu9HTBWVo-5iM5U01gBAAMCAAN4AAM1BA";
            sendPhoto(chatId, fileId, null);
        } else if ("/reply".equals(cmd)) {
            sendReplyKeyboard(chatId);
        } else if ("/hide".equals(cmd)) {
            hideReplyKeyboard(chatId);
        } else if ("/emoji".equals(cmd)) {
            String emojiMessage = EmojiParser.parseToUnicode("\uD83E\uDEE1");
            sendSimpleMessage(chatId, emojiMessage);
        } else if ("/menu".equals(cmd)) {
            sendMenu(chatId);
        }
    }

    private void sendMenu(Long chatId) {
        InlineKeyboardButton prev = InlineKeyboardButton.builder()
                .text("Previous")
                .callbackData("prev")
                .build();
        InlineKeyboardButton url = InlineKeyboardButton.builder()
                .text("Previous")
                .url("https://rubenlagus.github.io/TelegramBotsDocumentation/lesson-6.html")
                .build();
        InlineKeyboardButton next = InlineKeyboardButton.builder()
                .text("Next")
                .callbackData("next")
                .build();
        InlineKeyboardMarkup inlineKeyboardMarkup = InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(prev, url, next))
                .build();
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text("Menu")
                .replyMarkup(inlineKeyboardMarkup)
                .build();
        try {
            client.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendReplyKeyboard(Long chatId) {
        ReplyKeyboardMarkup replyKeyboardMarkup = ReplyKeyboardMarkup.builder()
                .keyboardRow(new KeyboardRow("Row1 Button1", "Row1 Button2"))
                .keyboardRow(new KeyboardRow("Row2 Button1", "Row2 Button2"))
                .build();
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text("Reply Keyboard")
                .replyMarkup(replyKeyboardMarkup)
                .build();
        try {
            client.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void hideReplyKeyboard(Long chatId) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text("Keyboard hidden")
                .replyMarkup(new ReplyKeyboardRemove(true))
                .build();
        try {
            client.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handlePhoto(Message message, Long chatId) {
        List<PhotoSize> photos = message.getPhoto();
        Optional<PhotoSize> maxPhotoSize = photos.stream()
                .max(Comparator.comparing(PhotoSize::getFileSize));
        String fileId = maxPhotoSize.map(PhotoSize::getFileId).orElse("");
        Integer photoWidth = maxPhotoSize.map(PhotoSize::getWidth).orElse(0);
        Integer photoHeight = maxPhotoSize.map(PhotoSize::getHeight).orElse(0);
        String caption = "file_id: " + fileId
                + "\nwidth: " + photoWidth
                + "\nheight: " + photoHeight;
        sendPhoto(chatId, fileId, caption);
    }

    private void sendPhoto(Long chatId, String fileId, String caption) {
        SendPhoto sendPhoto = SendPhoto.builder()
                .chatId(chatId)
                .photo(new InputFile(fileId))
                .caption(caption)
                .build();
        try {
            client.execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
