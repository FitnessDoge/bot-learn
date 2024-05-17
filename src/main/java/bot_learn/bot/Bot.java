package bot_learn.bot;

import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
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
                sendMessage(chatId, message.getText());
            }
        } else if (message.hasPhoto()) {
            handlePhoto(message, chatId);
        }
    }

    private void sendMessage(Long chatId, String message) {
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
            sendMessage(chatId, emojiMessage);
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
