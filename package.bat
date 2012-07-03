@echo off
call mvn clean:clean
mvn  -Dmaven.test.skip=true package
@pause