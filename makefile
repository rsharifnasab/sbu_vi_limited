
all : reset compile internalRun

reset:
	@reset

run: okTerminal internalRun resetTerminal

internalRun:
	@java -cp "out/" Vim


okTerminal:
	/bin/sh -c "stty -icanon min 1 </dev/tty"
	
resetTerminal:
	/bin/sh -c "stty icanon </dev/tty"	


compile: clean
	@echo "------------------------------------"
	@echo "compiling project.."
	@mkdir out && echo "created output folder" || echo "out folder exists"
	@javac src/Vim.java -g -d out/ -Werror -Xlint -Xmaxerrs 3
	@echo "compile done!"
	@echo "-------------------------------------"


clean:
	@echo "cleaning project folder.."
	@find . -name '*.class' -delete
	@echo "deleted class files"
	@find . -name '*.exe' -delete
	@echo "deleted exe files"
	@find . -name 'out' -delete

g:
	git status
	git add -A
	git commit
