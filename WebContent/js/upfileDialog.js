/**
 * @author caoguanjie
 * @status use
 */
function isEqualFile(file1, file2) {
	if (file1.name != file2.name) {
		return false;
	}
	if (file1.type != file2.type) {
		return false;
	}
	if (file1.size != file2.size) {
		return false;
	}
	return true;
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
function addFile(file) {
	var curFile = null;
	files[files.length] = file;
	var len = $('div#top2').children().length;
	if (files.length == 1) {
		curFile = $('div#top2').children().eq(0);
	} else {
		curFile = $('div#top2').children().eq(0).clone();
	}
	var text = file.name;
	curFile.attr('fullName', file.name);
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
		deleteFile(file.name);
	});
	if (files.length != 1) {
		$('div#top2').append(curFile);
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
function deleteFile(fileName) {
	if (confirm('Are you sure to delete file 《' + fileName + "》?") == false)
		return;
	var index = 0;
	while (index < files.length) {
		if (files[index].name == fileName) {
			break
		}
		index++;
	}
	if (index < files.length) {
		files.splice(index, 1);
		$('div#top2').children().eq(index).remove();
		return true;
	}
	throw new Error('The file ' + fileName + ' had been removed!');
}
function calculateMoney() {
	infos = [];
	var fileDivs = $('div.fileLIst');
	var len = fileDivs.length;
	var printNumber = null;
	var printStyle = null;
	var printColor = null;
	for (var index = 0; index < len; index++) {
		printNumber = fileDivs.eq(index).children().eq(3).children().eq(1).children().eq(1).val();
		printStyle = fileDivs.eq(index).children().eq(4).children().eq(1).val();
		printColor = fileDivs.eq(index).children().eq(5).children().eq(1).val();
		infos[index] = printColor + '|' + printStyle + '|' + printNumber;
	}
	var t = $('#top3 span#money').text('合计：' + files.length);
}
function uploadFile() {
	var data = new FormData();
	data.append('userID', '18405813906');
	for (var index = 0; index < files.length; index++) {
		data.append('file' + index, files[index]);
		data.append('info' + index, infos[index]);
	}
	var request = new XMLHttpRequest();
	request.upload.addEventListener("progress", function(event) {
		if (event.lengthComputable) {
			var fs = $('.fileLIst');
			var len = fs.length;
			if (event.loaded != event.total) {
				for (var index = 0; index < len; index++) {
					var name = fs.eq(index).children().eq(1).children().eq(2).attr('src');
					if (new RegExp(/\.png$/).test(name)) {
						name = name.replace(/\.png$/, '.gif');
						fs.eq(index).children().eq(1).children().eq(2).attr('src', name)
					} else {
						break;
					}
				}
			}
		} else {
			alert('unable to compute');
		}
	}, false);
	request.addEventListener("load", function(event) {
		var fs = $('.fileLIst');
		var len = fs.length;
		for (var index = 0; index < len; index++) {
			var name = fs.eq(index).children().eq(1).children().eq(2).attr('src');
			name = name.replace(/\.gif$/, '.png');
			fs.eq(index).children().eq(1).children().eq(2).attr('src', name);
		}
	}, false);
	request.addEventListener("error", function(event) {
		alert('error');
	}, false);
	request.addEventListener("abort", function(event) {
		alert('abort');
	}, false);
	request.open('POST', "upload");
	request.send(data);
}
function init() {
	$('span#confirmSubmit').text('合计订单');
	$('span#confirmSubmit').click(function() {
		var curValue = $(this).text();
		switch (curValue) {
		case '合计订单':
			calculateMoney();
			$(this).text('提交订单');
			break;
		case '提交订单':
			if (confirm('付款：' + $('#top3 span#money').text())) {
				uploadFile();
				$(this).text('合计订单');
				break;
			} else {
			}
		default:
			break;
		}
	});
}
$(document).ready(function(e) {
	files = [];
	infos = [];
	try {
		init();
	} catch (e) {
		alert(e);
	}
});