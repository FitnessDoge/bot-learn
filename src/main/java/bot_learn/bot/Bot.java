package bot_learn.bot;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot implements LongPollingSingleThreadUpdateConsumer {
    private static OkHttpTelegramClient client;

    public Bot() {
        client = new OkHttpTelegramClient(System.getenv("TG_BOT_TOKEN"));
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message msg = update.getMessage();
            System.out.println(msg.getText());
            SendMessage sendMessage = new SendMessage(msg.getChatId().toString(), msg.getText());
            try {
                client.execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
