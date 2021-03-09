var deepLinkList;
var landingPageList;
var deepLinkDataTable;

var domain = window.location.host;
//var context-path = "/deeplink-tool";

var deleteBizPermission = true;
var platformList = ["SAA", "GH"];

var utmParamRegex = /^[0-9a-zA-Z]+$/;

var btnCopy = new ClipboardJS("#deepLinkCopyButton");
var qrCode = new QRCode("linkQRInfoDiv");

$.ajaxSetup({
   contentType:"application/x-www-form-urlencoded;charset=utf-8",
   complete:function(XMLHttpRequest,textStatus){
     //通过XMLHttpRequest取得响应头，sessionstatus，
       if ( 401 == XMLHttpRequest.status) {
           var redirectUrl = XMLHttpRequest.responseJSON.data;
           location.href = redirectUrl;
       }
  }
});

// 点击add按钮
function addDeepLink(data) {
    clearPathTemplateParam();

	$('#deepLinkIdInput').val('');
	$('#deepLinkUTMSourceInput').val('');
	$('#deepLinkUTMMediumInput').val('');
	$('#deepLinkUTMCampaignInput').val('');
	$('#deepLinkUTMContentInput').val('');
	$('#deepLinkPlatformSelect').val('');
//	$('#deepLinkNameInput').val('');
	$('#deepLinkLandingPageSelect').val('');
	$('#deepLinkAddAlarmMessage').hide();
	$('#deepLinkAdd').modal('show');
}

// 保存deepLink
function saveDeepLink() {
	var utmSource = $('#deepLinkUTMSourceInput').val();
	var utmMedium = $('#deepLinkUTMMediumInput').val();
	var utmCampaign = $('#deepLinkUTMCampaignInput').val();
	var utmContent = $('#deepLinkUTMContentInput').val();
	var platform = $('#deepLinkPlatformSelect').val();
//	var name = $('#deepLinkNameInput').val();
	var landingPageId = $('#deepLinkLandingPageSelect').val();
	var landingPageParam = {};


    var landingPage;
    for (var i = 0; i < landingPageList.length; i++) {
        if (landingPageId == landingPageList[i].id) {
            landingPage = landingPageList[i];
            break;
        }
    }

    var pathTemplate = landingPage.pathTemplate;
    if (null != landingPage.paramList && landingPage.paramList.length > 0) {
        for (var i = 0; i< landingPage.paramList.length; i++) {
            var param = landingPage.paramList[i];
            var paramInputId = param + "Param";
            var value = $("#" + paramInputId).val();
            if (null == value || "" == value) {
                return;
            }

            if (value.indexOf(" ") > 0) {
                landingPageParam[param] = encodeURIComponent(value);
            } else {
                landingPageParam[param] = value;
            }
        }
    }


	if (isEmpty(utmSource) || isEmpty(utmMedium) || isEmpty(utmCampaign) || isEmpty(utmContent) || isEmpty(platform) || isEmpty(landingPageId)) {
		return;
	}

    var param = {};
    param["utmSource"] = utmSource;
    param["utmMedium"] = utmMedium;
    param["utmCampaign"] = utmCampaign;
    param["utmContent"] = utmContent;
    param["platform"] = platform;
    param["name"] = name;
    param["landingPageId"] = landingPageId;
    param["paramMap"] = landingPageParam;

    $.ajaxSetup({
        contentType : 'application/json;charset=utf-8'
    });

	$.post("/deeplink-tool/deep_link/add", JSON.stringify(param), function(data){
		if (null != data && "" != data && data.code == 200) {
			$('#deepLinkAdd').modal('hide');
			queryDeepLinkList();
		} else {
			showErrorMessage(data);
		}
	}, "json");

}

// 重新激活deepLink
function activateDeepLink(id) {
    if (!confirm("Are you sure？")){
    		return;
    }
    $.post("/deeplink-tool/deep_link/activate?id=" + id, function(response){
        if (null != response && "" != response && response.code == 200) {
            queryDeepLinkList();
        } else {
            showErrorMessage(response);
        }
    });
}

// 删除deepLink
function deleteDeepLink(id) {
    if (!confirm("Are you sure？")){
    		return;
    }
    $.post("/deeplink-tool/deep_link/delete?id=" + id, function(response){
        if (null != response && "" != response && response.code == 200) {
            queryDeepLinkList();
        } else {
            showErrorMessage(response);
        }
    });
}

