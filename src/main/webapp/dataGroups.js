function getListofDataGroups() {

    return [
        {id: 1, name: "Placement Base"},
        {id: 2, name: "ODS Client Base"},
        {id: 3, name: "ODS Client Address"},
        {id: 4, name: "Policy Cover Base"},
        {id: 5, name: "Policy Cover Cover Numbers"}
    ];
}

function getDataGroup() {
    var raw = webix.ajax().sync().get("/CosmicWorkBench/src/main/webapp/dg3.json");
    var dg3 = JSON.parse(raw.response);
    return dg3;
}

function getDataGroupWebixTemplate() {

    var template = "[ " +
        "{{#each attributes}}" +
        '{"view": "label", "label":"{{this}}"} ' +
        "{{#unless @last}} " +
        "," +
        "{{/unless}} " +
        "{{/each}} " +
        "]";

    return template;

}

function refreshFormWithNewData(id) {

    var template = getDataGroupWebixTemplate();
    var templateScript = Handlebars.compile(template);
    var context = getDataGroup();
    var formElements = templateScript(context);
    formElements = JSON.parse(formElements);

    dataGroupBody.elements = formElements;
    dataGroupBody.elements.push(commonFormControls);

    webix.ui([dataGroupWindow], $$("theWholeThing"));


}
var dataGroupList = {
    view: "list",
    width: 320,
    height: 500,
    select: true,
    template: "#name#",
    on: {
        onAfterSelect: function (listIndex, e) {
            var id = this.getItem(listIndex).id;
            refreshFormWithNewData(id);
        }
    }
};

var dataGroupListWithHeader = {
    rows: [
        {template: "<b>Data Groups</b>", height: 35},
        dataGroupList
    ]
};

var commonFormControls = {
    margin: 5, cols: [
        {view: "button", value: "Save", type: "form"},
        {view: "button", value: "Cancel"}
    ]
};

var dataGroupBody = {
    view: "form",
    id: "dataGroupBodyForm",
    elements: [



    ]

};




