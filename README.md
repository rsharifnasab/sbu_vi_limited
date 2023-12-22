## My DS project for DS class with DR.Ebrahim moghaddam in fall 98

### VIM editor written in java

### Rules
1. Not using any library (import or include)
2. Just with cpp or java
3. More info in TODO.pdf

### TODO
+ Update the iterator as you go (for insert with iterator)
+ delete
+ w : next word
+ b : prev word


### bugs
+ Press enter in the last line in command mode 
+ no file to save -> dialog box

### how to run: 
+ To test you can run: 
	
```bash
make
```
Be careful, this command will open the `Vim.java` from `src` folder to edit! 
	
	
	
+ If you want to create the jar file:
```bash
make create_jar
java -jar Vim.jar path/to/filename.txt
```
