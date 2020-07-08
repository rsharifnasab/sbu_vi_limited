**my DS project for DS class with DR.Ebrahim moghaddam in fall 98**

#### project is : VIM editor(!) explicity written in java or CPP

the rules are :
1. no using any library (import or include)
2. just with cpp or java
3. more info in TODO.pdf
 
### TODO
+ update iterator as you go (for insert with iterator)
+ delete
+ w : kalame badi
+ b : kalame ghabli 


### bugs
+ press enter in last line in command mode 
+ add 12 lines : eveything crash!
+ no file to save -> dialog box

# how to run: 
+ you can simply run : `make` 
	be careful, this command will open the Vim.java from src folder to edit! 
+ if you want to use jar file:
        ```bash
            make create_jar
            java -jar Vim.jar path/to/filename.txt
        ```
