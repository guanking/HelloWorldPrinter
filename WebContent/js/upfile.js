// JavaScript Document
function initMap(mapID) {
	map = new BMap.Map(mapID);
	map.centerAndZoom(new BMap.Point(116.404, 39.915), 11);
	map.enableScrollWheelZoom(true);
	getCurrentPosition();
	map.addControl(new BMap.NavigationControl());
	var overView = new BMap.OverviewMapControl();
	var overViewOpen = new BMap.OverviewMapControl({
		isOpen : true,
		anchor : BMAP_ANCHOR_BOTTOM_RIGHT
	});
	map.addControl(overView);
	map.addControl(overViewOpen);
	map.addControl(new BMap.ScaleControl({
		anchor : BMAP_ANCHOR_BOTTOM_LEFT
	}));
	function ZoomControl() {
		this.defaultAnchor = BMAP_ANCHOR_TOP_RIGHT;
		this.defaultOffset = new BMap.Size(10, 10);
	}
	ZoomControl.prototype = new BMap.Control();
	ZoomControl.prototype.initialize = function(map) {
		var div = document.createElement("div");
		var image = document.createElement('img');
		image.style.width = 44;
		image.style.height = 22;
		image.src = "images/ruler.jpg";
		div.appendChild(image);
		div.style.cursor = "pointer";
		div.style.border = "1px solid gray";
		div.style.backgroundColor = "white";
		div.onclick = function(e) {
			(new BMapLib.DistanceTool(map)).open();
		};
		map.getContainer().appendChild(div);
		return div;
	}
	var myZoomCtrl = new ZoomControl();
	map.addControl(myZoomCtrl);
}
function choicePrint(index){
	window.location='print.html?index='+index;
}
function searchPlace(text) {
	try{
		var len=$('#printersList').children().length;
		while(len>1){
			$('#printersList').children().eq(len-1).remove();
			len=$('#printersList').children().length;
		}
		var message=window.localStorage.getItem('printerMessage');
		message=JSON.parse(message);
		var tempDistance=0;
		len=message.length;
		var temp,address,top,point;
		for(var index=0;index<len;index++){
			if(index==0){
				temp=$('#printersList').children().eq(0);
				temp.show();
			}else{
				temp=$('#printersList').children().eq(0).clone();
			}
			temp.children().eq(0).css({'background':'url(images/home/markers.png) -23px '+-25*index+'px no-repeat'});
			temp.children().eq(1).children().eq(0).children().eq(0).text(message[index]['name']);//name
			address=message[index]['address'];
			address=address['province']+address['city']+address['district']+address['street']+address['streetNumber'];
			temp.children().eq(1).children().eq(1).children().eq(1).text(address);//address
			temp.children().eq(1).children().eq(2).children().eq(1).text(message[index]['phone']);//phone
			point=message[index]['BMapPoint'];
			tempDistance=message[index]['distance'];
			tempDistance=(tempDistance/1000.0).toFixed(2);
			addMark(new BMap.Point(point.lng,point.lat),'名称：'+message[index]['name'],'价格(元/张)：单页:+'+message[index]['signalPrice']+'<span style="margin-left:10px;">双页:+'+message[index]['doublePrice']+'</span><span style="margin-left:10px;">黑白:+'+message[index]['whiteAndBlackPrice']+'</span><span style="margin-left:10px;">彩色:+'+message[index]['colorPrice']+'<br>距离：'+tempDistance+'公里'+'<br>电话：'+message[index]['phone']+'<br>地址：'+address+'<br><span class="clickButtonToPrint"  onclick="choicePrint('+index+');">去打印</span>');
			temp.children().eq(1).children().eq(0).children().eq(1).attr('index',index);
			temp.children().eq(1).children().eq(0).children().eq(1).bind('click',function(){//
				var evObj = document.createEvent('MouseEvents');
        		evObj.initMouseEvent( 'click', true, true, window, 1, 12, 345, 7, 220, false, false, true, false, 0, null );
       		 	markers[$(this).attr('index')].dispatchEvent(evObj);
			});
			if(index!=0){
				$('#printersList').append(temp);
			}
		}
	}catch(e){
		alert(e);
	}
}
function addMark(point,title,message){
	var marker = new BMap.Marker(point);  // 创建标注
	markers.push(marker);
	map.addOverlay(marker);              // 将标注添加到地图中
	var opts = {
	  width : 400,
	  height: 125,  
	  title :title 
	}
	var infoWindow = new BMap.InfoWindow(message, opts); 
	marker.addEventListener("click", function(){          
		map.openInfoWindow(infoWindow,point); //开启信息窗口
	});
}
function getPrinterMessage(){
	var pms={};
	pms['latitude']=30.322066;
	pms['longitude']=120.344226;
	pms['phone']='18405813906';
	pms['colorPrice']=2;
	pms['whiteAndBlackPrice']=1;
	pms['signalPrice']=1;
	pms['doublePrice']=2;
	pms['name']='HP Layert 10223';
	pms1=copy(pms);
	pms1['longitude']=120.144225;
	pms2=copy(pms);
	pms2['longitude']=120.244224;
	pms3=copy(pms);
	pms3['longitude']=120.444227;
	pms4=copy(pms);
	pms4['longitude']=120.544218;
	return [pms,pms1,pms2,pms3,pms4];
}
function initPrintrtMessage(){
	/*var locations=getPrinterMessage();
	var curPosition=window.localStorage.getItem('currentLocation');
	curPosition=JSON.parse(curPosition);
	GPSPointTranslate(locations,function(points){
		var len=locations.length;
		for(var index=0;index<len;index++){
			locations[index]['BMapPoint']=points[index];
			locations[index]['distance']=calculateDistance(points[index],curPosition);
		}
		getAddressFromMapPoint(locations,function(ms){
			var len=ms.length;
			for(var index=0;index<len;index++){
				locations[index]['address']=ms[index];
			}
			//for(var index=0;index<len;index++){alert(locations[index]['phone']+' '+locations[index]['address']);}
			window.localStorage.setItem('printerMessage',JSON.stringify(locations));
		});
	});*/
	printerMessage='[{"latitude":30.322066,"longitude":120.344226,"phone":"18405813906","colorPrice":2,"whiteAndBlackPrice":1,"signalPrice":1,"doublePrice":2,"name":"HP Layert 10223","BMapPoint":{"lng":120.35520017786,"lat":30.326048507792},"distance":"1189.49","address":{"province":"浙江省","city":"杭州市","district":"江干区","street":"文泽路","streetNumber":""}},{"latitude":30.322066,"longitude":120.144225,"phone":"18405813906","colorPrice":2,"whiteAndBlackPrice":1,"signalPrice":1,"doublePrice":2,"name":"HP Layert 10223","BMapPoint":{"lng":120.15544106326,"lat":30.325778272887},"distance":"19230.91","address":{"province":"浙江省","city":"杭州市","district":"拱墅区","street":"台州路","streetNumber":"193号"}},{"latitude":30.322066,"longitude":120.244224,"phone":"18405813906","colorPrice":2,"whiteAndBlackPrice":1,"signalPrice":1,"doublePrice":2,"name":"HP Layert 10223","BMapPoint":{"lng":120.25511104352,"lat":30.325945890008},"distance":"9700.88","address":{"province":"浙江省","city":"杭州市","district":"江干区","street":"S2(杭甬高速公路)","streetNumber":""}},{"latitude":30.322066,"longitude":120.444227,"phone":"18405813906","colorPrice":2,"whiteAndBlackPrice":1,"signalPrice":1,"doublePrice":2,"name":"HP Layert 10223","BMapPoint":{"lng":120.4552470999,"lat":30.325742282184},"distance":"9650.34","address":{"province":"浙江省","city":"杭州市","district":"萧山区","street":"岩创线","streetNumber":""}},{"latitude":30.322066,"longitude":120.544218,"phone":"18405813906","colorPrice":2,"whiteAndBlackPrice":1,"signalPrice":1,"doublePrice":2,"name":"HP Layert 10223","BMapPoint":{"lng":120.55496690875,"lat":30.325173559761},"distance":"19183.71","address":{"province":"浙江省","city":"杭州市","district":"萧山区","street":"S9(苏绍高速公路)","streetNumber":""}}]';
window.localStorage.setItem('printerMessage',printerMessage);
}
$(document).ready(function(e) {
	markers=new Array();
	initMap("map");
	$("#upfileDialog").hide();
	$('#printersList').children().eq(0).hide();
	try{
		initPrintrtMessage();
		window.setTimeout(searchPlace(''),1000);
	}catch(e){
		alert(e);
	}
});