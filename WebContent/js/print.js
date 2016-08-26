// JavaScript Document
function init(){
	var url=window.location;
	var params=url.toString().split(/[\?]{1}/);
	if(params.length<=1){
		$('#confirmPrinter').hide();
		$('#noChoice').click(function(){
			window.location='index.html';
		});
	}
	else{
		printerMessage='[{"latitude":30.322066,"longitude":120.344226,"phone":"18405813906","colorPrice":2,"whiteAndBlackPrice":1,"signalPrice":1,"doublePrice":2,"name":"HP Layert 10223","BMapPoint":{"lng":120.35520017786,"lat":30.326048507792},"distance":"1189.49","address":{"province":"浙江省","city":"杭州市","district":"江干区","street":"文泽路","streetNumber":""}},{"latitude":30.322066,"longitude":120.144225,"phone":"18405813906","colorPrice":2,"whiteAndBlackPrice":1,"signalPrice":1,"doublePrice":2,"name":"HP Layert 10223","BMapPoint":{"lng":120.15544106326,"lat":30.325778272887},"distance":"19230.91","address":{"province":"浙江省","city":"杭州市","district":"拱墅区","street":"台州路","streetNumber":"193号"}},{"latitude":30.322066,"longitude":120.244224,"phone":"18405813906","colorPrice":2,"whiteAndBlackPrice":1,"signalPrice":1,"doublePrice":2,"name":"HP Layert 10223","BMapPoint":{"lng":120.25511104352,"lat":30.325945890008},"distance":"9700.88","address":{"province":"浙江省","city":"杭州市","district":"江干区","street":"S2(杭甬高速公路)","streetNumber":""}},{"latitude":30.322066,"longitude":120.444227,"phone":"18405813906","colorPrice":2,"whiteAndBlackPrice":1,"signalPrice":1,"doublePrice":2,"name":"HP Layert 10223","BMapPoint":{"lng":120.4552470999,"lat":30.325742282184},"distance":"9650.34","address":{"province":"浙江省","city":"杭州市","district":"萧山区","street":"岩创线","streetNumber":""}},{"latitude":30.322066,"longitude":120.544218,"phone":"18405813906","colorPrice":2,"whiteAndBlackPrice":1,"signalPrice":1,"doublePrice":2,"name":"HP Layert 10223","BMapPoint":{"lng":120.55496690875,"lat":30.325173559761},"distance":"19183.71","address":{"province":"浙江省","city":"杭州市","district":"萧山区","street":"S9(苏绍高速公路)","streetNumber":""}}]';
window.localStorage.setItem('printerMessage',printerMessage);
		$('#noChoice').hide();
		params=params[1];
		params=params.split(/[=]{1}/);
		var index=parseInt(params[1]);
		printerMessage=window.localStorage.getItem('printerMessage');
		printerMessage=JSON.parse(printerMessage);
		printerMessage=printerMessage[index];
		var address=printerMessage['address'];
		address=address['province']+address['city']+address['district']+address['street']+address['streetNumber'];
		var content='<center style="font-size:40px;">打印机信息</center><br><b>名称：</b><span style="margin-left:50px">'+printerMessage["name"]+"</span><br>"
		+'<b>价格：</b><br><span style="margin-left:50px">单页：+'+printerMessage['signalPrice']+'元/张</span><span style="margin-left:30px">双页：+'+printerMessage['doublePrice']+'元/张</span><br><span style="margin-left:50px">彩色：+'+printerMessage['colorPrice']+'元/张</span><span style="margin-left:30px">黑白：+'+printerMessage['whiteAndBlackPrice']+'元/张</span><br>'
		+'<b>距离：</b><span style="margin-left:50px">'+printerMessage["distance"]+'米</span><br>'
		+'<b>地址:</b><span style="margin-left:50px">'+address+'</span><br>'
		+'<b>联系电话：</b><span style="margin-left:50px">'+printerMessage["phone"]+'</span><br>';
		$('#printerMessage').html(content);
	}
}

