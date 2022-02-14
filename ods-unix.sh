#!/usr/bin/bash

# OpenDS install and run script for Linux and macOS
# Will download .jar file from latest GitHub release if not found
# Downloads local JRE copy if java not on PATH
# Configurable JRE version (jre_ver)

# Required: curl, echo, grep, awk, which, tar, mv, rm

jre_ver="11"
dl_basename="ods-jre$jre_ver"

bash -c '(sleep 2; kill $$) & exec nc -z 1.1.1.1 80' &> /dev/null
if [[ $? -ne 0 ]]; then
    ods_find=$(find . -maxdepth 1 -name 'open-ds*')
    if [[ -z $ods_ver && ! -z $ods_find ]]; then
        echo "No internet connection but other OpenDS JAR version found."
        if [[ $(which java) ]]; then
            echo -e "Java installation detected on PATH. Running OpenDS...\n"
            java -jar $ods_find
        elif [[ -d $dl_basename ]]; then
            echo -e "Running OpenDS from local JRE $jre_ver...\n"
            $dl_basename/bin/java -jar $ods_find
        else
            echo "No Java installation detected and no internet connection. Exiting."
        fi
    else
        echo "No internet connection and no OpenDS Jar found. Exiting."
    fi
    exit 0
fi

gh_latest_release=$(curl -s -m 2 "https://api.github.com/repos/Boomaa23/open-ds/releases/latest")
ods_ver=$(echo -e "$gh_latest_release" | grep -m 1 "tag_name" | awk "{ print substr (\$0, 16, 6) }")
ods_jar_name="open-ds-$ods_ver.jar"

if [[ ! -f "$ods_jar_name" ]]; then
    echo "No $ods_jar_name detected. Downloading latest release from GitHub."
    jar_url=$(echo -e "$gh_latest_release" | grep -m 1 "browser_download_url" | awk '{ gsub(/"/, "", $0); print substr ($0, 29) }')
    curl -s -L -m 2 $jar_url >> $ods_jar_name
fi

if [[ ! $(which java) ]]; then
    echo "No Java installation detected on the PATH."
    if [[ ! -d $dl_basename ]]; then
        echo "Downloading a local copy of JRE $jre_ver."
        sys_arch=$(uname -m)
        req_arch=""
        case $sys_arch in
            *"x86_64"*)
                req_arch="x64"
                ;;
            *"x86"*)
                req_arch="x86"
                ;;
            *"armv6"* | *"armv7"*)
                req_arch="arm"
                ;;
            *"armv8"*)
                req_arch="aarch64"
                ;;
        esac

        if [[ -z "$req_arch" ]]; then
            echo "FATAL: Architecture $sys_arch cannot be matched to a JRE version. Exiting."
            exit 1
        fi

        kernel_ver=$(uname -s)
        unix_type=""
        case $kernel_ver in
            *"Linux"*)
                unix_type="linux"
                ;;
            *"Darwin"*)
                unix_type="mac"
                ;;
        esac

        if [[ -z "$unix_type" ]]; then
            echo "FATAL: Unix type $kernel_ver is invalid. Exiting."
            exit 1
        fi
            
        echo "Determined system architecture as $sys_arch. Mapped to JRE architecture $req_arch."
        echo "Downloading..."

        curl -s -L -m 2 "https://api.adoptopenjdk.net/v3/binary/latest/$jre_ver/ga/$unix_type/$req_arch/jre/hotspot/normal/adoptopenjdk?project=jdk" >> $dl_basename.tar.gz
        echo "Download completed. Unzipping and extracting to $dl_basename/."
        tar -xf "$dl_basename.tar.gz"
        jre_def_dir=$(tar -tf $dl_basename.tar.gz --exclude '*/*' | rev | cut -c 2- | rev)
        mv "$jre_def_dir" "$dl_basename"
        rm "$dl_basesname.tar.gz"

        echo "Extraction completed."
    else
        echo "Local copy of JRE $jre_ver located."
    fi
    echo -e "Running OpenDS from local JRE $jre_ver...\n"
    $dl_basename/bin/java -jar $ods_jar_name
else
    echo -e "Java installation detected on PATH. Running OpenDS...\n"
    java -jar $ods_jar_name
fi