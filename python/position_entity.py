# coding: utf-8
import urllib.request
import json
import time 

keys=['Ft8DG0uaM37QHopNA0U3UW43NdLKIyiE','G3Qz7gRL1Ie59RxA4zwjOffebOIRZbR2','GpTMhCzysDnj632F3x14G8QbOBGyuMKr','BOLGzrgYOYYdtbKdljGpsAg5xTzVlcXk','A1in4pEnxlHVMI2GdmTmTfU3oSNzcam0']
link1 = 'http://api.map.baidu.com/place/v2/search?query='#后接标签
link2 = '&tag='#后接标签，同上
link3 = '&region='#接带市的区县级
link4 = '&output=json&ak='
link5 = '&qq-pf-to=pcqq.c2c'

fInput2 = open('quxian.txt')
qx_list = []
for i in fInput2:
	qx_list.append(i.replace('\n',''))
fInput2.close()
print(qx_list)

fInput1 = open('tag.txt')
tag_list = []
for i in fInput1:
	tag_list.append(i.replace('\n',''))
fInput1.close()
print(tag_list)

f = open('output1.txt','w',encoding='UTF-8')
try:
	index=0
	for i in qx_list:
		print(i)
		i1 = urllib.parse.quote(i)
		ij=0
		while ij < len(tag_list):
			j = tag_list[ij]
			print(j)
			try:
				index = index + 1						
				j = urllib.parse.quote(j)
				time.sleep(0.8)
				apiStr = link1 + j + link2 + j + link3 + i1 + link4+keys[index%len(keys)]+link5
				print("请求url："+apiStr)
				response = urllib.request.urlopen(apiStr)
				apiCont = response.read().decode('utf-8')
				obj = json.loads(apiCont)
				
				print("请求结果："+str(obj))

				if obj['status'] !=0 :
					if obj['status'] == 4:
						# 尝试换key
						for _in in range(len(keys)):
							try_key = keys[_in]
							apiStr = link1 + j + link2 + j + link3 + i1 + link4+keys[index%len(keys)]+link5
							response = urllib.request.urlopen(apiStr)
							apiCont = response.read().decode('utf-8')
							obj = json.loads(apiCont)
							if obj['status']!=4:
								index=_in-1
								continue
						else:
							print("api调用出受限，暂停一小时")
							time.sleep(3600)
							continue
					else:
						print("api调用出现未知问题："+str(obj['status']))
						continue

				
				for m in obj['results']:
					try:
						#print(str(i.replace('\n','')))
						#print(m['area'])
						judge = str(i.replace('\n','')).find(m['area'])

						print("是否是本地区的地点："+str(judge))

						try:
							print("包含街道信息："+m['street_id'])
							continue
						except:
							print("不包含街道，可取")
							pass

						if judge!=-1:
							f.write('name:'+ str(m['name']) + ' longitude:' + str(m['location']['lng']) + ' latitude:' + str(m['location']['lat']) + ' fatherID:'+ i.replace('\n','') +' uid:' + str(m['uid']) + '\n')
							#print ('写入')
						else:
							print("区县名不符或者是街道")
					except Exception:
						print('没有area,不写')	

				ij=ij+1
			except Exception as e:
				ij=ij+1
				print(e)
				continue
except Exception as e:
	print(e)
	print("程序出错！！")
	
f.close()
