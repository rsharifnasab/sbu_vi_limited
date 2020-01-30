
all: reset compile q_combine create_jar run

reset:
	@reset

run: runArg1

justRun:
	@java -cp "out/" vi_limited.Vim

runArg1:
	@java -cp "out/" vi_limited.Vim ~/Vim.java

runArg2:
	@java -cp "out/" vi_limited.Vim ~/a.txt


clean_doc:
	@rm -r ./doc/* || echo "doc directory is clean"
	@find . -name 'doc' -delete
	@echo "deleted doc folder"

doc:
	javadoc src/vi_limited/*.java  -Xdoclint:none  -d ./doc

q_combine:
	@./q_combiner.py
	@javac ./quera/Vim.java

run_jar: 
	java -jar Vim.jar
