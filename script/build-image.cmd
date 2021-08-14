CALL "C:\Program Files (x86)\Microsoft Visual Studio\2019\Community\VC\Auxiliary\Build\vcvars64.bat"

native-image^
    --no-fallback^
    -jar "./build/libs/%1"
