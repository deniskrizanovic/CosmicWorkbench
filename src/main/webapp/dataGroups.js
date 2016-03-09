
function getListofDataGroups() {

    return [
        {id: 1, name: "Placement Base"},
        {id: 2, name: "ODS Client Base"},
        {id: 3, name: "ODS Client Address"},
        {id: 4, name: "Policy Cover Base"},
        {id: 5, name: "Policy Cover Cover Numbers"}
    ];
}

function refreshFormWithNewData(name){

    //go and get the dataGroup definition data from the database
    //apply a handlebars template.

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

var dataGroupBody = {
    view: "form",
    elements: [
        {view: "label", label: "Name"},
        {view: "label", label: "Description"},
        {view: "label", label: "Attribute 1"},
        {view: "label", label: "Attribute 1"},
        {view: "label", label: "Attribute 2"},
        {view: "label", label: "Attribute 3"},
        {view: "label", label: "Attribute 4"},
        {view: "label", label: "Description"},

        {
            margin: 5, cols: [
            {view: "button", value: "Save", type: "form"},
            {view: "button", value: "Cancel"}
        ]
        }
    ]

};




