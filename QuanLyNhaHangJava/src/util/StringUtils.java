package util;

import java.text.Normalizer;

public class StringUtils {

    public static String normalizeVietnamese(String str) {

        if (str == null) {
            return "";
        }

        // trim + đưa về lowercase
        str = str.trim().toLowerCase();

        // nhiều khoảng trắng -> 1 khoảng trắng
        str = str.replaceAll("\\s+", " ");

        // bỏ dấu tiếng Việt
        str = Normalizer.normalize(str, Normalizer.Form.NFD);
        str = str.replaceAll("\\p{M}", "");

        // xử lý riêng đ/Đ
        str = str.replace("đ", "d");

        return str;
    }
}
