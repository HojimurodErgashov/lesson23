package org.example;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.example.model.TelegramDto.TelegramFileDto;
import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.streams.StreamReader;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.groupadministration.SetChatPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class MainApplication extends TelegramLongPollingBot {
    private final String FINAL_BOT_TOKen = "6302370489:AAEMT7wL0UTJ9rrTEZkQtK7H2jTVanPGoGQ";
    private final String BOT_USER_NAME = "@Muharram1989bot_bot";
    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(new MainApplication());
        } catch (TelegramApiException e) {
            System.out.println(e);
        }
    }
    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if(message.hasText()){
            String text = message.getText();
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(message.getChatId());
            String msg = "";
            if(text.equals("/start")){
                User user = message.getFrom();
                msg = String.format("Assalomu aleykum %s %s , botga xush kelibsiz! Tilni tanlang:" , user.getFirstName() , user.getLastName());
                sendMessage.setReplyMarkup(langsbutton());
            }
            else if(text.equals("Uz")){
                msg  = "O'zbek tili Kontaktingizni yuboring!";
                sendMessage.setReplyMarkup(Contactsbutton());
            }else if(text.equals("Ru")){
                msg  = "Rus tili";
                sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
            }else if(text.equals("/file")){
                msg = null;
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(message.getChatId());
                InputFile file = new InputFile(new File("C:\\Users\\PC Asus\\Pictures\\2ab8ecb0842dc9da213e27caaaef5dac.mp4"));
                sendPhoto.setPhoto(file);
//                System.out.println();
                sendPhoto.setCaption("Fayl yubordik");
                try {
                    execute(sendPhoto);
                } catch (TelegramApiException e) {
                    System.out.println(e);
                }
            }
            if(msg != null){
                sendMessage.setText(msg);
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    System.out.println(e);
                }
            }
        }
        else if(message.hasContact()){
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(message.getChatId());
            sendMessage.setText(String.format("%s shu raqam to'g'rimi? , Lokatsiya yuboring!" , message.getContact().getPhoneNumber()));
//            System.out.println(message.getContact().getPhoneNumber());
            sendMessage.setReplyMarkup(Lokationbutton());
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                System.out.println(e);
            }
        }
        else if(message.hasLocation()){
            SendMessage sendMessage = new SendMessage();
            Location location = message.getLocation();
            sendMessage.setChatId(message.getChatId());
            sendMessage.setText(String.format("lat: %f, lang: %f" ,location.getLatitude() , location.getLongitude()));
            sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                System.out.println(e);
            }
        }
        else if(message.hasPhoto()){
            List<PhotoSize> list = message.getPhoto();
            for (PhotoSize photoSize : list) {
                if(saveFromTg(photoSize.getFileId())){
                    System.out.println(photoSize.getFileId() + " saqlandi");
                }else{
                    System.out.println("Saqlanmadi!");
                }
            }
//            execute()
        }
    }
    @Override
    public String getBotUsername() {
        return BOT_USER_NAME;
    }
    @Override
    public String getBotToken() {
        return FINAL_BOT_TOKen;
    }
    public ReplyKeyboardMarkup langsbutton(){
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setSelective(true);

        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        KeyboardButton button = new KeyboardButton();
        button.setText("Uz");
        row.add(button);

        button = new KeyboardButton();
        button.setText("Ru");
        row.add(button);

        rows.add(row);
        markup.setKeyboard(rows);
        return markup;
    }

    public ReplyKeyboardMarkup Contactsbutton(){
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setSelective(true);

        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        KeyboardButton button = new KeyboardButton();
        button.setText("Kontaktni ulashish:");
        button.setRequestContact(true);
        row.add(button);

        rows.add(row);
        markup.setKeyboard(rows);
        return markup;
    }
    public ReplyKeyboardMarkup Lokationbutton(){
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setSelective(true);

        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        KeyboardButton button = new KeyboardButton();
        button.setText("Joylashuvni ulashish:");
        button.setRequestLocation(true);
        row.add(button);

        rows.add(row);
        markup.setKeyboard(rows);
        return markup;
    }
    public boolean saveFromTg(String file_id){
        Gson gson = new Gson();
        try{
            URL url = new URL(String.format("https://api.telegram.org/bot<bot%s/getFile?file_id=%s" , getBotToken() , file_id));
            URLConnection connection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String json = "";
            String line  = null;
            while ((line = reader.readLine()) != null){
                json += line;
            }
            TelegramFileDto telegramFileDto = gson.fromJson(json , TelegramFileDto.class);
            if(telegramFileDto.isOk()){
                FileUtils.copyURLToFile(new URL(String.format("https://api.telegram.org/file/bot%s/%s" , FINAL_BOT_TOKen , telegramFileDto.getResult().getFile_path())), new File(("C:\\Users\\PC Asus\\Desktop\\"+telegramFileDto.getResult().getFile_path().substring(telegramFileDto.getResult().getFile_path().lastIndexOf("/") + 1))));
                return true;
            }else {
                return false;
            }
        } catch (MalformedURLException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        }
        return false;
    }

}