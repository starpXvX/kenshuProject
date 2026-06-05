package jp.swell.controller;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.stream.Stream;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 一時ファイルを定期的に監視し、古いファイルを削除するクラス
 * 
 * @author Shusei Tanaka
 */

@Component
public class TempFileCleaner {

    @Autowired
    private ServletContext servletContext;

    @Scheduled(fixedRate = 1800000)
    public void deleteOldTempFiles() {

        String tempPath =
            servletContext.getRealPath("/upload/temp");

        Path tempFile = Paths.get(tempPath);

        try (Stream<Path> files = Files.list(tempFile)) {

            files.forEach(path -> {

                try {

                    FileTime lastModified =
                        Files.getLastModifiedTime(path);

                    long diff =
                        System.currentTimeMillis()
                        - lastModified.toMillis();

                    if (diff > 30 * 60 * 1000) {

                        Files.deleteIfExists(path);

                        System.out.println("削除:" + path);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
