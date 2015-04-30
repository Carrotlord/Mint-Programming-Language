@echo off
title Python 3
"C:\Program Files\Python3.2\python.exe" %1
echo [Program Finished.]
pause
exit

REM ==WARNING==
REM If you want to perform File I/O operations with a Python program,
REM Please note that the current-working-directory is the
REM same folder that THIS BATCH FILE is in, not the .py file.