FROM debian:bookworm-slim

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get install -y --no-install-recommends curl

RUN curl http://www.mirbsd.org/~tg/Debs/sources.txt/wtf-bookworm.sources -o wtf-bookworm.sources && \
    mkdir -p /etc/apt/sources.list.d && \
    mv wtf-bookworm.sources /etc/apt/sources.list.d/

RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential \
    libc6-dev-arm64-cross \
    gcc-mingw-w64 \
    openjdk-8-jdk-headless \
    gcc \
    make \
    linux-headers-amd64 \
    wget

RUN wget https://musl.cc/aarch64-linux-musl-cross.tgz &&  \
    tar -xvzf aarch64-linux-musl-cross.tgz && \
    ln -s /aarch64-linux-musl-cross/bin/aarch64-linux-musl-gcc /usr/bin/aarch64-linux-musl-gcc

RUN wget https://github.com/mstorsjo/llvm-mingw/releases/download/20250613/llvm-mingw-20250613-ucrt-ubuntu-22.04-x86_64.tar.xz && \
    tar -xf llvm-mingw-20250613-ucrt-ubuntu-22.04-x86_64.tar.xz && \
    mv llvm-mingw-20250613-ucrt-ubuntu-22.04-x86_64 llvm-mingw

ENV PATH="/llvm-mingw/bin:$PATH"

WORKDIR "/opends"