// 点击expire按钮
function expireDeepLink(id) {
    if (!confirm("Are you sure?")) {
        return;
    }
    $.post("/deeplink-tool/deep_link/expire?id=" + id, function(response){
        if (null != response && "" != response && response.code == 200) {
            queryDeepLinkList();
        } else {
            showErrorMessage(response);
        }
    });
}

// 点击update utm按钮
function updateUtm(deepLinkId) {
    var deepLink;
    for (var i = 0; i < deepLinkList.length; i++) {
        if (deepLinkId == deepLinkList[i].id) {
            deepLink = deepLinkList[i];
            break;
        }
    }
    $("#deepLinkUpdateIdInput").val(deepLinkId);
    $("#deepLinkUpdateUTMSourceInput").val(deepLink.utmSource);
    $("#deepLinkUpdateUTMMediumInput").val(deepLink.utmMedium);
    $("#deepLinkUpdateUTMCampaignInput").val(deepLink.utmCampaign);
    $("#deepLinkUpdateUTMContentInput").val(deepLink.utmContent);

    $("#deepLinkUpdate").modal('show');
}

function updateDeepLink() {
    var id = $("#deepLinkUpdateIdInput").val();
    var utmSource = $("#deepLinkUpdateUTMSourceInput").val();
    var utmMedium = $("#deepLinkUpdateUTMMediumInput").val();
    var utmCampaign = $("#deepLinkUpdateUTMCampaignInput").val();
    var utmContent = $("#deepLinkUpdateUTMContentInput").val();

    var param = {};
    param["utmSource"] = utmSource;
    param["utmMedium"] = utmMedium;
    param["utmCampaign"] = utmCampaign;
    param["utmContent"] = utmContent;
    param["id"] = id;


    $.ajaxSetup({
        contentType : 'application/json;charset=utf-8'
    });
    $.post("/deeplink-tool/deep_link/updateUTM", JSON.stringify(param), function(response){
       if (null != response && "" != response && response.code == 200) {
           $('#deepLinkUpdate').modal('hide');
           queryDeepLinkList();
       } else {
           showErrorMessage(response);
       }
   });
}

// 查询所有的deepLink
function queryDeepLinkList() {
	$.getJSON("/deeplink-tool/short_link_business/listDeepLink", function(response){
	        if (response.code != 200) {
	            return;
	        }
			deepLinkList = response.data;
			drawDeepLinkListResult();
	});
}

