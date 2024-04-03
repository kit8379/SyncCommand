@echo off

REM Set the path to the JAR file
SET JAR_FILE=target\SyncCommand.jar

REM Check if the JAR file exists
IF NOT EXIST "%JAR_FILE%" (
    ECHO Jar file not found.
    PAUSE
    EXIT /B 1
)

ECHO Jar file found.

REM Ask for the tag name
SET /P TAG_NAME=Enter the tag name: 

REM Check if the TAG_NAME is empty
IF "%TAG_NAME%"=="" (
    ECHO Tag name cannot be empty.
    PAUSE
    EXIT /B 1
)

ECHO Publishing to GitHub releases...
ECHO Command: gh release create %TAG_NAME% "%JAR_FILE%" --repo kit8379/SyncCommand --title "New Release" --notes "Release notes"
gh release create %TAG_NAME% "%JAR_FILE%" --repo kit8379/SyncCommand --title "New Release" --notes "Release notes"
IF %ERRORLEVEL% NEQ 0 (
    ECHO GitHub release failed.
    PAUSE
    EXIT /B 1
)

ECHO Published to GitHub releases.
PAUSE
