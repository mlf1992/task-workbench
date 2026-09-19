param([Parameter(Mandatory=$true)][string]$AppDir)

$ErrorActionPreference = 'Continue'
$exe   = Join-Path $AppDir 'TaskWorkbench.exe'
$unins = Join-Path $AppDir 'uninstall.cmd'

function New-Lnk($lnkPath, $target, $workDir) {
    try {
        $ws = New-Object -ComObject WScript.Shell
        $sc = $ws.CreateShortcut($lnkPath)
        $sc.TargetPath = $target
        $sc.WorkingDirectory = $workDir
        $sc.IconLocation = "$target,0"
        $sc.Save()
    } catch {
        Write-Warning "create shortcut failed: $lnkPath -> $($_.Exception.Message)"
    }
}

# 创建桌面与开始菜单快捷方式（单项失败不影响安装）
$desktop  = [Environment]::GetFolderPath('Desktop')
$programs = [Environment]::GetFolderPath('Programs')
New-Lnk (Join-Path $desktop '任务安排工作台.lnk') $exe $AppDir
New-Lnk (Join-Path $programs '任务安排工作台.lnk') $exe $AppDir

$smDir = Join-Path $programs '任务安排工作台'
try { New-Item -ItemType Directory -Force -Path $smDir | Out-Null } catch {}
New-Lnk (Join-Path $smDir '卸载任务安排工作台.lnk') $unins $AppDir

# 注册到「设置 - 应用 - 已安装的应用」(HKCU，无需管理员)
try {
    $key = 'HKCU:\Software\Microsoft\Windows\CurrentVersion\Uninstall\TaskWorkbench'
    New-Item -Force -Path $key | Out-Null
    Set-ItemProperty $key -Name DisplayName     -Value '任务安排工作台'
    Set-ItemProperty $key -Name DisplayVersion  -Value '1.0.0'
    Set-ItemProperty $key -Name Publisher       -Value 'mlf1992'
    Set-ItemProperty $key -Name DisplayIcon     -Value "$exe,0"
    Set-ItemProperty $key -Name InstallLocation -Value $AppDir
    Set-ItemProperty $key -Name UninstallString -Value "`"$unins`""
    Set-ItemProperty $key -Name NoModify        -Value 1 -Type DWord
    Set-ItemProperty $key -Name NoRepair        -Value 1 -Type DWord
} catch {
    Write-Warning "register uninstall entry failed: $($_.Exception.Message)"
}

# 复制卸载脚本到安装目录
Copy-Item -LiteralPath (Join-Path $PSScriptRoot 'uninstall.cmd') -Destination $unins -Force
Copy-Item -LiteralPath (Join-Path $PSScriptRoot 'uninstall-task.ps1') -Destination (Join-Path $AppDir 'uninstall-task.ps1') -Force
