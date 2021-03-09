function editResourceInfo(item, index) {
	var data = searchResult[index];

	item.html('');

	var sMetaList = data.sMetaList;
	var iMetaList = data.iMetaList;
	var metaList = [];
	if (null != sMetaList) {
		for (var i = 0; i < sMetaList.length; i++) {
			metaList.push(sMetaList[i]);
		}
	}

	if (null != iMetaList) {
		for (var i = 0; i < iMetaList.length; i++) {
			metaList.push(iMetaList[i]);
		}
	}
	for (var i = 0; i < metaList.length; i++) {
		var metaItem = metaList[i];
		generateFormItemHtml(metaItem.metaName, metaItem.metaValue, true, false).appendTo($(item));
	}

	item.html(item.html() + "<div class=\"jupiter-form-group\" style=\"text-align: center;\"><span class=\"fa fa-plus\" onClick=addFormItem(this,0) /></div>")

	$(item).parents(".fade").modal('show');
}

function generateFormItemHtml(label, value, isRemove, disabled) {
	var html = "<div class=\"jupiter-form-group\">";

	if (disabled) {
		html = html + "<label>" + label +"：</label>" +
			"<input class=\"jupiter-form-control\" name=\"\" value=\"" + value + "\"  disabled />";
	} else {
		html = html + "<input class=\"jupiter-form-control-s\" name=\"\" value=\"" + label + "\" onBlur = \"checkInput(this)\" />" +
			"<input class=\"jupiter-form-control\" name=\"\" value=\"" + value + "\"  onBlur = \"checkInput(this)\" />";
	}

	if (isRemove) {
		html += "<span class=\"glyphicon glyphicon-remove\" onClick=removeCurrentFormItem(this) />";
	}
	html += " </div>"
	return $(html);
}

function removeCurrentFormItem(itemDiv) {
	itemDiv.parentElement.remove();
}

function addFormItem(plus, ignoreCount) {
	var isCompleted = true;
	var formItems = $(plus).parents('.modal-body').children('.jupiter-form-group');
	for (var i = ignoreCount; i < formItems.length - 1; i++) {
		var formItem = formItems[i];
		var key = formItem.children[0].value;
		var value = formItem.children[1].value;
		if (null == key || "" == key) {
			formItem.children[0].classList.add("alarm-border");
			isCompleted = false;
		}

		if (null == value || "" == value) {
			formItem.children[1].classList.add("alarm-border");
			isCompleted = false;
		}
	}

	if (!isCompleted) {
		return;
	}

	var html = "<div class=\"jupiter-form-group\">" +
		"<input class=\"jupiter-form-control-s\" name=\"\" value=\"\" onBlur = \"checkInput(this)\" />" +
		"<input class=\"jupiter-form-control\" name=\"\" value=\"\" onBlur = \"checkInput(this)\" />" +
		"<span class=\"glyphicon glyphicon-remove\" onClick=removeCurrentFormItem(this) />" +
		"</div>";
	plus.parentElement.outerHTML = html + plus.parentElement.outerHTML;
}

function saveResourceInfo(item, index) {
	var result = {};
	var formItems = $(item).parents(".modal-content").children(".modal-body").children(".jupiter-form-group");
	var meta = {};
	var checkResult = true;
	for (var i = 0; i < formItems.length - 1; i++) {
		var formItem = formItems[i];
		var key = formItem.children[0].value;
		var value = formItem.children[1].value;

		if (null == key || "" == key) {
			$($(formItem).children()[0]).addClass("alarm-border");
			result = false;
		} else if (null== value || "" == value) {
			$($(formItem).children()[1]).addClass("alarm-border");
			result = false;
		} else {
			$(formItem).children().removeClass("alarm-border");
		}

		meta[key] = value;
		if (parseInt(value)) {
			meta[key] = parseInt(value);
		}
	}

	if (!checkResult) {
		return;
	}

	var resource_name =  searchResult[index].name;
	var resource_type = searchResult[index].type;
	var store_scope = searchResult[index].storeScope;

	var param = "resource=" + resource_name + "&resourceType=" + resource_type + "&storeScope=" + store_scope + "&meta=" + JSON.stringify(meta);

	$.getJSON("/jupiter/resource/update?" + encodeURI(param), function(data){
		if (data.code == 0) {
			$(item).parents(".fade").modal("hide");
			searchResourceInfo();
		} else {
			showErrorMessage(data);
		}
	});
}

function checkInput(item) {
	if (null != item.value && "" != item.value) {
		item.classList.remove("alarm-border");
	} else {
		item.classList.add("alarm-border");
	}
}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//search

var businessDataTable = [];
businessDataTable.push("");
businessDataTable.push("");

var searchResult = "";

function searchResourceInfo() {
	var searchResourceName = $("#resourceNameSearchInput").val();
	if (null == searchResourceName || "" == searchResourceName) {
		return;
	}

	queryBizList();

	//send search request
	hideSearchAlarmMessage();
	$.ajax({
		url: "/jupiter/resource/search?resource_name=" + searchResourceName,
		success: function (data, status, jxhr) {
			$("#privateInfo").hide();
			$("#publicInfo").hide();
			if (null != data && "" != data) {
				searchResult = data;
				drawSearchResultPrivate(data[0]);
				if (data.length > 1) {
					drawSearchResultPublic(data[1]);
				}
			} else if (data.length == 0) {
				showSearchAlarmMessage("不存在");
			} else {
				showSearchAlarmMessage("查询失败");
			}
		},
		error: function (data, status, jxhr) {
			if (0 == data.status) {
				alert("请重新登录！");
				location.reload();
			} else {
				alert("请求出错！")
			}
		}
	});
}

