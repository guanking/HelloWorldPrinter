/**
 * @author caoguanjie
 * @status unuse
 * @warn need the global var:map,pointTranslated
 * 这里涉及到的百度的Point可以使用 alert('您的位置：'+point.lng+','+point.lat);来查看point的值
 */
/**
 * 地图展示 display map with the giving place
 */
function mapDisplay(divID, place) {
	map = new BMap.Map(divID); // 创建Map实例
	map.centerAndZoom(new BMap.Point(116.404, 39.915), 11); // 初始化地图,设置中心点坐标和地图级别
	map.addControl(new BMap.MapTypeControl()); // 添加地图类型控件
	map.setCurrentCity(place); // 设置地图显示的城市 此项是必须设置的
	map.enableScrollWheelZoom(true); // 开启鼠标滚轮缩放
}
function nearbySearch() {
	map.centerAndZoom(new BMap.Point(116.404, 39.915), 11);
	var local = new BMap.LocalSearch(map, {
		renderOptions : {
			map : map,
			autoViewport : true
		}
	});
	local.searchNearby("小吃", "后门");
}
/**
 * 根据名字设置地图的中心
 * 
 * @param name
 */
function setCenterByName(name) {
	map.centerAndZoom(name, 15);
}
/**
 * 为指定的点添加标号
 * 
 * @param point
 *            位置，使用 BMap.Point构造
 * @returns marker 供函数bindMessageBox函数使用
 */
function addMarker(point) {
	var marker = new BMap.Marker(point); // 创建标注 
	map.addOverlay(marker); // 将标注添加到地图中
	return marker;
}
/**
 * 为指定的mark添加信息框,在这里会为指定的mark添加一个click事件
 * 
 * @param marker
 * @param title
 * @param message
 * @return infoWindow 返回创建的信息窗口，可以通过getContent获取窗口的内容
 */
function bindMessageBox(point,marker, titleText, message) {
	var opts = {
		width : 200, // 信息窗口宽度
		height : 100, // 信息窗口高度
		title : titleText, // 信息窗口标题
		enableMessage : true,// 设置允许信息窗发送短息
		message : "享印欢迎您！"
	}
	var infoWindow = new BMap.InfoWindow(message, opts); // 创建信息窗口对象
	marker.addEventListener("click", function() {
		map.openInfoWindow(infoWindow, point); // 开启信息窗口
	});
	return infoWindow;
}
/**
 * 为指定的marker添加图文信息
 * 
 * @param marker
 * @param title
 * @param message
 * @param src
 *            图片的路径
 * @return infoWindow 返回创建的信息窗口，可以通过getContent获取窗口的内容
 * @warn 这里为marker添加了click事件，中间自己定义了div块，使用了img元素的id，这里ID可能会有问题
 */
function bindMessageContainingTextAndImage(marker, title, message, src) {
	var sContent = "<h4 style='margin:0 0 5px 0;padding:0.2em 0'>" + title + "</h4>"
			+ "<img style='float:right;margin:4px' id='imgDemo' src=" + src + " width='139' height='104' title='天安门'/>"
			+ "<p style='margin:0;line-height:1.5;font-size:13px;text-indent:2em'>" + message + "</p>" + "</div>";
	var infoWindow = new BMap.InfoWindow(sContent);
	marker.addEventListener("click", function() {
		this.openInfoWindow(infoWindow);
		document.getElementById('imgDemo').onload = function() {
			infoWindow.redraw(); // 防止在网速较慢，图片未加载时，生成的信息框高度比图片的总高度小，导致图片部分被隐藏
		}
	});
	return infoWindow;
}
/**
 * 谷歌地图坐标转化为百度坐标； 这里需要一个全局变量pointTranslated，如果没有定义的话会自己定义
 * 转化的结果就保留在这个变量中，如果需要查看值就是用这个数组查询： pointTranslated['(' + x + ',' + y + ')']
 * 
 * @param x
 *            谷歌坐标x
 * @param y
 *            谷歌坐标y
 */
