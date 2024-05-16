package bot_learn.bot;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Bot implements LongPollingSingleThreadUpdateConsumer {
    private static OkHttpTelegramClient client;

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
                // AgACAgUAAxkBAAMUZkYeNzWfCtwt3xeN0-j_QCY7-JMAAp-7MRu9HTBWVo-5iM5U01gBAAMCAAN4AAM1BA
                String cmd = message.getText();
                if ("/pic".equals(cmd)) {
                    SendPhoto sendPhoto = SendPhoto.builder()
                            .chatId(chatId)
                            .photo(new InputFile("AgACAgUAAxkBAAMUZkYeNzWfCtwt3xeN0-j_QCY7-JMAAp-7MRu9HTBWVo-5iM5U01gBAAMCAAN4AAM1BA"))
                            .build();
                    try {
                        client.execute(sendPhoto);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }

            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(message.getText())
                    .build();
            try {
                client.execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if (message.hasPhoto()) {
            List<PhotoSize> photos = message.getPhoto();
            Optional<PhotoSize> maxPhotoSize = photos.stream()
                    .max(Comparator.comparing(PhotoSize::getFileSize));
            String fileId = maxPhotoSize.map(PhotoSize::getFileId).orElse("");
            Integer photoWidth = maxPhotoSize.map(PhotoSize::getWidth).orElse(0);
            Integer photoHeight = maxPhotoSize.map(PhotoSize::getHeight).orElse(0);
            String caption = "file_id: " + fileId
                    + "\nwidth: " + photoWidth
                    + "\nheight: " + photoHeight;
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
}
