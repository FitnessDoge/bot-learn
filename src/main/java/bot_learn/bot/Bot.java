package bot_learn.bot;

import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

public class Bot implements LongPollingSingleThreadUpdateConsumer {
    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            System.out.println(update.getMessage().getText());
        }
    }
}