function showSearchAlarmMessage(content) {
	$("#searchAlarmMessage").show();
	$("#searchAlarmMessage").html(content);
}
function hideSearchAlarmMessage() {
	$("#searchAlarmMessage").hide();
	$("#searchAlarmMessage").html('');
}

function drawSearchResultPrivate(data) {
	$("#privateInfo").show();
	var trList = $("#privateInfo tbody").first().children("tr");
	trList.remove();

	var storeScope = "公有云";
	if ("private" == data.storeScope) {
		storeScope = "私有云";
	}

	var sMetaList = data.sMetaList;
	var iMetaList = data.iMetaList;
	var metaList = [];

	var temp1 = {};
	temp1["metaName"] = "资源名称";
	temp1["metaValue"]=$("#resourceNameSearchInput").val();
	var temp2 = {};
	temp2["metaName"]="资源类型";
	temp2["metaValue"]=data.type;
	var temp3 = {};
	temp3["metaName"]="存储类型";
	temp3["metaValue"]=storeScope;
	var temp4 = {};
	temp4["metaName"]="创建时间";
	temp4["metaValue"]=formatDate(data.createTime);
	var temp5 = {};
	temp5["metaName"]="更新时间";
	temp5["metaValue"]=formatDate(data.updateTime);

	metaList.push(temp1);
	metaList.push(temp2);
	metaList.push(temp3);
	metaList.push(temp4);
	metaList.push(temp5);

	if (null != sMetaList) {
		for (var i = 0; i < sMetaList.length; i++) {
			metaList.push(sMetaList[i]);
		}
	}

	if (null != iMetaList) {
		for (var i = 0; i < iMetaList.length; i++) {
			metaList.push(iMetaList[i]);
		}
	}

	var i = 0;
	var tr = $("<tr></tr>");
	for (; i < metaList.length; i++) {

		td = $("<td  style=\"text-align: right;\"><label>" + metaList[i].metaName + "：</label></td> <td>" + metaList[i].metaValue + "</td>");
		td.appendTo(tr);
		if (i % 3 == 2) {
			tr.appendTo($("#resourceInfoTablePrivate"));

			tr = $("<tr></tr>");
		}
	}

	if (i % 3 != 0) {
		while (i % 3 != 0) {
			var td = $("<td></td><td></td>");
			td.appendTo(tr);
			i++;
		}
		tr.appendTo($("#resourceInfoTablePrivate"));
	}

	// businessInfoTable
	if (null != businessDataTable[0] && "" != businessDataTable[0]) {
		businessDataTable[0].destroy();
	}

	var trList = $("#privateInfo tbody").last().children("tr");
	trList.remove();

	for (var i = 0; i < data.bizList.length; i++) {
        var bizInfo = data.bizList[i];
        var iPro = bizInfo.iProList;
        var sPro = bizInfo.sProList;
        var pro = [];

        if (null != iPro) {
            for (var index = 0; index < iPro.length; index++) {
                pro.push(iPro[index]);
            }
        }
        if (null != sPro) {
            for (var index = 0; index < sPro.length; index++) {
                pro.push(sPro[index]);
            }
        }

        var proStr = "";
        for (var j = 0; j < pro.length; j++) {
            var name = pro[j].proName;
            var value = pro[j].proValue;
			proStr = proStr + name + ":" + value + "<br>";

        }

        var newTr = $("<tr></tr>");
        newTr.appendTo($("#privateInfo tbody").last());

        var columns = [{"title": "主业务"}, {"title": "子业务"}, {"title": "属性"}, {"title": "更新时间"}, {"title": "创建时间"}];
        if (deleteResourceBizPermission) {
            columns.push({"title": "操作"});
        }

        $("<td>" + bizInfo.bizType + "</td>").appendTo(newTr);
        if (bizInfo.bizSubType) {
            $("<td>" + bizInfo.bizSubType + "</td>").appendTo(newTr);
        } else {
            $("<td> </td>").appendTo(newTr);
        }
		$("<td><pre>" + proStr + "</pre></td>").appendTo(newTr);

		$("<td>" + formatDate(bizInfo.updateTime) + "</td>").appendTo(newTr);
		$("<td>" + formatDate(bizInfo.createTime) + "</td>").appendTo(newTr);
		if (deleteResourceBizPermission) {
			$("<td class=\"center\"><button class=\"btn btn-outline btn-link btn-s\" onClick=\"updateBusinessInfo(this,$('#addBusinessInfoPrivateDiv'), 0)\" >更新</button> <button class=\"btn btn-outline btn-link btn-s\" onClick=\"deleteBusinessInfo(this, 0)\" >删除</button> </td>").appendTo(newTr);
		}
	}

	var tempDataTable = $('#privateInfo table').last().DataTable({
		// "data": tableData,
		"columns": columns,
		"responsive": true,
		"aaSorting": [[3, 'desc']],
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
	businessDataTable[0] = tempDataTable;

}
function drawSearchResultPublic(data) {
	$("#publicInfo").show();

	var trList = $("#publicInfo tbody").first().children("tr");
	trList.remove();

	var storeScope = "公有云";
	if ("private" == data.storeScope) {
		storeScope = "私有云";
	}

	var sMetaList = data.sMetaList;
	var iMetaList = data.iMetaList;
	var metaList = [];

	var temp1 = {};
	temp1["metaName"] = "资源名称";
	temp1["metaValue"]=$("#resourceNameSearchInput").val();
	var temp2 = {};
	temp2["metaName"]="资源类型";
	temp2["metaValue"]=data.type;
	var temp3 = {};
	temp3["metaName"]="存储类型";
	temp3["metaValue"]=storeScope;
	var temp4 = {};
	temp4["metaName"]="创建时间";
	temp4["metaValue"]=formatDate(data.createTime);
	var temp5 = {};
	temp5["metaName"]="更新时间";
	temp5["metaValue"]=formatDate(data.updateTime);

	metaList.push(temp1);
	metaList.push(temp2);
	metaList.push(temp3);
	metaList.push(temp4);
	metaList.push(temp5);

	if (null != sMetaList) {
		for (var i = 0; i < sMetaList.length; i++) {
			metaList.push(sMetaList[i]);
		}
	}

	if (null != iMetaList) {
		for (var i = 0; i < iMetaList.length; i++) {
			metaList.push(iMetaList[i]);
		}
	}

	var i = 0;
	var tr = $("<tr></tr>");
	for (; i < metaList.length; i++) {

		td = $("<td  style=\"text-align: right;\"><label>" + metaList[i].metaName + "：</label></td> <td>" + metaList[i].metaValue + "</td>");
		td.appendTo(tr);
		if (i % 3 == 2) {
			tr.appendTo($("#resourceInfoTablePublic"));

			tr = $("<tr></tr>");
		}
	}

	if (i % 3 != 0) {
		while (i % 3 != 0) {
			var td = $("<td></td><td></td>");
			td.appendTo(tr);
			i++;
		}
		tr.appendTo($("#resourceInfoTablePublic"));
	}


	// businessInfoTable
	if (null != businessDataTable[1] && "" != businessDataTable[1]) {
		businessDataTable[1].destroy();
	}

	var trList = $("#publicInfo tbody").last().children("tr");
	trList.remove();

	for (var i = 0; i < data.bizList.length; i++) {
		var bizInfo = data.bizList[i];
		var iPro = bizInfo.iProList;
		var sPro = bizInfo.sProList;
		var pro = [];

		if (null != iPro) {
			for (var index = 0; index < iPro.length; index++) {
				pro.push(iPro[index]);
			}
		}
		if (null != sPro) {
			for (var index = 0; index < sPro.length; index++) {
				pro.push(sPro[index]);
			}
		}

		var proStr = "";
		for (var j = 0; j < pro.length; j++) {
			var name = pro[j].proName;
			var value = pro[j].proValue;
			proStr = proStr + name + ":" + value + "<br>";

		}

		var newTr = $("<tr></tr>");
		newTr.appendTo($("#publicInfo tbody").last());

		var columns= [{"title": "主业务"},{"title": "子业务"},{"title": "属性"},{"title": "更新时间"},{"title": "创建时间"}];
		if (deleteResourceBizPermission) {
			columns.push({"title": "操作"});
		}

		$("<td>" + bizInfo.bizType + "</td>").appendTo(newTr);
        if (bizInfo.bizSubType) {
            $("<td>" + bizInfo.bizSubType + "</td>").appendTo(newTr);
        } else {
            $("<td> </td>").appendTo(newTr);
        }
		$("<td><pre>" + proStr + "</pre></td>").appendTo(newTr);
		$("<td>" + formatDate(bizInfo.updateTime) + "</td>").appendTo(newTr);
		$("<td>" + formatDate(bizInfo.createTime) + "</td>").appendTo(newTr);
		if (deleteResourceBizPermission) {
			$("<td class=\"center\"><button class=\"btn btn-outline btn-link btn-s\" onClick=\"updateBusinessInfo(this,$('#addBusinessInfoPublicDiv'), 1)\" >更新</button> <button class=\"btn btn-outline btn-link btn-s\" onClick=\"deleteBusinessInfo(this, 1)\" >删除</button> </td>").appendTo(newTr);
		}
	}

	var tempDataTable = $('#publicInfo table').last().DataTable({
		// "data": tableData,
		"columns": columns,
		"responsive": true,
		"aaSorting": [[3, 'desc']],
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

	businessDataTable[1] = tempDataTable;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//business

function addBusinessInfo(data, item) {
	item.html('');


	generateBusinessSelectHtml(primaryBusinessList, businessList, "", "", item);

	$("<div class=\"jupiter-form-group\" style=\"text-align: center;\"><span class=\"fa fa-plus\" onClick=addFormItem(this,2) /></div>").appendTo(item);

	$(item).parents(".fade").modal('show');
}

function generateBusinessSelectHtml(primaryBusinessList, businessList, primaryBusiness, subBusiness, item) {
	var subBusinessList = "";
	var primaryDiv = $("<div class=\"jupiter-form-group\"><label>主业务：</label></div>");
	var primarySelect = $("<select class=\"jupiter-form-control\" onchange='changeBizType(this)'></select>");
	for (var i = 0; i < primaryBusinessList.length; i++) {
		var option = $("<option value=\"" + primaryBusinessList[i] + "\" >" + primaryBusinessList[i] + "</option>");
		if ((null != primaryBusiness && "" != primaryBusiness) && primaryBusiness == primaryBusinessList[i]) {
			option.attr("selected", "selected");
		}
		option.appendTo(primarySelect);
	}
	primarySelect.appendTo(primaryDiv);
	primaryDiv.appendTo(item);

	var subDiv = $("<div class=\"jupiter-form-group\"><label>子业务：</label></div>");
	var subSelect = $("<select class=\"jupiter-form-control\" ></select>");
	$("<option value=''>请选择</option>").appendTo(subSelect);

	if (null == primaryBusiness || "" == primaryBusiness) {
		primaryBusiness = primaryBusinessList[0];
	}

	var subBusinessList = businessList[primaryBusiness].split(",");
	for (var i = 0; i < subBusinessList.length; i++) {
		var option = $("<option value=\"" + subBusinessList[i] + "\" >" + subBusinessList[i] + "</option>");
		if ((null != subBusiness && "" != subBusiness) && subBusiness == subBusinessList[i]) {
			option.attr("selected", "selected");
		}
		option.appendTo(subSelect);
	}
	subSelect.appendTo(subDiv);
	subDiv.appendTo(item);
}

function changeBizType(item) {
	var selectedBizType = $(item).val();
	var bizSubTypeSelect = $(item).parents(".modal-body").children('.jupiter-form-group').children('select').last();
	bizSubTypeSelect.html('');

	$("<option value=''>请选择</option>").appendTo(bizSubTypeSelect);

	var subBusinessList = businessList[selectedBizType].split(",");
	for (var i = 0; i < subBusinessList.length; i++) {
		var option = $("<option value=\"" + subBusinessList[i] + "\" >" + subBusinessList[i] + "</option>");
		option.appendTo(bizSubTypeSelect);
	}
}

function generateBusinessFormItemHtml(label, value, isRemove, disabledValue, disabledKey) {
	var html = "<div class=\"jupiter-form-group\">";
	    		
	if (disabledKey) {
		html = html + "<label>" + label +"：</label>";
	} else {
		html += "<input class=\"jupiter-form-control-s\" name=\"\" value=\"" + label + "\" onBlur = \"checkInput(this)\" />";
	}

	if (disabledValue) {
        html += "<input class=\"jupiter-form-control\" name=\"\" value=\"" + value + "\"  disabled />";
	} else {
		html += "<input class=\"jupiter-form-control\" name=\"\" value=\"" + value + "\"  onBlur = \"checkInput(this)\" />";
	}

	if (isRemove) {
		html += "<span class=\"glyphicon glyphicon-remove\" onClick=removeCurrentFormItem(this) />";
	}
	html += " </div>"
	return $(html);
}
function saveBusinessInfo(item, index) {
	var properties = {};
	var formItems = $(item).parents(".modal-content").children(".modal-body").children(".jupiter-form-group");
	if (!checkBizBeforeSave(formItems, index)) {
		return;
	}
	for (var i = 2; i < formItems.length - 1; i++) {
		var formItem = formItems[i];
		var key = formItem.children[0].value;
		var value = formItem.children[1].value;
		properties[key] = value;
		if (parseInt(value)) {
			properties[key] = parseInt(value);
		}
	}
	var bizTypeValue =  formItems[0].children[1].value;
	var bizSubTypeValue = formItems[1].children[1].value;

	var param = "bizType="+ bizTypeValue + "&bizSubType="+ bizSubTypeValue + "&resource="+ searchResult[index].name + "&storeScope="+ searchResult[index].storeScope + "&properties="+ JSON.stringify(properties);

	//send post request
	$.getJSON("/jupiter/resource/biz/update?" + encodeURI(param), function(data){
		if (data.code == 0) {
			$(item).parents(".fade").modal('hide');
			searchResourceInfo();
		} else {
			showErrorMessage(data);
		}
	});
}


function checkBizBeforeSave(formItems, index) {
	var result = true;
	var isAdd = $(formItems[0].lastChild).children("option").length > 0;
	if (isAdd) {
		var bizTypeValue =  formItems[0].children[1].value;
		var bizSubTypeValue = formItems[1].children[1].value;
		for (var i = 0; i < searchResult[index].bizList.length; i++) {
			if (bizTypeValue == searchResult[index].bizList[i].bizType
				&& bizSubTypeValue == searchResult[index].bizList[i].bizSubType) {
				$(formItems[0]).children().addClass("alarm-border");
				$(formItems[1]).children().addClass("alarm-border");
				result = false;
			}
		}
	}

	if (!result) {
		$(formItems[0]).children().removeClass("alarm-border");
		$(formItems[1]).children().removeClass("alarm-border");
	}

	for (var i = 2; i < formItems.length - 1; i++) {
		var formItem = formItems[i];
		var key = formItem.children[0].value;
		var value = formItem.children[1].value;
		if (null == key || "" == key) {
			$($(formItem).children()[0]).addClass("alarm-border");
			result = false;
		} else if (null== value || "" == value) {
			$($(formItem).children()[1]).addClass("alarm-border");
			result = false;
		} else {
			$(formItem).children().removeClass("alarm-border");
		}
	}

	return result;
}

function updateBusinessInfo(item, div, index) {
	var result = searchResult[index];

	var rowIndex = businessDataTable[index].row(item.parentElement.parentElement).index();
	var data = result.bizList[rowIndex];

	var bizType = data.bizType;
	var bizSubType = data.bizSubType;
	var iPro = data.iProList;
	var sPro = data.sProList;
	var pro = [];
	if (null != iPro) {
		for (var i = 0; i < iPro.length; i++) {
			pro.push(iPro[i]);
		}
	}
	if (null != sPro) {
		for (var i = 0; i < sPro.length; i++) {
			pro.push(sPro[i]);
		}
	}

	div.html('');

	generateBusinessFormItemHtml("主业务", bizType, false, true, true).appendTo(div);
	generateBusinessFormItemHtml("子业务", bizSubType, false, true, true).appendTo(div);

	for (var i = 0; i < pro.length; i++) {
		generateBusinessFormItemHtml(pro[i].proName, pro[i].proValue, true, false, false).appendTo(div);
	}

	$("<div class=\"jupiter-form-group\" style=\"text-align: center;\"><span class=\"fa fa-plus\" onClick=addFormItem(this,2) /></div>").appendTo(div);

	$(div).parents(".fade").modal("show");
}

function deleteBusinessInfo(item, index) {
	if (!confirm("确定要删除吗？")){
		return;
	}
	var i = businessDataTable[index].row(item.parentElement.parentElement).index();
	var data = searchResult[index].bizList[i];
	var primaryBusiness = data.bizType;
	var subBusiness = data.bizSubType;

	var param = "bizType="+ primaryBusiness + "&bizSubType="+ subBusiness + "&resource="+ searchResult[index].name + "&storeScope="+ searchResult[index].storeScope;
	$.getJSON("/jupiter/resource/biz/delete?" + encodeURI(param), function(data){
		if (data.code == 0) {
			businessDataTable[index].row(item.parentElement.parentElement).remove().draw();
			searchResourceInfo();
		} else {
			showErrorMessage(data);
		}
	});
}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
function formatDate(timestamp) {
	var date = new Date(timestamp);
	Y = date.getFullYear() + '-';
	M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-';
	D = (date.getDate() < 10 ? '0' + date.getDate(): date.getDate()) + " ";
	h = (date.getHours() < 10 ? '0' + date.getHours(): date.getHours()) + ":";
	m = (date.getMinutes() < 10 ? '0' + date.getMinutes(): date.getMinutes()) + ":";
	s = (date.getSeconds() < 10 ? '0' + date.getSeconds(): date.getSeconds()) + "";
	return Y+M+D+h+m+s;
}

function parseDate(dateStr) {
    return Date.parse(dateStr.replace(/-/g,   "/"));
}

function formatJSON(json, indent, leftBracesInSameLine)
{
	function getIndentStr(level)
	{
		var str = '';
		for(var i=0; i<level; i++) str += (indent || '  ');
		return str;
	}
	function format(obj, level)
	{
		level = level == undefined ? 0 : level;
		var result = '';
		if(typeof obj == 'object' && obj != null) // 如果是object或者array
		{
			var isArray = obj instanceof Array, idx = 0;
			result += (isArray ? '[ \n' : '{');
			for(var i in obj)
			{
				result += (idx++ > 0 ? ',\n' : ''); // 如果不是数组或对象的第一个内容，追加逗号
				var nextIsObj = (typeof obj[i] == 'object' && obj[i] != null), indentStr = getIndentStr(level+1);
				result += (isArray && nextIsObj) ? '' : ''; // 如果当前是数组并且子项是对象，无需缩进
				result += isArray ? '' : ('"' + i + '": ' + (nextIsObj && !leftBracesInSameLine ? '\n' : '') );
				result += (!nextIsObj || (nextIsObj && leftBracesInSameLine && !isArray)) ? '' : indentStr;
				result += format(obj[i], level+1); // 递归调用
			}
			result += (isArray ? '\n]' : '}') + '';
		}
		else // 如果是 null、number、boolean、string
		{
			var quot = typeof obj == 'string' ? '"' : '';//是否是字符串
			result += (quot + obj + quot + '');
		}
		return result;
	}
	return format(eval('(' + json + ')')); // 使用eval的好处是可以解析非标准JSON
}


function showErrorMessage(data) {
	if (null != data.desc) {
		alert(data.desc);
	} else {
		alert("系统错误");
	}
}

var deleteResourceBizPermission = false;
var deleteBizPermission = false;

function queryPermissionList(permissionURL) {
	var permissionData = {};
	$.ajax({
		url: permissionURL,
		xhrFields: {
			withCredentials: true
		},
		success: function (data, status, jxhr) {
			if (null != data && "" != data) {
				permissionData = data;
				checkPermission(permissionData);
			} else {
				showErrorMessage({"desc": "获取权限失败！"});
			}
		}
	});
}

function checkPermission(permissionData) {
	if (permissionData.result.success != true) {
		alert("获取权限失败");
		return;
	}

	var functionPermission = {};

	var modelArray = permissionData.data.model;
	for (var i = 0; i < modelArray.length; i++) {
		var menuSubResources = modelArray[i].subResources;

		if (menuSubResources.length > 0) {
			for (var j = 0; j < menuSubResources.length; j++) {
				var pageSubResources = menuSubResources[j].subResources;

				if (pageSubResources.length > 0) {
					for (var k = 0; k < pageSubResources.length; k++) {
						var functionName = pageSubResources[k].name;
						var functionCode = pageSubResources[k].code;
						var functionType = pageSubResources[k].resourceType.name;

						functionPermission[functionCode] = functionName;
					}
				}
			}
		}
	}

	var functionItems = $("[permission]");
	for (var i = 0; i < functionItems.length; i++) {
		var requiredPermission = $(functionItems[i]).attr("permission");
		if (null == functionPermission[requiredPermission]) {
			functionItems[i].remove();
		}
	}
	deleteResourceBizPermission = functionPermission["resource_biz_delete_api"];
	deleteBizPermission = functionPermission["biz_delete_api"];
}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//

var bizList;
var bizDataTable;
var businessList = {};
var primaryBusinessList = [];


function queryBizList() {
	$.getJSON("/jupiter/biz/list", function(data){
			bizList = data;
			businessList = {};
			primaryBusinessList = [];
			$.each(data, function (name, value) {
				var type = value.bizType;
				var subType = value.bizSubType;
				if (primaryBusinessList.indexOf(type) < 0) {
					primaryBusinessList.push(type);
				}

				if (businessList[type] == null) {
					businessList[type] = subType + "";
				} else {
					businessList[type] = businessList[type] + "," + subType;
				}
			});
			drawListResult();
	});
}

function drawListResult() {
	var hideDiv = $("#biz-page-wrapper .default-hide");
	for (var i = 0; i < hideDiv.length; i++) {
		hideDiv[i].classList.remove("default-hide");
	}

	if (null != bizDataTable && "" != bizDataTable) {
		bizDataTable.destroy();
	}

	var trList = $("#bizListTable tr");
	trList.remove();

	var columns = [{"title": "主业务"},{"title": "子业务"},	{"title": "更新时间"},{"title": "创建时间"}];
	if (deleteBizPermission) {
		columns.push({"title": "操作"});
	}

	for (var i = 0; i < bizList.length; i++) {
		var newTr = $("<tr></tr>");
		newTr.appendTo("#bizListTable tbody");

		$("<td>" + bizList[i].bizType + "</td>").appendTo(newTr);
		$("<td>" + bizList[i].bizSubType + "</td>").appendTo(newTr);
		$("<td>" + formatDate(bizList[i].updateTime) + "</td>").appendTo(newTr);
		$("<td>" + formatDate(bizList[i].createTime) + "</td>").appendTo(newTr);
		if (deleteBizPermission) {
			$("<td class=\"center\"> <button class=\"btn btn-outline btn-link btn-s\" onClick=\"deleteBizInfo(" + bizList[i].id + ")\" >删除</button> </td>").appendTo(newTr);
		}
	}

	bizDataTable = $('#bizListTable').DataTable({
		"columns": columns,
		"responsive": true,
		"aaSorting": [[2, 'desc']],
		"sPaginationType": "full_numbers",
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
	$.getJSON("/jupiter/biz/add?" + encodeURI(param), function(data){
		if (null != data && "" != data && data.code == 0) {
			$('#bizInfoAdd').modal('hide');
			queryBizList();
		} else {
			showErrorMessage(data);
		}
	});

}

function deleteBizInfo(id) {
	if (!confirm("确定要删除吗？")){
		return;
	}
	$.getJSON("/jupiter/biz/delete?id=" + id, function(data){
		if (null != data && "" != data && data.code == 0) {
			queryBizList();
		} else {
			showErrorMessage(data);
		}
	});
}

function checkBizInput(item) {
	if (null != item.value && "" != item.value) {
		item.classList.remove("alarm-border");
	} else {
		item.classList.add("alarm-border");
	}
	$('#alarmMessage').hide();
}



var exportTaskDataTable;

function searchExportTaskInfo() {
	var searchTaskId = $("#taskIdSearchInput").val();
	var noTaskId = (null == searchTaskId || "" == searchTaskId);
	var requestURL = "/jupiter/task/list";
	if (!noTaskId) {
		requestURL = "/jupiter/task/status?task_id=" + searchTaskId;
	}

	$.ajax({
		url: requestURL,
		success: function (data, status, jxhr) {
			data = JSON.parse(data);
			if (data.code == 0) {
                if (null != exportTaskDataTable) {
                    exportTaskDataTable.destroy();
                    exportTaskDataTable = null;

                    var trList = $("#exportTaskListTable tr");
                    trList.remove();
                }

				if (noTaskId) {
					for (var i = 0; i < data.data.length; i++) {
						var taskItem = data.data[i];
						addColumnToTaskTable(taskItem);
					}
				} else {
					addColumnToTaskTable(data.data);
				}

				exportTaskDataTable = $("#exportTaskListTable").DataTable({
					// "data": tableData,
					"columns": [
						{"title": "任务ID"},
						{"title": "导出参数"},
						{"title": "状态"},
						{"title": "任务开始时间"},
						{"title": "任务结束时间"},
						{"title": "记录数"},
						{"title": "下载"}
					],
					"responsive": true,
					"aaSorting": [[3, 'desc']],
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
			} else {
				alert(data.data);
			}
		},
		error: function (data, status, jxhr) {
			if (0 == data.status) {
				alert("请重新登录！");
				location.reload();
			} else {
				alert("请求出错！")
			}
		}
	});
}

function addColumnToTaskTable(taskItem) {
	var newTr = $("<tr></tr>");
	newTr.appendTo("#exportTaskListTable tbody");

	$("<td>" + taskItem.taskId + "</td>").appendTo(newTr);
	var jsonParam = JSON.parse(taskItem.exportParam);
	if (null == jsonParam.bizSubType || "" == jsonParam.bizSubType) {
		delete jsonParam.bizSubType;
	}
	if (null == jsonParam.startTime || "" == jsonParam.startTime || "0" == jsonParam.startTime) {
		delete jsonParam.startTime;
	} else {
		jsonParam.startTime = formatDate(Number.parseInt(jsonParam.startTime));
	}

	if (null == jsonParam.resourceType || "" == jsonParam.resourceType) {
		delete jsonParam.resourceType;
	}

	jsonParam.endTime = formatDate(Number.parseInt(jsonParam.endTime));

	var paramStr = "";
	for (var key in jsonParam) {
		paramStr = paramStr + key + ":" + jsonParam[key] + "<br>";
	}

	$("<td><pre>" + paramStr + "</pre></td>").appendTo(newTr);
	$("<td>" + taskItem.status + "</td>").appendTo(newTr);
	$("<td>" + formatDate(taskItem.taskStartTime) + "</td>").appendTo(newTr);
	$("<td>" + formatDate(taskItem.taskEndTime) + "</td>").appendTo(newTr);
	$("<td>" + taskItem.count + "</td>").appendTo(newTr);
	if (taskItem.status == "SUCCESS") {
		$("<td><a href=\"/jupiter/download/file?task_id=" + taskItem.taskId + "\">下载</a></td>").appendTo(newTr);
	} else {
		$("<td></td>").appendTo(newTr);
	}
}

var searchBizResourceDataTable;
var searchRecordCount = 0;

function searchBizResourceInfo() {
	if(null == $("#searchBizTypeSelect").val() || "" == $("#searchBizTypeSelect").val()) {
		alert("请选择业务类型");
		return;
	}
	if (null == $("#searchEndTime").val() || "" == $("#searchEndTime").val()){
		alert("请选择结束时间");
		return;
	}
	if (null == $("#searchStartTime").val() || "" == $("#searchStartTime").val()){
		alert("请选择开始时间");
		return;
	}
	if (null != $("#searchStartTime").val() && "" != $("#searchStartTime").val()) {
		if (parseDate($("#searchStartTime").val()) > parseDate($("#searchEndTime").val())) {
			alert("开始时间必须小于结束时间");
			return;
		}
	}
	if (null != searchBizResourceDataTable) {
		searchBizResourceDataTable.destroy();
		searchBizResourceDataTable = null;
	}

	searchBizResourceDataTable = $("#searchBizResourceTable").dataTable({
		"columns": [
			{"data": "name"},
			{"data": "type"},
			{"data": "storeScope"},
			{"data": "bizSubType"},
			{"data": "pro"},
			{"data": "meta"},
			{"data": "createTime"},
			{"data": "businessTime"}
		],
		"stripeClasses": ["odd", "even"],  //为奇偶行加上样式，兼容不支持CSS伪类的场合
		"processing": false,  //隐藏加载提示,自行处理
		"serverSide": true,  //启用服务器端分页
		"searching": false,  //禁用原生搜索
		"orderMulti": false,  //启用多列排序
		"order": [],  //取消默认排序查询,否则复选框一列会出现小箭头
		"renderer": "bootstrap",  //渲染样式：Bootstrap和jquery-ui
		"pagingType": "simple_numbers",  //分页样式：simple,simple_numbers,full,full_numbers
		"responsive": true,
		"aaSorting": [[3, 'desc']],
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
		},
		"ajax": function (data, callback, settings) {
			var param = {};
			param.pageSize = data.length;//页面显示记录条数，在页面显示每页显示多少项的时候
			param.start = data.start;//开始的记录序号
			param.pageNumber = (data.start / data.length)+1;//当前页码
			param.bizType = $("#searchBizTypeSelect").val();
			param.bizSubType = $("#searchBizSubTypeSelect").val();
			param.resourceType = $("#searchResourceTypeSelect").val();
			param.startTime = $("#searchStartTime").val();
			if (null != param.startTime && "" != param.startTime) {
				param.startTime = parseDate(param.startTime);
			}

			param.endTime = $("#searchEndTime").val();
			if (null == param.endTime || "" == param.endTime) {
				param.endTime = new Date().getTime();
			} else {
				param.endTime = parseDate(param.endTime);
			}
			$.ajax({
				type: "GET",
				data: param,
				url: "/jupiter/biz/resource/search",
				dataType: "json",
				success: function(result) {
					if (result.code == 0) {
						var returnData = {};
						returnData.draw = data.draw;//这里直接自行返回了draw计数器,应该由后台返回
						returnData.recordsTotal = result.data.total;//返回数据全部记录
						returnData.recordsFiltered = result.data.total;//后台不实现过滤功能，每次查询均视作全部结果
						returnData.data = formatBizResourceResult(result);//返回的数据列表
						searchRecordCount = result.data.total;
						//console.log(returnData);
						//调用DataTables提供的callback方法，代表数据已封装完成并传回DataTables进行渲染
						//此时的数据需确保正确无误，异常判断应在执行此回调前自行处理完毕
						callback(returnData);
					} else {
						alert(result.data);
					}
				},
				error: function(result) {
					if (0 == result.status) {
						alert("请重新登录！");
						location.reload();
					} else {
						alert("请求出错！")
					}
				}
			});
		}
	}).api();
}

function formatBizResourceResult(result) {
	var dataArray = [];
	for (var k = 0; k < result.data.resultList.length; k++) {
		var resourceItem = result.data.resultList[k];
		var dataItem = {};

		var storeScope = "公有云";
		if ("private" == resourceItem.storeScope) {
			storeScope = "私有云";
		}

		var sMetaList = resourceItem.sMetaList;
		var iMetaList = resourceItem.iMetaList;
		var metaList = [];
		if (null != sMetaList) {
			for (var i = 0; i < sMetaList.length; i++) {
				metaList.push(sMetaList[i]);
			}
		}

		if (null != iMetaList) {
			for (var i = 0; i < iMetaList.length; i++) {
				metaList.push(iMetaList[i]);
			}
		}

		var metaTb = "";
		for (var i = 0; i < metaList.length; i++) {
			if (i != 0) {
				metaTb += "<br>";
			}
			metaTb += metaList[i].metaName + ": " + metaList[i].metaValue;
		}

		var bizInfo = resourceItem.bizList[0];
		var iPro = bizInfo.iProList;
		var sPro = bizInfo.sProList;
		var pro = [];

		if (null != iPro) {
			for (var index = 0; index < iPro.length; index++) {
				pro.push(iPro[index]);
			}
		}
		if (null != sPro) {
			for (var index = 0; index < sPro.length; index++) {
				pro.push(sPro[index]);
			}
		}

		var proStr = "";
		for (var j = 0; j < pro.length; j++) {
			var name = pro[j].proName;
			var value = pro[j].proValue;
			proStr = proStr + name + ":" + value + "<br>";
		}

		dataItem.name=resourceItem.name;
		dataItem.type=resourceItem.type;
		dataItem.storeScope = storeScope;
		dataItem.meta = metaTb;
		dataItem.bizSubType = "";
		if (null != bizInfo.bizSubType) {
			dataItem.bizSubType = bizInfo.bizSubType;
		}
		dataItem.pro = proStr;
		dataItem.createTime = formatDate(resourceItem.createTime);
		dataItem.businessTime = formatDate(bizInfo.createTime);
		dataArray.push(dataItem);
	}
	return dataArray;
}


function exportBizResourceInfo() {
	if (0 == searchRecordCount) {
		alert("结果数为空！");
		return;
	}

    if(null == $("#searchBizTypeSelect").val() || "" == $("#searchBizTypeSelect").val()) {
        alert("请选择业务类型");
        return;
    }
    if (null == $("#searchEndTime").val() || "" == $("#searchEndTime").val()){
        alert("请选择结束时间");
        return;
    }
    if (null == $("#searchStartTime").val() || "" == $("#searchStartTime").val()){
        alert("请选择开始时间");
        return;
    }
    if (null != $("#searchStartTime").val() && "" != $("#searchStartTime").val()) {
        if (parseDate($("#searchStartTime").val()) > parseDate($("#searchEndTime").val())) {
            alert("开始时间必须小于结束时间");
            return;
        }
    }

	var param = {};
	param.bizType = $("#searchBizTypeSelect").val();
	param.bizSubType = $("#searchBizSubTypeSelect").val();
	param.resourceType = $("#searchResourceTypeSelect").val();
	param.startTime = $("#searchStartTime").val();
	if (null != param.startTime && "" != param.startTime) {
		param.startTime = parseDate(param.startTime);
	}

	param.endTime = $("#searchEndTime").val();
	if (null == param.endTime || "" == param.endTime) {
		param.endTime = new Date().getTime();
	} else {
		param.endTime = parseDate(param.endTime);
	}

	$.ajax({
		type: "GET",
		data: param,
		url: "/jupiter/task/create",
		dataType: "json",
		success: function(result) {
			if (result.code == 0) {
				$("#exportTaskIdBody").html(result.data.taskId);
				$("#exportTaskId").modal('show');
			} else {
				alert(result.data);
			}
		},
		error: function(result) {
			if (0 == result.status) {
				alert("请重新登录！");
				location.reload();
			} else {
				alert("请求出错！")
			}
		}
	});
}

function initSearchParams() {
	for (var i = 0; i < primaryBusinessList.length; i++) {
		// $("<option value=\"" + primaryBusinessList[i] + "\">">primaryBusinessList[i] + "</option>").appendTo($("#searchBizTypeSelect"));
		var option = $("<option value=\"" + primaryBusinessList[i] + "\" >" + primaryBusinessList[i] + "</option>");
		option.appendTo($("#searchBizTypeSelect"));
	}

	$("#searchEndTime").datetimepicker({
		value: formatDate(new Date().getTime()),
		format:'Y-m-d H:i:s',
		minDate: jQuery('#searchStartTime').val()?jQuery('#searchStartTime').val():false
	});

	$("#searchStartTime").datetimepicker({
		value: formatDate(new Date().getTime() - 7 * 24 * 3600 * 1000),
		format:'Y-m-d H:i:s',
		maxDate: jQuery('#searchEndTime').val()?jQuery('#searchEndTime').val():false
	});
}

function changeBizType1(item) {
	var selectedBizType = $(item).val();
	$("#searchBizSubTypeSelect option").remove();
	$("<option value=\"\" >请选择</option>").appendTo($("#searchBizSubTypeSelect"));
    if (null != businessList[selectedBizType] && "" != businessList[selectedBizType]) {
        var subBusinessList = businessList[selectedBizType].split(",");
        for (var i = 0; i < subBusinessList.length; i++) {
            var option = $("<option value=\"" + subBusinessList[i] + "\" >" + subBusinessList[i] + "</option>");
            option.appendTo($("#searchBizSubTypeSelect"));
        }
    }
}