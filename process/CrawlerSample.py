from selenium import webdriver
import pathlib
import hashlib
import re
import requests
from datetime import datetime
from time import sleep


'''*****************************************************************
# 保存页面并遍历子页面
# ***************************************************************'''
def getSubPage(webdriver):
    # 保存网页文件，文件名为URL的SHA1编码
    url = driver.current_url  #获取当前路径
    html = driver.page_source  # 获取当前网页源码
    
    #获取网页生成时间
    rqq = requests.get(url)
    dd = rqq.headers['Last-Modified']
    time = datetime.strptime(dd, '%a, %d %b %Y %H:%M:%S GMT')#GMT时间格式转正常时间格式
    timestr = time.strftime('%Y-%m-%d')#datetime转string

    #字符加密功能/摘要算法，把任意长度的数据转换为一个长度固定的数据串
    sha1 = hashlib.sha1()  #建立sha1对象
    sha1.update(html.encode("utf8"))  #传入需要加密的信息，加密的字符串类型为二进制编码，直接加密字符串会报错，使用encode进行转换

    path = pathlib.Path("d:/timepages/" + sha1.hexdigest() + ".txt")  #创建路径，本代码为创建txt文件sha1.hexdigest()方法进行加密处理
    sleep(1)  # 间隔一秒继续，避免触发反爬机制
    # 如果网页文件不存在（该网页没有被爬过）
    if not path.exists():
        # 保存渲染后的HTML文本，若须保存网页中的图片、附件或截图，可在此添加代码
        with open(str(path), "w", encoding="utf-8") as f:
            f.write(url + "\r\n" + timestr + "\r\n" + html)    # 构造网页内容，将URL放在第一行
            f.close()
        print("成功 " + url)
        # 遍历子网页（递归）
        for subLink in getSubURLFromHTML(html, url):
            try:
                webdriver.get(subLink)  # 打开子页面
                getSubPage(webdriver)   # 保存子页面并遍历孙页面
            except:
                print("失败 " + subLink)
    else:
        print("已存在 " + url)
        

'''*****************************************************************
# 解析HTML中的有效URL
# ***************************************************************'''
def getSubURLFromHTML(html, currUrl):
    homeUrl = re.compile(r"^https?://[^/]+").findall(currUrl)[0]    # 提取本站首页URL
    fatherUrl = re.compile(r"^https?://[^?]+/").findall(currUrl)[0] # 提取本页URL父目录
    subUrls = re.compile(r"(?<=href=[\"'])[^\"']+").findall(html)   # 提取所有子URL
    urls = []
    for url in subUrls:
        # 忽略样式表JavaScript和位置点标记
        if url.endswith(".css") or ".css?" in url or url.startswith("javascript:") or url.startswith("mailto:") or url.startswith("#"):
            continue
        # 不含域名的路径
        tmpUrl = ""
        if(not url.startswith("http")):
            if url.startswith("/") or url.startswith("~"):    # 站内绝对路径
                tmpUrl = homeUrl + url
            else:                       # 相对路径
                tmpUrl = fatherUrl + url
        # 含域名的绝对路径
        else:
            tmpUrl = url

        # 去掉../符号
        while ".." in tmpUrl:
            tmpUrl = re.sub(r"/[^/]+/\.\.", "", tmpUrl)
        # 装入路径，忽略超出限制范围和已爬取的网页
        if tmpUrl != "" and len(re.compile(r"^https?://" + limited).findall(tmpUrl)) > 0 and not tmpUrl in crawedURLs:
            urls.append(tmpUrl)
            crawedURLs.append(tmpUrl)
        #else:
        #    print("忽略 " + url)
    # 提取所有
    return urls

'''*****************************************************************
# 爬虫入口
# ***************************************************************'''
driver = webdriver.Chrome('C:\Program Files\Google\Chrome\Application\chromedriver.exe')

#超时设置/等待时间过长自动停止
driver.set_page_load_timeout(8)  # 设置页面加载超时
driver.set_script_timeout(8)  # 设置页面异步js执行超时

# 入口为计科院首页，限制只爬取计科院网页
enterUrl = "https://www.swpu.edu.cn/"    # 爬虫入口地址
limited = "www.swpu.edu.cn"               # 爬虫域名限制
driver.get(enterUrl)    # 打开入口页面
crawedURLs = [enterUrl] # 将入口页面URL放入到已爬取队列
getSubPage(driver)      # 保存入口页面并打开其子页面
driver.quit()           # 退出驱动程序