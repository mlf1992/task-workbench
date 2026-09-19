@echo off
call "C:\Users\Administrator\AppData\Local\Programs\TaskWorkbench\uninstall.cmd" > "%TEMP%\twb-uninstall.log" 2>&1
echo WRAPPER_DONE >> "%TEMP%\twb-uninstall.log"
