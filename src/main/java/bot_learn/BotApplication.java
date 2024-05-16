package bot_learn;

import bot_learn.bot.Bot;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class BotApplication {
    public static void main(String[] args) {
        String token = System.getenv("TG_BOT_TOKEN");
        try {
            // Instantiate TG bots API
            TelegramBotsLongPollingApplication application = new TelegramBotsLongPollingApplication();
            // Register bot
            application.registerBot(token, new Bot());
        }catch (TelegramApiException e){
            e.printStackTrace();
        }
    }
}