// 查询deepLink之后，绘制table
function drawDeepLinkListResult() {
	var hideDiv = $("#deeplink-page-wrapper .default-hide");
	for (var i = 0; i < hideDiv.length; i++) {
		hideDiv[i].classList.remove("default-hide");
	}

	if (null != deepLinkDataTable && "" != deepLinkDataTable) {
		deepLinkDataTable.destroy();
	}

	var trList = $("#deepLinkListTable tr");
	trList.remove();

	var columns = [{"title": "ID"},{"title": "Platform"},	{"title": "Name"}, {"title": "LandingPage"}, {"title": "UpdateTime"}, {"title": "Status"}, {"title": "Link"}];
	if (deleteBizPermission) {
		columns.push({"title": "Operation"});
	}

	var newFootTr = $("<tr></tr>");
	newFootTr.appendTo("#deepLinkListTable tfoot");
	for (var i = 0; i < columns.length; i++) {
//	    $("<th>" + columns[i].title + "</th>").appendTo(newFootTr);
	    $("<th></th>").appendTo(newFootTr);
	}

	for (var i = 0; i < deepLinkList.length; i++) {
	    var landingPagePath = deepLinkList[i].landingPagePath;
	    if (!landingPagePath) {
	        landingPagePath = "";
	    }
	    var defaultShortLinkName = "deepLink" + "_" + deepLinkList[i].platform + "_" + deepLinkList[i].id

		var newTr = $("<tr></tr>");
		newTr.appendTo("#deepLinkListTable tbody");

		$("<td>" + deepLinkList[i].id + "</td>").appendTo(newTr);
		$("<td>" + deepLinkList[i].platform + "</td>").appendTo(newTr);
		$("<td>" + deepLinkList[i].name + "</td>").appendTo(newTr);
//		$("<td>" + deepLinkList[i].utm + "</td>").appendTo(newTr);
		$("<td>" + deepLinkList[i].landingPageVo.name + "</td>").appendTo(newTr);
		$("<td>" + formatDate(deepLinkList[i].updateTime) + "</td>").appendTo(newTr);
		$("<td>" + deepLinkList[i].status + "</td>").appendTo(newTr);

		if ("GH" == deepLinkList[i].platform) {
		    $("<td class=\"center\"> " +
                "<button class=\"btn btn-outline btn-link btn-s\" onClick=\"showDetailLink('" + deepLinkList[i].id + "')\" >Show Detail</button>" +
                "</td>").appendTo(newTr);
		} else if (!deepLinkList[i].shortLinkId && "ACTIVE" == deepLinkList[i].status) {
    		$("<td class=\"center\"> " +
	    	    "<button class=\"btn btn-outline btn-link btn-s\" onClick=\"showDetailLink('" + deepLinkList[i].id + "')\" >Show Detail</button>" +
		        " | " +
                "<button class=\"btn btn-outline btn-link btn-s\" onClick=\"initCreateShortLinkParam('" + deepLinkList[i].id + "', '" + defaultShortLinkName + "')\" >Create ShortLink</button>" +
                "</td>").appendTo(newTr);
        } else if (!deepLinkList[i].shortLinkId && "OFF" == deepLinkList[i].status) {
            $("<td class=\"center\"> " +
                "<button class=\"btn btn-outline btn-link btn-s\" onClick=\"showDetailLink('" + deepLinkList[i].id + "')\" >Show Detail</button>" +
                "</td>").appendTo(newTr);
        } else {
            $("<td class=\"center\"> " +
                "<button class=\"btn btn-outline btn-link btn-s\" onClick=\"showDetailLink('" + deepLinkList[i].id + "')\" >Show Detail</button>" +
                " | " +
		        "<button class=\"btn btn-outline btn-link btn-s\" onClick=\"showDeepLinkRelatedShortLink('" + deepLinkList[i].shortLinkId + "')\" >Show ShortLink</button>" +
		        "</td>").appendTo(newTr);
        }
//        else {
//            $("<td class=\"center\"> " +
//                "<button class=\"btn btn-outline btn-link btn-s\" onClick=\"showDetailLink('" + deepLinkList[i].id + "')\" >Show Detail</button>" +
//                "</td>").appendTo(newTr);
//        }
		if (deleteBizPermission) {
		    if ("ACTIVE" == deepLinkList[i].status) {
			    $("<td class=\"center\"> " +
			    " <button class=\"btn btn-outline btn-link btn-s\" onClick=\"updateUtm(" + deepLinkList[i].id + ")\" >Edit UTM</button> | " +
			    "<button class=\"btn btn-outline btn-link btn-s\" onClick=\"expireDeepLink(" + deepLinkList[i].id + ")\" >Expire</button> </td>").appendTo(newTr);
			 } else {
			    $("<td class=\"center\"> <button class=\"btn btn-outline btn-link btn-s\" onClick=\"activateDeepLink(" + deepLinkList[i].id + ")\" >Activate</button> </td>").appendTo(newTr);
			 }
		}
	}

	deepLinkDataTable = $('#deepLinkListTable').DataTable({
		"columns": columns,
		"responsive": true,
		"aaSorting": [[0, 'desc']],
//        "ordering": false,
		"sPaginationType": "full_numbers",
		"lengthMenu": [10, 50, 100, 500],
		"oLanguage": {
			"sProcessing": "loading......",
			"sLengthMenu": "per page _MENU_ records",
			"sZeroRecords": "Sorry, no data.",
			"sEmptyTable": "No data!",
			"sInfo": "current show _START_ to _END_ records，total _TOTAL_ records",
			"sInfoFiltered": "total _MAX_ records",
			"sSearch": "Search",
			"oPaginate": {
				"sFirst": "First",
				"sPrevious": "Previous",
				"sNext": "Next",
				"sLast": "Last"
			}
		},
		initComplete: function () {
            var api = this.api();
            api.columns().indexes().flatten().each( function ( i ) {
                if (i == 1 || i == 3 || i == 5) {
                    var column = api.column( i );
                    var select = $('<select><option value=""></option></select>')
                        .appendTo( $(column.footer()).empty() )
                        .on( 'change', function () {
                            var val = $.fn.dataTable.util.escapeRegex(
                                $(this).val()
                            );
                            column
                                .search( val ? '^'+val+'$' : '', true, false )
                                .draw();
                        } );
                    column.data().unique().sort().each( function ( d, j ) {
                        select.append( '<option value="'+d+'">'+d+'</option>' )
                    } );
                }
            } );
        }
	});

	$('#deepLinkListTable').on( 'length.dt', function ( e, settings, len ) {
        console.log( 'New page length: '+len );
    } );

    $('#deepLinkListTable').on( 'page.dt', function () {
        var info = deepLinkDataTable.page.info();
        console.log( 'Showing page: '+info.page+' of '+info.pages );
    } );
}

