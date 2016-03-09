

function getListOfFunctionalProcesses() {

    return [
        {id: 1, name: "1FuncProce10"},
        {id: 2, name: "2FuncProce20"},
        {id: 3, name: "3FuncProce30"},
        {id: 4, name: "4FuncProce40"},
        {id: 5, name: "5FuncProce50"}
    ];

}

var gridConfig = {
    view: "datatable",
    id: "fpGrid",
    select: "cell",
    multiselect: true,
    dragColumn: true,
    drag: true,
    scroll: "xy",
    leftSplit: 1,
    tooltip: function (row, columnContainer) {
        var rowValue = row[columnContainer.column.id];
        if (rowValue != "-") {
            return "<i>" + " display attributes here for cell:: " + row.id + ":" + columnContainer.column.id + "</i>";
        }
        else {
            return "<i>" + rowValue + "</i>";
        }
    },

    on: {
        onItemDblClick: function (id, e) {

            var gridCoOrds = this.getSelectedId(true, true);
            var rowNum = gridCoOrds[0].slice(0, gridCoOrds[0].indexOf("_"));
            var colName = gridCoOrds[0].slice(gridCoOrds[0].indexOf("_") + 1, gridCoOrds[0].length);

            var item = this.getItem(id);
            webix.message(item[colName]);

            //we need to send id to form to know what item to edit
            var formData = {type: item[colName], id: item.id, dg: colName};
            var form = $$("editDM");
            form.setValues(formData);

            var formWindow = $$("editDMWindow");

            readAttributesFromAServer();
            formWindow.show();


        }
    }
};

var contextMenu = {
    view: "contextmenu",
    id: "ctxMenu",
    data: [
        {id: "DGUsage", value: "Where else this dataGroup is used?"},
        {id: "removeStep", value: "Remove Step"},
        {id: "removeDG", value: "Remove Data Group"}
    ],
    width: 300,
    click: function (id, context) {
        var dt = $$("fpGrid");
        webix.message(id + " on row " + this.getContext().id);
        var text = dt.getSelectedId(true, true);
        webix.message(JSON.stringify(text));
    }
};

var editDataMovementForm = {
    container: "webixUIForm",
    view: "form",
    id: "editDM",
    hidden: "true",
    width: 300,
    elements: [
        {view: "select", name: "type", label: "Type", options: ["E", "X", "R", "W"]},
        {view: "text", name: "dg", label: "dg", hidden: "true"},
        {
            view: "button", id: "SubmitButton", inputWidth: 200, value: "Save", click: function () {
            saveDataToGrid();
            $$('editDMWindow').hide();
        }
        }]

};

var selectDataGroupForm = {
    view: "form",
    id: "selectDG",
    width: 300,
    hidden: "true",
    elements: [
        {
            view: "select",
            name: "NewDataGroup",
            label: "Choose a Data Group",
            options: ["get my DG's from the db", "DataGroup4", "DataGroup56666", "DataGroup6"]
        },
        {view: "text", name: "dg", label: "dg", hidden: "true"},
        {
            view: "button", id: "SubmitButton2", inputWidth: 200, value: "Save", click: function () {
            saveDataGroupToGrid();
        }
        }]

};

var addStepButton = {
    view: "button",
    id: "addStepButton",
    type: "icon",
    value: "AddStep",
    icon: "list-ol",
    tooltip: "Add Step",
    align: "left",
    click: function (id, context) {
        addStep();
    }
};


var addDataGroupButton = {
    view: "button",
    id: "addDataGroupButton",
    value: "Add Data Group",
    type: "icon",
    icon: "files-o",
    align: "left",
    tooltip: "Add Data Group",
    click: function (id, context) {
        $$("AddDGWindow").show();
    }
};

var DGformHeader = {

    type: "clean",
    cols: [
        {template: "Enter a new Data Group"},
        {view: "button", type: "icon", icon: "close", width: 25, align: "right", click: ("$$('AddDGWindow').hide();")}
    ]
};

var editDGformHeader = {

    type: "clean",
    cols: [
        {template: "Edit the Data Group Attributes"},
        {view: "button", type: "icon", icon: "close", width: 25, align: "right", click: ("$$('editDMWindow').hide();")}
    ]
};


var addDataGroupWindow = {
    view: "window",
    id: "AddDGWindow",
    position: "Centre",
    modal: true,
    head: DGformHeader,
    body: selectDataGroupForm,
    on: {
        onBeforeShow: function () {
            $$("selectDG").show();
        }
    }
};


