$ErrorActionPreference = 'SilentlyContinue'

# 移除「已安装的应用」注册项
Remove-Item 'HKCU:\Software\Microsoft\Windows\CurrentVersion\Uninstall\TaskWorkbench' -Recurse -Force

# 移除桌面 / 开始菜单快捷方式
$desktop = [Environment]::GetFolderPath('Desktop')
Remove-Item (Join-Path $desktop '任务安排工作台.lnk') -Force
$programs = [Environment]::GetFolderPath('Programs')
Remove-Item (Join-Path $programs '任务安排工作台.lnk') -Force
Remove-Item (Join-Path $programs '任务安排工作台') -Recurse -Force
