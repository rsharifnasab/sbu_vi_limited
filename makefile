
run:
	@echo "running application.."
	@java -cp "src/" Vim
	@echo "program finished"

compile:
	@echo "compiling project.."
	@javac src/Vim.java
	@echo "compile done!"


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
