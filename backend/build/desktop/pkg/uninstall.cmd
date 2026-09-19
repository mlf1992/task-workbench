@echo off
setlocal
set "PATH=%SystemRoot%\System32;%SystemRoot%;%SystemRoot%\System32\WindowsPowerShell\v1.0;%PATH%"
set "APPDIR=%~dp0"
set "APPDIR=%APPDIR:~0,-1%"
echo Uninstalling TaskWorkbench, please wait...
copy /Y "%~dp0uninstall-task.ps1" "%TEMP%\twb-uninstall-task.ps1" >nul 2>&1
pushd "%TEMP%"
powershell -NoProfile -ExecutionPolicy Bypass -File "%TEMP%\twb-uninstall-task.ps1" -AppDir "%APPDIR%"
endlocal
exit /b 0
