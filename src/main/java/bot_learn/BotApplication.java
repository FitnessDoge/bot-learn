package bot_learn;

import bot_learn.bot.Bot;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

public class BotApplication {
    public static void main(String[] args) {
        String token = System.getenv("TG_BOT_TOKEN");
        try (TelegramBotsLongPollingApplication application = new TelegramBotsLongPollingApplication()) {
            // Register bot
            application.registerBot(token, new Bot());
            System.out.println("Bot successfully started...");
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
