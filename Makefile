SH_JAR=./auto_gen_jar.sh
SH_DEMO=./auto_clear_redundant.sh
LIB=zzsdk-lib.jar
DEMO=zzsdk-demo.tar.gz

all: release

clean: 
	ant clean
	rm ${LIB} ${DEMO} *-debug.apk
	
demo:
	${SH_DEMO}

jar:
	${SH_JAR}
	
release:
	make jar
	make demo
	@echo "生成文件：MainActivity-debug.apk zzsdk-demo.tar.gz zzsdk-lib.jar"


