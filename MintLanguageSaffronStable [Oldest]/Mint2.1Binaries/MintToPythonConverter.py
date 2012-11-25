# Pyella Version 0.1 - Written in Python 3
# @author Oliver Chu (known as Jiangcheng Chu in some circles)
# Pyella is a Mint to Python translator.
# Mint is a high level language that resembles JavaScript and Ruby.
# Python is a very popular language that supports a large number of
# built-in libraries.

import sys

file_functions = """
def strToFile(megastr, filepath):
    fileobj = open(filepath, "wb")
    fileobj.write(megastr.encode())
    fileobj.close()

def fileToStr(filepath):
    try:
        fileobj = open(filepath, "rb")
    except IOError:
        error("Cannot find file: " + filepath)
    else:
        megastr = ""
        chunk = ""
        while True:
            # Decode each chunk of bytes into chars.
            chunk = fileobj.read(1024).decode()
            megastr += chunk
            if len(chunk) < 1024:
                break
        fileobj.close()
        return megastr

"""

# We test if the object we are printing is iterable.
# Note that indexTable has return None implied.
required_code = """# Mint Program in Python 3 - Produced by Pyella.py
def is_iterable(data):
    try:
        if type(data) is str:
            raise TypeError
        for _ in data:
            return True
    except TypeError:
        return False

def deep_contains(data, element):
    if not is_iterable(data):
        return False
    if element in data:
        return True
    trues_and_falses = []
    for elem in data:
        trues_and_falses.append(deep_contains(elem, element))
    return True in trues_and_falses

def indexTable(dictionary, index):
    if index < 0:
        index += len(dictionary)
    for k in dictionary:
        if index < 1:
            return [k, dictionary[k]]
        index -= 1

def xprint(obj, ending):
    if is_iterable(obj):
        result = None
        if deep_contains(obj, None) or deep_contains(obj, True) or deep_contains(obj, False):
            result = str(obj).replace("None", "null").replace("True", "true").replace("False", "false")
        if type(obj) in [list, dict, tuple, set]:
            result = result.replace(chr(39), chr(34))
        print(result, end=ending)
    else:
        if obj is None:
            print("null", end=ending)
        elif obj is True:
            print("true", end=ending)
        elif obj is False:
            print("false", end=ending)
        else:
            print(obj, end=ending)

def print_all(*objs):
    do_show = False
    if len(objs) > 0 and objs[0] == "@MODE?SHOW#":
        do_show = True
        objs = objs[1:]
    for obj in objs:
        xprint(obj, str() if do_show else chr(10))
"""

def str_to_file(filepath, megastr):
    fileobj = open(filepath, "wb")
    fileobj.write(megastr.encode())
    fileobj.close()

def file_to_str(filepath):
    try:
        fileobj = open(filepath, "rb")
    except IOError:
        error("Cannot find file: " + filepath)
    else:
        megastr = ""
        chunk = ""
        while True:
            # Decode each chunk of bytes into chars.
            chunk = fileobj.read(1024).decode()
            megastr += chunk
            if len(chunk) < 1024:
                break
        fileobj.close()
        return megastr

def starts_with_any(text, items):
    for item in items:
        if text.startswith(item):
            return True
    return False
    
def count_indentation(ln):
    if len(ln) > 0 and ln[0] == ' ':
        return 1 + count_indentation(ln[1:])
    return 0

def translate_mint_to_python(mintCode):
    mintCode = mintCode.replace(";", "\n")
    lines = mintCode.split("\n")
    saved_lines = []
    for line in lines:
        if "end" not in line:
            indent = count_indentation(line)
            line = line.strip()
            line = line.replace("otherwise", "else")
            line = line.replace("true", "True")
            line = line.replace("false", "False")
            line = line.replace("null", "None")
            line = line.replace("anonymous", "lambda")
            line = line.replace("when", "if")
            line = line.replace("++", " += 1")
            line = line.replace("is", "==")
            line = line.replace("equals", "==")
            line = line.replace("//", "#")
            line = line.replace("/*", "#")
            line = line.replace("else if", "elif")
            line = line.replace("length(", "len(")
            line = line.replace("size(", "len(")
            if "*/" in line:
                line = "#" + line
            # Comment out the following 3 lines to get a Python 2 converter.
            if "print " in line:
                line = line.replace("print ", "print_all(")
                line += ")"
            if "show " in line:
                line = line.replace("show ", 'print_all("@MODE?SHOW#", ')
                line += ")"
            if line.startswith("sub "):
                line = line.replace("sub", "def")
            if line.startswith("repeat "):
                line = line.replace("repeat ", "for _unused_variable_ in range(")
                line += ")"
            if "each" in line or "of" in line:
                line = line.replace(" each", "")
                line = line.replace("of", "in")
            control_flow = ["if ", "if(", "def ", "while ", "while(",
                            "for", "for(", "else", "elif"]
            if starts_with_any(line, control_flow):
                line += ":"
            saved_lines += [(" " * indent) + line]
    return "\n".join(saved_lines)
    
def run_mint(program_filename):
    internal_code = file_to_str(program_filename)
    try:
        pycode = translate_mint_to_python(internal_code)
        if "fileToStr" in pycode or "strToFile" in pycode:
            pycode = file_functions + pycode
        str_to_file(program_filename.replace(".mint", "") + ".py", required_code + pycode)
    except BaseException as e:
        print("Error: " + str(e))
    
command_line_args = sys.argv[1:]
if len(command_line_args) > 0:
    for program in command_line_args:
        run_mint(program)
else:
    run_mint("program.mint")
 