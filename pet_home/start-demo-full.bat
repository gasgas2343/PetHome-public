@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

echo ============================================
echo   PetHome 展示環境一鍵啟動腳本
echo ============================================
echo.

REM ---- 1. 啟動 Redis ----
echo [1/5] 啟動 Redis 容器...
docker-compose up -d

echo.
echo [2/5] 等待 Redis 就緒...
timeout /t 5 /nobreak >nul
docker-compose ps

echo.
echo [3/5] 啟動 ngrok（開新視窗，請勿關閉）...
start "ngrok" cmd /k "ngrok http 8080"

echo 等待 ngrok 建立連線...
timeout /t 6 /nobreak >nul

REM ---- 4. 自動抓取 ngrok 公開網址，並寫回 application.properties ----
echo.
echo [4/5] 抓取 ngrok 公開網址...
set NGROK_URL=
for /f "delims=" %%i in ('powershell -NoProfile -Command "try { (Invoke-RestMethod -Uri http://127.0.0.1:4040/api/tunnels).tunnels | Where-Object {$_.proto -eq 'https'} | Select-Object -ExpandProperty public_url } catch { '' }"') do set NGROK_URL=%%i

if "%NGROK_URL%"=="" (
    echo [警告] 抓不到 ngrok 網址，請確認 ngrok 視窗有正常顯示 Forwarding，
    echo         並手動到 application.properties 更新 ecpay.return-url 後，
    echo         再自行執行: .\mvnw spring-boot:run
    goto :end
) else (
    echo 取得 ngrok 網址：%NGROK_URL%
    echo 正在更新 application.properties 的 ecpay.return-url...
    powershell -NoProfile -Command ^
      "(Get-Content 'src\main\resources\application.properties') -replace '^ecpay.return-url=.*', 'ecpay.return-url=%NGROK_URL%/api/payments/ecpay/notify' | Set-Content 'src\main\resources\application.properties'"
    echo 更新完成，ReturnURL 已指向：%NGROK_URL%/api/payments/ecpay/notify
)

REM ---- 5. 啟動 Spring Boot 後端 ----
echo.
echo [5/5] 啟動 Spring Boot 後端（開新視窗，請勿關閉）...
start "SpringBoot Backend" cmd /k ".\mvnw spring-boot:run"

:end
echo.
echo ============================================
echo   請依序確認以下三個視窗都正常：
echo   1. Redis        - docker-compose ps 顯示 healthy
echo   2. ngrok         - 顯示 Forwarding 網址（不要關閉此視窗）
echo   3. Spring Boot   - 等待畫面出現 Started ...Application
echo.
echo   全部就緒後，建議先手動測試下一筆訂單，
echo   確認付款導頁與 Webhook 通知都正常，
echo   再開始正式展示，過程中不要關閉任何視窗。
echo ============================================
pause