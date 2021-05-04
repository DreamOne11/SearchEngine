import re

content = '''苹果，是绿色的
橙子，是橙色的
香蕉，是黄色的
乌鸦，是黑色的
'''
'''p = re.compile(r'.色')
for one in  p.findall(content):
    print(one)

print(type(one))
print(type(p.findall(content)))'''

content1 = '''苹果，是绿色的 
橙子，是橙色的
香蕉，是黄色的
乌鸦，是黑色的
猴子，'''

import re
p = re.compile(r'，.*')  #若不加*只有.  则第五行匹配不上，因为.表示一个字符 但是第五行‘，’后面没字符了
for one in  p.findall(content1):
    print(one)