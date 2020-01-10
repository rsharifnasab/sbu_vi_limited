
all : reset compile run

reset:
	@reset

run: okTerminal internalRun resetTerminal

internalRun:
	@java -cp "src/" Vim


okTerminal:
	/bin/sh -c "stty -icanon min 1 </dev/tty"
	
resetTerminal:
	/bin/sh -c "stty icanon </dev/tty"	


compile:
	@echo "compiling project.."
	@javac src/Vim.java
	@echo "compile done!"
	@echo "-------------------------------------"


clean:
	@echo "cleaning project folder.."
	@find . -name '*.class' -delete
	@echo "deleted class files"
	@find . -name '*.exe' -delete
	@echo "deleted exe files"

g:
	git status
	git add -A
	git commit
