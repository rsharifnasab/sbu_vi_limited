
all : reset compile run

reset:
	@reset

run:
	@java -cp "src/" Vim

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