function increaseFileNumber(node, yes) {
	var cur = node.val();
	if (!(new RegExp(/[0-9]{0,9}/)).test(cur) || parseInt(cur) < 1) {
		cur = 1;
	}
	if (yes) {
		cur++;
	} else {
		if (cur == 1) {
			alert('This number must bigger than 0');
		} else {
			cur--;
		}
	}
	node.val(cur);
}
function printNumberOfInput_blur(node) {
	var cur = node.val();
	if (!(new RegExp(/^[\d]{0,9}$/)).test(cur) || parseInt(cur) < 1) {
		alert("This input must be a number and bigger than 0!");
		cur = 1;
		node.val(cur);
	}
}
function tempInputFileToUpload_change() {
	try {
		var file = document.getElementById('tempInputFileToUpload').files[0];
		if (file) {
			addFile(file);
		} else {
			alert('no file');
		}
	} catch (e) {
		alert(e);
	}
}
function addFile(file) {
	var curFile = null;
	files.push(file);
	//alert(files.length);
	var len = $('#fileList').children().length;
	if (files.length == 1) {
		curFile = $('#fileList').children().eq(0);
		curFile.slideDown('fast');
		$('#confirmUpload').slideDown('fast');
	} else {
		curFile = $('#fileList').children().eq(0).clone();
		curFile.hide();
	}
	var text = file.name;
	curFile.attr('fullName', text);
	if (text.length > 10) {
		text = text.substr(0, 15) + '...';
	}
	var types = file.name.split(/[\.]{1}/);
	var type = types[types.length - 1];
	type = type.toLowerCase();
	switch (type) {
	case 'doc':
		curFile.children().eq(0).attr('src', 'images/docIcon.png');
		break;
	case 'ppt':
		curFile.children().eq(0).attr('src', 'images/pptIcon.png');
		break;
	case 'pdf':
		curFile.children().eq(0).attr('src', 'images/pdfIcon.png');
		break;
	default:
		curFile.children().eq(0).attr('src', 'images/docIcon.png');
		break;
	}
	curFile.children().eq(3).children().eq(1).children().eq(1).val('1');
	curFile.children().eq(4).children().eq(1).val('signal');
	curFile.children().eq(5).children().eq(1).val('whiteAndBlack');
	curFile.children().eq(1).children().eq(0).text(text);
	curFile.children().eq(2).children().eq(0).bind('click', function() {
		var fileName=$(this).parent().parent().attr('fullName');
		deleteFile(fileName);
	});
	if (files.length != 1) {
		$('#fileList').append(curFile);
		curFile.slideDown();
	}
}
function deleteFile(fileName) {
	var index = 0;
	while (index < files.length) {
		if (files[index].name == fileName) {
			break
		}
		index++;
	}
	if (index < files.length) {
		files.splice(index, 1);
		if(files.length==0){
			$('#fileList').children().eq(index).hide();
			$('#confirmUpload').hide();
		}
		else{
			$('#fileList').children().eq(index).remove();
		}
		return true;
	}
	throw new Error('The file ' + fileName + ' had been removed!');
}
function confirmUpload_click(){
	infos=new Array();
	var fileDivs = $('#fileList').children();
	var len = fileDivs.length;
	var printNumber = null;
	var printStyle = null;
	var printColor = null;
	for (var index = 0; index < len; index++) {
		printNumber = fileDivs.eq(index).children().eq(3).children().eq(1).children().eq(1).val();
		printStyle = fileDivs.eq(index).children().eq(4).children().eq(1).val();
		printColor = fileDivs.eq(index).children().eq(5).children().eq(1).val();
		infos.push({
			printColor:printColor,
			printStyle:printStyle,
			printNumber:printNumber
		});
	}
	itemClick(2);
}
function initOrder(){
	var len=$('#orderList').children().length;
	for(var index=len-1;index>0;index--){
		$('#orderList').children().eq(index).remove();
	}
	var totalMoney=0,itemPrice=0;
	$('#orderList').children().eq(0).hide();
	len=infos.length;
	var temp=null,require=null;
	for(var index=0;index<len;index++){
		if(index==0){
			temp=$('#orderList').children().eq(0);
			temp.show();
		}
		else{
			temp=$('#orderList').children().eq(0).clone();
		}
		temp.children().eq(1).text(files[index].name);
		if(infos[index]['printColor']=='whiteAndBlack'){
			require='黑白';
		}else{
			require='彩色';
		}
		if(infos[index]['printStyle']=='signal'){
			require=require+'单面打印';
		}else{
			require=require+'双面打印';
		}
		itemPrice=infos[index]['printNumber'];
		totalMoney+=parseInt(itemPrice);
		temp.children().eq(4).text(require+infos[index]['printNumber']+'份');
		temp.children().eq(7).text(itemPrice+'元');
		if(index!=0){
			$('#orderList').append(temp);
		}
	}
	$("#totalMoney").text(totalMoney+'元');
}
function upFileAnimation(){
	var str=['文件上传中.','文件上传中..','文件上传中...','文件上传中....','文件上传中.....','文件上传中......'];
	var index=0;
	var process=0;
	try{
		var len=files.length;
		var formData=new FormData();
		for(index=0;index<len;index++){
			formData.append('info',JSON.stringify(infos[index]));
			formData.append('file',files[index]);
		}
		index=0;
		var req= new XMLHttpRequest();
		req.upload.addEventListener("progress", uploadProgress, false);
		req.addEventListener("load", uploadComplete, false);
		req.addEventListener("error", uploadFailed, false);
		req.addEventListener("abort", uploadCanceled, false);
		req.open("POST", "/HelloWorld/upload");
		req.send(formData);
		function uploadProgress(evt) {
			if (evt.lengthComputable) {
				var percentComplete = Math.round(evt.loaded * 100 / evt.total);
				process=5*percentComplete;
				$('#processBar').css('background-position',-(500-process)+'px 0px');
				$('#processBar').text(percentComplete.toFixed(0)+'%');
			} else {
				document.getElementById('progressNumber').innerHTML = 'unable to compute';
			}
			
		}
	
		function uploadComplete(evt) {
			process=500;
		}
	
		function uploadFailed(evt) {
			
		}
	
		function uploadCanceled(evt) {

		}
	}catch(e){
		alert(e);
	}
	$('#confirmAllFinished').hide();
	var timer=window.setInterval(function(){
		index++;
		if(index==str.length) index=0;
		$('#fileUploading').text(str[index]);
		if(process==500){
			window.clearInterval(timer);
			$('#fileUploading').text('传输完成');
			$('#upfileAnimationImg').attr('src','images/print/bg.jpg');
			$('#confirmAllFinished').fadeIn(500);
			var light=true;
			var timer2=window.setInterval(function(){
				if(light){
					$('#confirmAllFinished').css({'border-color':'#F00','color':'#000'});
				}else{
					$('#confirmAllFinished').css({'border-color':'#90C','color':'#FFF'});
				}
				light=!light;
			},500);
			$('#confirmAllFinished').bind('click',function(){
				window.clearInterval(timer2);
				window.location="index.html";
			});
		}
	},500);
	var timer1=window.setInterval(function(){
		
		if(process==500){
			 window.clearInterval(timer1);
		}
	},50);
}
$(document).ready(function(e) {
	files=new Array();
	infos=null;
	printerMessage=null;
	$('#fileList').children().eq(0).hide();
	$('#confirmUpload').hide();
	try{
		init();
	}catch(e){
		alert(e);
	}
});
function addDocument_click(){
	$('#tempInputFileToUpload').click();
}