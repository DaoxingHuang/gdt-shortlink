var oneLinkList;
var landingPageList;
var oneLinkDataTable;

var domain = window.location.host;
//var context-path = "/deeplink-tool";

var deleteBizPermission = true;
//var platformList = ["SAA", "GH"];

var utmParamRegex = /^[0-9a-zA-Z]+$/;

var btnCopyForOneLink = new ClipboardJS("#oneLinkCopyButton");
var qrCodeForOneLink = new QRCode("oneLinkQRInfoDiv");

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
function addOneLink(data) {
    clearOneLinkPathTemplateParam();

    $('#oneLinkIdInput').val('');
    $('#oneLinkUTMSourceInput').val('');
    $('#oneLinkUTMMediumInput').val('');
    $('#oneLinkUTMCampaignInput').val('');
    $('#oneLinkUTMContentInput').val('');
//    $('#oneLinkPlatformSelect').val('');
//  $('#oneLinkNameInput').val('');
    $('#oneLinkLandingPageSelect').val('');
    $('#oneLinkAddAlarmMessage').hide();
    $('#oneLinkCustomizeParamForAddDiv div').remove();
    initCustomizeParamDivForAdd();
    $('#oneLinkAdd').modal('show');
}

// 保存oneLink
function saveOneLink() {
    var utmSource = $('#oneLinkUTMSourceInput').val();
    var utmMedium = $('#oneLinkUTMMediumInput').val();
    var utmCampaign = $('#oneLinkUTMCampaignInput').val();
    var utmContent = $('#oneLinkUTMContentInput').val();
    var platform = "SAA";
    var landingPageId = $('#oneLinkLandingPageSelect').val();
    var landingPageParam = {};

    if (isEmpty(utmSource) || isEmpty(utmMedium) || isEmpty(utmCampaign) || isEmpty(utmContent) || isEmpty(platform) || isEmpty(landingPageId)) {
        $('#oneLinkUTMSourceInput').trigger("onblur");
        $('#oneLinkUTMMediumInput').trigger("onblur");
        $('#oneLinkUTMCampaignInput').trigger("onblur");
        $('#oneLinkUTMContentInput').trigger("onblur");
        $('#oneLinkLandingPageSelect').trigger("onchange");
        return;
    }


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

    var customizeParamList = [];
    var customizeParamIndex = 0;
    var customizeParamInputList = $("#oneLinkCustomizeParamForAddDiv input");
    for (var i = 0; i < customizeParamInputList.length; i += 2) {
        var key = customizeParamInputList[i].value;
        var value = customizeParamInputList[i+1].value;
        var customizeParam = {};
        if (!key || !value) {
            return;
        }
        customizeParam["key"] = key;
        customizeParam["value"] = value;

        customizeParamList[customizeParamIndex] = customizeParam;
        customizeParamIndex++;
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
    param["customizeParamList"] = customizeParamList;

    $.ajaxSetup({
        contentType : 'application/json;charset=utf-8'
    });

    $.post("/deeplink-tool/one_link/add", JSON.stringify(param), function(data){
        if (null != data && "" != data && data.code == 200) {
            $('#oneLinkAdd').modal('hide');
            queryOneLinkList();
        } else {
            showErrorMessage(data);
        }
    }, "json");

}

// 重新激活oneLink
function activateOneLink(id) {
    if (!confirm("Are you sure？")){
            return;
    }
    $.post("/deeplink-tool/one_link/activate?id=" + id, function(response){
        if (null != response && "" != response && response.code == 200) {
            queryOneLinkList();
        } else {
            showErrorMessage(response);
        }
    });
}

// 删除oneLink
function deleteOneLink(id) {
    if (!confirm("Are you sure？")){
            return;
    }
    $.post("/deeplink-tool/one_link/delete?id=" + id, function(response){
        if (null != response && "" != response && response.code == 200) {
            queryOneLinkList();
        } else {
            showErrorMessage(response);
        }
    });
}

// 点击expire按钮
function expireOneLink(id) {
    if (!confirm("Are you sure?")) {
        return;
    }
    $.post("/deeplink-tool/one_link/expire?id=" + id, function(response){
        if (null != response && "" != response && response.code == 200) {
            queryOneLinkList();
        } else {
            showErrorMessage(response);
        }
    });
}

// 点击update utm按钮
function updateOneLinkUtm(oneLinkId) {
    var oneLink;
    for (var i = 0; i < oneLinkList.length; i++) {
        if (oneLinkId == oneLinkList[i].id) {
            oneLink = oneLinkList[i];
            break;
        }
    }
    $("#oneLinkUpdateIdInput").val(oneLinkId);
    $("#oneLinkUpdateUTMSourceInput").val(oneLink.utmSource);
    $("#oneLinkUpdateUTMMediumInput").val(oneLink.utmMedium);
    $("#oneLinkUpdateUTMCampaignInput").val(oneLink.utmCampaign);
    $("#oneLinkUpdateUTMContentInput").val(oneLink.utmContent);
    $('#oneLinkCustomizeParamForUpdateDiv div').remove();
    initCustomizeParamDivForUpdate(oneLink);
    $("#oneLinkUpdate").modal('show');
}

function updateOneLink() {
    var id = $("#oneLinkUpdateIdInput").val();
    var utmSource = $("#oneLinkUpdateUTMSourceInput").val();
    var utmMedium = $("#oneLinkUpdateUTMMediumInput").val();
    var utmCampaign = $("#oneLinkUpdateUTMCampaignInput").val();
    var utmContent = $("#oneLinkUpdateUTMContentInput").val();
    if (isEmpty(utmSource) || isEmpty(utmMedium) || isEmpty(utmCampaign) || isEmpty(utmContent)) {
        $('#oneLinkUpdateUTMSourceInput').trigger("onblur");
        $('#oneLinkUpdateUTMMediumInput').trigger("onblur");
        $('#oneLinkUpdateUTMCampaignInput').trigger("onblur");
        $('#oneLinkUpdateUTMContentInput').trigger("onblur");
        return;
    }

    var customizeParamList = [];
    var customizeParamIndex = 0;
    var customizeParamInputList = $("#oneLinkCustomizeParamForUpdateDiv input");
    for (var i = 0; i < customizeParamInputList.length; i += 2) {
        var key = customizeParamInputList[i].value;
        var value = customizeParamInputList[i+1].value;
        var customizeParam = {};
        if (!key || !value) {
            return;
        }
        customizeParam["key"] = key;
        customizeParam["value"] = value;

        customizeParamList[customizeParamIndex] = customizeParam;
        customizeParamIndex++;
    }

    var param = {};
    param["utmSource"] = utmSource;
    param["utmMedium"] = utmMedium;
    param["utmCampaign"] = utmCampaign;
    param["utmContent"] = utmContent;
    param["id"] = id;
    param["customizeParamList"] = customizeParamList;


    $.ajaxSetup({
        contentType : 'application/json;charset=utf-8'
    });
    $.post("/deeplink-tool/one_link/updateUTM", JSON.stringify(param), function(response){
       if (null != response && "" != response && response.code == 200) {
           $('#oneLinkUpdate').modal('hide');
           queryOneLinkList();
       } else {
           showErrorMessage(response);
       }
   });
}

// 查询所有的oneLink
function queryOneLinkList() {
    $.getJSON("/deeplink-tool/short_link_business/listGDTOneLink", function(response){
            if (response.code != 200) {
                return;
            }
            oneLinkList = response.data;
            drawOneLinkListResult();
    });
}

// 查询oneLink之后，绘制table
function drawOneLinkListResult() {
    var hideDiv = $("#onelink-page-wrapper .default-hide");
    for (var i = 0; i < hideDiv.length; i++) {
        hideDiv[i].classList.remove("default-hide");
    }

    if (null != oneLinkDataTable && "" != oneLinkDataTable) {
        oneLinkDataTable.destroy();
    }

    var trList = $("#oneLinkListTable tr");
    trList.remove();

    var columns = [{"title": "ID"},  {"title": "Name"}, {"title": "LandingPage"}, {"title": "UpdateTime"}, {"title": "Status"}, {"title": "Link"}];
    if (deleteBizPermission) {
        columns.push({"title": "Operation"});
    }

    var newFootTr = $("<tr></tr>");
    newFootTr.appendTo("#oneLinkListTable tfoot");
    for (var i = 0; i < columns.length; i++) {
//      $("<th>" + columns[i].title + "</th>").appendTo(newFootTr);
        $("<th></th>").appendTo(newFootTr);
    }

    for (var i = 0; i < oneLinkList.length; i++) {
        var landingPagePath = oneLinkList[i].landingPagePath;
        if (!landingPagePath) {
            landingPagePath = "";
        }
        var defaultShortLinkName = "oneLink" + "_" + oneLinkList[i].id;

        var newTr = $("<tr></tr>");
        newTr.appendTo("#oneLinkListTable tbody");

        $("<td>" + oneLinkList[i].id + "</td>").appendTo(newTr);
//        $("<td>" + oneLinkList[i].platform + "</td>").appendTo(newTr);
        $("<td>" + oneLinkList[i].name + "</td>").appendTo(newTr);
//        $("<td>" + oneLinkList[i].utm + "</td>").appendTo(newTr);
        $("<td>" + oneLinkList[i].landingPageVo.name + "</td>").appendTo(newTr);
        $("<td>" + formatDate(oneLinkList[i].updateTime) + "</td>").appendTo(newTr);
        $("<td>" + oneLinkList[i].status + "</td>").appendTo(newTr);
        if (!oneLinkList[i].shortLinkId && "ACTIVE" == oneLinkList[i].status) {
            $("<td class=\"center\"> " +
                "<button class=\"btn btn-outline btn-link btn-s\" onClick=\"showDetailOneLink('" + oneLinkList[i].id + "')\" >Show Detail</button>" +
                " | " +
                "<button class=\"btn btn-outline btn-link btn-s\" onClick=\"initCreateShortLinkParamForOneLink('" + oneLinkList[i].id + "', '" + defaultShortLinkName + "')\" >Create ShortLink</button>" +
            "</td>").appendTo(newTr);
        } else if (!oneLinkList[i].shortLinkId && "OFF" == oneLinkList[i].status) {
            $("<td class=\"center\"> " +
                "<button class=\"btn btn-outline btn-link btn-s\" onClick=\"showDetailOneLink('" + oneLinkList[i].id + "')\" >Show Detail</button>" +
            "</td>").appendTo(newTr);
        } else {
            $("<td class=\"center\"> " +
                "<button class=\"btn btn-outline btn-link btn-s\" onClick=\"showDetailOneLink('" + oneLinkList[i].id + "')\" >Show Detail</button>" +
                " | " +
                "<button class=\"btn btn-outline btn-link btn-s\" onClick=\"showOneLinkRelatedShortLink('" + oneLinkList[i].shortLinkId + "')\" >Show ShortLink</button>" +
            "</td>").appendTo(newTr);
        }
        if (deleteBizPermission) {
            if ("ACTIVE" == oneLinkList[i].status) {
                $("<td class=\"center\"> " +
                " <button class=\"btn btn-outline btn-link btn-s\" onClick=\"updateOneLinkUtm(" + oneLinkList[i].id + ")\" >Edit UTM</button> | " +
                "<button class=\"btn btn-outline btn-link btn-s\" onClick=\"expireOneLink(" + oneLinkList[i].id + ")\" >Expire</button> </td>").appendTo(newTr);
             } else {
                $("<td class=\"center\"> <button class=\"btn btn-outline btn-link btn-s\" onClick=\"activateOneLink(" + oneLinkList[i].id + ")\" >Activate</button> </td>").appendTo(newTr);
             }
        }
    }

    oneLinkDataTable = $('#oneLinkListTable').DataTable({
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
                if (i == 2 || i == 4) {
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

    $('#oneLinkListTable').on( 'length.dt', function ( e, settings, len ) {
        console.log( 'New page length: '+len );
    } );

    $('#oneLinkListTable').on( 'page.dt', function () {
        var info = oneLinkDataTable.page.info();
        console.log( 'Showing page: '+info.page+' of '+info.pages );
    } );
}

// 展示oneLink的link详细信息，包括二维码
function showDetailOneLink(id) {
    var currentOneLink;
    for (var i = 0; i < oneLinkList.length; i++) {
        if (id == oneLinkList[i].id) {
            currentOneLink = oneLinkList[i];
            break;
        }
    }
    if (null == currentOneLink.landingPagePath || "" == currentOneLink.landingPagePath) {
        $("#oneLinkDetailLandingPagePath").hide();
        $("#oneLinkDetailLandingPagePath").prev().hide()
    } else {
        $("#oneLinkDetailLandingPagePath").text(currentOneLink.landingPagePath);
        $("#oneLinkDetailLandingPagePath").prev().show()
        $("#oneLinkDetailLandingPagePath").show();
    }
    $("#detailOneLink").text(currentOneLink.link);
    $("#oneLinkCopyButton").attr("data-clipboard-text", currentOneLink.link);
    qrCodeForOneLink.clear();
    qrCodeForOneLink.makeCode(currentOneLink.link);
    $('#onelinkInfo').modal('show');
}

// 添加oneLink时，初始化platform参数
//function initOneLinkPlatformParams() {
//    $("#oneLinkPlatformSelect").find("option:selected").text("");
//    $("#oneLinkPlatformSelect").empty();
//    for (var i = 0; i < platformList.length; i++) {
//        var option = $("<option value=\"" + platformList[i] + "\" >" + platformList[i] + "</option>");
//        option.appendTo($("#oneLinkPlatformSelect"));
//    }
//}

//// platform参数变更事件
//function changeOneLinkPlatform(item) {
//    initLandingPageParams();
//}

// 添加oneLink时，初始化landingPage参数
function initOneLinkLandingPageParams() {
    $("#oneLinkLandingPageSelect").find("option:selected").text("");
    $("#oneLinkLandingPageSelect").empty();
    clearOneLinkPathTemplateParam();

    $.getJSON("/deeplink-tool/landing_page/listAll?platform=SAA", function(response){
        if (null != response && "" != response && response.code == 200) {
            landingPageList = response.data;
            for (var i = 0; i < landingPageList.length; i++) {
                var option = $("<option value=\"" + landingPageList[i].id + "\" >" + landingPageList[i].name + "</option>");
                option.appendTo($("#oneLinkLandingPageSelect"));
            }
        } else {
            showErrorMessage(response);
        }

//        changeOneLinkLandingPage($("#oneLinkLandingPageSelect"));
        $("#oneLinkLandingPageSelect").trigger("change");
    });
}

// landingPage参数变更事件
function changeOneLinkLandingPage(item) {
    checkSelectValue(item);
    clearOneLinkPathTemplateParam();
    var landingPageId = $("#oneLinkLandingPageSelect").val();
    var landingPage;
    for (var i = 0; i < landingPageList.length; i++) {
        if (landingPageId == landingPageList[i].id) {
            landingPage = landingPageList[i];
            break;
        }
    }

    if (landingPage.native) {
        $("#oneLinkFullPathLabel").val('');
        $("#oneLinkFullPathLabel").parent().hide();
    } else {
        $("#oneLinkFullPathLabel").val(landingPage.pathTemplate);
        $("#oneLinkFullPathLabel").parent().show();
    }

    if (null != landingPage.paramList && landingPage.paramList.length > 0) {
        var paramDivHtml = $("<div id=\"pathTemplateParamDiv\"></div>");
        for (var i = 0; i< landingPage.paramList.length; i++) {
            var paramInput = $("<div class=\"oneLink-form-group\"><label> " + landingPage.paramList[i] + "：</label><input class=\"jupiter-form-control\" name=\"\" value=\"\"  onBlur=\"replaceOneLinkPathTemplateParam()\" id=\"" + landingPage.paramList[i] + "Param\" /></div>");
            paramInput.appendTo(paramDivHtml);
        }
        paramDivHtml.insertAfter(item.parentNode);

    }
}

// 填写PathTemplate参数之后，生成完整的path
function replaceOneLinkPathTemplateParam() {
    var landingPageId = $("#oneLinkLandingPageSelect").val();
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
        $("#oneLinkFullPathLabel").val(pathTemplate);
    }
}

// 添加oneLink时，清除之前的参数
function clearOneLinkPathTemplateParam() {
    $("#pathTemplateParamDiv").remove();
    $("#landingPagePathTemplate").text();
    $("#oneLinkFullPathLabel").val("");
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

function checkSelectValue(item) {
    var selectedValue = item.value;
    if (null == selectedValue || "" == selectedValue) {
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

function initCreateShortLinkParamForOneLink(oneLinkId, defaultShortLinkName) {
    var currentDate = new Date();
    var currentSecond = currentDate.getTime() / 1000 - currentDate.getHours() * 3600 - currentDate.getMinutes() * 60 - currentDate.getSeconds();
    $("#oneToShortLinkExpiredTimeInput").datetimepicker({
        value: formatDate(currentSecond * 1000 + 31 * 24 * 3600 * 1000 - 1000),
        format:'Y-m-d H:i:s'
//        ,
//        maxDate: jQuery('#searchEndTime').val()?jQuery('#searchEndTime').val():false
    });
    $("#oneToShortLinkIdInput").val(oneLinkId);
    $("#oneToShortLinkNameInput").val(defaultShortLinkName);

    $('#oneToShortLinkCreator').modal('show');
}

function createOneToShortLink() {
    var oneLinkId = $("#oneToShortLinkIdInput").val();
    var shortLinkName = $("#oneToShortLinkNameInput").val();
    var expiredTime = $("#oneToShortLinkExpiredTimeInput").val();


    if (isEmpty(oneLinkId) || isEmpty(shortLinkName) || isEmpty(expiredTime)) {
        return;
    }

    var param = {};
    param["oneLinkId"] = oneLinkId;
    param["expiredTime"] = expiredTime;
    param["name"] = shortLinkName;

    $.ajaxSetup({
        contentType : 'application/json;charset=utf-8'
    });

    $.post("/deeplink-tool/short_link_business/generateForOneLink?gdtOneLinkId=" + oneLinkId, JSON.stringify(param), function(data){
        if (null != data && "" != data && data.code == 200) {
            $('#oneToShortLinkCreator').modal('hide');
//          queryOneLinkList();
            queryShortLinkList();
            showPage('shortlink-page-wrapper')
            $.getJSON("/deeplink-tool/short_link_business/findIdByGDTOneLink?gdtOneLinkId=" + oneLinkId, function(response){
                if (null != response && "" != response && response.code == 200) {
                    var shortLinkId = response.data;
                    if (null != shortLinkId) {
                        showDetailShortLink(shortLinkId);
                    }
                } else {
                    showErrorMessage(response);
                }
            });

        } else {
            showErrorMessage(data);
        }
    }, "json");
}


function showOneLinkRelatedShortLink(shortLinkId) {
    $.ajaxSettings.async = false;
    queryShortLinkList();
    $.ajaxSettings.async = true;
    showPage('shortlink-page-wrapper')
    showDetailShortLink(shortLinkId);
}

function initCustomizeParamDivForAdd() {
    $("<div class=\"oneLink-form-group\"><label></label><span class=\"glyphicon glyphicon-plus\" aria-hidden=\"true\" onClick=\"addCustomizeParamInput(this)\"></span></div>").appendTo($('#oneLinkCustomizeParamForAddDiv'));
}

function initCustomizeParamDivForUpdate(oneLink) {
    var plusButton = $("<div class=\"oneLink-form-group\"><label></label><span class=\"glyphicon glyphicon-plus\" aria-hidden=\"true\" onClick=\"addCustomizeParamInput(this)\"></span></div>")
    plusButton.appendTo($('#oneLinkCustomizeParamForUpdateDiv'));

    if (!oneLink.customizeParamList) {
        return;
    }

    for (var i = 0; i < oneLink.customizeParamList.length; i++) {
        var customizeParamInput = $("<div class=\"oneLink-form-group\">" +
            "<label><input class=\"jupiter-form-control\" name=\"\" value=\"" + oneLink.customizeParamList[i]["key"] + "\" id=\"\" placeholder=\"input key\" onBlur=\"checkCustomizeParamKeyInput(this)\" /></label>" +
            "<input class=\"jupiter-form-control\" name=\"\" value=\"" + oneLink.customizeParamList[i]["value"] + "\" id=\"\" placeholder=\"input value\" onBlur=\"checkCustomizeParamInput(this)\" />" +
            "<span class=\"glyphicon glyphicon-remove\" aria-hidden=\"true\" onClick=\"removeCustomizeParamInput(this)\"></span>" +
            "</div>");
            customizeParamInput.insertBefore(plusButton);
    }
}

function addCustomizeParamInput(item) {
    var inputList = $(item).parent(".oneLink-form-group").parent('.oneLink-form-group').find('input');
    for (var i = 0; i < inputList.length; i++) {
        if (!inputList[i].value) {
            customizeParamInputAlter(inputList[i], true)
            return;
        } else {
            customizeParamInputAlter(inputList[i], false)
        }
    }

    var customizeParamInput = $("<div class=\"oneLink-form-group\">" +
    "<label><input class=\"jupiter-form-control\" name=\"\" value=\"\" id=\"\" placeholder=\"input key\" onBlur=\"checkCustomizeParamKeyInput(this)\" /></label>" +
    "<input class=\"jupiter-form-control\" name=\"\" value=\"\" id=\"\" placeholder=\"input value\" onBlur=\"checkCustomizeParamInput(this)\" />" +
    "<span class=\"glyphicon glyphicon-remove\" aria-hidden=\"true\" onClick=\"removeCustomizeParamInput(this)\"></span>" +
    "</div>");
    customizeParamInput.insertBefore(item.parentNode);
}

function checkCustomizeParamKeyInput(item) {
    var inputList = $(item).parent(".oneLink-form-group").parent('.oneLink-form-group').find('input');
    var currentKey = item.value;

    checkCustomizeParamInput(item);

    for (var i = 0; i < inputList.length; i += 2) {
        if (item == inputList[i]) {
            continue;
        }

        if (currentKey == inputList[i].value) {
            customizeParamInputAlter(inputList[i], true)
            return;
        } else {
            customizeParamInputAlter(inputList[i], false)
        }
    }
}

function checkCustomizeParamInput(item) {
    var currentKey = item.value;

    if (!currentKey) {
        customizeParamInputAlter(item, true)
        return;
    } else {
        customizeParamInputAlter(item, false)
    }
}


function removeCustomizeParamInput(item) {
    item.parentNode.remove();
}

function customizeParamInputAlter(item, shouldAlter) {
    if (!shouldAlter) {
        item.classList.remove("alarm-border");
    } else {
        item.classList.add("alarm-border");
    }
}