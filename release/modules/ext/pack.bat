REM pack200 --repack -G -O -E9 --modification-time=latest --segment-limit=-1 -v org.eclipse.jdt.core_3.11.1.v20150902-1521.jar 2.jar 

"C:\Program Files\Java\jdk1.8.0_71\bin\pack200" --repack -G -O -E9 --modification-time=latest -v %1 
pause