// 展示deepLink的link详细信息，包括二维码
function showDetailLink(id) {
    var currentDeepLink;
    for (var i = 0; i < deepLinkList.length; i++) {
        if (id == deepLinkList[i].id) {
            currentDeepLink = deepLinkList[i];
            break;
        }
    }
    if (null == currentDeepLink.landingPagePath || "" == currentDeepLink.landingPagePath) {
        $("#detailLandingPagePath").hide();
        $("#detailLandingPagePath").prev().hide()
    } else {
        $("#detailLandingPagePath").text(currentDeepLink.landingPagePath);
        $("#detailLandingPagePath").prev().show()
        $("#detailLandingPagePath").show();
    }
    $("#detailLink").text(currentDeepLink.link);
    $("#deepLinkCopyButton").attr("data-clipboard-text", currentDeepLink.link);
    qrCode.clear();
    qrCode.makeCode(currentDeepLink.link);
    $('#linkInfo').modal('show');
}

// 添加deepLink时，初始化platform参数
function initDeepLinkPlatformParams() {
    $("#deepLinkPlatformSelect").find("option:selected").text("");
    $("#deepLinkPlatformSelect").empty();
	for (var i = 0; i < platformList.length; i++) {
		var option = $("<option value=\"" + platformList[i] + "\" >" + platformList[i] + "</option>");
		option.appendTo($("#deepLinkPlatformSelect"));
	}
}

// platform参数变更事件
function changeDeepLinkPlatform(item) {
    initLandingPageParams();
}

// 添加deepLink时，初始化landingPage参数
function initLandingPageParams() {
    $("#deepLinkLandingPageSelect").find("option:selected").text("");
    $("#deepLinkLandingPageSelect").empty();
    clearPathTemplateParam();

    $.getJSON("/deeplink-tool/landing_page/listAll?platform=" + $("#deepLinkPlatformSelect").val(), function(response){
        if (null != response && "" != response && response.code == 200) {
            landingPageList = response.data;
            for (var i = 0; i < landingPageList.length; i++) {
                var option = $("<option value=\"" + landingPageList[i].id + "\" >" + landingPageList[i].name + "</option>");
                option.appendTo($("#deepLinkLandingPageSelect"));
            }
        } else {
            showErrorMessage(response);
        }

//        changeDeepLinkLandingPage($("#deepLinkLandingPageSelect"));
        $("#deepLinkLandingPageSelect").trigger("change");
    });
}

// landingPage参数变更事件
function changeDeepLinkLandingPage(item) {
    clearPathTemplateParam();
    var landingPageId = $("#deepLinkLandingPageSelect").val();
    var landingPage;
    for (var i = 0; i < landingPageList.length; i++) {
        if (landingPageId == landingPageList[i].id) {
            landingPage = landingPageList[i];
            break;
        }
    }

    if (landingPage.native) {
        $("#fullPathLabel").val('');
        $("#fullPathLabel").parent().hide();
    } else {
        $("#fullPathLabel").val(landingPage.pathTemplate);
        $("#fullPathLabel").parent().show();
    }

    if (null != landingPage.paramList && landingPage.paramList.length > 0) {
        var paramDivHtml = $("<div id=\"pathTemplateParamDiv\"></div>");
        for (var i = 0; i< landingPage.paramList.length; i++) {
            var paramInput = $("<div class=\"jupiter-form-group\"><label> " + landingPage.paramList[i] + "：</label><input class=\"jupiter-form-control\" name=\"\" value=\"\"  onBlur=\"replacePathTemplateParam()\" id=\"" + landingPage.paramList[i] + "Param\" /></div>");
            paramInput.appendTo(paramDivHtml);
        }
        paramDivHtml.insertAfter(item.parentNode);

    }
}

