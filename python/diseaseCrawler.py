#coding:utf-8

import urllib.request

#从科室疾病url获取疾病列表
def getDisease(url):
	data = urllib.request.urlopen(url).read()
	data = data.decode('gb2312','ignore')
	print(data)
	start = data.find('<ul class="mulu-body">')
	end=data.find('<ul class="mulu-body2">')
	content = data[start:end]
	index=content.find('<a')
	res=[]
	while index != -1:
		nstart = content.find('>',index)+1
		nend = content.find('<',nstart)
		dname=content[nstart:nend]
		res.append(dname)
		content = content[content.find('</a>')+4:]
		index=content.find('<a')
	return res


# res=getDisease("http://jb39.com/neike/")
# print(res)

#从文本文件读取科室和具体疾病url的对应关系
keshi=""
f = open("C:\\Users\\huqj\\Desktop\\keshi.txt")
for fl in f:
	keshi=keshi+fl

print("科室："+keshi)
dList = list()
index=keshi.find("<li>")
rooturl="http://jb39.com"
while index != -1:
	d=dict()
	hstart = keshi.find('href="')+6
	hend = keshi.find('">',hstart)
	d["url"]=rooturl+keshi[hstart:hend]
	nstart = hend+2
	nend = keshi.find("</a>",nstart)
	d["name"]=keshi[nstart:nend]
	dList.append(d)
	keshi = keshi[keshi.find('</li>')+5:]
	index=keshi.find("<li>")

print(dList)

result = open("C:\\Users\\huqj\\Desktop\\result.txt",'w')
num=1
for d in dList:
	res=getDisease(d['url'])
	for r in res:
		result.write(str(num)+","+d['name']+","+r+"\n")
		num=num+1

result.close()
	

