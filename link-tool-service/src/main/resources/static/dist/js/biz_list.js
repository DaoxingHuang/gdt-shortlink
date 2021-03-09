var bizList;
var bizDataTable;
var businessList = {};
var primaryBusinessList = [];


function queryBizList() {
	$.getJSON("/biz/list", function(data){
		if (null != data && "" != data) {
			bizList = data;
			$.each(data, function (value) {
				var type = value.bizType;
				var subType = value.bizSubType;
				if (primaryBusinessList.indexOf(type) < 0) {
					primaryBusinessList.push(type);
				}

				if (businessList[type] == null) {
					businessList[type] = subType + "";
				} else {
					businessList[type] += subType;
				}
			})
			drawListResult();
		} else {
			console.log("search fail");
		}
	});
}

function drawListResult() {
	var hideDiv = $(".default-hide");
	for (var i = 0; i < hideDiv.length; i++) {
		hideDiv[i].classList.remove("default-hide");
	}

	if (null != bizDataTable && "" != bizDataTable) {
		bizDataTable.destroy();
	}

	var trList = $("#bizListTable tr");
	trList.remove();

	for (var i = 0; i < bizList.length; i++) {
		var newTr = $("<tr></tr>");
		newTr.appendTo("#bizListTable tbody");
		
		$("<td>" + bizList[i].bizType + "</td>").appendTo(newTr);
		$("<td>" + bizList[i].bizSubType + "</td>").appendTo(newTr);
		$("<td>" + formatDate(bizList[i].updateTime) + "</td>").appendTo(newTr);
		$("<td>" + formatDate(bizList[i].createTime) + "</td>").appendTo(newTr);
		$("<td class=\"center\"><button class=\"btn btn-outline btn-link btn-s\" onClick=\"updateBizInfo(" + bizList[i].id + ")\" >更新</button> <button class=\"btn btn-outline btn-link btn-s\" onClick=\"deleteBizInfo(" + bizList[i].id + ")\" >删除</button> </td>").appendTo(newTr);
	}

	bizDataTable = $('#bizListTable').DataTable({
		// "data": tableData,
		"columns": [
			{"title": "主业务"},
			{"title": "子业务"},
			{"title": "更新时间"},
			{"title": "创建时间"},
			{"title": "操作"}
		],
        "responsive": true,
        "aaSorting": [[0, 'desc']],
        "sPaginationType": "full_numbers",
        // "aLengthMenu": [[10, 25, 50, -1], ["10条", "25条", "50条", "All"]],
        "oLanguage": {
            "sProcessing": "正在加载中......",
            "sLengthMenu": "每页显示 _MENU_ 条记录",
            "sZeroRecords": "对不起，查询不到相关数据！",
            "sEmptyTable": "表中无数据存在！",
            "sInfo": "当前显示 _START_ 到 _END_ 条，共 _TOTAL_ 条记录",
            "sInfoFiltered": "数据表中共 _MAX_ 条记录",
            "sSearch": "搜索",
            "oPaginate": {
                "sFirst": "首页",
                "sPrevious": "上一页",
                "sNext": "下一页",
                "sLast": "末页"
            }
        }
    });
}

function addbizInfo(data) {
	$('#bizIdInput').val('')
	$('#bizTypeInput').val('');
	$('#bizSubTypeInput').val('');
	$('#alarmMessage').hide();
	$('#bizInfoAdd').modal('show');
}

function updateBizInfo(id) {
	for (var i = 0; i < bizList.length; i++) {
		if(id == bizList[i].id) {
			$('#bizTypeInput').val(bizList[i].bizType);
			$('#bizSubTypeInput').val(bizList[i].bizSubType);
			$('#bizIdInput').val(id);
			break;
		}
	}
	$('#alarmMessage').hide();
	$('#bizInfoAdd').modal('show');
}

function savebizInfo() {
	var bizId = $('#bizIdInput').val();
	var bizType = $('#bizTypeInput').val();
	var bizSubType = $('#bizSubTypeInput').val();

	if (null == bizType || "" == bizType) {
		return;
	}

	var subTypeIsNull = (null == bizSubType || "" == bizSubType);

	for (var i = 0; i < bizList.length; i++) {
		if (subTypeIsNull && bizType == bizList[i].bizType) {
			$('#alarmMessage').show();
			return;
		} 

		if (!subTypeIsNull && bizType == bizList[i].bizType && bizSubType == bizList[i].bizSubType && bizId != bizList[i].id) {
			$('#alarmMessage').show();
			return;
		}
	}
	var param = "id=" + bizId + "&bizType=" + bizType + "&bizSubType=" + bizSubType;
	$.getJSON("/biz/add?" + encodeURI(param), function(data){
		if (null != data && "" != data && data.code == 0) {
			$('#bizInfoAdd').modal('hide');
			queryBizList();
		} else {
			console.log("search fail");
		}
	});

}

function deleteBizInfo(id) {
	$.getJSON("/biz/delete?id=" + id, function(data){
		if (null != data && "" != data && data.code == 0) {
			queryBizList();
		} else {
			console.log("search fail");
		}
	});
}

function checkInput(item) {
	if (null != item.value && "" != item.value) {
		item.classList.remove("alarm-border");
	} else {
		item.classList.add("alarm-border");
	}
	$('#alarmMessage').hide();
}