var editDataAttributesWindow = {
    view: "window",
    id: "editDMWindow",
    position: "Centre",
    modal: true,
    head: editDGformHeader,
    body: editDataMovementForm,
    on: {
        onBeforeShow: function () {
            webix.message("do i get here?");
            $$("editDM").show();
        }
    }
};

var contextToolBar = {
    view: "toolbar",
    id: "contextToolBar",
    paddingY: 2,
    height: 40,
    cols: [
        addStepButton,
        addDataGroupButton,
        {}]
};

var dataGridBody = {
    align: "left",
    rows: [
        gridConfig, contextToolBar
    ]
};


var fpList = {
    view: "list",
    width: 320,
    height: 500,
    select: true,
    template: "#name#",
    header: "header",
    on: {
        onAfterSelect: function (listIndex, e) {

            var fpName = this.getItem(listIndex).name;
            refreshDataTableWithNewFunctionalProcess(fpName);

        }
    }
};

var fpListWithHeader = {
    rows: [
        {template: "<b>Functional Processes</b>", height: 35},
        fpList
    ]
};


function saveDataGroupToGrid() {

    var form = $$("selectDG");
    var dt = $$("fpGrid");

    var values = form.getValues();

    var columns = webix.toArray(dt.config.columns);

    columns.insertAt({
        id: values.NewDataGroup.replace(" ", ""),
        header: values.NewDataGroup,
        css: "centre",
        width: 200
    }, columns.length);

    form.hide();
    $$("AddDGWindow").hide();

    webix.message(dt.count());

    //add the empty record.
    dt.data.each(function (obj) {

        obj[values.NewDataGroup] = "-";

    });

    dt.refreshColumns();
    //$$("theWholeThing").resize();


}


function addDataGroup() {

    var form = $$("selectDG");
    form.show();
}

function addStep() {
    var dt = $$("fpGrid");

    var rows = dt.count();
    dt.add({
        fp: "step3",
        dg1: "-",
        dg2: "-",
        dg3: "-"
    });

}

function saveDataToGrid() {

    var values = $$("editDM").getValues();
    webix.message(JSON.stringify(values));

    if (values.id) {
        var item = $$("fpGrid").getItem(values.id);

        var column = $$("editDM").getValues()["dg"];

        item[column] = values.type;
        $$("fpGrid").updateItem(item.id, item);
        //do something to save attr1 and attr2
    }

    $$("editDM").hide();
}

function readAttributesFromAServer() {

    var raw = webix.ajax().sync().get("/CosmicWorkBench/src/main/webapp/dg3.json");
    var dg3 = JSON.parse(raw.response);
    var attribs = dg3["attributes"];

    var form2 = $$("editDM");

    var pos = form2.index($$("SubmitButton"));

    //there should be some check to see if we've already read the datagroups.. because this just stupidly repeats the read
    for (i = 0; i < attribs.length; i++) {

        var attrib = attribs[i];

        form2.addView({
            id: "myID" + attrib,
            view: "checkbox",
            name: attrib,
            label: attrib
        }, pos);
    }


    form2.refresh();

}


function getFunctionalProcessColumns() {

    return [
        {id: "fp", editor: "text", header: "steps", autowidth: "true"},
        {
            id: "dg1",
            header: "Data Group 1",
            css: "centre",
            autowidth: "true",
            width: 150
        },
        {
            id: "dg2",
            header: "Data Group 2",
            css: "centre",
            autowidth: "true",
            width: 150
        },
        {
            id: "dg3",
            header: "Data Group 3",
            css: "centre",
            autowidth: "true",
            width: 150
        }
    ];

}


function getFunctionalProcessData() {
    return [
        {id: 1, fp: "step1", dg1: "-", dg2: "E", dg3: "-"},
        {id: 2, fp: "step2", dg1: "E", dg2: "-", dg3: "-"}
    ];

}


function refreshDataTableWithNewFunctionalProcess(fpName) {

    var newFPData = getFunctionalProcessData();
    newFPData.push({id: 3, fp: "step" + fpName, dg1: "-", dg2: "-", dg3: "W"})

    var dt = $$("fpGrid");
    dt.config.columns = getFunctionalProcessColumns();

    dt.clearAll();
    for (var i in newFPData) {
        dt.add(newFPData[i]);
    }
}


