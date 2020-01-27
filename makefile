
all : reset compile q_combine run

reset:
	@reset

run: runArg

runFull: okTerminal justRun resetTerminal

justRun:
	@java -cp "out/" vi_limited.Vim

runArg:
	@java -cp "out/" vi_limited.Vim ~/help.txt




okTerminal:
	/bin/sh -c "stty -icanon min 1 </dev/tty"

resetTerminal:
	/bin/sh -c "stty icanon </dev/tty"


compile: clean
	@echo "------------------------------------"
	@echo "compiling project.."
	@mkdir out && echo "created output folder" || echo "out folder exists"
	@javac src/vi_limited/Vim.java -cp "./src/" -g -d out/ -Werror -Xlint -Xmaxerrs 3
	@echo "compile done!"
	@echo "-------------------------------------"


clean:
	@echo "cleaning project folder.."
	@find . -name '*.class' -delete
	@echo "deleted class files"
	@find . -name '*.exe' -delete
	@echo "deleted exe files"

	@find . -wholename './out/log*.txt' -delete
	@echo "deleted lof file in out folder"
	
	
	@find . -wholename './out/vi_limited/piecetable' -delete
	@echo "deleted piece table folder"

	@find . -wholename './out/vi_limited' -delete
	@echo "deleted vi_limited folder"

	@find . -name 'out' -delete
	@echo "deleted out folder"

clean_log:
	@find . -name 'log*.txt' -delete
	@echo "deleted log file"
	@echo "log file :" > log1.txt

clean_doc:
	@rm -r ./doc/* || echo "doc directory is clean"
	@find . -name 'doc' -delete
	@echo "deleted doc folder"

clean_all: clean clean_doc clean_log

doc:
	javadoc src/vi_limited/*.java  -Xdoclint:none  -d ./doc

q_combine: clean
	@./q_combiner.py
	@javac ./quera/Vim.java
