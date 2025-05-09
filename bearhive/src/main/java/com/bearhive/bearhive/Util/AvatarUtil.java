package com.bearhive.bearhive.Util;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.springframework.core.io.ClassPathResource;

public class AvatarUtil {
    public static String getRandomAvatar() {
        try {
            File folder = new ClassPathResource("static/image/avt").getFile();
            File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
            if (files == null || files.length == 0) {
                return "/image/avt/cat.png";
            }
            Random r = new Random();
            return "/image/avt/" + files[r.nextInt(files.length)].getName();
        } catch (IOException e) {
            return "/image/avt/cat.png";  
        }
    }
}
