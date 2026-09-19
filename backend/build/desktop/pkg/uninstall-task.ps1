param([Parameter(Mandatory=$true)][string]$AppDir)

$ErrorActionPreference = 'SilentlyContinue'
$Host.UI.RawUI.WindowTitle = 'Uninstall TaskWorkbench'

Write-Host 'Closing TaskWorkbench...'
Stop-Process -Name TaskWorkbench -Force
# 等待进程真正退出、释放文件句柄
$deadline = (Get-Date).AddSeconds(20)
while (((Get-Process -Name TaskWorkbench -ErrorAction SilentlyContinue | Measure-Object).Count -gt 0) -and (Get-Date) -lt $deadline) {
    Start-Sleep -Milliseconds 500
}
Start-Sleep -Seconds 1

Write-Host 'Removing shortcuts and registry entry...'
Remove-Item 'HKCU:\Software\Microsoft\Windows\CurrentVersion\Uninstall\TaskWorkbench' -Recurse -Force
$desktop = [Environment]::GetFolderPath('Desktop')
Remove-Item (Join-Path $desktop '任务安排工作台.lnk') -Force
$programs = [Environment]::GetFolderPath('Programs')
Remove-Item (Join-Path $programs '任务安排工作台.lnk') -Force
Remove-Item (Join-Path $programs '任务安排工作台') -Recurse -Force

Write-Host 'Removing program files...'
$removed = $false
for ($i = 1; $i -le 30; $i++) {
    try {
        Remove-Item -LiteralPath $AppDir -Recurse -Force -ErrorAction Stop
        $removed = $true
        break
    } catch {
        Start-Sleep -Seconds 1
    }
}

if ($removed) {
    Write-Host ''
    Write-Host 'Done. TaskWorkbench has been uninstalled.' -ForegroundColor Green
    Write-Host 'Your data is kept at %APPDATA%\TaskWorkbench.'
} else {
    Write-Host ''
    Write-Host 'Some files are locked. Please restart Windows and delete this folder manually:' -ForegroundColor Yellow
    Write-Host $AppDir
}
Start-Sleep -Seconds 4
