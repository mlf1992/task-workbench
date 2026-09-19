[Version]
Class=IEXPRESS
SEDVersion=3
[Options]
PackagePurpose=InstallApp
ShowInstallProgramWindow=0
HideExtractAnimation=1
UseLongFileName=1
InsideCompressed=0
CAB_FixedSize=0
CAB_ResvCodeSigning=0
RebootMode=N
InstallPrompt=%InstallPrompt%
DisplayLicense=%DisplayLicense%
FinishMessage=%FinishMessage%
TargetName=E:\AIProject\workP\task-workbench\backend\build\desktop\output\TaskWorkbench-Setup-1.0.0.exe
FriendlyName=TaskWorkbench Setup
AppLaunched=cmd.exe /c install.cmd
PostInstallCmd=%PostInstallCmd%
AdminQuietInstCmd=cmd.exe /c install.cmd
UserQuietInstCmd=cmd.exe /c install.cmd
SourceFiles=SourceFiles
[Strings]
InstallPrompt=
DisplayLicense=
FinishMessage=
PostInstallCmd=<None>
[SourceFiles]
SourceFiles0=E:\AIProject\workP\task-workbench\backend\build\desktop\pkg\
[SourceFiles0]
twb-app.zip=
install.cmd=
setup.ps1=
uninstall.cmd=
uninstall-task.ps1=
