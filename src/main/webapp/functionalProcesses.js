function getListOfFunctionalProcesses() {

    return [
        {id: 1, name: "1FuncProce10"},
        {id: 2, name: "2FuncProce20"},
        {id: 77878, name: "3FuncProce30"},
        {id: 4, name: "4FuncProce40"},
        {id: 5, name: "5FuncProce50"}
    ];

}

function getAttributesForDataGroup(row, columnContainer) {

    var selectedFP = $$("fpList").getSelectedId();
    var fp = getFunctionalProcess(selectedFP);

    var attributeNames = [];

    //this is probably as nested as it gets. Otherwise, we could do some better queries or something.
    for (var i = 0; i < fp.movements.length; i++) {
        var movement = fp.movements[i];
        if (movement.step.id == row.id) {
            for (var j = 0; j < movement.dataGroups.length; j++) {
                var dg = movement.dataGroups[j];
                if (dg.dataGroupId == columnContainer.column.id) {
                    var attribs = dg.attributes;
                    for (var k = 0; k < attribs.length; k++) {
                        var attrib = attribs[k];
                        attributeNames.push(attrib.name);

                    }
                }
            }
        }
    }

    return attributeNames.toString().replace("/,/g", ",<p>");
    //for the minute I don't mind that this doesnt seem to work. Maybe a quick html template might help here.
}
function deleteOldStateFromForm() {

    var children = $$("editDM").getChildViews();
    var i = children.length;
    while (i--) {
        var obj = children[i];
        if (obj.config.id.startsWith("attrib:")) {
            $$("editDM").removeView(obj.config.id);
        }
    }

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
            return getAttributesForDataGroup(row, columnContainer);
        }
        else {
            return "<i>" + rowValue + "</i>";
        }
    },

    on: {
        onItemDblClick: function (id, e) {

            deleteOldStateFromForm();


            var gridCoOrds = this.getSelectedId(true, true);
            var column = gridCoOrds[0].slice(gridCoOrds[0].indexOf("_") + 1, gridCoOrds[0].length);
            var row = this.getItem(id);

            //we need to send id to form to know what row to edit
            var formData = {type: row[column], id: row.id, dg: column};
            var form = $$("editDM");
            form.setValues(formData);

            readAttributesFromAServerAndSetThemIntoTheForm(row.id, column);


            var formWindow = $$("editDMWindow");
            formWindow.show();


        }
    }
};

var contextMenu = {
    view: "contextmenu",
    id: "ctxMenu",
    data: [
        {id: "removeStep", value: "Remove Step"},
        {id: "removeDG", value: "Remove Data Group"},
        {id: "existingSizing", value: "Add this to an existing Sizing Scenario"},
        {id: "newSizing", value: "Add this to an new Sizing Scenario"}
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
            saveAttributesToModelAndGrid();
            $$('editDMWindow').hide();
        }
        }]
};

function getDataGroupsFromTheServer(contextId) {

    var dataGroupsFromTheServer = [
        {id: 11, name: "DataGroupXX99"},
        {id: 22, name: "DataGroup4"},
        {id: 33, name: "DataGroup56666"},
        {id: 44, name: "DataGroup6"}];

    return dataGroupsFromTheServer;
}

function getDataGroupsNotInMatrix() {

    var dt = $$("fpGrid");
    var columns = webix.toArray(dt.config.columns);

    var dataGroupsFromTheServer = getDataGroupsFromTheServer();

    for (var i = 0; i < columns.length; i++) {
        var col = columns[i];

        for (var j = 0; j < dataGroupsFromTheServer.length; j++) {
            var dg = dataGroupsFromTheServer[j];
            if (col.id == dg.id) {
                dataGroupsFromTheServer.splice(j, 1);
                break;
            }
        }
    }

    return dataGroupsFromTheServer;

}


