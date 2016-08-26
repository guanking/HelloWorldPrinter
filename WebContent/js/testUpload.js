// JavaScript Document
function upload(bucket, key) {
	var data = new FormData();
	data.append('bucket', bucket);
	data.append('key', key);
	$.ajax({
		type : POST,
		url : '/uploadFile',
		data : data
	});
	return 'uptoken';
}
$(document).ready(function(e) {
	$('#uploadFile').click(function(e) {
		var file = document.getElementById('upfile').files[0];
		$('#fileKey').val(file.name);
		var fileKey = file.name;
		alert(fileKey);
		$.ajax({
			url : 'Token',
			type : 'POST',
			data : 'key=' + fileKey,
			error : function(req) {
				alert(req);
			},
			success : function(data) {
				$('#upToken').val(data);
			},
			complete: function(req){
				alert(req);
			}
		});
		alert($('#upToken').val());
	});
});