// 填写PathTemplate参数之后，生成完整的path
function replacePathTemplateParam() {
    var landingPageId = $("#deepLinkLandingPageSelect").val();
    var landingPage;
    for (var i = 0; i < landingPageList.length; i++) {
        if (landingPageId == landingPageList[i].id) {
            landingPage = landingPageList[i];
            break;
        }
    }

    var pathTemplate = landingPage.pathTemplate;
    if (null != landingPage.paramList && landingPage.paramList.length > 0) {
//        var paramDivHtml = $("<div id=\"pathTemplateParamDiv\"></div>");
        for (var i = 0; i< landingPage.paramList.length; i++) {
            var param = landingPage.paramList[i];
            var paramInputId = param + "Param";
            var value = $("#" + paramInputId).val();
            if (null != value && "" != value) {
                pathTemplate = pathTemplate.replace("{" + param + "}", encodeURIComponent(value));
            }
        }
        $("#fullPathLabel").val(pathTemplate);
    }
}

// 添加deepLink时，清除之前的参数
function clearPathTemplateParam() {
    $("#pathTemplateParamDiv").remove();
    $("#landingPagePathTemplate").text();
    $("#fullPathLabel").val("");
}

// 检查utm参数的输入
function checkUtmInput(item) {
    var inputUtmParam = item.value;
    if (null == inputUtmParam || "" == inputUtmParam || !utmParamRegex.test(inputUtmParam)) {
        item.classList.add("alarm-border");
    } else {
        item.classList.remove("alarm-border");
    }
}

function showErrorMessage(data) {
	if (null != data.message) {
		alert(data.message);
	} else {
		alert("system error");
	}
}

function isEmpty(param) {
    return null == param || "" == param;
}

function initCreateShortLinkParam(deepLinkId, defaultShortLinkName) {
    var currentDate = new Date();
    var currentSecond = currentDate.getTime() / 1000 - currentDate.getHours() * 3600 - currentDate.getMinutes() * 60 - currentDate.getSeconds();
    $("#deepToShortLinkExpiredTimeInput").datetimepicker({
        value: formatDate(currentSecond * 1000 + 31 * 24 * 3600 * 1000 - 1000),
        format:'Y-m-d H:i:s'
//        ,
//        maxDate: jQuery('#searchEndTime').val()?jQuery('#searchEndTime').val():false
    });
    $("#deepToShortLinkIdInput").val(deepLinkId);
    $("#deepToShortLinkNameInput").val(defaultShortLinkName);

	$('#deepToShortLinkCreator').modal('show');
}

function createDeepToShortLink() {
    var deepLinkId = $("#deepToShortLinkIdInput").val();
    var shortLinkName = $("#deepToShortLinkNameInput").val();
    var expiredTime = $("#deepToShortLinkExpiredTimeInput").val();


	if (isEmpty(deepLinkId) || isEmpty(shortLinkName) || isEmpty(expiredTime)) {
		return;
	}

    var param = {};
    param["deepLinkId"] = deepLinkId;
    param["expiredTime"] = expiredTime;
    param["name"] = shortLinkName;

    $.ajaxSetup({
        contentType : 'application/json;charset=utf-8'
    });

	$.post("/deeplink-tool/short_link_business/generateForDeepLink?deepLinkId=" + deepLinkId, JSON.stringify(param), function(data){
		if (null != data && "" != data && data.code == 200) {
			$('#deepToShortLinkCreator').modal('hide');
//			queryDeepLinkList();
            queryShortLinkList();
            showPage('shortlink-page-wrapper')
            $.getJSON("/deeplink-tool/short_link_business/findIdByDeepLink?deepLinkId=" + deepLinkId, function(response){
                if (null != response && "" != response && response.code == 200) {
                    var shortLinkId = response.data;
                    if (null != shortLinkId) {
                        showDetailShortLink(shortLinkId);
                    }
                } else {
                    showErrorMessage(response);
                }

        //        changeDeepLinkLandingPage($("#deepLinkLandingPageSelect"));
//                $("#deepLinkLandingPageSelect").trigger("change");
            });

		} else {
			showErrorMessage(data);
		}
	}, "json");
}

function showDeepLinkRelatedShortLink(shortLinkId) {
    queryShortLinkList();
    showPage('shortlink-page-wrapper')
    showDetailShortLink(shortLinkId);
}