function transformDataGroupsIntoOptions(dgNotinMatrix) {

    var options = [];

    for (var i = 0; i < dgNotinMatrix.length; i++) {
        var dg = dgNotinMatrix[i];
        var newOption = {};
        newOption.id = dg.id;
        newOption.value = dg.name;
        options.push(newOption);
    }

    return options;
}
var selectDataGroupForm = {
    view: "form",
    id: "selectDG",
    width: 300,
    hidden: "true",
    elements: [
        {
            view: "select",
            id: "selectDataGroup",
            name: "NewDataGroup",
            label: "Add a Data Group",
            options: [],
            on: {
                onBeforeRender: function (data) {
                    var dgNotinMatrix = getDataGroupsNotInMatrix();
                    var options = transformDataGroupsIntoOptions(dgNotinMatrix);
                    webix.message("onBeforeRender!");
                    $$("selectDG").elements.NewDataGroup.config.options = options;

                }
            }
        },
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
    width: 20,
    click: function (id, context) {
        addStep();
    }
};

var saveButton = {
    view: "button",
    id: "saveButton",
    type: "icon",
    value: "Save",
    icon: "save",
    tooltip: "Save",
    align: "left",
    width: 30,
    click: function (id, context) {
        saveMatrix();
    }
};


var addDataGroupButton = {
    view: "button",
    id: "addDataGroupButton",
    value: "Add Data Group",
    type: "icon",
    icon: "files-o",
    align: "left",
    width: 30,
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

            //need to refresh the select box so that I can invoke the onBeforeRender event in the select
            var children = $$("selectDG").getChildViews();
            for (var i = 0; i < children.length; i++) {
                var obj = children[i];
                if (obj.config.id == "selectDataGroup") {
                    obj.refresh();
                }
            }

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
        saveButton,
        {}]
};

var dataGridBody = {
    align: "left",
    rows: [
        gridConfig, contextToolBar
    ]
};


