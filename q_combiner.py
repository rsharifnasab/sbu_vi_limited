#!/usr/bin/python3

from os import listdir,mkdir
from os.path import isfile, join
from os.path import exists,isdir
from shutil import rmtree
from re import sub as replace_by_regex
from os import system as shelll

def greet():
    greet_text = """this script will combine all java files
and make one compilable java file for you"""
    print(greet_text)

def find_java_files(mypath):
    return [f for f in listdir(mypath) if isfile(join(mypath, f)) and f.endswith(".java")]

def clean_java(file_add):
    class_text = open(file_add).read()

    class_text = class_text.replace("package vi_limited;","")
    class_text = class_text.replace("import java.io.*;","")
    class_text = class_text.replace("import java.io.File;","")

    class_text = class_text.replace("public class","class"); # change public classes to normal classes
    class_text = class_text.replace("public enum","enum"); # change public enums to normal

    return class_text

def clean_folder(mypath):
    try:
        if exists(mypath) and isdir(mypath):
            rmtree(mypath)
        mkdir(mypath)

    except Exception as e:
            print(e)
            exit(1)

def write_fun(file_add):
    shelll( f""" echo "/*" >> {file_add} """ )
    fun_text = "dear TA, im just submitting this for testing, don't take it serious"
    shelll(f"""cowsay "{fun_text}" >> {file_add}""")
    shelll( f""" echo "*/" >> {file_add} """ )


def write_file(file_add,text):
    shelll(f"""echo "" > {file_add} """)
    write_fun(file_add)
    print(f"writing to {file_add}")
    f = open(file_add, "a") #append mode
    f.write(text)
    f.close()

source_folder = "./src/main/java/vi_limited/"
quera_folder = "./out/quera/"
out_file = quera_folder + "Vim.java"



def get_combined_text(cleaned_javas):
        text = "\n/************ next file **********/\n".join(cleaned_javas)
       # text = "import java.io.*;\n" + text
        text = text.replace("class Vim", "public class Vim")
        return text

greet()
files = find_java_files(source_folder)
cleaned_javas = [ clean_java(source_folder+java_file) for java_file in files ]
combined_text = get_combined_text(cleaned_javas)
clean_folder(quera_folder)
write_file(out_file,combined_text)
print("done")
