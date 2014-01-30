#!/bin/bash

cd build
java audiosteganography.Encoder "test message" /Users/kklin/development/java-audio-steganography/test_file.wav
echo ========================================================
java audiosteganography.Decoder /Users/kklin/development/java-audio-steganography/test_file-Encoded.wav