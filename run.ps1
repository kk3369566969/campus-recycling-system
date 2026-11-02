param([switch]$Build)
$env:JAVA_HOME = 'D:\work\environment\jdk\jdk 17'
$env:MAVEN_OPTS = '-Duser.home=D:\work\gouzao\job1'
$maven = 'C:\Users\33695\tools\apache-maven-3.9.11\bin\mvn.cmd'
$mavenArgs = @('-Dmaven.repo.local=D:\work\gouzao\job1\.m2')
if ($Build) { & $maven @mavenArgs clean package }
if (-not (Test-Path 'target/campus-cycle-0.1.0.jar')) {
    Write-Host '请先执行：.\run.ps1 -Build' -ForegroundColor Yellow
    exit 1
}
java -jar target/campus-cycle-0.1.0.jar
