@@setlocal
@@echo off
@@set PS_CMD_ARGS=%*
@@if defined PS_CMD_ARGS set PS_CMD_ARGS=%PS_CMD_ARGS:"=\"%
@@powershell -Command Invoke-Expression $('$args=@(^&{$args} %PS_CMD_ARGS%);'+[String]::Join([char]10,$((Get-Content '%~f0') -notmatch '^^@@'))) & goto :EOF

# OpenDS install and run script for Windows
# Will download .jar file from latest GitHub release if not found
# Downloads local JRE copy if java not on PATH
# Configurable JRE version (jre_ver)

# Required: powershell 5.1 or newer

$ProgressPreference = 'SilentlyContinue';

$jre_ver = "11";

$dl_basename = "ods-jre" + $jre_ver;
$gh_latest_release = Invoke-RestMethod -Uri "https://api.github.com/repos/Boomaa23/open-ds/releases/latest";
$ods_jar_name = "open-ds-" + $gh_latest_release.tag_name + ".jar";
$jre_loc = $pwd.Path + '/' + $dl_basename;


if (-Not(Test-Path -Path ($pwd.Path + "/" + $ods_jar_name) -PathType leaf)) {
    Write-Host "No $ods_jar_name detected. Downloading latest release from GitHub.";
    Invoke-WebRequest -Uri $gh_latest_release.assets[0].browser_download_url -OutFile ($pwd.Path + "/" + $ods_jar_name);
}

$installed_java_ver = Get-Command java;
if (-Not($installed_java_ver -Match "java.exe")) {
    Write-Host "No Java installation detected on the PATH.";
    if (-Not(Test-Path -Path $jre_loc)) {
        Write-Host "Downloading a local copy of JRE $jre_ver.";
        $sys_arch = $Env:PROCESSOR_ARCHITECTURE;
        $req_arch = $null;

        if ($sys_arch -Match "64") {
            $req_arch = "x64";
        } elseif ($sys_arch -Match "86") {
            $req_arch = "x86";
        } elseif ($sys_arch -Match "ARM") {
            $req_arch = "aarch64";
        }

        if ($null -eq $req_arch) {
            throw "FATAL: Architecture $sys_arch cannot be matched to a JRE version. Exiting.";
        }

        Write-Host "Determined system architecture as $sys_arch. Mapped to JRE architecture $req_arch.";
        Write-Host "Downloading...";

        Invoke-WebRequest -Uri "https://api.adoptopenjdk.net/v3/binary/latest/$jre_ver/ga/windows/$req_arch/jre/hotspot/normal/adoptopenjdk?project=jdk" -OutFile ($jre_loc + ".zip");

        Write-Host "Download completed. Unzipping and extracting to $dl_basename/."
        Expand-Archive -Force -LiteralPath ($jre_loc + '.zip') -DestinationPath $pwd.Path;

        $ver_lookup_url = "https://api.adoptopenjdk.net/v3/info/release_versions?heap_size=normal&image_type=jdk&page=0&page_size=1&project=jdk&release_type=ga&sort_method=DEFAULT&sort_order=DESC&vendor=adoptopenjdk&version=%5B11%2C12%29";
        $dl_jre_ver = (Invoke-RestMethod -Uri $ver_lookup_url).versions[0].openjdk_version;

        $null = New-Item -Path $pwd.Path -Name $dl_basename -ItemType "directory";
        $dl_jre_inner = ($pwd.Path + "/jdk-" + $dl_jre_ver + "-jre");
        Get-ChildItem -Path $dl_jre_inner -Recurse | Move-Item -Destination $jre_loc;
        Remove-Item $dl_jre_inner;
        Write-Host "Extraction completed.";
    } else {
        Write-Host "Local copy of JRE $jre_ver located.";
    }
    Write-Host "Running OpenDS from local JRE $jre_ver...`n"
    Start-Process -WindowStyle Hidden ($jre_loc + "/bin/java.exe") -ArgumentList '-jar', $ods_jar_name;
} else {
    Write-Host "Java installation detected on PATH. Running OpenDS...`n";
    Start-Process -WindowStyle Hidden java -ArgumentList '-jar', $ods_jar_name;
}