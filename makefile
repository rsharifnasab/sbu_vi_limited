quick: reset compile run

all: reset compile q_combine create_jar run

reset:
	@reset

run: runArg1

runFull: okTerminal justRun resetTerminal

justRun:
	@java -cp "out/" vi_limited.Vim

runArg1:
	@java -cp "out/" vi_limited.Vim src/vi_limited/Vim.java


okTerminal:
	/bin/sh -c "stty -icanon min 1 </dev/tty"

resetTerminal:
	/bin/sh -c "stty icanon </dev/tty"


compile: clean
	@echo "------------------------------------"
	@echo "compiling project.."
	@mkdir out && echo "created output folder" || echo "out folder exists"
	@javac src/vi_limited/Vim.java -cp "./src/" -g -d out -Werror -Xlint -Xmaxerrs 3
	@echo "compile done!"
	@echo "-------------------------------------"


clean:
	@echo "cleaning project folder.."

	@find . -name '*.class' -delete
	@echo "deleted class files"

	@find . -name '*.exe' -delete
	@echo "deleted exe files"

	@find . -name '*.jar' -delete
	@echo "deleted jar files"

	@find . -wholename './out/log*.txt' -delete
	@echo "deleted lof file in out folder"
		
	@find . -wholename './out/vi_limited' -delete
	@echo "deleted vi_limited folder"

	@find . -name 'out' -delete
	@echo "deleted out folder"


clean_doc:
	@rm -r ./doc/* || echo "doc directory is clean"
	@find . -name 'doc' -delete
	@echo "deleted doc folder"

clean_all: clean clean_doc

doc:
	javadoc src/vi_limited/*.java  -Xdoclint:none  -d ./doc

q_combine: clean
	@./q_combiner.py
	@javac ./quera/Vim.java
	
create_jar: compile
	cd out && jar cfe ../Vim.jar vi_limited.Vim   vi_limited/*.class && cd ..

run_jar : 
	java -jar Vim.jar

jar : create_jar run_jar