var fpList = {
    id: "fpList",
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


function saveMatrix() {


    webix.message("what do I want to save?");

}

function getNameOfDataGroup(addedDataGroupId) {

    var dataGroups = getDataGroupsFromTheServer();

    //maybe one day this should be a map!
    for (var i = 0; i < dataGroups.length; i++) {
        var dg = dataGroups[i];
        if (addedDataGroupId == dg.id) {
            return dg.name;
        }
    }


}
function addNewDataGroupToModel(addedDataGroupId) {

    var nameOfDataGroup = getNameOfDataGroup(addedDataGroupId);
    var dataGroup = {};
    dataGroup.id = addedDataGroupId;
    dataGroup.name = nameOfDataGroup;
    model.dataGroups.push(dataGroup);

    for (var i = 0; i < model.movements.length; i++) {
        var movement = model.movements[i];
        var newMapping = {};
        newMapping.dataGroupId = dataGroup.id;
        newMapping.type = '-';
        newMapping.attributes = [];

        movement.dataGroups.push(newMapping);

    }

    webix.message("did my model make it changed.")


}
function saveDataGroupToGrid() {

    var form = $$("selectDG");
    var dt = $$("fpGrid");

    var addedDataGroupId = form.getValues()["NewDataGroup"];
    var nameOfDataGroup = getNameOfDataGroup(addedDataGroupId);

    var columns = webix.toArray(dt.config.columns);

    columns.insertAt({
        id: addedDataGroupId,
        header: nameOfDataGroup,
        css: "centre",
        width: 200
    }, columns.length);


    form.hide();
    $$("AddDGWindow").hide();

    //add the empty record to each step of the functional process.
    dt.data.each(function (obj) {

        obj[addedDataGroupId] = "-";

    });

    addNewDataGroupToModel(addedDataGroupId);
    dt.refreshColumns();

}


function addStep() {

    var newStep = {
        "step": {
            "id": "",
            "desc": "New Step"
        },
        "dataGroups": []
    };

    newStep.step.id = "newStep" + Math.floor((Math.random() * 1000000) + 1);

    for (var i = 0; i < model.dataGroups.length; i++) {
        var dg = model.dataGroups[i];

        var newMapping = {
            "dataGroupId": dg.id,
            "type": "-",
            "attributes": []
        };
        newStep.dataGroups.push(newMapping);

    }

    window.model.movements.push(newStep);


    refreshDataTableWithNewFunctionalProcess();


}

function updateMovement(stepId, dataGroupId, attributes, type) {

    for (var i = 0; i < model.movements.length; i++) {

        var movement = model.movements[i];
        if (movement.step.id == stepId) {

            for (var j = 0; j < movement.dataGroups.length; j++) {

                var dataGroup = movement.dataGroups[j];
                if (dataGroup.dataGroupId == dataGroupId) {
                    dataGroup.attributes = attributes;
                    dataGroup.type = type;
                }
            }
        }
    }
}


function saveAttributesToModelAndGrid() {

    var values = $$("editDM").getValues();

    if (values.id) {
        var row = $$("fpGrid").getItem(values.id);
        var dataGroupId = $$("editDM").getValues()["dg"];
        var movementType = $$("editDM").getValues()["type"];

        row[dataGroupId] = values.type;
        $$("fpGrid").updateItem(row.id, row);
    }

    var attributes = [];
    for (property in values) {
        if (property.startsWith("attrib:")) {
            var value = values[property];
            if (value != 0) {
                var newAttribute = {};
                newAttribute.attributeid = property.substring(property.indexOf(":") + 1, property.length);
                newAttribute.name = ""; //gonna have to lookup the attribute name on the server. Although I already have it!
                attributes.push(newAttribute);
            }

        }
    }

    updateMovement(row.id, dataGroupId, attributes, movementType);

    $$("editDM").hide();
}


function attributeAlreadyUsedInFunctionalProcess(stepId, dataGroupdId, attributeId) {

    for (var i = 0; i < model.movements.length; i++) {

        var movement = model.movements[i];
        if (movement.step.id == stepId) {

            for (var j = 0; j < movement.dataGroups.length; j++) {
                var dataGroup = movement.dataGroups[j];

                if (dataGroupdId == dataGroup.dataGroupId) {

                    for (var k = 0; k < dataGroup.attributes.length; k++) {
                        var attribute = dataGroup.attributes[k];
                        if (attribute.attributeid == attributeId) {
                            return true;
                        }
                    }
                }
            }
        }
    }


}


function readAttributesFromAServerAndSetThemIntoTheForm(stepId, dataGroupdId) {
    var dg3 = getDataGroup();
    var attribs = dg3["attributes"];

    var form2 = $$("editDM");

    var pos = form2.index($$("SubmitButton"));

    //there should be some check to see if we've already read the datagroups.. because this just stupidly repeats the read
    for (i = 0; i < attribs.length; i++) {

        var attrib = attribs[i];

        var attribInUse = attributeAlreadyUsedInFunctionalProcess(stepId, dataGroupdId, attrib.id);

        form2.addView({
            id: "attrib:" + attrib.id,
            view: "checkbox",
            name: "attrib:" + attrib.id,
            label: attrib.name,
            value: attribInUse
        }, pos);
    }


    form2.refresh();

}


function getFunctionalProcess() {

    if (window.model == null) {
        var raw = webix.ajax().sync().get("/CosmicWorkBench/src/main/webapp/FunctionalProcess.json");
        window.model = JSON.parse(raw.response);
    }
    return model;

}


function getHeaderTemplate() {

    //there must be a way I can read all these template in from a .hb file or something!
    var template = ' [ {{#each datagroups}}' +
        '{ "id": "{{id}}", ' +
        '"header": "{{name}}", ' +
        '"css": {"text-align": "center"}, ' +
        '"autowidth": "true", ' +
        '"autoheight": "true" }' +
        "{{#unless @last}} " +
        "," +
        "{{/unless}} " +
        '{{/each}} ]';

    return template;
}

function getFunctionalProcessColumns() {

    var fp = getFunctionalProcess();

    var templateScript = Handlebars.compile(getHeaderTemplate());
    var dgHeaders = templateScript({"datagroups": fp.dataGroups});
    dgHeaders = JSON.parse(dgHeaders);

    var stepHeader = {id: "fp", editor: "text", header: "", autowidth: "true", autoheight: "true"};

    var columns = [];
    columns.push(stepHeader);
    columns = columns.concat(dgHeaders);

    return columns;
}


function getFunctionalProcessRowTemplate() {

    return '  [  {{#each movements}}' +
        ' { ' +
        ' {{#each dataGroups}} ' +
        '  "{{dataGroupId}}":"{{type}}", ' +
        ' {{/each}} ' +
        '  "id":"{{step.id}}", ' +
        '  "fp":"{{step.desc}}" ' +
        '  }' +
        "{{#unless @last}} " +
        "," +
        "{{/unless}} " +
        '{{/each}} ]'
}

function getFunctionalProcessData() {

    //yes yes I know global variables and all, I do expect this to get fixed in the future.
    if (window.model == null) {
        model = getFunctionalProcess();
    }

    var templateScript = Handlebars.compile(getFunctionalProcessRowTemplate());
    var row = templateScript(model);
    row = JSON.parse(row);


    return row;

}


function refreshDataTableWithNewFunctionalProcess(fpName) {

    //this function is broken because it doesn't calculate the actual columns, but shows how to move data from the list to the child control
    var rows = getFunctionalProcessData();
    if (fpName) {
        rows.push({id: 3, fp: "step" + fpName, dg1: "-", dg2: "-", dg3: "W"});
    }

    var dt = $$("fpGrid");
    dt.config.columns = getFunctionalProcessColumns();

    dt.clearAll();
    for (var i in rows) {
        dt.add(rows[i]);
    }
}


