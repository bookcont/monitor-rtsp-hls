@echo off
set url1="%~dp0\third\nginx\nginx.exe"
set url2="%~dp0\third\ffmpeg\ffmpeg.exe"
echo 本程序用来手动关闭nginx.exe进程和ffmpeg.exe进程，并不会关闭hls服务，所以一旦关闭nginx.exe后，hls服务将无法正常访问，需要用户手动关闭start.bat窗口！
set /p var="请选择y/n:"
if /i "%var%"=="y" (
	for /f "tokens=1,2" %%a in ('"wmic process where name="nginx.exe" get processid,executablepath| findstr /C:%url1%"') do (
		taskkill /f /pid %%b
	)
	for /f "tokens=1,2" %%a in ('"wmic process where name="ffmpeg.exe" get processid,executablepath| findstr /C:%url2%"') do (
		taskkill /f /pid %%b
	)
)

pause