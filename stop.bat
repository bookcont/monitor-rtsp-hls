@echo off
set url1="%~dp0\third\nginx\nginx.exe"
set url2="%~dp0\third\ffmpeg\ffmpeg.exe"
echo �����������ֶ��ر�nginx.exe���̺�ffmpeg.exe���̣�������ر�hls��������һ���ر�nginx.exe��hls�����޷��������ʣ���Ҫ�û��ֶ��ر�start.bat���ڣ�
set /p var="��ѡ��y/n:"
if /i "%var%"=="y" (
	for /f "tokens=1,2" %%a in ('"wmic process where name="nginx.exe" get processid,executablepath| findstr /C:%url1%"') do (
		taskkill /f /pid %%b
	)
	for /f "tokens=1,2" %%a in ('"wmic process where name="ffmpeg.exe" get processid,executablepath| findstr /C:%url2%"') do (
		taskkill /f /pid %%b
	)
)

pause