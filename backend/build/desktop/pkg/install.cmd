@echo off
setlocal enabledelayedexpansion
rem IExpress 静默模式下 PATH 可能为空，显式补齐系统目录
set "PATH=%SystemRoot%\System32;%SystemRoot%;%SystemRoot%\System32\WindowsPowerShell\v1.0;%SystemRoot%\System32\Wbem;%PATH%"
set LOG=%TEMP%\twb-install.log
echo ===== %DATE% %TIME% ===== > "%LOG%"
echo cwd=%~dp0 >> "%LOG%"
echo LOCALAPPDATA=%LOCALAPPDATA% >> "%LOG%"
echo APPDATA=%APPDATA% >> "%LOG%"

set BASE=%LOCALAPPDATA%\Programs
echo [1/3] Closing old version... >> "%LOG%"
taskkill /IM TaskWorkbench.exe /F >> "%LOG%" 2>&1

echo [2/3] Extracting... >> "%LOG%"
if not exist "%BASE%" mkdir "%BASE%" >> "%LOG%" 2>&1
where tar >> "%LOG%" 2>&1
tar -xf "%~dp0twb-app.zip" -C "%BASE%" >> "%LOG%" 2>&1
echo tar_exit=%errorlevel% >> "%LOG%"
if not exist "%BASE%\TaskWorkbench\TaskWorkbench.exe" (
  echo EXE_MISSING_AFTER_EXTRACT >> "%LOG%"
  exit /b 2
)

echo [3/3] Shortcuts... >> "%LOG%"
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0setup.ps1" -AppDir "%BASE%\TaskWorkbench" >> "%LOG%" 2>&1
echo ps_exit=%errorlevel% >> "%LOG%"

copy /Y "%~dp0uninstall.cmd" "%BASE%\TaskWorkbench\uninstall.cmd" >> "%LOG%" 2>&1
copy /Y "%~dp0uninstall-task.ps1" "%BASE%\TaskWorkbench\uninstall-task.ps1" >> "%LOG%" 2>&1

echo Launching app... >> "%LOG%"
start "" "%BASE%\TaskWorkbench\TaskWorkbench.exe"
echo DONE >> "%LOG%"
endlocal
exit /b 0