function googlePointTranslate(x, y) {
	var ggPoint = new BMap.Point(x, y);
	var convertor = new BMap.Convertor();
	var pointArr = [];
	pointArr.push(ggPoint);
	convertor.translate(pointArr, 3, 5, function(data) {
		if (data.status === 0) {
			if (pointTranslated == null) {
				pointTranslated = []
			}
			pointTranslated['(' + x + ',' + y + ')'] = data.points[0];
		}
	});
}
/**
 * GPS坐标转化为百度坐标； 这里需要一个全局变量pointTranslated，如果没有定义的话会自己定义
 * 转化的结果就保留在这个变量中，如果需要查看值就是用这个数组查询： pointTranslated['(' + x + ',' + y + ')']
 * 
 * @param gpsPointsMessage
 *            从服务器获取的打印机信息，是一个对象数组，含有属性longitude，latitude
 * @param exePoints
 *            对结果的回调函数，结果为BMap.Point数组
 */
function GPSPointTranslate(gpsPointsMessage,exePoints) {
	var len=gpsPointsMessage.length;
	var points=new Array(len);
	for(var index=0;index<len;index++){
		points[index]=new BMap.Point(gpsPointsMessage[index]['longitude'],gpsPointsMessage[index]['latitude']);
	}
	var convertor = new BMap.Convertor();
	convertor.translate(points, 1, 5, function(data) {
		if (data.status === 0) {
			if (data.status === 0) {
				exePoints(data.points);
			}
		}
	});
}
/**
 * 获取当前位置
 */
function getCurrentPosition() {
	var geolocation = new BMap.Geolocation();
	geolocation.getCurrentPosition(function(r) {
		if (this.getStatus() == BMAP_STATUS_SUCCESS) {
			var mk = new BMap.Marker(r.point);
			map.panTo(r.point);
			window.localStorage.setItem('currentLocation',JSON.stringify(r.point));
		} else {
			setToCurrentCity();
		}
	}, {
		enableHighAccuracy : true
	});
}
/**
 * 设置显示地图显示当前所在城市的位置
 */
function setToCurrentCity() {
	var myCity = new BMap.LocalCity();
	myCity.get(function(result) {
		map.setCenter(result.name);
		(new BMap.Geocoder()).getPoint(result.name, function(point){
			if (point) {
				window.localStorage.setItem('currentLocation',JSON.stringify(point));
			}
		});
	});
}
/**
 * 根据坐标（BMap.Point）数组获取位置信息，并对结果进行回调处理
 * 
 * @param points 二维数组，表示坐标（坐标为转化好的百度坐标）
 * @param exeResult 回调函数，对转化结果进行处理。结果是一个Object数组，属性有：province，city，district，street，streetNumber
 */
function getAddressFromMapPoint(printerMessage, exeResult) {
	var len = printerMessage.length;
	var res = new Array(len);
	var adds = new Array(len);
	for (var i = 0; i < len; i++) {
		adds[i] = printerMessage[i]['BMapPoint'];
	}
	var geoCoder = new BMap.Geocoder();
	var index = 0;
	function geoTransBegin() {
		geoCoder.getLocation(adds[index], function(rs) {
			if (index < len) {
				var addComp = rs.addressComponents;
				res[index] = {
					'province' : addComp.province,
					'city' : addComp.city,
					'district' : addComp.district,
					'street' : addComp.street,
					'streetNumber' : addComp.streetNumber
				};
				index++;
				geoTransBegin();
			} else {
				exeResult(res);
			}
		});
	}
	geoTransBegin();
}
function copy(obj){
	var result={};
	for(var key in obj){
		result[key]=typeof(obj[key])==='object'?copy(obj[key]):obj[key];
	}
	return result;
}
function calculateDistance(point1,point2){
	return (map.getDistance(point1,point2)).toFixed(2);
}

