javac gitlet/*.java
java gitlet.Main init
java gitlet.Main add wug.txt
java gitlet.Main commit "added wug"
#java gitlet.Main checkout 9af89f092ada43c561b74a2c27654bc750d2d628 -- test.sh.txt
java gitlet.Main log