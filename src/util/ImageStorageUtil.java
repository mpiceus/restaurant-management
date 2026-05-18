package util;

import java.io.File;
import java.nio.file.*;

public class ImageStorageUtil {

    public static String saveMonAnImage(File sourceFile, int monId) throws Exception {

        if (sourceFile == null) {
            return null;
        }

        // tạo folder nếu chưa có
        Files.createDirectories(Paths.get("assets/monan"));
        // xóa ảnh cũ
        deleteOldImages(monId);
        String fileName = sourceFile.getName();

        String extension = "";

        int dotIndex = fileName.lastIndexOf(".");

        if (dotIndex >= 0) {
            extension = fileName.substring(dotIndex);
        }

        // tên mới theo mon_id
        String newFileName = "mon_" + monId + extension;

        Path destination = Paths.get("assets/monan/" + newFileName);
        Files.copy(
                sourceFile.toPath(),
                destination,
                StandardCopyOption.REPLACE_EXISTING
        );

        return destination.toString().replace("\\", "/");
    }

    public static void deleteOldImages(int monId) throws Exception {

        Path folder = Paths.get("assets/monan");

        if (!Files.exists(folder)) {
            return;
        }

        DirectoryStream<Path> stream =
                Files.newDirectoryStream(folder);

        for (Path path : stream) {

            String fileName =
                    path.getFileName().toString();

            if (fileName.startsWith("mon_" + monId + ".")) {

                Files.deleteIfExists(path);
            }
        }
    }
}