import requests
from datetime import datetime

def downloadPageTime(url):
    #获取网页生成时间
    rqq = requests.get(url)
    dd = rqq.headers['Last-Modified']
    time = datetime.strptime(dd, '%a, %d %b %Y %H:%M:%S GMT')#GMT时间格式转正常时间格式
    timestr = time.strftime('%Y-%m-%d')#datetime转string
    return timestr
