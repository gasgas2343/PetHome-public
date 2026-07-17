package com.system.petpost.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 中文註解：寵物日誌本地圖片靜態資源設定。
 *
 * PetPostService 上傳圖片後會回傳 /uploads/xxx.jpg。
 * 由於 application.properties 設定 server.servlet.context-path=/api，
 * 實際瀏覽器可讀取路徑會是：
 * http://localhost:8080/api/uploads/xxx.jpg
 *
 * 這支設定只服務 uploads 資料夾，不影響會員、商品、預約等組員模組。
 */
@Configuration
public class PetpostUploadResourceConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

        registry
                .addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath.toUri().toString());
    }
}
