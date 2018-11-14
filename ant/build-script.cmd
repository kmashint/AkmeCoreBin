c:\util\gawk -W posix -f "%~dpn0.awk" build-data-big.txt >build-data.txt.txt
rem java -cp "%CLASSPATH%;C:\Java\ant-1.8.4\lib\bcel-5.2.jar;C:\Java\ant-1.8.4\lib\jawk.1_02.jar" ^
rem org.jawk.Awk -Z -f "%~dpn0.awk" <build-data-big.txt >build-data.txt.txt
pause